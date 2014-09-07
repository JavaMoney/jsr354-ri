package org.javamoney.moneta;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryCurrencies;
import javax.money.UnknownCurrencyException;

import org.javamoney.moneta.ToStringMonetaryAmountFormat.ToStringMonetaryAmountFormatStyle;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ToStringMonetaryAmountFormatTest {
	private static final CurrencyUnit BRAZILIAN_REAL = MonetaryCurrencies
			.getCurrency("BRL");

	private MonetaryAmount money;
	private MonetaryAmount fastMoney;
	private MonetaryAmount roundedMoney;



	@BeforeTest
	public void init() {
		money = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		fastMoney = FastMoney.of(BigDecimal.TEN, BRAZILIAN_REAL);
		roundedMoney = RoundedMoney.of(BigDecimal.TEN, BRAZILIAN_REAL);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldRunNPE() {
		ToStringMonetaryAmountFormat format = ToStringMonetaryAmountFormat
				.of(ToStringMonetaryAmountFormatStyle.FAST_MONEY);
		format.parse(null);
	}

	@Test(expectedExceptions = NumberFormatException.class)
	public void shouldRunNumberFormatException() {
		ToStringMonetaryAmountFormat format = ToStringMonetaryAmountFormat
				.of(ToStringMonetaryAmountFormatStyle.FAST_MONEY);
		format.parse("BRL 23AD");
	}

	@Test(expectedExceptions = UnknownCurrencyException.class)
	public void shouldRunUnknownCurrencyException() {
		ToStringMonetaryAmountFormat format = ToStringMonetaryAmountFormat
				.of(ToStringMonetaryAmountFormatStyle.FAST_MONEY);
		format.parse("AXD 23");
	}

	@Test
	public void parserMoneyTest() {
		executeTest(money, fastMoney, roundedMoney,
				ToStringMonetaryAmountFormatStyle.MONEY);
	}

	@Test
	public void parserFastMoneyTest() {
		executeTest(fastMoney, money, roundedMoney,
				ToStringMonetaryAmountFormatStyle.FAST_MONEY);
	}

	@Test
	public void parserRoundedMoneyTest() {
		executeTest(roundedMoney, fastMoney, money,
				ToStringMonetaryAmountFormatStyle.ROUNDED_MONEY);
	}

	private void executeTest(MonetaryAmount expectedMoney, MonetaryAmount a,
			MonetaryAmount b, ToStringMonetaryAmountFormatStyle type) {

		MonetaryAmount parserAResult = parser(a, type);
		MonetaryAmount parserBResult = parser(b, type);

		assertEquals(parserAResult, expectedMoney);
		assertEquals(parserBResult, expectedMoney);
		assertEquals(parserBResult, parserAResult);
	}

	private MonetaryAmount parser(MonetaryAmount a,
			ToStringMonetaryAmountFormatStyle style) {
		return ToStringMonetaryAmountFormat.of(style).parse(a.toString());
	}
}
