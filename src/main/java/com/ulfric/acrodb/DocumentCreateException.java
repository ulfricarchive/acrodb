package com.ulfric.acrodb;

public class DocumentCreateException extends RuntimeException {

	public DocumentCreateException(String message) {
		super(message);
	}

	public DocumentCreateException(Throwable thrown) {
		super(thrown);
	}

}
