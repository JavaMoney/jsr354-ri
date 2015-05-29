package org.javamoney.moneta.internal.convert;

import static javax.money.convert.MonetaryConversions.getExchangeRateProvider;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryException;
import javax.money.convert.ConversionQuery;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.convert.ExchangeRateType;
import org.javamoney.moneta.convert.HistoricConversionQueryBuilder;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class IMFHistoricRateProviderTest {
	  private static final CurrencyUnit EURO = Monetary
	            .getCurrency("EUR");
	    private static final CurrencyUnit DOLLAR = Monetary
	            .getCurrency("USD");
	    private static final CurrencyUnit BRAZILIAN_REAL = Monetary
	            .getCurrency("BRL");

	    private ExchangeRateProvider provider;

	    @BeforeTest
	    public void setup() {
	        provider = getExchangeRateProvider(ExchangeRateType.IMF_HIST);
	    }

	    @Test
	    public void shouldReturnsIMFRateProvider() {
	        assertTrue(Objects.nonNull(provider));
	        assertEquals(provider.getClass(), IMFHistoricRateProvider.class);
	    }

	    @Test
	    public void shouldReturnsSameDollarValue() {
	        CurrencyConversion currencyConversion = provider.getCurrencyConversion(DOLLAR);
	        assertNotNull(currencyConversion);
	        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
	        MonetaryAmount result = currencyConversion.apply(money);

	        assertEquals(result.getCurrency(), DOLLAR);
	        assertEquals(result.getNumber().numberValue(BigDecimal.class),
	                BigDecimal.TEN);

	    }

	    @Test
	    public void shouldReturnsSameBrazilianValue() {
	        CurrencyConversion currencyConversion = provider
	                .getCurrencyConversion(BRAZILIAN_REAL);
	        assertNotNull(currencyConversion);
	        MonetaryAmount money = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
	        MonetaryAmount result = currencyConversion.apply(money);

	        assertEquals(result.getCurrency(), BRAZILIAN_REAL);
	        assertEquals(result.getNumber().numberValue(BigDecimal.class),
	                BigDecimal.TEN);

	    }

	    @Test
	    public void shouldReturnsSameEuroValue() {
	        CurrencyConversion currencyConversion = provider
	                .getCurrencyConversion(EURO);
	        assertNotNull(currencyConversion);
	        MonetaryAmount money = Money.of(BigDecimal.TEN, EURO);
	        MonetaryAmount result = currencyConversion.apply(money);

	        assertEquals(result.getCurrency(), EURO);
	        assertEquals(result.getNumber().numberValue(BigDecimal.class),
	                BigDecimal.TEN);

	    }

	    @Test
	    public void shouldConvertsDollarToEuro() {
	        CurrencyConversion currencyConversion = provider
	                .getCurrencyConversion(EURO);
	        assertNotNull(currencyConversion);
	        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
	        MonetaryAmount result = currencyConversion.apply(money);

	        assertEquals(result.getCurrency(), EURO);
	        assertTrue(result.getNumber().doubleValue() > 0);

	    }

	    @Test
	    public void shouldConvertsEuroToDollar() {
	        CurrencyConversion currencyConversion = provider
	                .getCurrencyConversion(DOLLAR);
	        assertNotNull(currencyConversion);
	        MonetaryAmount money = Money.of(BigDecimal.TEN, EURO);
	        MonetaryAmount result = currencyConversion.apply(money);

	        assertEquals(result.getCurrency(), DOLLAR);
	        assertTrue(result.getNumber().doubleValue() > 0);

	    }

	    @Test
	    public void shouldConvertsBrazilianToDollar() {
	        CurrencyConversion currencyConversion = provider
	                .getCurrencyConversion(DOLLAR);
	        assertNotNull(currencyConversion);
	        MonetaryAmount money = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
	        MonetaryAmount result = currencyConversion.apply(money);

	        assertEquals(result.getCurrency(), DOLLAR);
	        assertTrue(result.getNumber().doubleValue() > 0);

	    }

	    @Test
	    public void shouldConvertsDollarToBrazilian() {
	        CurrencyConversion currencyConversion = provider
	                .getCurrencyConversion(BRAZILIAN_REAL);
	        assertNotNull(currencyConversion);
	        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
	        MonetaryAmount result = currencyConversion.apply(money);

	        assertEquals(result.getCurrency(), BRAZILIAN_REAL);
	        assertTrue(result.getNumber().doubleValue() > 0);

	    }

	    @Test(expectedExceptions = MonetaryException.class)
	    public void shouldReturnErrorWhenDoesNotFindTheExchangeRate() {


	        LocalDate localDate = YearMonth.of(2011, Month.JANUARY).atDay(9);
			ConversionQuery conversionQuery = HistoricConversionQueryBuilder
					.of(EURO).withDay(localDate).build();
	        CurrencyConversion currencyConversion = provider
	                .getCurrencyConversion(conversionQuery);
	        assertNotNull(currencyConversion);
	        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
	        currencyConversion.apply(money);
	        fail();
	    }

	    @Test
	    public void shouldFindFromHistoricalUsingPeriod() {


	        LocalDate localDate = YearMonth.of(2011, Month.JANUARY).atDay(9);
			ConversionQuery conversionQuery = HistoricConversionQueryBuilder
					.of(EURO).withDaysBetween(localDate, localDate.plusDays(10)).build();
	        CurrencyConversion currencyConversion = provider
	                .getCurrencyConversion(conversionQuery);
	        assertNotNull(currencyConversion);
	        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
	        MonetaryAmount result = currencyConversion.apply(money);

	        assertEquals(result.getCurrency(), EURO);
	        assertTrue(result.getNumber().doubleValue() > 0);
	    }

	    @Test(expectedExceptions = MonetaryException.class)
	    public void shouldReturnErrorWhenFindFromHistoricalUsingPeriod() {


	        LocalDate localDate = YearMonth.of(2011, Month.JANUARY).atDay(9);
			ConversionQuery conversionQuery = HistoricConversionQueryBuilder
					.of(EURO).withDaysBetween(localDate.minusDays(1), localDate).build();
	        CurrencyConversion currencyConversion = provider
	                .getCurrencyConversion(conversionQuery);
	        assertNotNull(currencyConversion);
	        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
	        currencyConversion.apply(money);
	        fail();
	    }

	    @Test
	    public void shouldSetTimeInLocalDateTime() {

	        LocalDate localDate = YearMonth.of(2014, Month.JANUARY).atDay(9);
	        ConversionQuery conversionQuery = HistoricConversionQueryBuilder
					.of(EURO).withDay(localDate).build();
	        CurrencyConversion currencyConversion = provider
	                .getCurrencyConversion(conversionQuery);
	        assertNotNull(currencyConversion);
	        MonetaryAmount money = Money.of(BigDecimal.TEN, DOLLAR);
	        MonetaryAmount result = currencyConversion.apply(money);

	        assertEquals(result.getCurrency(), EURO);
	        assertTrue(result.getNumber().doubleValue() > 0);

	    }
}