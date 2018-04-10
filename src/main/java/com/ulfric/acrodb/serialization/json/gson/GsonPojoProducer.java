package com.ulfric.acrodb.serialization.json.gson;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ulfric.acrodb.serialization.json.JsonPojoProducer;

public class GsonPojoProducer extends JsonPojoProducer<JsonElement> {

	protected final Gson gson;

	public GsonPojoProducer() {
		this(new Gson());
	}

	public GsonPojoProducer(Gson gson) {
		Objects.requireNonNull(gson, "gson");

		this.gson = gson;
	}

	@Override
	public JsonElement emptyData() {
		return new JsonObject();
	}

	@Override
	public JsonElement readData(Reader reader) {
		return gson.fromJson(reader, JsonElement.class);
	}

	@Override
	public <R> R fromData(JsonElement data, Type type) {
		return gson.fromJson(data, type);
	}

	@Override
	public JsonElement toData(Object pojo, Type type) {
		return gson.toJsonTree(pojo, type);
	}

}
