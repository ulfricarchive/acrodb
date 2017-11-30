package com.ulfric.acrodb;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

import com.ulfric.acrodb.json.JsonProducer;

public final class Document extends ConcurrentSaveable {

	private final Context context;
	private final Path path;
	private volatile Object json;
	private volatile boolean changed;

	Document(Context context, Path path) {
		Objects.requireNonNull(context, "context");
		Objects.requireNonNull(path, "path");

		this.context = context;
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

	public <T> void edit(Class<T> type, Consumer<T> consumer) {
		writeLocked(() -> {
			T value = readUnsafe(type);
			consumer.accept(value);
			json = context.getJsonProducer().toJson(value, type);
			setChanged();
		});
	}

	public void write(Object value) {
		writeLocked(() -> {
			json = context.getJsonProducer().toJson(value, value == null ? Object.class : value.getClass());
			setChanged();
		});
	}

	public <T> T read(Class<T> type) {
		return readLocked(() -> {
			return readUnsafe(type);
		});
	}

	public <T> T read(Type type) {
		return readLocked(() -> {
			return readUnsafe(type);
		});
	}

	private <T> T readUnsafe(Type type) {
		if (json == null) {
			json = readJsonFromPath();
		}

		@SuppressWarnings("unchecked")
		JsonProducer<Object> producer = (JsonProducer<Object>) context.getJsonProducer();
		return producer.fromJson(json, type);
	}

	private Object readJsonFromPath() {
		Object json;
		try {
			json = context.getJsonProducer().readJson(Files.newBufferedReader(path));
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}

		return json == null ? context.getJsonProducer().empty() : json;
	}

	private void setChanged() {
		changed = true;
	}

	@Override
	protected void onConcurrentSave() {
		super.onConcurrentSave();

		if (!changed) {
			return;
		}

		try {
			Files.write(path, json.toString().getBytes(StandardCharsets.UTF_8));
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}

		changed = false;
	}

	void invalidate() {
		writeLocked(() -> {
			json = null;
			changed = false;
		});
	}

}
