/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
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
package org.javamoney.moneta.loader.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.spi.JavaMoneyConfig;

import org.javamoney.moneta.spi.LoaderService;
import org.javamoney.moneta.spi.LoaderService.UpdatePolicy;

class LoaderConfigurator {

	private static final String LOAD = "load.";

	private static final String TYPE = "type";

	private static final Logger LOG = Logger.getLogger(LoaderConfigurator.class
			.getName());

	private LoaderService loaderService;

	public LoaderConfigurator(LoaderService loaderService) {
		Objects.requireNonNull(loaderService);
		this.loaderService = loaderService;
	}

	public void load() {
		Map<String, String> config = JavaMoneyConfig.getConfig();
		// collect loads
		Set<String> loads = new HashSet<>();
		for (String key : config.keySet()) {
			if (key.startsWith(LOAD) && key.endsWith('.' + TYPE)) {
				String res = key.substring(LOAD.length());
				res = res.substring(0, res.length() - ('.' + TYPE).length());
				loads.add(res);
			}
		}
		// init loads
		for (String l : loads) {
			try {
				initResource(l, config);
			} catch (Exception e) {
				LOG.log(Level.SEVERE,
						"Failed to initialize/register resource: " + l, e);
			}
		}
	}

	private void initResource(String name, Map<String, String> allProps)
			throws MalformedURLException {
		Map<String, String> props = mapProperties(allProps, name);
		UpdatePolicy updatePolicy = UpdatePolicy.valueOf(props.get(TYPE));
		String fallbackRes = props.get("resource");
		if (fallbackRes == null) {
			throw new IllegalArgumentException(LOAD + name
					+ ".resource (classpath resource) required.");
		}
		String resourcesString = props.get("urls");
		String[] resources;
		if (resourcesString == null) {
			LOG.info("No update URLs configured for: " + name);
			resources = new String[0];
		} else {
			resources = resourcesString.split(",");
		}
		URL[] urls = createURLs(resources);
		this.loaderService.registerData(name, updatePolicy, props,
				getClassLoaderLocation(fallbackRes), urls);
	}

	private URL[] createURLs(String[] resources) throws MalformedURLException {
		List<URL> urls = new ArrayList<>(resources.length);
		for (String res : resources) {
			if (res.trim().isEmpty()) {
				continue;
			}
			urls.add(new URL(res.trim()));
		}
		return urls.toArray(new URL[0]);
	}

	private URL getClassLoaderLocation(String res) {
		URL url = null;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl != null) {
			url = cl.getResource(res);
		}
		if (url == null) {
			url = getClass().getResource(res);
		}
		if (url == null) {
			throw new IllegalArgumentException("Resource not found: " + res);
		}
		return url;
	}

	private Map<String, String> mapProperties(Map<String, String> allProps,
			String name) {
		Map<String, String> props = new HashMap<>();
		String start = LOAD + name;
		for (Map.Entry<String, String> entry : allProps.entrySet()) {
			if (entry.getKey().startsWith(start)) {
				props.put(entry.getKey().substring(start.length() + 1),
						entry.getValue());
			}
		}
		return props;
	}

}
