package com.ulfric.acrodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleSaveable implements Saveable {

	private final List<Runnable> save = new ArrayList<>();

	@Override
	public void save() {
		save.forEach(Runnable::run);
	}

	@Override
	public void onSave(Runnable runnable) {
		Objects.requireNonNull(runnable, "runnable");

		save.add(runnable);
	}

}
