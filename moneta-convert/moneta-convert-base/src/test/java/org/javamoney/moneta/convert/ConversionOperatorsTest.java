package org.javamoney.moneta.convert;

import static org.javamoney.moneta.convert.ConversionOperators.max;
import static org.javamoney.moneta.convert.ConversionOperators.min;
import static org.javamoney.moneta.function.MonetaryFunctions.sortCurrencyUnit;
import static org.javamoney.moneta.function.MonetaryFunctions.sortCurrencyUnitDesc;
import static org.javamoney.moneta.function.MonetaryFunctions.sortNumber;
import static org.javamoney.moneta.function.MonetaryFunctions.sortNumberDesc;
import static org.javamoney.moneta.convert.ConversionOperators.sum;
import static org.javamoney.moneta.convert.ConversionOperators.summarizingMonetary;
import static org.javamoney.moneta.convert.CurrencyConversionMock.BRAZILIAN_REAL;
import static org.javamoney.moneta.convert.CurrencyConversionMock.DOLLAR;
import static org.javamoney.moneta.convert.CurrencyConversionMock.EURO;
import static org.javamoney.moneta.convert.CurrencyConversionMock.currencies;
import static org.javamoney.moneta.convert.CurrencyConversionMock.currenciesToSummary;
import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;
import javax.money.convert.ExchangeRateProvider;

import junit.framework.Assert;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.convert.ExchangeRateProviderMock;
import org.javamoney.moneta.function.MonetarySummaryStatistics;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ConversionOperatorsTest {

	private ExchangeRateProvider provider;

	@BeforeTest
	public void init() {
		provider = new ExchangeRateProviderMock();
	}

	@Test
	public void sortCurrencyUnitTest() {
		MonetaryAmount money = currencies().sorted(sortCurrencyUnit())
				.findFirst().get();
		Assert.assertEquals(BRAZILIAN_REAL, money.getCurrency());
	}

	@Test
	public void sortCurrencyUnitDescTest() {
		MonetaryAmount money = currencies().sorted(sortCurrencyUnitDesc())
				.findFirst().get();
		Assert.assertEquals(DOLLAR, money.getCurrency());
	}

	@Test
	public void sortorderNumberTest() {
		MonetaryAmount money = currencies().sorted(sortNumber()).findFirst()
				.get();
		Assert.assertEquals(BigDecimal.ZERO,
				money.getNumber().numberValue(BigDecimal.class));
	}

	@Test
	public void sortorderNumberDescTest() {
		MonetaryAmount money = currencies().sorted(sortNumberDesc())
				.findFirst().get();
		Assert.assertEquals(BigDecimal.TEN,
				money.getNumber().numberValue(BigDecimal.class));
	}

	@Test
	public void sortCurrencyUnitAndNumberTest() {
		MonetaryAmount money = currencies()
				.sorted(sortCurrencyUnit().thenComparing(sortNumber()))
				.findFirst().get();

		Assert.assertEquals(BRAZILIAN_REAL, money.getCurrency());
		Assert.assertEquals(BigDecimal.ZERO,
				money.getNumber().numberValue(BigDecimal.class));
	}
	
	/**
	 * Do exchange of currency, in other words, create the monetary amount with the
	 * same value but with currency different.
	 * <p>
	 * For example, 'EUR 2.35', using the currency 'USD' as exchange parameter, will return 'USD 2.35',
	 * and 'BHD -1.345', using the currency 'USD' as exchange parameter, will return 'BHD -1.345'.
	 * <p>
	 *<pre>
	 *{@code
	 *Currency real = Monetary.getCurrency("BRL");
	 *MonetaryAmount money = Money.parse("EUR 2.355");
	 *MonetaryAmount result = MonetaryOperators.exchangeCurrency(real).apply(money);//BRL 2.355
	 *}
	 *</pre>
	 * @param roundingMode rounding to be used
	 * @return the major part as {@link MonetaryOperator}
	 */
	public static MonetaryOperator exchange(CurrencyUnit currencyUnit){
		return new ExchangeCurrencyOperator(Objects.requireNonNull(currencyUnit));
	}

	@Test
	public void shouldSumExchangeCorrectly() {
		Stream<MonetaryAmount> stream = currencies();
		MonetaryAmount sum = stream.reduce(sum(provider, DOLLAR)).get();
		Assert.assertTrue(sum.getNumber().intValue() > 20);
	}

	@Test
	public void shouldMinExchangeCorretly() {
		Stream<MonetaryAmount> stream = Stream.of(Money.of(7, EURO),
				Money.of(9, BRAZILIAN_REAL), Money.of(8, DOLLAR));
		MonetaryAmount min = stream.reduce(min(provider)).get();
		Assert.assertEquals(Money.of(9, BRAZILIAN_REAL), min);
	}

	@Test
	public void shouldMaxExchangeCorrectly() {
		Stream<MonetaryAmount> stream = Stream.of(Money.of(7, EURO),
				Money.of(9, BRAZILIAN_REAL), Money.of(8, DOLLAR));
		MonetaryAmount max = stream.reduce(max(provider)).get();
		Assert.assertEquals(Money.of(7, EURO), max);
	}

	@Test
	public void summarizingMonetaryExchangeTest() {
		MonetarySummaryStatistics summary = currenciesToSummary().collect(
				summarizingMonetary(BRAZILIAN_REAL, provider));

		assertEquals(10L, summary.getCount());
	}
	
	   @Test
	    public void shouldExecuteValiableOrder() {

	        Stream<MonetaryAmount> stream = Stream.of(Money.of(7, EURO),
	                Money.of(9, BRAZILIAN_REAL), Money.of(8, DOLLAR));
	        List<MonetaryAmount> list = stream.sorted(
	        		ConversionOperators.sortValuable(provider)).collect(
	                Collectors.toList());

	        Assert.assertEquals(Money.of(9, BRAZILIAN_REAL), list.get(0));
	        Assert.assertEquals(Money.of(8, DOLLAR), list.get(1));
	        Assert.assertEquals(Money.of(7, EURO), list.get(2));
	    }

	    @Test
	    public void shouldExecuteValiableOrderDesc() {

	        Stream<MonetaryAmount> stream = Stream.of(Money.of(7, EURO),
	                Money.of(9, BRAZILIAN_REAL), Money.of(8, DOLLAR));
	        List<MonetaryAmount> list = stream.sorted(
	        		ConversionOperators.sortValuableDesc(provider)).collect(
	                Collectors.toList());

	        Assert.assertEquals(Money.of(7, EURO), list.get(0));
	        Assert.assertEquals(Money.of(8, DOLLAR), list.get(1));
	        Assert.assertEquals(Money.of(9, BRAZILIAN_REAL), list.get(2));
	    }
	    
		//
		@Test(expectedExceptions = NullPointerException.class)
		public void shouldReturnErrorWhenExchangeCurrencyIsNull() {
			ConversionOperators.exchange(null);
		}
}
