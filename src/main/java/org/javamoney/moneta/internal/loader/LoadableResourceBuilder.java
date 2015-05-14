package org.javamoney.moneta.internal.loader;

import java.util.Objects;

import org.javamoney.moneta.spi.LoadDataInformation;

/**
 * @param resourceId       The dataId.
 * @param cache            The cache to be used for storing remote data locally.
 * @param properties       The configuration properties.
 * @param fallbackLocation teh fallback ULR, not null.
 * @param locations        the remote locations, not null (but may be empty!)
 */
public class LoadableResourceBuilder {

	private LoadDataInformation loadDataInformation;

	private ResourceCache cache;

	public LoadableResourceBuilder withLoadDataInformation(LoadDataInformation loadDataInformation) {
		this.loadDataInformation = loadDataInformation;
		return this;
	}

	public LoadableResourceBuilder withCache(ResourceCache cache) {
		this.cache = cache;
		return this;
	}

	public LoadableResource build() {
		if(Objects.isNull(cache)) {
			throw new IllegalStateException("The cache should be informed");
		}
		if(Objects.isNull(loadDataInformation)) {
			throw new IllegalStateException("The loadDataInformation should be informed");
		}
		return new LoadableResource(cache, loadDataInformation);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(LoadableResourceBuilder.class.getName()).append('{')
		.append(" loadDataInformation: ").append(loadDataInformation).append(',')
		.append(" cache: ").append(loadDataInformation).append('}');
		return sb.toString();
	}
}
