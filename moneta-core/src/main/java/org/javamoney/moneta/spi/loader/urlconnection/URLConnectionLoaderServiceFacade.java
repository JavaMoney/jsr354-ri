/*
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
package org.javamoney.moneta.spi.loader.urlconnection;

import java.util.Map;
import java.util.Timer;

class URLConnectionLoaderServiceFacade {

	private final ScheduledDataService scheduledDataLoaderService;

	private final LoadDataService loadDataLoaderService;

	private final LoadLocaDataService loadDataLocalLoaderService;

	private final LoadRemoteDataService loadRemoteDataLoaderService;

	URLConnectionLoaderServiceFacade(Timer timer, URLConnectionLoaderListener listener, Map<String, LoadableURLResource> resources){
		this.scheduledDataLoaderService = new ScheduledDataService(timer, listener);
		this.loadDataLoaderService = new LoadDataService(listener);
		this.loadDataLocalLoaderService = new LoadLocaDataService(resources, listener);
		this.loadRemoteDataLoaderService = new LoadRemoteDataService(listener);
	}

	public void scheduledData(LoadableURLResource load) {
		scheduledDataLoaderService.execute(load);
	}

	public boolean loadData(String resourceId, Map<String, LoadableURLResource> resources){
		return loadDataLoaderService.execute(resourceId, resources);
	}

	public boolean loadDataLocal(String resourceId){
		return loadDataLocalLoaderService.execute(resourceId);
	}

	public boolean loadDataRemote(String resourceId, Map<String, LoadableURLResource> resources){
		return loadRemoteDataLoaderService.execute(resourceId, resources);
	}
	@Override
	public String toString() {
        return URLConnectionLoaderServiceFacade.class.getName() + '{' +
                " scheduledDataLoaderService: " + scheduledDataLoaderService + ',' +
                " asyncLoaderService: " + loadDataLoaderService + ',' +
                " loadDataLocalLoaderService: " + loadDataLocalLoaderService + ',';
	}
}
