/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.spi;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.javamoney.moneta.spi.LoaderService.LoaderListener;
import org.javamoney.moneta.spi.LoaderService.UpdatePolicy;
import org.testng.annotations.Test;

public class LoadDataInformationBuilderTest {


	@Test
	public void shouldCreateLoadDataInformation() throws URISyntaxException {
		String resourceId = "resourceId";
		UpdatePolicy updatePolicy = UpdatePolicy.LAZY;
	    Map<String, String> properties = new HashMap<>();
	    LoaderListener loaderListener = new LoaderListener() {

			@Override
			public void newDataLoaded(String resourceId, InputStream is) {
			}
		};
        URI backupResource = new URI("localhost");
        URI[] resourceLocations = new URI[]{new URI("localhost")};

		LoadDataInformation loadInformation = new LoadDataInformationBuilder().withResourceId(resourceId)
				.withUpdatePolicy(updatePolicy).withProperties(properties)
				.withLoaderListener(loaderListener)
				.withBackupResource(backupResource).withStartRemote(true)
				.withResourceLocations(resourceLocations).build();

		assertEquals(loadInformation.getResourceId(), resourceId);
		assertEquals(loadInformation.getUpdatePolicy(), updatePolicy);
		assertEquals(loadInformation.getProperties(), properties);
		assertEquals(loadInformation.getBackupResource(), backupResource);
		assertEquals(loadInformation.getResourceLocations(), resourceLocations);
		assertTrue(loadInformation.isStartRemote());

	}

	@Test
	public void shouldCreateLoadDataInformationWithOutListener() throws URISyntaxException {
		String resourceId = "resourceId";
		UpdatePolicy updatePolicy = UpdatePolicy.LAZY;
	    Map<String, String> properties = new HashMap<>();
        URI backupResource = new URI("localhost");
        URI[] resourceLocations = new URI[]{new URI("localhost")};

		LoadDataInformation loadInformation = new LoadDataInformationBuilder().withResourceId(resourceId)
				.withUpdatePolicy(updatePolicy).withProperties(properties)
				.withBackupResource(backupResource).withStartRemote(false)
				.withResourceLocations(resourceLocations).build();

		assertEquals(loadInformation.getResourceId(), resourceId);
		assertEquals(loadInformation.getUpdatePolicy(), updatePolicy);
		assertEquals(loadInformation.getProperties(), properties);
		assertEquals(loadInformation.getBackupResource(), backupResource);
		assertEquals(loadInformation.getResourceLocations(), resourceLocations);
		assertFalse(loadInformation.isStartRemote());

	}

	@Test
	public void shouldCreateLoadDataInformationWithOutBackupResource() throws URISyntaxException {
		String resourceId = "resourceId";
		UpdatePolicy updatePolicy = UpdatePolicy.LAZY;
	    Map<String, String> properties = new HashMap<>();
	    LoaderListener loaderListener = new LoaderListener() {

			@Override
			public void newDataLoaded(String resourceId, InputStream is) {
			}
		};
        URI[] resourceLocations = new URI[]{new URI("localhost")};

		LoadDataInformation loadInformation = new LoadDataInformationBuilder().withResourceId(resourceId)
				.withUpdatePolicy(updatePolicy).withProperties(properties)
				.withLoaderListener(loaderListener)
				.withResourceLocations(resourceLocations).build();

		assertEquals(loadInformation.getResourceId(), resourceId);
		assertEquals(loadInformation.getUpdatePolicy(), updatePolicy);
		assertEquals(loadInformation.getProperties(), properties);
		assertNull(loadInformation.getBackupResource());
		assertEquals(loadInformation.getResourceLocations(), resourceLocations);
		assertFalse(loadInformation.isStartRemote());

	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldReturnsErrorWhenResourceIdWasNotInformed() throws URISyntaxException {
		UpdatePolicy updatePolicy = UpdatePolicy.LAZY;
	    Map<String, String> properties = new HashMap<>();
	    LoaderListener loaderListener = new LoaderListener() {

			@Override
			public void newDataLoaded(String resourceId, InputStream is) {
			}
		};
        URI[] resourceLocations = new URI[]{new URI("localhost")};

		new LoadDataInformationBuilder()
				.withUpdatePolicy(updatePolicy).withProperties(properties)
				.withLoaderListener(loaderListener)
				.withResourceLocations(resourceLocations).build();

	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldReturnsErrorWhenUpdatePolicyWasNotInformed() throws URISyntaxException {
		String resourceId = "resourceId";
	    Map<String, String> properties = new HashMap<>();
	    LoaderListener loaderListener = new LoaderListener() {

			@Override
			public void newDataLoaded(String resourceId, InputStream is) {
			}
		};
        URI[] resourceLocations = new URI[]{new URI("localhost")};

		new LoadDataInformationBuilder().withResourceId(resourceId)
				.withProperties(properties)
				.withLoaderListener(loaderListener)
				.withResourceLocations(resourceLocations).build();

	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldReturnsErrorWhenPropertiesWasNotInformed() throws URISyntaxException {
		String resourceId = "resourceId";
		UpdatePolicy updatePolicy = UpdatePolicy.LAZY;
	    LoaderListener loaderListener = new LoaderListener() {

			@Override
			public void newDataLoaded(String resourceId, InputStream is) {
			}
		};
        URI[] resourceLocations = new URI[]{new URI("localhost")};

		new LoadDataInformationBuilder().withResourceId(resourceId)
				.withUpdatePolicy(updatePolicy)
				.withLoaderListener(loaderListener)
				.withResourceLocations(resourceLocations).build();

	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void shouldReturnsErrorWhenResourceLocationsWasNotInformed() throws URISyntaxException {
		String resourceId = "resourceId";
		UpdatePolicy updatePolicy = UpdatePolicy.LAZY;
	    Map<String, String> properties = new HashMap<>();
	    LoaderListener loaderListener = new LoaderListener() {

			@Override
			public void newDataLoaded(String resourceId, InputStream is) {
			}
		};

		new LoadDataInformationBuilder().withResourceId(resourceId)
				.withUpdatePolicy(updatePolicy).withProperties(properties)
				.withLoaderListener(loaderListener)
				.build();
	}

}
