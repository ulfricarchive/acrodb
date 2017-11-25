package com.ulfric.acrodb;

import java.util.stream.Stream;

public interface DocumentStore {

	Document openDocument(String name);

	void deleteDocument(String name);

	Stream<Document> loadAllDocuments();

}
