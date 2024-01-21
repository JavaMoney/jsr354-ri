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

import org.javamoney.moneta.spi.loader.ConnectionLoaderListener;

import java.util.Map;
import java.util.Timer;

class HttpConnectionLoaderServiceFacade {

	private final ScheduledHttpDataService scheduledDataLoaderService;

	private final HttpLoadDataService loadDataLoaderService;

	private final HttpLoadLocalDataService loadDataLocalLoaderService;

	private final HttpLoadRemoteDataService loadRemoteDataLoaderService;

	HttpConnectionLoaderServiceFacade(Timer timer, ConnectionLoaderListener listener, Map<String, LoadableHttpResource> resources){
		this.scheduledDataLoaderService = new ScheduledHttpDataService(timer, listener);
		this.loadDataLoaderService = new HttpLoadDataService(listener);
		this.loadDataLocalLoaderService = new HttpLoadLocalDataService(resources, listener);
		this.loadRemoteDataLoaderService = new HttpLoadRemoteDataService(listener);
	}

	public void scheduledData(LoadableHttpResource load) {
		scheduledDataLoaderService.execute(load);
	}

	public boolean loadData(String resourceId, Map<String, LoadableHttpResource> resources){
		return loadDataLoaderService.execute(resourceId, resources);
	}

	public boolean loadDataLocal(String resourceId){
		return loadDataLocalLoaderService.execute(resourceId);
	}

	public boolean loadDataRemote(String resourceId, Map<String, LoadableHttpResource> resources){
		return loadRemoteDataLoaderService.execute(resourceId, resources);
	}
	@Override
	public String toString() {
        return HttpConnectionLoaderServiceFacade.class.getName() + '{' +
                " scheduledDataLoaderService: " + scheduledDataLoaderService + ',' +
                " asyncLoaderService: " + loadDataLoaderService + ',' +
                " loadDataLocalLoaderService: " + loadDataLocalLoaderService + ',';
	}
}
