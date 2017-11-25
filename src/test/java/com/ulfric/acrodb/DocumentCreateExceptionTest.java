package com.ulfric.acrodb;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

class DocumentCreateExceptionTest {

	@Test
	void testNewWithMessage() {
		Truth.assertThat(new DocumentCreateException("hello")).hasMessageThat().isEqualTo("hello");
	}

	@Test
	void testNewWithCause() {
		Truth.assertThat(new DocumentCreateException(new RuntimeException())).hasCauseThat().isInstanceOf(RuntimeException.class);
	}

}
