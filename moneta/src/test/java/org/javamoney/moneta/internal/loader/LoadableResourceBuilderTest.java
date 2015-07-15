package org.javamoney.moneta.internal.loader;

import static org.testng.Assert.assertNotNull;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.javamoney.moneta.spi.LoadDataInformation;
import org.javamoney.moneta.spi.LoadDataInformationBuilder;
import org.javamoney.moneta.spi.LoaderService.LoaderListener;
import org.javamoney.moneta.spi.LoaderService.UpdatePolicy;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoadableResourceBuilderTest {

	private LoadDataInformation loadInformation;

	@BeforeMethod
	public void setup() throws URISyntaxException {
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

		loadInformation = new LoadDataInformationBuilder().withResourceId(resourceId)
				.withUpdatePolicy(updatePolicy).withProperties(properties)
				.withLoaderListener(loaderListener)
				.withBackupResource(backupResource)
				.withResourceLocations(resourceLocations).build();

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
