package com.ulfric.acrodb.json.gson;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ulfric.acrodb.json.JsonProducer;

public class GsonProducer implements JsonProducer<JsonElement> {

	protected final Gson gson;

	public GsonProducer() {
		this(new Gson());
	}

	public GsonProducer(Gson gson) {
		Objects.requireNonNull(gson, "gson");

		this.gson = gson;
	}

	@Override
	public JsonElement empty() {
		return new JsonObject();
	}

	@Override
	public JsonElement readJson(Reader reader) {
		return gson.fromJson(reader, JsonElement.class);
	}

	@Override
	public <R> R fromJson(JsonElement json, Type type) {
		return gson.fromJson(json, type);
	}

	@Override
	public JsonElement toJson(Object bean, Type type) {
		return gson.toJsonTree(bean, type);
	}

}
