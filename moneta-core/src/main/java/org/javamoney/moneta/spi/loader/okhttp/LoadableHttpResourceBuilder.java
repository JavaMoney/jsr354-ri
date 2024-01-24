/*
  Copyright (c) 2023, 2024, Werner Keil and others by the @author tag.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
 */
package org.javamoney.moneta.spi.loader.okhttp;

import org.javamoney.moneta.spi.loader.LoadDataInformation;
import org.javamoney.moneta.spi.loader.ResourceCache;

import java.util.Objects;

/**
 * Builder for {@link LoadableHttpResource}.
 * @author Werner Keil
 */
class LoadableHttpResourceBuilder {

	private LoadDataInformation loadDataInformation;

	private ResourceCache cache;

	public LoadableHttpResourceBuilder withLoadDataInformation(LoadDataInformation loadDataInformation) {
		this.loadDataInformation = loadDataInformation;
		return this;
	}

	public LoadableHttpResourceBuilder withCache(ResourceCache cache) {
		this.cache = cache;
		return this;
	}

	public LoadableHttpResource build() {
		if(Objects.isNull(cache)) {
			throw new IllegalStateException("The cache should be present");
		}
		if(Objects.isNull(loadDataInformation)) {
			throw new IllegalStateException("The loadDataInformation should be present");
		}
		return new LoadableHttpResource(cache, loadDataInformation);
	}

	@Override
	public String toString() {
		return LoadableHttpResourceBuilder.class.getName() + '{' +
                " loadDataInformation: " + loadDataInformation + ',' +
                " cache: " + loadDataInformation + '}';
	}
}
