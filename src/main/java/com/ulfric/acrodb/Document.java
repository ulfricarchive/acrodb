package com.ulfric.acrodb;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class Document implements ReadWriteLocked, Saveable {

	private static final Gson GSON = new Gson();

	private final Path path;
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private volatile JsonElement json;
	private volatile boolean changed;

	Document(Path path) {
		Objects.requireNonNull(path, "path");

		this.path = path;
		setupDocument();
	}

	private void setupDocument() {
		if (Files.exists(path)) {
			if (Files.isRegularFile(path)) {
				return;
			}

			throw new DocumentCreateException(path + " is an existing non-file, could not create bucket");
		}

		try {
			Files.createFile(path);
		} catch (IOException exception) {
			throw new DocumentCreateException(exception);
		}
	}

	@Override
	public void lockRead() {
		lock.readLock().lock();
	}

	@Override
	public void lockWrite() {
		lock.writeLock().lock();
	}

	@Override
	public void unlockRead() {
		lock.readLock().unlock();
	}

	@Override
	public void unlockWrite() {
		lock.writeLock().unlock();
	}

	public <T> void edit(Class<T> type, Consumer<T> consumer) {
		writeLocked(() -> {
			T value = readUnsafe(type);
			consumer.accept(value);
			json = GSON.toJsonTree(value, type);
			setChanged();
		});
	}

	public void write(Object value) {
		writeLocked(() -> {
			json = GSON.toJsonTree(value);
			setChanged();
		});
	}

	public <T> T read(Class<T> type) {
		return readLocked(() -> {
			return readUnsafe(type);
		});
	}

	private <T> T readUnsafe(Type type) {
		if (json == null) {
			json = readJsonFromPath();
		}

		return GSON.fromJson(json, type);
	}

	private JsonElement readJsonFromPath() {
		JsonElement json;
		try {
			json = GSON.fromJson(Files.newBufferedReader(path), JsonElement.class);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}

		return json == null ? new JsonObject() : json;
	}

	private void setChanged() {
		changed = true;
	}

	@Override
	public void save() {
		writeLocked(() -> {
			if (!changed) {
				return;
			}

			try {
				Files.write(path, json.toString().getBytes(StandardCharsets.UTF_8));
			} catch (IOException exception) {
				throw new UncheckedIOException(exception);
			}

			changed = false;
		});
	}

	void invalidate() {
		writeLocked(() -> {
			json = null;
			changed = false;
		});
	}

}
