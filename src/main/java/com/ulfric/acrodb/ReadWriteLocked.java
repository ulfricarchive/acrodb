package com.ulfric.acrodb;

import java.util.function.Supplier;

public interface ReadWriteLocked {

	void lockRead();

	void lockWrite();

	void unlockRead();

	void unlockWrite();

	default <T> T readLocked(Supplier<T> supplier) {
		lockRead();
		try {
			return supplier.get();
		} finally {
			unlockRead();
		}
	}

	default void writeLocked(Runnable runnable) {
		lockWrite();
		try {
			runnable.run();
		} finally {
			unlockWrite();
		}
	}

}
