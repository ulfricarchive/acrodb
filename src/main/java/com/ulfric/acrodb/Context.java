package com.ulfric.acrodb;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import com.ulfric.acrodb.json.JsonProducer;
import com.ulfric.acrodb.json.gson.GsonProducer;

public final class Context {

	public static Context defaultContext() {
		return builder().build();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private JsonProducer<?> jsonProducer;
		private FileSystem fileSystem;
		private String rootDirectory;

		Builder() {
		}

		public Context build() {
			JsonProducer<?> jsonProducer = this.jsonProducer;
			if (jsonProducer == null) {
				jsonProducer = new GsonProducer();
			}

			FileSystem fileSystem = this.fileSystem;
			if (fileSystem == null) {
				fileSystem = FileSystems.getDefault();
			}

			String rootDirectory = this.rootDirectory;
			if (rootDirectory == null) {
				rootDirectory = "acrodb";
			}

			return new Context(jsonProducer, fileSystem, rootDirectory);
		}

		public Builder setJsonProducer(JsonProducer<?> jsonProducer) {
			this.jsonProducer = jsonProducer;
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

	private final JsonProducer<?> jsonProducer;
	private final FileSystem fileSystem;
	private final String rootDirectory;

	private Context(JsonProducer<?> jsonProducer, FileSystem fileSystem, String rootDirectory) {
		this.jsonProducer = jsonProducer;
		this.fileSystem = fileSystem;
		this.rootDirectory = rootDirectory;
	}

	public JsonProducer<?> getJsonProducer() {
		return jsonProducer;
	}

	public FileSystem getFileSystem() {
		return fileSystem;
	}

	public String getRootDirectory() {
		return rootDirectory;
	}

}
