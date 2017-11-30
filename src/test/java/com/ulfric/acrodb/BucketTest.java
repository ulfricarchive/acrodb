package com.ulfric.acrodb;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

class BucketTest implements JimfsTestBase {

	@Test
	void testNewNullPath() {
		Assertions.assertThrows(NullPointerException.class, () -> new Bucket((Path) null));
	}

	@Test
	void testNewNullContext() {
		Assertions.assertThrows(NullPointerException.class, () -> new Bucket((Context) null));
	}

	@JimfsTest
	void testNewCreatesDirectory(FileSystem jimfs) {
		Path path = jimfs.getPath("acrodb");
		new Bucket(path);
		Truth.assertThat(Files.isDirectory(path)).isTrue();
	}

	@JimfsTest
	void testFailsWhenDirectoryIsExistingNonDirectory(FileSystem jimfs) throws IOException {
		Path path = jimfs.getPath("acrodb");
		Files.createFile(path);
		Assertions.assertThrows(BucketCreateException.class, () -> new Bucket(path));
	}

	@JimfsTest
	void testFailsWhenDirectoryIsExistingDirectory(FileSystem jimfs) throws IOException {
		Path path = jimfs.getPath("acrodb");
		Files.createDirectory(path);
		new Bucket(path);
		Truth.assertThat(Files.isDirectory(path)).isTrue();
	}

	@JimfsTest
	void testOpenBucketNull(FileSystem jimfs) {
		Path path = jimfs.getPath("acrodb");
		Bucket bucket = new Bucket(path);
		Assertions.assertThrows(NullPointerException.class, () -> bucket.openBucket(null));
	}

	@JimfsTest
	void testOpenBucketEmpty(FileSystem jimfs) {
		Path path = jimfs.getPath("acrodb");
		Bucket bucket = new Bucket(path);
		Assertions.assertThrows(IllegalArgumentException.class, () -> bucket.openBucket(""));
	}

	@JimfsTest
	void testOpenBucketIllegalPath(FileSystem jimfs) {
		Path path = jimfs.getPath("acrodb");
		Bucket bucket = new Bucket(path);
		Assertions.assertThrows(IllegalArgumentException.class, () -> bucket.openBucket("hello!"));
	}

	@JimfsTest
	void testOpenBucketValidPath(FileSystem jimfs) {
		Path path = jimfs.getPath("acrodb");
		new Bucket(path).openBucket("hello");
		Truth.assertThat(Files.isDirectory(path.resolve("hello"))).isTrue();
	}

	@JimfsTest
	void testOpenDocumentNull(FileSystem jimfs) {
		Path path = jimfs.getPath("acrodb");
		Bucket bucket = new Bucket(path);
		Assertions.assertThrows(NullPointerException.class, () -> bucket.openDocument(null));
	}

	@JimfsTest
	void testOpenDocumentEmpty(FileSystem jimfs) {
		Path path = jimfs.getPath("acrodb");
		Bucket bucket = new Bucket(path);
		Assertions.assertThrows(IllegalArgumentException.class, () -> bucket.openDocument(""));
	}

	@JimfsTest
	void testOpenDocumentIllegalPath(FileSystem jimfs) {
		Path path = jimfs.getPath("acrodb");
		Bucket bucket = new Bucket(path);
		Assertions.assertThrows(IllegalArgumentException.class, () -> bucket.openDocument("hello!"));
	}

	@JimfsTest
	void testOpenDocumentValidPath(FileSystem jimfs) {
		Path path = jimfs.getPath("acrodb");
		new Bucket(path).openDocument("hello");
		Truth.assertThat(Files.isRegularFile(path.resolve("hello.json"))).isTrue();
	}

	@JimfsTest
	void testSavesChildren(FileSystem jimfs) throws JsonSyntaxException, JsonIOException, IOException {
		Path path = jimfs.getPath("acrodb");
		Bucket bucket = new Bucket(path);
		Bucket childBucket = bucket.openBucket("child");
		Document document = childBucket.openDocument("hello");

		String message = "hello world";
		JsonObject json = new JsonObject();
		json.addProperty("message", message);
		document.write(json);

		bucket.save();

		json = new Gson().fromJson(Files.newBufferedReader(path.resolve("child").resolve("hello.json")), JsonObject.class);
		Truth.assertThat(json.get("message").getAsString()).isEqualTo(message);
	}

	@JimfsTest
	void testDeleteDocumentRemovesFile(FileSystem jimfs) throws JsonSyntaxException, JsonIOException {
		Path path = jimfs.getPath("acrodb");
		Bucket bucket = new Bucket(path);
		Document document = bucket.openDocument("hello");

		document.write(new JsonObject());

		bucket.save();
		Truth.assertThat(Files.exists(path.resolve("hello.json"))).isTrue();

		bucket.deleteDocument("hello");
		Truth.assertThat(Files.notExists(path.resolve("hello.json"))).isTrue();
	}

	@JimfsTest
	void testSaveOnDeletedDocumentDoesNothing(FileSystem jimfs) throws JsonSyntaxException, JsonIOException {
		Path path = jimfs.getPath("acrodb");
		Bucket bucket = new Bucket(path);
		Document document = bucket.openDocument("hello");

		document.write(new JsonObject());
		bucket.deleteDocument("hello");
		document.save();

		Truth.assertThat(Files.notExists(path.resolve("hello.json"))).isTrue();
	}

	@JimfsTest
	void testDeleteDocumentWhenNotPresentDoesNothing(FileSystem jimfs) {
		Path path = jimfs.getPath("acrodb");
		Bucket bucket = new Bucket(path);
		bucket.deleteDocument("some-bucket");
	}

	@JimfsTest
	void testLoadAllDocumentsFindsUnloaded(FileSystem jimfs) throws IOException {
		Path path = jimfs.getPath("acrodb");
		Path documentPath = path.resolve("hello.json");

		SampleBean sampleBean = new SampleBean();
		sampleBean.someField = "some data";
		Files.createDirectory(path);
		Files.write(documentPath, new Gson().toJson(sampleBean).getBytes());

		Document document = new Bucket(path).loadAllDocuments().findAny().get();
		sampleBean = document.read(SampleBean.class);
		Truth.assertThat(sampleBean.someField).isEqualTo("some data");
	}

	static class SampleBean {
		String someField;
	}

}
