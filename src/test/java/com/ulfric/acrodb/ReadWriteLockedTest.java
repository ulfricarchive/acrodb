package com.ulfric.acrodb;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.google.common.truth.Truth;

class ReadWriteLockedTest {

	@Test
	void testReadLockedDoesLock() {
		ReadWriteLocked mock = Mockito.mock(ReadWriteLocked.class);
		Mockito.doCallRealMethod().when(mock).readLocked(ArgumentMatchers.any());
		boolean locked = mock.readLocked(() -> {
			Mockito.verify(mock, Mockito.times(1)).lockRead();
			return true;
		});
		Truth.assertThat(locked).isTrue();
		Mockito.verify(mock, Mockito.times(1)).unlockRead();
	}

	@Test
	void testWriteLockedDoesLock() {
		ReadWriteLocked mock = Mockito.mock(ReadWriteLocked.class);
		Mockito.doCallRealMethod().when(mock).writeLocked(ArgumentMatchers.any());
		boolean[] locked = { false };
		mock.writeLocked(() -> {
			Mockito.verify(mock, Mockito.times(1)).lockWrite();
			locked[0] = true;
		});
		Truth.assertThat(locked[0]).isTrue();
		Mockito.verify(mock, Mockito.times(1)).unlockWrite();
	}

}
