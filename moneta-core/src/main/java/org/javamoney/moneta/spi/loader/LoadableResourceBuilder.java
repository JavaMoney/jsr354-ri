/*
  Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.

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
package org.javamoney.moneta.spi.loader;

import java.util.Objects;

/**
 * Builder for {@link LoadableResource}.
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
		return LoadableResourceBuilder.class.getName() + '{' +
                " loadDataInformation: " + loadDataInformation + ',' +
                " cache: " + loadDataInformation + '}';
	}
}
