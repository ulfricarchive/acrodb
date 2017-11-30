package com.ulfric.acrodb;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentSaveable extends SkeletalConcurrentSaveable {

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	public final void lockRead() {
		lock.readLock().lock();
	}

	@Override
	public final void lockWrite() {
		lock.writeLock().lock();
	}

	@Override
	public final void unlockRead() {
		lock.readLock().unlock();
	}

	@Override
	public final void unlockWrite() {
		lock.writeLock().unlock();
	}

}
