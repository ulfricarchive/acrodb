package com.ulfric.acrodb;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;
import com.google.gson.Gson;

class DocumentTest implements JimfsTestBase {

	@Test
	void testNewBothNull() {
		Assertions.assertThrows(NullPointerException.class, () -> new Document(null, null));
	}

	@JimfsTest
	void testNewAlreadyExistingRegularFile(FileSystem jimfs) throws IOException {
		Path path = jimfs.getPath("some-document");
		Files.createFile(path);
		newDocument(path);
		Truth.assertThat(Files.isRegularFile(path));
	}

	@JimfsTest
	void testNewAlreadyExistingNotRegularFile(FileSystem jimfs) throws IOException {
		Path path = jimfs.getPath("some-document");
		Files.createDirectory(path);
		Assertions.assertThrows(DocumentCreateException.class, () -> newDocument(path));
	}

	@JimfsTest
	void testEdit(FileSystem jimfs) {
		Document document = newDocument(jimfs.getPath("some-document"));

		String message = "hello world";
		document.edit(SomeBean.class, someBean -> someBean.message = message);

		SomeBean[] instance = { null };
		document.edit(SomeBean.class, someBean -> instance[0] = someBean);

		Truth.assertThat(instance[0].message).isEqualTo(message);
	}

	@JimfsTest
	void testEditWithExistingData(FileSystem jimfs) throws IOException {
		Path path = jimfs.getPath("some-document");

		String message = "hello world";

		SomeBean dataMock = new SomeBean();
		dataMock.message = message;

		Files.write(path, new Gson().toJson(dataMock).getBytes());

		Document document = newDocument(path);

		SomeBean[] instance = { null };
		document.edit(SomeBean.class, someBean -> instance[0] = someBean);

		Truth.assertThat(instance[0].message).isEqualTo(message);
	}

	@JimfsTest
	void testReadEmpty(FileSystem jimfs) {
		Document document = newDocument(jimfs.getPath("some-document"));

		SomeBean instance = document.read(SomeBean.class);

		Truth.assertThat(instance.message).isNull();
	}

	@JimfsTest
	void testEditThenRead(FileSystem jimfs) {
		Document document = newDocument(jimfs.getPath("some-document"));

		String message = "hello world";
		document.edit(SomeBean.class, someBean -> someBean.message = message);

		SomeBean instance = document.read(SomeBean.class);

		Truth.assertThat(instance.message).isEqualTo(message);
	}

	@JimfsTest
	void testWriteThenRead(FileSystem jimfs) {
		Document document = newDocument(jimfs.getPath("some-document"));

		String message = "hello world";
		SomeBean someBean = new SomeBean();
		someBean.message = message;
		document.write(someBean);

		SomeBean instance = document.read(SomeBean.class);

		Truth.assertThat(instance.message).isEqualTo(message);
	}

	@JimfsTest
	void testEditWithMissingFileForCodeCoverage(FileSystem jimfs) throws IOException {
		Path path = jimfs.getPath("some-document");
		Document document = newDocument(path);
		Files.delete(path);

		Assertions.assertThrows(UncheckedIOException.class, () -> document.edit(SomeBean.class, ignore -> { }));
	}

	@JimfsTest
	void testReadWithExistingData(FileSystem jimfs) throws IOException {
		Path path = jimfs.getPath("some-document");

		String message = "hello world";

		SomeBean dataMock = new SomeBean();
		dataMock.message = message;

		Files.write(path, new Gson().toJson(dataMock).getBytes());

		Document document = newDocument(path);

		SomeBean instance = document.read(SomeBean.class);

		Truth.assertThat(instance.message).isEqualTo(message);
	}

	@JimfsTest
	void testReadWithMissingFileForCodeCoverage(FileSystem jimfs) throws IOException {
		Path path = jimfs.getPath("some-document");
		Document document = newDocument(path);
		Files.delete(path);

		Assertions.assertThrows(UncheckedIOException.class, () -> document.read(SomeBean.class));
	}

	@JimfsTest
	void testSaveWithNoData(FileSystem jimfs) throws IOException {
		Path path = jimfs.getPath("some-document");
		Document document = newDocument(path);
		document.save();

		Truth.assertThat(Files.readAllBytes(path)).isEmpty();
	}

	@JimfsTest
	void testSaveWithData(FileSystem jimfs) throws IOException {
		Path path = jimfs.getPath("some-document");
		Document document = newDocument(path);

		String message = "hello world";
		SomeBean someBean = new SomeBean();
		someBean.message = message;
		document.write(someBean);
		document.save();

		someBean = new Gson().fromJson(new String(Files.readAllBytes(path)), SomeBean.class);
		Truth.assertThat(someBean.message).isEqualTo(message);
	}

	private Document newDocument(Path path) {
		return new Document(Context.defaultContext(), path);
	}

	static class SomeBean {
		String message;
	}

}
