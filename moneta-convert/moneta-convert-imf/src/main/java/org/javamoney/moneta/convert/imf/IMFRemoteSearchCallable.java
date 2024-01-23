/*
  Copyright (c) 2012, 2021, Werner Keil and others by the @author tag.

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
package org.javamoney.moneta.convert.imf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.YearMonth;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.javamoney.moneta.convert.imf.IMFRemoteSearchCallable.IMFRemoteSearchResult;

class IMFRemoteSearchCallable implements Callable<IMFRemoteSearchResult>{

	private static final Logger LOG = Logger.getLogger(IMFRemoteSearchCallable.class.getName());

	private final IMFHistoricalType type;
	private final YearMonth yearMonth;
	private final String userAgent;

	IMFRemoteSearchCallable(IMFHistoricalType type, YearMonth yearMonth, String userAgent) {
		this.type = Objects.requireNonNull(type);
		this.yearMonth = Objects.requireNonNull(yearMonth);
		this.userAgent = Objects.requireNonNull(userAgent);
	}


	@Override
	public IMFRemoteSearchResult call() throws Exception {
		//connection.addRequestProperty("User-Agent", userAgent);
		// TODO apply userAgent where applicable
		final OkHttpClient client = new OkHttpClient.Builder()
				.build();

		final Request request = new Request.Builder()
				.url(getUrl())
				.build();

		final Call call = client.newCall(request);

        try (InputStream inputStream =  call.execute().body().byteStream();
			 ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            byte[] data = new byte[4096];
            int read = inputStream.read(data);
            while (read > 0) {
                stream.write(data, 0, read);
                read = inputStream.read(data);
            }
            return new IMFRemoteSearchResult(type, new ByteArrayInputStream(stream.toByteArray()));
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to load resource from url " + getUrl(), e);
        }
		return null;
	}


	private String getUrl() {
		return type.getUrl(yearMonth);
	}

	@Override
	public String toString() {
        return IMFRemoteSearchCallable.class.getName() + '{' +
                " type: " + type + ", yearMonth: " + yearMonth + '}';
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
            return IMFRemoteSearchResult.class.getName() + '{' +
                    " type: " + type + ", stream: " + stream + '}';
		}
	}
}
