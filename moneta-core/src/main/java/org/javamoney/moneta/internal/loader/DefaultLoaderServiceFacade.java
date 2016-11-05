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
package org.javamoney.moneta.internal.loader;

import java.util.Map;
import java.util.Timer;

class DefaultLoaderServiceFacade {

	private final ScheduledDataLoaderService scheduledDataLoaderService;

	private final LoadDataLoaderService loadDataLoaderService;

	private final LoadDataLocalLoaderService loadDataLocalLoaderService;

	private final LoadRemoteDataLoaderService loadRemoteDataLoaderService;

	DefaultLoaderServiceFacade(Timer timer, DefaultLoaderListener listener, Map<String, LoadableResource> resources){
		this.scheduledDataLoaderService = new ScheduledDataLoaderService(timer);
		this.loadDataLoaderService = new LoadDataLoaderService(listener);
		this.loadDataLocalLoaderService = new LoadDataLocalLoaderService(resources, listener);
		this.loadRemoteDataLoaderService = new LoadRemoteDataLoaderService(listener);
	}

	public void scheduledData(LoadableResource load) {
		scheduledDataLoaderService.execute(load);
	}

	public boolean loadData(String resourceId, Map<String, LoadableResource> resources){
		return loadDataLoaderService.execute(resourceId, resources);
	}

	public boolean loadDataLocal(String resourceId){
		return loadDataLocalLoaderService.execute(resourceId);
	}

	public boolean loadDataRemote(String resourceId, Map<String, LoadableResource> resources){
		return loadRemoteDataLoaderService.execute(resourceId, resources);
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(DefaultLoaderServiceFacade.class.getName()).append('{')
		.append(" scheduledDataLoaderService: ").append(scheduledDataLoaderService).append(',')
		.append(" asyncLoaderService: ").append(loadDataLoaderService).append(',')
		.append(" loadDataLocalLoaderService: ").append(loadDataLocalLoaderService).append(',');
		return sb.toString();
	}
}
