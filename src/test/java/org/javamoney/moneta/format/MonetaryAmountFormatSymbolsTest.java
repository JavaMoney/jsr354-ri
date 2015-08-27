package org.javamoney.moneta.format;

import static org.testng.Assert.assertEquals;

import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MoneyProducer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class MonetaryAmountFormatSymbolsTest {

	private MonetaryAmount money;

	private CurrencyUnit currency;

	private Number number = Long.valueOf(10_000_000L);

	@BeforeMethod
	public void setup() {
		currency = Monetary.getCurrency(Locale.getDefault(Locale.Category.FORMAT));
		money = Money.of(number, currency);
	}

	@Test
	public void shouldReturnDefaultMonetaryAmountFormatSymbol() {
		MonetaryAmountFormatSymbols defafult = MonetaryAmountFormatSymbols.getDefault();
		String format = defafult.format(money);
		MonetaryAmount amount = defafult.parse(format);
		assertEquals(amount.getCurrency(), currency);
		assertEquals(amount.getNumber().numberValue(Long.class), number);
	}

	@Test
	public void shouldReturnMonetaryAmountFromPattern() {
		MonetaryAmountFormatSymbols monetaryFormat = MonetaryAmountFormatSymbols.of("##,####,####" , new MonetaryAmountSymbols(), new MoneyProducer());
		money = Money.of(number, currency);
		String format = monetaryFormat.format(money);
		MonetaryAmount amount = monetaryFormat.parse(format);
		assertEquals(amount.getCurrency(), currency);
		assertEquals(amount.getNumber().numberValue(Long.class), number);

	}

	@Test
	public void shouldReturnMonetaryAmountDecimalSymbols() {
		CurrencyUnit dollar = Monetary.getCurrency(Locale.US);
		MonetaryAmountSymbols symbols = new MonetaryAmountSymbols();
		symbols.setCurrency(dollar);
		MonetaryAmountFormatSymbols monetaryFormat = MonetaryAmountFormatSymbols.of(symbols, new MoneyProducer());
		money = Money.of(number, dollar);
		String format = monetaryFormat.format(money);
		MonetaryAmount amount = monetaryFormat.parse(format);
		assertEquals(amount.getCurrency(), dollar);
		assertEquals(amount.getNumber().numberValue(Long.class), number);

	}


}
