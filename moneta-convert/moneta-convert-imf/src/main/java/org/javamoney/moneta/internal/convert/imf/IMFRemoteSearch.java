package org.javamoney.moneta.internal.convert.imf;

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

import org.javamoney.moneta.internal.convert.imf.IMFRemoteSearchCallable.IMFRemoteSearchResult;

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
