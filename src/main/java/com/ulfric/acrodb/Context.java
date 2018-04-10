package com.ulfric.acrodb;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import com.ulfric.acrodb.serialization.PojoProducer;
import com.ulfric.acrodb.serialization.json.gson.GsonPojoProducer;

public final class Context {

	public static Context defaultContext() {
		return builder().build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private PojoProducer<?> pojoProducer;
		private FileSystem fileSystem;
		private String rootDirectory;

		Builder() {
		}

		public Context build() {
			PojoProducer<?> pojoProducer = this.pojoProducer;
			if (pojoProducer == null) {
				pojoProducer = new GsonPojoProducer();
			}

			FileSystem fileSystem = this.fileSystem;
			if (fileSystem == null) {
				fileSystem = FileSystems.getDefault();
			}

			String rootDirectory = this.rootDirectory;
			if (rootDirectory == null) {
				rootDirectory = "acrodb";
			}

			return new Context(pojoProducer, fileSystem, rootDirectory);
		}

		public Builder setPojoProducer(PojoProducer<?> pojoProducer) {
			this.pojoProducer = pojoProducer;
			return this;
		}

		public Builder setFileSystem(FileSystem fileSystem) {
			this.fileSystem = fileSystem;
			return this;
		}

		public Builder setRootDirectory(String rootDirectory) {
			this.rootDirectory = rootDirectory;
			return this;
		}
	}

	private final PojoProducer<?> pojoProducer;
	private final FileSystem fileSystem;
	private final String rootDirectory;

	private Context(PojoProducer<?> pojoProducer, FileSystem fileSystem, String rootDirectory) {
		this.pojoProducer = pojoProducer;
		this.fileSystem = fileSystem;
		this.rootDirectory = rootDirectory;
	}

	public PojoProducer<?> getPojoProducer() {
		return pojoProducer;
	}

	public FileSystem getFileSystem() {
		return fileSystem;
	}

	public String getRootDirectory() {
		return rootDirectory;
	}

}
