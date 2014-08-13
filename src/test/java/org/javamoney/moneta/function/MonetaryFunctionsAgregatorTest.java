package org.javamoney.moneta.function;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryException;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Class to test the class MonetaryFunctions, but
 * just the agregator methods
 * @author otaviojava
 */
public class MonetaryFunctionsAgregatorTest {


	@Test
	public void shouldSumCorretly() {
		Stream<MonetaryAmount> stream = StreamFactory.streamNormal();
		MonetaryAmount sum = stream.reduce(MonetaryFunctions.sum()).get();
		Assert.assertTrue(sum.getNumber().intValue() == 20);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldsumWithNPEWhenAnElementIsNull() {
		Stream<MonetaryAmount> stream = StreamFactory.streamNull();
		stream.reduce(MonetaryFunctions.sum()).get();
	}

	@Test(expectedExceptions = MonetaryException.class)
	public void shouldSumMoneratyExceptionWhenHasDifferenctsCurrencies() {
		Stream<MonetaryAmount> stream = StreamFactory.streamCurrencyDifferent();
		stream.reduce(MonetaryFunctions.sum()).get();
	}

	@Test
	public void shouldMinCorretly() {
		Stream<MonetaryAmount> stream = StreamFactory.streamNormal();
		MonetaryAmount min = stream.reduce(MonetaryFunctions.min()).get();
		Assert.assertTrue(min.getNumber().intValue() == 0);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldMinWithNPEWhenAnElementIsNull() {
		Stream<MonetaryAmount> stream = StreamFactory.streamNull();
		stream.reduce(MonetaryFunctions.min()).get();
	}

	@Test(expectedExceptions = MonetaryException.class)
	public void shouldMinMoneratyExceptionWhenHasDifferenctsCurrencies() {
		Stream<MonetaryAmount> stream = StreamFactory.streamCurrencyDifferent();
		stream.reduce(MonetaryFunctions.min()).get();
	}

	@Test
	public void shouldMaxCorretly() {
		Stream<MonetaryAmount> stream = StreamFactory.streamNormal();
		MonetaryAmount max = stream.reduce(MonetaryFunctions.max()).get();
		Assert.assertTrue(max.getNumber().intValue() == 10);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldMaxWithNPEWhenAnElementIsNull() {
		Stream<MonetaryAmount> stream = StreamFactory.streamNull();
		stream.reduce(MonetaryFunctions.max()).get();
	}

	@Test(expectedExceptions = MonetaryException.class)
	public void shouldMaxMoneratyExceptionWhenHasDifferenctsCurrencies() {
		Stream<MonetaryAmount> stream = StreamFactory.streamCurrencyDifferent();
		stream.reduce(MonetaryFunctions.max()).get();
	}

	@Test
	public void groupByCurrencyUnitTest() {
		 Map<CurrencyUnit, List<MonetaryAmount>> groupBy = StreamFactory.currencies().collect(MonetaryFunctions.groupByCurrencyUnit());
		 Assert.assertEquals(3, groupBy.entrySet().size());
		 Assert.assertEquals(3, groupBy.get(StreamFactory.BRAZILIAN_REAL).size());
		 Assert.assertEquals(3, groupBy.get(StreamFactory.DOLLAR).size());
		 Assert.assertEquals(3, groupBy.get(StreamFactory.EURO).size());
	}

	@Test
	public void summarizingMonetary() {

		MonetarySummaryStatistics summary = StreamFactory
				.currenciesToSummary()
				.filter(MonetaryFunctions
						.isCurrency(StreamFactory.BRAZILIAN_REAL))
                .collect(MonetaryFunctions.summarizingMonetary(StreamFactory.BRAZILIAN_REAL));
        Assert.assertEquals(8L, summary.getCount());
		Assert.assertEquals(0L, summary.getMin().getNumber().longValue());
		Assert.assertEquals(10L, summary.getMax().getNumber().longValue());
		Assert.assertEquals(16L, summary.getSum().getNumber().longValue());
		Assert.assertEquals(2L, summary.getAvarage().getNumber().longValue());

	}

}
