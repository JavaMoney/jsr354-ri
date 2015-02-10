package org.javamoney.moneta.convert.internal;

import static javax.money.convert.MonetaryConversions.getExchangeRateProvider;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryCurrencies;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;

import org.javamoney.moneta.ExchangeRateType;
import org.javamoney.moneta.Money;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class IdentityRateProviderTest {

	private static final CurrencyUnit EURO = MonetaryCurrencies
			.getCurrency("EUR");
	private static final CurrencyUnit DOLLAR = MonetaryCurrencies
			.getCurrency("USD");
	private static final CurrencyUnit BRAZILIAN_REAL = MonetaryCurrencies
			.getCurrency("BRL");

	private ExchangeRateProvider provider;

	@BeforeTest
	public void setup() throws InterruptedException {
		provider = getExchangeRateProvider(ExchangeRateType.IDENTITY);
	}

	@Test
	public void shouldReturnsIdentityRateProvider() {
		assertTrue(Objects.nonNull(provider));
		assertEquals(provider.getClass(), IdentityRateProvider.class);
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


}
