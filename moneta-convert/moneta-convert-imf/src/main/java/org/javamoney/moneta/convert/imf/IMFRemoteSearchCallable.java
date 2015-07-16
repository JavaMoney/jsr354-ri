package org.javamoney.moneta.internal.convert.imf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.YearMonth;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.javamoney.moneta.internal.convert.imf.IMFRemoteSearchCallable.IMFRemoteSearchResult;


class IMFRemoteSearchCallable implements Callable<IMFRemoteSearchResult>{

	private static final Logger LOG = Logger.getLogger(IMFRemoteSearch.class.getName());

	private final IMFHistoricalType type;

	private final YearMonth yearMonth;

	IMFRemoteSearchCallable(IMFHistoricalType type, YearMonth yearMonth) {
		this.type = Objects.requireNonNull(type);
		this.yearMonth = Objects.requireNonNull(yearMonth);
	}


	@Override
	public IMFRemoteSearchResult call() throws Exception {

		URLConnection connection = getConnection();
		if(Objects.isNull(connection)) {
			return null;
		}
        try (InputStream inputStream = connection.getInputStream(); ByteArrayOutputStream stream = new ByteArrayOutputStream();) {
            byte[] data = new byte[4096];
            int read = inputStream.read(data);
            while (read > 0) {
                stream.write(data, 0, read);
                read = inputStream.read(data);
            }
            return new IMFRemoteSearchResult(type, new ByteArrayInputStream(stream.toByteArray()));
        } catch (Exception e) {
            LOG.log(Level.INFO, "Failed to load resource from url " + type.getUrl(yearMonth), e);
        }
		return null;
	}


	private URLConnection getConnection() {
		try {
			return new URL(type.getUrl(yearMonth)).openConnection();
		} catch (Exception e) {
			LOG.log(Level.INFO, "Failed to load resource from url "
					+ type.getUrl(yearMonth), e);
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(IMFRemoteSearchCallable.class.getName()).append('{')
		.append(" type: ").append(type).append(", yearMonth: ").append(yearMonth).append('}');
		return sb.toString();
	}

	class IMFRemoteSearchResult {

		private final IMFHistoricalType type;

		private final InputStream stream;

		IMFRemoteSearchResult(IMFHistoricalType type, InputStream stream) {
			this.type = type;
			this.stream = stream;
		}

		public IMFHistoricalType getType() {
			return type;
		}

		public InputStream getStream() {
			return stream;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(IMFRemoteSearchResult.class.getName()).append('{')
			.append(" type: ").append(type).append(", stream: ").append(stream).append('}');
			return sb.toString();
		}
	}

}
