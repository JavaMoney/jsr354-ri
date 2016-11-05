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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.time.Month;
import java.time.YearMonth;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.javamoney.moneta.internal.convert.IMFRemoteSearchCallable.IMFRemoteSearchResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class IMFRemoteSearchCallableTest {


	private static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();

	@AfterClass
	public void end() {
		SERVICE.shutdown();
	}


	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenIMFHistoricalTypeIsNull() {
		new IMFRemoteSearchCallable(null, YearMonth.of(2015, Month.MAY));
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenYearMonthIsNull() {
		new IMFRemoteSearchCallable(IMFHistoricalType.Currency_SDR, null);
	}

	@Test
	public void shouldReturnIMFCurrencySDR() throws InterruptedException, ExecutionException {

		IMFHistoricalType type = IMFHistoricalType.Currency_SDR;
		IMFRemoteSearchCallable task = new IMFRemoteSearchCallable(type, YearMonth.of(2015, Month.MAY));
		Future<IMFRemoteSearchResult> resultFuture = SERVICE.submit(task);
		IMFRemoteSearchResult result = resultFuture.get();
		assertNotNull(result);
		assertEquals(result.getType(), type);
		assertNotNull(result.getStream());
	}

	@Test
	public void shouldReturnIMFSDRCurrency() throws InterruptedException, ExecutionException {
		IMFHistoricalType type = IMFHistoricalType.SDR_Currency;
		IMFRemoteSearchCallable task = new IMFRemoteSearchCallable(type, YearMonth.of(2015, Month.MAY));
		Future<IMFRemoteSearchResult> resultFuture = SERVICE.submit(task);
		IMFRemoteSearchResult result = resultFuture.get();
		assertNotNull(result);
		assertEquals(result.getType(), type);
		assertNotNull(result.getStream());
	}

}
