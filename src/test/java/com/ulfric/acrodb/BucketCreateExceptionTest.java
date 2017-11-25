package com.ulfric.acrodb;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

class BucketCreateExceptionTest {

	@Test
	void testNewWithMessage() {
		Truth.assertThat(new BucketCreateException("hello")).hasMessageThat().isEqualTo("hello");
	}

	@Test
	void testNewWithCause() {
		Truth.assertThat(new BucketCreateException(new RuntimeException())).hasCauseThat().isInstanceOf(RuntimeException.class);
	}

}
