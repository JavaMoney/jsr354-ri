package org.javamoney.moneta.internal.convert.imf;

import static org.testng.Assert.assertNotNull;

import java.io.InputStream;
import java.time.Month;
import java.time.YearMonth;
import java.util.Map;

import org.testng.annotations.Test;

public class IMFRemoteSearchTest {

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorWhenYearMonthIsNull(){
		IMFRemoteSearch.INSTANCE.getResources(null);
	}

	@Test
	public void shouldReturnStream(){
		Map<IMFHistoricalType, InputStream> resources = IMFRemoteSearch.INSTANCE.getResources(YearMonth.of(2015, Month.MAY));
		assertNotNull(resources.get(IMFHistoricalType.Currency_SDR));
		assertNotNull(resources.get(IMFHistoricalType.SDR_Currency));
	}

}
