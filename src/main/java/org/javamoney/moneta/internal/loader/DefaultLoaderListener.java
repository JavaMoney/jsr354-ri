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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.javamoney.moneta.spi.LoaderService.LoaderListener;

class DefaultLoaderListener {

	private static final Logger LOG = Logger.getLogger(DefaultLoaderListener.class.getName());

	private final Map<String, List<LoaderListener>> listenersMap = new ConcurrentHashMap<>();

	 /**
     * Evaluate the {@link LoaderListener} instances, listening fo a dataId
     * given.
     *
     * @param dataId The dataId, not null
     * @return the according listeners
     */
    public List<LoaderListener> getListeners(String dataId) {
        if (Objects.isNull(dataId)) {
            dataId = "";
        }
        List<LoaderListener> listeners = this.listenersMap.get(dataId);
        if (Objects.isNull(listeners)) {
            synchronized (listenersMap) {
                listeners = this.listenersMap.get(dataId);
                if (Objects.isNull(listeners)) {
                    listeners = Collections.synchronizedList(new ArrayList<>());
                    this.listenersMap.put(dataId, listeners);
                }
            }
        }
        return listeners;
    }

    /**
     * Trigger the listeners registered for the given dataId.
     *
     * @param dataId the data id, not null.
     * @param is     the InputStream, containing the latest data.
     */
    public void trigger(String dataId, InputStream is) {
        List<LoaderListener> listeners = getListeners("");
        synchronized (listeners) {
            for (LoaderListener ll : listeners) {
                try {
                    ll.newDataLoaded(dataId, is);
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error calling LoadListener: " + ll, e);
                }
            }
        }
        if (!(Objects.isNull(dataId) || dataId.isEmpty())) {
            listeners = getListeners(dataId);
            synchronized (listeners) {
                for (LoaderListener ll : listeners) {
                    try {
                        ll.newDataLoaded(dataId, is);
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Error calling LoadListener: " + ll, e);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(DefaultLoaderListener.class.getName()).append('{')
    	.append("listenersMap: ").append(listenersMap).append('}');
    	return sb.toString();
    }
}
