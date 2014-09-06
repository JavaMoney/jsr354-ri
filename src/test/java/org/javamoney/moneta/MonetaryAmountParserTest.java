package org.javamoney.moneta;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryCurrencies;
import javax.money.UnknownCurrencyException;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class MonetaryAmountParserTest {
	private static final CurrencyUnit BRAZILIAN_REAL = MonetaryCurrencies
			.getCurrency("BRL");

	private MonetaryAmount money;
	private MonetaryAmount fastMoney;
	private MonetaryAmount roundedMoney;

	private enum ParserType {
		MONEY, FAST_MONEY, ROUNDED_MONEY;
	}

	@BeforeTest
	public void init() {
		money = Money.of(BigDecimal.TEN, BRAZILIAN_REAL);
		fastMoney = FastMoney.of(BigDecimal.TEN, BRAZILIAN_REAL);
		roundedMoney = RoundedMoney.of(BigDecimal.TEN, BRAZILIAN_REAL);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldRunNPE() {
		MonetaryAmountParser.parserMoney(null);
	}

	@Test(expectedExceptions = NumberFormatException.class)
	public void shouldRunNumberFormatException() {
		MonetaryAmountParser.parserMoney("BRL 23AD");
	}

	@Test(expectedExceptions = UnknownCurrencyException.class)
	public void shouldRunUnknownCurrencyException() {
		MonetaryAmountParser.parserMoney("AXD 23");
	}

	@Test
	public void parserMoneyTest() {
		executeTest(money, fastMoney, roundedMoney, ParserType.MONEY);
	}

	@Test
	public void parserFastMoneyTest() {
		executeTest(fastMoney, money, roundedMoney, ParserType.FAST_MONEY);
	}

	@Test
	public void parserRoundedMoneyTest() {
		executeTest(roundedMoney, fastMoney, money, ParserType.ROUNDED_MONEY);
	}

	private void executeTest(MonetaryAmount expectedMoney, MonetaryAmount a,
			MonetaryAmount b, ParserType type) {

		MonetaryAmount parserAResult = parser(a, type);
		MonetaryAmount parserBResult = parser(b, type);

		assertEquals(parserAResult, expectedMoney);
		assertEquals(parserBResult, expectedMoney);
		assertEquals(parserBResult, parserAResult);
	}

	private MonetaryAmount parser(MonetaryAmount a, ParserType type) {
		switch (type) {
		case MONEY:
			return MonetaryAmountParser.parserMoney(a.toString());

		case FAST_MONEY:
			return MonetaryAmountParser.parserFastMoney(a.toString());
		case ROUNDED_MONEY:
			return MonetaryAmountParser.parserRoundedMoney(a.toString());
		}
		throw new IllegalArgumentException();
	}
}
