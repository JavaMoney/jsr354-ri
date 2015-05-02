package org.javamoney.moneta.internal.loader;

import java.util.Map;
import java.util.Timer;

class DefaultLoaderServiceFacade {

	private ScheduledDataLoaderService scheduledDataLoaderService;

	private LoadDataRemoteLoaderService loadDataRemoteLoaderService;

	private LoadDataLocalLoaderService loadDataLocalLoaderService;

	DefaultLoaderServiceFacade(Timer timer, DefaultLoaderListener listener, Map<String, LoadableResource> resources){
		this.scheduledDataLoaderService = new ScheduledDataLoaderService(timer);
		this.loadDataRemoteLoaderService = new LoadDataRemoteLoaderService(listener);
		this.loadDataLocalLoaderService = new LoadDataLocalLoaderService(resources, listener);
	}

	public void scheduledData(LoadableResource load) {
		scheduledDataLoaderService.execute(load);
	}

	public boolean loadRetome(String resourceId, Map<String, LoadableResource> resources){
		return loadDataRemoteLoaderService.execute(resourceId, resources);
	}

	public boolean loadDataLocal(String resourceId){
		return loadDataLocalLoaderService.execute(resourceId);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(DefaultLoaderServiceFacade.class.getName()).append('{')
		.append(" scheduledDataLoaderService: ").append(scheduledDataLoaderService).append(',')
		.append(" asyncLoaderService: ").append(loadDataRemoteLoaderService).append(',')
		.append(" loadDataLocalLoaderService: ").append(loadDataLocalLoaderService).append(',');
		return sb.toString();
	}
}
