package com.ulfric.acrodb;

public interface DocumentStore {

	Document openDocument(String name);

	void deleteDocument(String name);

}
