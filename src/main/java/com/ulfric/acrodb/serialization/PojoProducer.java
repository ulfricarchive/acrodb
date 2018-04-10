package com.ulfric.acrodb.serialization;

import java.io.Reader;
import java.lang.reflect.Type;

public interface PojoProducer<T> {

	String fileExtension();

	T emptyData();

	T readData(Reader reader);

	<R> R fromData(T data, Type type);

	T toData(Object pojo, Type type);

}
