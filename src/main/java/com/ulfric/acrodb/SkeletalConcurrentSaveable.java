package com.ulfric.acrodb;

public abstract class SkeletalConcurrentSaveable extends SimpleSaveable implements ReadWriteLocked {

	@Override
	public final void save() {
		writeLocked(() -> {
			super.save();
			onConcurrentSave();
			return null;
		});
	}

	@Override
	public final void onSave(Runnable runnable) {
		writeLocked(() -> {
			super.onSave(runnable);
			return null;
		});
	}

	protected void onConcurrentSave() {
	}

}
