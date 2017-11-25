package com.ulfric.acrodb.json;

import java.io.Reader;
import java.lang.reflect.Type;

public interface JsonProducer<T> {

	T empty();

	T readJson(Reader reader);

	<R> R fromJson(T json, Type type);

	T toJson(Object bean, Type type);

}
