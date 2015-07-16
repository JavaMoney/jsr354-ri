package org.javamoney.moneta.function;

import static org.javamoney.moneta.function.StreamFactory.BRAZILIAN_REAL;
import static org.javamoney.moneta.function.StreamFactory.DOLLAR;
import static org.javamoney.moneta.function.StreamFactory.EURO;

import java.util.Map;

import javax.money.CurrencyUnit;

import org.javamoney.moneta.Money;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GroupMonetarySummaryStatisticsTest {

	@Test
	public void shouldCreateEmptyGroupSummary() {
		GroupMonetarySummaryStatistics group = new GroupMonetarySummaryStatistics();
		Map<CurrencyUnit, MonetarySummaryStatistics> map = group.get();
		Assert.assertTrue(map.isEmpty());
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldErrorWhenIsNull() {
		GroupMonetarySummaryStatistics group = new GroupMonetarySummaryStatistics();
		group.accept(null);
	}

	@Test
	public void acceptTest() {
		GroupMonetarySummaryStatistics group = new GroupMonetarySummaryStatistics();
		group.accept(Money.of(10, BRAZILIAN_REAL));
		group.accept(Money.of(20, BRAZILIAN_REAL));

		Map<CurrencyUnit, MonetarySummaryStatistics> map = group.get();
		Assert.assertEquals(map.keySet().size(), 1);
	}

	@Test
	public void shouldReturnsAnEmptySummaryWhenCurrencyThereIsNot() {
		GroupMonetarySummaryStatistics group = new GroupMonetarySummaryStatistics();
		MonetarySummaryStatistics brazilianSummary = group.get().get(
				BRAZILIAN_REAL);
		verifySummary(brazilianSummary, 0, 0, 0L);
	}

	@Test
	public void shouldGroupByCorrectly() {
		GroupMonetarySummaryStatistics group = createGroupMonetary();

		Map<CurrencyUnit, MonetarySummaryStatistics> map = group.get();
		Assert.assertEquals(map.keySet().size(), 3);
		Assert.assertNotNull(map.get(BRAZILIAN_REAL));
		Assert.assertNotNull(map.get(EURO));
		Assert.assertNotNull(map.get(DOLLAR));

		MonetarySummaryStatistics brazilianSummary = map
				.get(BRAZILIAN_REAL);
		verifySummary(brazilianSummary, 15, 30, 2L);

		MonetarySummaryStatistics euroSummary = map.get(EURO);
		verifySummary(euroSummary, 50, 100, 2L);

		MonetarySummaryStatistics dollarSummary = map.get(DOLLAR);
		verifySummary(dollarSummary, 60, 120, 2L);

	}

	@Test
	public void shouldCompineCorrectly() {
		GroupMonetarySummaryStatistics group = createGroupMonetary();
		GroupMonetarySummaryStatistics group2 = createGroupMonetary();
		group.combine(group2);
		Map<CurrencyUnit, MonetarySummaryStatistics> map = group.get();
		Assert.assertEquals(map.keySet().size(), 3);
		Assert.assertNotNull(map.get(BRAZILIAN_REAL));
		Assert.assertNotNull(map.get(EURO));
		Assert.assertNotNull(map.get(DOLLAR));

		MonetarySummaryStatistics brazilianSummary = map
				.get(BRAZILIAN_REAL);
		verifySummary(brazilianSummary, 15, 60, 4L);

		MonetarySummaryStatistics euroSummary = map.get(EURO);
		verifySummary(euroSummary, 50, 200, 4L);

		MonetarySummaryStatistics dollarSummary = map.get(DOLLAR);
		verifySummary(dollarSummary, 60, 240, 4L);
	}

	@Test
	public void shouldCompineDiferentMoneyCorrectly() {
		GroupMonetarySummaryStatistics group = new GroupMonetarySummaryStatistics();
		group.accept(Money.of(10, BRAZILIAN_REAL));
		group.accept(Money.of(20, BRAZILIAN_REAL));

		GroupMonetarySummaryStatistics group2 = new GroupMonetarySummaryStatistics();
		group2.accept(Money.of(50, EURO));
		group2.accept(Money.of(50, EURO));

		GroupMonetarySummaryStatistics group3 = new GroupMonetarySummaryStatistics();
		group3.accept(Money.of(100, DOLLAR));
		group3.accept(Money.of(20, DOLLAR));

		group.combine(group2).combine(group3);

		Map<CurrencyUnit, MonetarySummaryStatistics> map = group.get();
		Assert.assertEquals(map.keySet().size(), 3);
		Assert.assertNotNull(map.get(BRAZILIAN_REAL));
		Assert.assertNotNull(map.get(EURO));
		Assert.assertNotNull(map.get(DOLLAR));

		MonetarySummaryStatistics brazilianSummary = map
				.get(BRAZILIAN_REAL);
		verifySummary(brazilianSummary, 15, 30, 2L);

		MonetarySummaryStatistics euroSummary = map.get(EURO);
		verifySummary(euroSummary, 50, 100, 2L);

		MonetarySummaryStatistics dollarSummary = map.get(DOLLAR);
		verifySummary(dollarSummary, 60, 120, 2L);
	}

	private GroupMonetarySummaryStatistics createGroupMonetary() {
		GroupMonetarySummaryStatistics group = new GroupMonetarySummaryStatistics();
		group.accept(Money.of(10, BRAZILIAN_REAL));
		group.accept(Money.of(20, BRAZILIAN_REAL));

		group.accept(Money.of(50, EURO));
		group.accept(Money.of(50, EURO));

		group.accept(Money.of(100, DOLLAR));
		group.accept(Money.of(20, DOLLAR));
		return group;
	}

	private void verifySummary(MonetarySummaryStatistics summary,
			int expectedAvarage, int expectedSumm, long count) {
		Assert.assertEquals(summary.getCount(), count);
		Assert.assertEquals(summary.getAverage().getNumber()
				.intValue(), expectedAvarage);
		Assert.assertEquals(summary.getSum().getNumber().intValue(),
				expectedSumm);
	}

}

