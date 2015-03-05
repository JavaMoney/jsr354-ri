/**
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta.format;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

import javax.money.MonetaryAmounts;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.javamoney.moneta.Money;
import org.testng.annotations.Test;

/**
 * @author Anatole
 */
public class MonetaryAmountFormatTest {


    /**
     * Test method for
     * {@link javax.money.format.MonetaryAmountFormat#format(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testFormat() {
        MonetaryAmountFormat defaultFormat = MonetaryFormats.getAmountFormat(Locale.GERMANY);
        assertEquals("12,50 CHF", defaultFormat
                .format(MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF").setNumber(12.50)
                        .create()));
        assertEquals("123.456.789.101.112,12 INR", defaultFormat
                .format(MonetaryAmounts.getDefaultAmountFactory().setCurrency("INR")
                        .setNumber(123456789101112.123456).create()));
        defaultFormat = MonetaryFormats.getAmountFormat(new Locale("", "IN"));
        assertEquals("CHF 1,211,112.50", defaultFormat
                .format(MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF").setNumber(1211112.50)
                        .create()));
        assertEquals("INR 123,456,789,101,112.12", defaultFormat
                .format(MonetaryAmounts.getDefaultAmountFactory().setCurrency("INR")
                        .setNumber(123456789101112.123456).create()));
        // Locale india = new Locale("", "IN");
        // defaultFormat = MonetaryFormats.getAmountFormatBuilder(india)
        // .setNumberGroupSizes(3, 2).of();
        // assertEquals("INR 12,34,56,78,91,01,112.12",
        // defaultFormat.format(MonetaryAmounts.getAmount("INR",
        // 123456789101112.123456)));
    }

    /**
     * Test method for
     * {@link javax.money.format.MonetaryAmountFormat#format(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testFormatWithBuilder() {
        MonetaryAmountFormat defaultFormat =
                MonetaryFormats.getAmountFormat(AmountFormatQueryBuilder.of(Locale.JAPANESE).build());
        assertEquals("CHF 12.50", defaultFormat
                .format(MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF").setNumber(12.50)
                        .create()));
    }

    /**
     * Test method for
     * {@link javax.money.format.MonetaryAmountFormat#format(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testFormatWithBuilder2() {
        MonetaryAmountFormat format = MonetaryFormats
                .getAmountFormat(AmountFormatQueryBuilder.of(Locale.GERMANY).set(CurrencyStyle.NUMERIC_CODE).build());
        assertEquals("12,50 756", format.format(
                MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF").setNumber(12.50).create()));
        format = MonetaryFormats
                .getAmountFormat(AmountFormatQueryBuilder.of(Locale.US).set(CurrencyStyle.SYMBOL).build());
        assertEquals("$123,456.56", format.format(
                MonetaryAmounts.getDefaultAmountFactory().setCurrency("USD").setNumber(123456.561)
                        .create()));
    }

    /**
     * Test method for
     * {@link javax.money.format.MonetaryAmountFormat#print(java.lang.Appendable, javax.money.MonetaryAmount)}
     * .
     *
     * @throws IOException
     */
    @Test
    public void testPrint() throws IOException {
        StringBuilder b = new StringBuilder();
        MonetaryAmountFormat defaultFormat = MonetaryFormats.getAmountFormat(Locale.GERMANY);
        defaultFormat.print(b, MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF").setNumber(12.50).create());
        assertEquals("12,50 CHF", b.toString());
        b.setLength(0);
        defaultFormat.print(b, MonetaryAmounts.getDefaultAmountFactory().setCurrency("INR")
                .setNumber(123456789101112.123456).create());
        assertEquals("123.456.789.101.112,12 INR", b.toString());
        b.setLength(0);
        defaultFormat = MonetaryFormats.getAmountFormat(new Locale("", "IN"));
        defaultFormat
                .print(b, MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF").setNumber(1211112.50).create());
        assertEquals("CHF 1,211,112.50", b.toString());
        b.setLength(0);
        defaultFormat.print(b, MonetaryAmounts.getDefaultAmountFactory().setCurrency("INR")
                .setNumber(123456789101112.123456).create());
        assertEquals("INR 123,456,789,101,112.12", b.toString());
        b.setLength(0);
        // Locale india = new Locale("", "IN");
        // defaultFormat = MonetaryFormats.getAmountFormat(india)
        // .setNumberGroupSizes(3, 2).of();
        // defaultFormat.print(b, MonetaryAmounts.getAmount("INR",
        // 123456789101112.123456));
        // assertEquals("INR 12,34,56,78,91,01,112.12",
        // b.toString());
    }

    /**
     * Test method for {@link javax.money.format.MonetaryAmountFormat#parse(java.lang.CharSequence)}
     * .
     *
     * @throws ParseException
     */
    @Test
    public void testParse() throws ParseException {
        MonetaryAmountFormat defaultFormat = MonetaryFormats.getAmountFormat(Locale.GERMANY);
        assertEquals(MonetaryAmounts.getDefaultAmountFactory().setCurrency("EUR").setNumber(new BigDecimal("12.50"))
                .create(), defaultFormat.parse("12,50 EUR"));
        assertEquals(MonetaryAmounts.getDefaultAmountFactory().setCurrency("EUR").setNumber(new BigDecimal("12.50"))
                .create(), defaultFormat.parse("  \t 12,50 EUR"));
        assertEquals(MonetaryAmounts.getDefaultAmountFactory().setCurrency("EUR").setNumber(new BigDecimal("12.50"))
                .create(), defaultFormat.parse("  \t 12,50 \t\n EUR  \t\n\n "));
        assertEquals(MonetaryAmounts.getDefaultAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("12.50"))
                .create(), defaultFormat.parse("12,50 CHF"));
    }


    /**
     * Test related to {@link https://java.net/jira/browse/JAVAMONEY-92}.
     */
    @Test
    public void testWithCustomPattern() {
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(
                AmountFormatQueryBuilder.of(Locale.GERMANY)
                        .set(CurrencyStyle.SYMBOL)
                        .set("pattern", "#,##0.00### ¤")
                        .build());
        Money money = Money.of(12345.23456789, "EUR");
        assertEquals("12.345,23457 €", format.format(money));

        format = MonetaryFormats.getAmountFormat(
                AmountFormatQueryBuilder.of(Locale.GERMANY)
                        .set(CurrencyStyle.SYMBOL)
                        .build());
        assertEquals("12.345,23 €", format.format(money));


    }
}
