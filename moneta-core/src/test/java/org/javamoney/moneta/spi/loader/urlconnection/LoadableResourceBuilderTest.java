/**
 * Copyright (c) 2012, 2023, Werner Keil and others by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta.spi.loader.urlconnection;

import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

import org.javamoney.moneta.spi.loader.LoadDataInformation;
import org.javamoney.moneta.spi.loader.LoadDataInformationBuilder;
import org.javamoney.moneta.spi.loader.LoaderService.LoaderListener;
import org.javamoney.moneta.spi.loader.LoaderService.UpdatePolicy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoadableResourceBuilderTest {

	private LoadDataInformation loadInformation;

	@BeforeMethod
	public void setup() throws URISyntaxException {
		String resourceId = "resourceId";
		UpdatePolicy updatePolicy = UpdatePolicy.LAZY;
	    Map<String, String> properties = Collections.emptyMap();
	    LoaderListener loaderListener = (id, is) -> {};
        URI backupResource = new URI("localhost");
        URI[] resourceLocations = new URI[]{new URI("localhost")};

		loadInformation = new LoadDataInformationBuilder()
				.withResourceId(resourceId)
				.withUpdatePolicy(updatePolicy)
				.withProperties(properties)
				.withLoaderListener(loaderListener)
				.withBackupResource(backupResource)
				.withResourceLocations(resourceLocations)
				.build();

	}

	@Test
	public void shouldCreateLoadableResource() {
		LoadableResource resource = new LoadableResourceBuilder()
				.withCache(new DefaultResourceCache())
				.withLoadDataInformation(loadInformation).build();
		assertNotNull(resource);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldReturnErrorWhendResourceCacheWasNotInformed() {
		new LoadableResourceBuilder()
				.withLoadDataInformation(loadInformation).build();
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldReturnErrorWhendLoadDataInformationWasNotInformed() {
		new LoadableResourceBuilder()
				 .withLoadDataInformation(loadInformation).build();
	}
}
