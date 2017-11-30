package com.ulfric.acrodb;

class SimpleSaveableTest implements SaveableContract<SimpleSaveable> {

	@Override
	public SimpleSaveable createSaveable() {
		return new SimpleSaveable();
	}

}
