package org.javamoney.moneta.internal.convert.imf;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.time.Month;
import java.time.YearMonth;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.javamoney.moneta.internal.convert.imf.IMFRemoteSearchCallable.IMFRemoteSearchResult;
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
