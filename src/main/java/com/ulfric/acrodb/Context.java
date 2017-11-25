package com.ulfric.acrodb;

import com.ulfric.acrodb.json.JsonProducer;
import com.ulfric.acrodb.json.gson.GsonProducer;

public final class Context {

	public static Context defaultContext() {
		return builder().build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private JsonProducer<?> jsonProducer;

		Builder() {
		}

		public Context build() {
			JsonProducer<?> jsonProducer = this.jsonProducer;
			if (jsonProducer == null) {
				jsonProducer = new GsonProducer();
			}
			return new Context(jsonProducer);
		}
	}

	private final JsonProducer<?> jsonProducer;

	private Context(JsonProducer<?> jsonProducer) {
		this.jsonProducer = jsonProducer;
	}

	public JsonProducer<?> getJsonProducer() {
		return jsonProducer;
	}

}
