package com.ulfric.acrodb;

public class BucketCreateException extends RuntimeException {

	public BucketCreateException(String message) {
		super(message);
	}

	public BucketCreateException(Throwable thrown) {
		super(thrown);
	}

}
