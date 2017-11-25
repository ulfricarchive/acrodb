package com.ulfric.acrodb;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public final class Bucket implements BucketStore, DocumentStore, Saveable {

	private static final Pattern VALID_NAME = Pattern.compile("[a-zA-Z0-9]+([a-zA-Z0-9-]+[a-zA-Z0-9]+)?");
	private final Path path;
	private final ConcurrentMap<Path, Bucket> childBuckets = new ConcurrentHashMap<>(2);
	private final ConcurrentMap<Path, Document> documents = new ConcurrentHashMap<>(2);

	public Bucket() {
		this(Paths.get("acrodb"));
	}

	public Bucket(Path path) {
		Objects.requireNonNull(path, "path");

		this.path = path;
		setupBucket();
	}

	private void setupBucket() {
		if (Files.exists(path)) {
			if (Files.isDirectory(path)) {
				return;
			}

			throw new BucketCreateException(path + " is an existing non-directory, could not create bucket");
		}

		try {
			Files.createDirectories(path);
		} catch (IOException exception) {
			throw new BucketCreateException(exception);
		}
	}

	@Override
	public Bucket openBucket(String name) {
		validateBucketName(name);

		return childBuckets.computeIfAbsent(path.resolve(name), Bucket::new);
	}

	@Override
	public Document openDocument(String name) {
		validateDocumentName(name);

		return openDocument(path.resolve(name + ".json"));
	}

	@Override
	public void deleteDocument(String name) {
		validateDocumentName(name);

		documents.compute(path.resolve(name + ".json"), (path, document) -> {
			if (document != null) {
				document.invalidate();
			}

			try {
				Files.deleteIfExists(path);
			} catch (IOException exception) {
				throw new UncheckedIOException(exception);
			}

			return document;
		});
	}

	private Document openDocument(Path path) {
		return documents.computeIfAbsent(path, Document::new);
	}

	private void validateBucketName(String name) {
		validateName("Bucket", name);
	}

	private void validateDocumentName(String name) {
		validateName("Document", name);
	}

	private void validateName(String type, String name) {
		Objects.requireNonNull(name, "name");

		if (name.isEmpty()) {
			throw new IllegalArgumentException(type + " name '" + "' must not be empty");
		}

		if (!VALID_NAME.matcher(name).matches()) {
			throw new IllegalArgumentException(type + " name '" + name + "' must match pattern " + VALID_NAME.pattern());
		}
	}

	@Override
	public void save() {
		documents.values().forEach(Document::save);
		childBuckets.values().forEach(Bucket::save);
	}

}
