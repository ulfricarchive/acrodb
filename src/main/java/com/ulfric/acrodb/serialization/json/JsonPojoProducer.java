package com.ulfric.acrodb.serialization.json;

import com.ulfric.acrodb.serialization.PojoProducer;

public abstract class JsonPojoProducer<T> implements PojoProducer<T> {

	@Override
	public final String fileExtension() {
		return "json";
	}

}
