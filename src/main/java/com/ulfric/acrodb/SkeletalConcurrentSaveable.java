package com.ulfric.acrodb;

public abstract class SkeletalConcurrentSaveable extends SimpleSaveable implements ReadWriteLocked {

	@Override
	public final void save() {
		writeLocked(() -> {
			super.save();
			onConcurrentSave();
		});
	}

	@Override
	public final void onSave(Runnable runnable) {
		writeLocked(() -> {
			super.onSave(runnable);
		});
	}

	protected void onConcurrentSave() {
	}

}
