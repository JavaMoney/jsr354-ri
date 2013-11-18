/**
 * 
 */
package org.javamoney.moneta.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.MoneyCurrency;
import org.junit.Test;

/**
 * @author Anatole
 * 
 */
public class MonetaryAmountFormatTest {

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.format.MonetaryAmountFormat#getAmountStyle()}
	 * .
	 */
	@Test
	public void testGetAmountStyle() {

	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.format.MonetaryAmountFormat#getDefaultCurrency()}
	 * .
	 */
	@Test
	public void testGetDefaultCurrency() {
		MonetaryAmountFormat defaultFormat = new MonetaryAmountFormat.Builder(
				Locale.GERMANY).build();
		assertNull(defaultFormat.getDefaultCurrency());
		defaultFormat = new MonetaryAmountFormat.Builder(
				Locale.GERMANY).withDefaultCurrency(MoneyCurrency.of("CHF"))
				.build();
		assertEquals(MoneyCurrency.of("CHF"),
				defaultFormat.getDefaultCurrency());
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.format.MonetaryAmountFormat#format(javax.money.MonetaryAmount)}
	 * .
	 */
	@Test
	public void testFormat() {
		MonetaryAmountFormat defaultFormat = new MonetaryAmountFormat.Builder(
				Locale.GERMANY).build();
		assertEquals("CHF 12,50"
				, defaultFormat.format(Money.of("CHF", 12.50)));
		assertEquals("INR 123.456.789.101.112,12",
				defaultFormat.format(Money.of("INR",
						123456789101112.123456)));
		defaultFormat = new MonetaryAmountFormat.Builder(new Locale("", "IN"))
				.build();
		assertEquals("CHF 1,211,112.50",
				defaultFormat.format(Money.of("CHF", 1211112.50)));
		assertEquals("INR 123,456,789,101,112.12",
				defaultFormat.format(Money.of("INR",
						123456789101112.123456)));
		Locale india = new Locale("", "IN");
		defaultFormat = new MonetaryAmountFormat.Builder(india)
				.withNumberGroupSizes(3, 2).build();
		assertEquals("INR 12,34,56,78,91,01,112.12",
				defaultFormat.format(Money.of("INR",
						123456789101112.123456)));
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.format.MonetaryAmountFormat#print(java.lang.Appendable, javax.money.MonetaryAmount)}
	 * .
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPrint() throws IOException {
		StringBuilder b = new StringBuilder();
		MonetaryAmountFormat defaultFormat = new MonetaryAmountFormat.Builder(
				Locale.GERMANY).build();
		defaultFormat.print(b, Money.of("CHF", 12.50));
		assertEquals("CHF 12,50"
				, b.toString());
		b.setLength(0);
		defaultFormat.print(b, Money.of("INR",
				123456789101112.123456));
		assertEquals("INR 123.456.789.101.112,12",
				b.toString());
		b.setLength(0);
		defaultFormat = new MonetaryAmountFormat.Builder(new Locale("", "IN"))
				.build();
		defaultFormat.print(b, Money.of("CHF", 1211112.50));
		assertEquals("CHF 1,211,112.50",
				b.toString());
		b.setLength(0);
		defaultFormat.print(b, Money.of("INR",
				123456789101112.123456));
		assertEquals("INR 123,456,789,101,112.12",
				b.toString());
		b.setLength(0);
		Locale india = new Locale("", "IN");
		defaultFormat = new MonetaryAmountFormat.Builder(india)
				.withNumberGroupSizes(3, 2).build();
		defaultFormat.print(b, Money.of("INR",
				123456789101112.123456));
		assertEquals("INR 12,34,56,78,91,01,112.12",
				b.toString());
	}

	/**
	 * Test method for
	 * {@link org.javamoney.moneta.format.MonetaryAmountFormat#parse(java.lang.CharSequence)}
	 * .
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testParse() throws ParseException {
		MonetaryAmountFormat defaultFormat = new MonetaryAmountFormat.Builder(
				Locale.GERMANY).build();
		assertEquals(Money.of("EUR", new BigDecimal("12.50")),
				defaultFormat.parse("EUR 12,50"));
		assertEquals(Money.of("EUR", new BigDecimal("12.50")),
				defaultFormat.parse("  \t EUR 12,50"));
		assertEquals(Money.of("EUR", new BigDecimal("12.50")),
				defaultFormat.parse("  \t EUR  \t\n\r 12,50"));
		assertEquals(Money.of("CHF", new BigDecimal("12.50")),
				defaultFormat.parse("CHF 12,50"));
		defaultFormat = new MonetaryAmountFormat.Builder(new Locale("", "IN"))
				.withNumberGroupSizes(3, 2).build();
		assertEquals(Money.of("INR", new BigDecimal("123456789101112.12")),
				defaultFormat.parse("INR 12,34,56,78,91,01,112.12"));
	}
}
