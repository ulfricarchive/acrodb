package com.ulfric.acrodb;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.ulfric.acrodb.serialization.PojoProducer;

public final class Document extends ConcurrentSaveable {

	private final Context context;
	private final Path path;
	private volatile Object rawData;
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

	public <T> void editAndWrite(Class<T> type, Consumer<T> consumer) {
		editAndWriteIf(type, value -> {
			consumer.accept(value);
			return true;
		});
	}

	public <T> boolean editAndWriteIf(Class<T> type, Predicate<T> consumer) {
		return writeLocked(() -> {
			T value = readUnsafe(type);
			if (consumer.test(value)) {
				rawData = context.getPojoProducer().toData(value, type);
				setChanged();
				return true;
			}
			return false;
		});
	}

	public void write(Object value) {
		writeLocked(() -> {
			rawData = context.getPojoProducer().toData(value, value == null ? Object.class : value.getClass());
			setChanged();
			return null;
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
		if (rawData == null) {
			rawData = readRawDataFromPath();
		}

		@SuppressWarnings("unchecked")
		PojoProducer<Object> producer = (PojoProducer<Object>) context.getPojoProducer();
		return producer.fromData(rawData, type);
	}

	private Object readRawDataFromPath() {
		Object rawData;
		try {
			rawData = context.getPojoProducer().readData(Files.newBufferedReader(path));
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}

		return rawData == null ? context.getPojoProducer().emptyData() : rawData;
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
			Files.write(path, rawData.toString().getBytes(StandardCharsets.UTF_8));
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}

		changed = false;
	}

	void invalidate() {
		writeLocked(() -> {
			rawData = null;
			changed = false;
			return null;
		});
	}

}
