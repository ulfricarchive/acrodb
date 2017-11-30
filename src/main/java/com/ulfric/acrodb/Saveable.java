package com.ulfric.acrodb;

public interface Saveable {

	void save();

	void onSave(Runnable runnable);

}
