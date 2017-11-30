package com.ulfric.acrodb;

class ConcurrentSaveableTest implements SaveableContract<ConcurrentSaveable> {

	@Override
	public ConcurrentSaveable createSaveable() {
		return new ConcurrentSaveable();
	}

}
