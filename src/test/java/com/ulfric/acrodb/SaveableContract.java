package com.ulfric.acrodb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.truth.Truth;

public interface SaveableContract<T extends Saveable> {

	@Test
	default void testOnSaveWithNull() {
		Exception exception = Assertions.assertThrows(NullPointerException.class, () -> createSaveable().onSave(null));
		Truth.assertThat(exception).hasMessageThat().isEqualTo("runnable");
	}

	@Test
	default void testOnSaveWorks() {
		Runnable mock = Mockito.mock(Runnable.class);
		T saveable = createSaveable();
		saveable.onSave(mock);
		saveable.save();
		Mockito.verify(mock, Mockito.times(1)).run();
	}

	T createSaveable();

}
