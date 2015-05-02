package org.javamoney.moneta.spi;

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
				.withBackupResource(backupResource)
				.withResourceLocations(resourceLocations).build();

		assertEquals(loadInformation.getResourceId(), resourceId);
		assertEquals(loadInformation.getUpdatePolicy(), updatePolicy);
		assertEquals(loadInformation.getProperties(), properties);
		assertEquals(loadInformation.getBackupResource(), backupResource);
		assertEquals(loadInformation.getResourceLocations(), resourceLocations);


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
				.withBackupResource(backupResource)
				.withResourceLocations(resourceLocations).build();

		assertEquals(loadInformation.getResourceId(), resourceId);
		assertEquals(loadInformation.getUpdatePolicy(), updatePolicy);
		assertEquals(loadInformation.getProperties(), properties);
		assertEquals(loadInformation.getBackupResource(), backupResource);
		assertEquals(loadInformation.getResourceLocations(), resourceLocations);


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
