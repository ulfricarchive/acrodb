package com.ulfric.acrodb;

import java.nio.file.FileSystem;
import java.util.stream.Stream;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public abstract class JimfsTestBase {

	static Stream<FileSystem> fileSystemProvider() {
		return Stream.of(Configuration.unix(), Configuration.osX(), Configuration.windows())
				.map(Jimfs::newFileSystem);
	}

}
