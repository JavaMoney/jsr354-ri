package org.javamoney.moneta.format;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatContext;
import javax.money.format.MonetaryParseException;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryAmountProducer;
import org.javamoney.moneta.function.MoneyProducer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DefaultMonetaryAmountFormatSymbolsTest {

	private MonetaryAmountSymbols symbols;

	private DefaultMonetaryAmountFormatSymbols monetaryAmountFormat;

	private Locale locale;

	private CurrencyUnit currency;

	private MonetaryAmountProducer producer;

	@BeforeMethod
	public void setup() {
		locale = new Locale("pt", "BR");
		symbols = new MonetaryAmountSymbols(locale);
		currency = Monetary.getCurrency(locale);
		producer = new MoneyProducer();
		monetaryAmountFormat = new DefaultMonetaryAmountFormatSymbols(symbols, producer);

	}

	@Test
	public void shouldReturnContext() {
		AmountFormatContext context = monetaryAmountFormat.getContext();
		Assert.assertEquals(DefaultMonetaryAmountFormatSymbols.STYLE, context.getFormatName());
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnsErrorWhenAppendableIsNull() throws IOException {
		monetaryAmountFormat.print(null, null);
	}

	@Test
	public void shouldPrintNullWhenMonetaryAmountIsNull() throws IOException {
		StringBuilder sb = new StringBuilder();
		monetaryAmountFormat.print(sb, null);
		assertEquals(sb.toString(), "null");
	}

	@Test
	public void shouldPrintMonetaryAmount() throws IOException {
		StringBuilder sb = new StringBuilder();
		MonetaryAmount money = Money.of(10, currency);
		monetaryAmountFormat.print(sb, money);
		assertEquals(sb.toString(), "R$ 10,00");
	}

	@Test
	public void shouldQueryFromNullWhenMonetaryAmountIsNull() throws IOException {
		assertEquals(monetaryAmountFormat.queryFrom(null), "null");
	}

	@Test
	public void shouldQueryFromMonetaryAmount() throws IOException {
		MonetaryAmount money = Money.of(10, currency);
		assertEquals(monetaryAmountFormat.queryFrom(money), "R$ 10,00");
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnErrorParseWhenMonetaryAmountIsNull() {
		monetaryAmountFormat.parse(null);
	}

	@Test(expectedExceptions = MonetaryParseException.class)
	public void shouldReturnErrorParseWhenMonetaryAmountIsInvalid() {
		monetaryAmountFormat.parse("ERROR");
	}

	@Test
	public void shouldParseMonetaryAmount() throws IOException {
		MonetaryAmount money = Money.of(10, currency);
		String parse = monetaryAmountFormat.queryFrom(money);
		assertEquals(monetaryAmountFormat.parse(parse), money);
	}
}

