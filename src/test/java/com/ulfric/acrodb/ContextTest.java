package com.ulfric.acrodb;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;
import com.ulfric.acrodb.serialization.PojoProducer;
import com.ulfric.acrodb.serialization.json.gson.GsonPojoProducer;

class ContextTest {

	@Test
	void testSetJsonProducerWorks() {
		PojoProducer<?> producer = new GsonPojoProducer();
		PojoProducer<?> actualProducer = Context.builder()
			.setPojoProducer(producer)
			.build()
			.getPojoProducer();

		Truth.assertThat(actualProducer).isSameAs(producer);
	}

	@Test
	void testSetJsonProducerIsDefaulted() {
		PojoProducer<?> actualProducer = Context.builder()
			.build()
			.getPojoProducer();

		Truth.assertThat(actualProducer).isNotNull();
	}

}
