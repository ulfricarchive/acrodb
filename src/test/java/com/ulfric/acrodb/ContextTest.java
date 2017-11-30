package com.ulfric.acrodb;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;
import com.ulfric.acrodb.json.JsonProducer;
import com.ulfric.acrodb.json.gson.GsonProducer;

class ContextTest {

	@Test
	void testSetJsonProducerWorks() {
		JsonProducer<?> producer = new GsonProducer();
		JsonProducer<?> actualProducer = Context.builder()
			.setJsonProducer(producer)
			.build()
			.getJsonProducer();

		Truth.assertThat(actualProducer).isSameAs(producer);
	}

	@Test
	void testSetJsonProducerIsDefaulted() {
		JsonProducer<?> actualProducer = Context.builder()
			.build()
			.getJsonProducer();

		Truth.assertThat(actualProducer).isNotNull();
	}

}
