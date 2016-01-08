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
package org.javamoney.moneta.internal.convert;

import java.io.InputStream;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.javamoney.moneta.internal.convert.IMFRemoteSearchCallable.IMFRemoteSearchResult;

public enum IMFRemoteSearch {
	INSTANCE;

	private static final Logger LOG = Logger.getLogger(IMFRemoteSearch.class.getName());

	private final ExecutorService executor = Executors.newCachedThreadPool();

	public Map<IMFHistoricalType, InputStream> getResources(YearMonth yearMonth) {
		Objects.requireNonNull(yearMonth);

		Map<IMFHistoricalType, InputStream> map = new EnumMap<>(IMFHistoricalType.class);
		try {
			List<Future<IMFRemoteSearchResult>> results = new ArrayList<>(2);
			for (IMFHistoricalType type : IMFHistoricalType.values()) {
				results.add(executor.submit(new IMFRemoteSearchCallable(type,
						yearMonth)));
			}

			for (Future<IMFRemoteSearchResult> result : results) {
				IMFRemoteSearchResult imfRemoteSearchResult = result.get();
				if (Objects.nonNull(imfRemoteSearchResult)) {
					map.put(imfRemoteSearchResult.getType(),
							imfRemoteSearchResult.getStream());
				}
			}
		} catch (Exception exception) {
			LOG.log(Level.INFO, "Failed to load resource input for find resource from date " + yearMonth, exception);
		}
		return map;
	}



}
