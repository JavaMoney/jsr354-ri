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
package org.javamoney.moneta.internal.format;

import static java.util.Locale.*;
import static org.javamoney.moneta.format.CurrencyStyle.*;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQuery;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.CurrencyStyle;
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
        MonetaryAmountFormat defaultFormat = MonetaryFormats.getAmountFormat(GERMANY);
        MonetaryAmount amountChf1 = Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(12.50).create();
        assertEquals("12,50 CHF", defaultFormat.format(amountChf1));
        MonetaryAmount amountInr = Monetary.getDefaultAmountFactory().setCurrency("INR").setNumber(123456789101112.123456).create();
        assertEquals("123.456.789.101.112,12 INR", defaultFormat.format(amountInr));
        defaultFormat = MonetaryFormats.getAmountFormat(new Locale("", "IN"));
        MonetaryAmount amountChf = Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(1211112.50).create();
        assertEquals("CHF 1,211,112.50", defaultFormat.format(amountChf));
        assertEquals("INR 123,456,789,101,112.12", defaultFormat.format(amountInr));
        // Locale india = new Locale("", "IN");
        // defaultFormat = MonetaryFormats.getAmountFormatBuilder(india)
        // .setNumberGroupSizes(3, 2).of();
        // assertEquals("INR 12,34,56,78,91,01,112.12",
        // defaultFormat.format(Monetary.getAmount("INR",
        // 123456789101112.123456)));
    }

    /**
     * Test method for
     * {@link javax.money.format.MonetaryAmountFormat#format(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testFormatWithBuilder() {
        AmountFormatQuery formatQuery = AmountFormatQueryBuilder.of(JAPANESE).build();
        MonetaryAmountFormat defaultFormat = MonetaryFormats.getAmountFormat(formatQuery);
        MonetaryAmount amountChf = Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(12.50).create();
        assertEquals("CHF12.50", defaultFormat.format(amountChf));
    }

    /**
     * Test method for
     * {@link javax.money.format.MonetaryAmountFormat#format(javax.money.MonetaryAmount)} .
     */
    @Test
    public void testFormatWithBuilder2() {
        AmountFormatQuery formatQuery = AmountFormatQueryBuilder.of(GERMANY).set(NUMERIC_CODE).build();
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(formatQuery);
        MonetaryAmount amountChf = Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(12.50).create();
        assertEquals("12,50 756", format.format(amountChf));
        format = MonetaryFormats.getAmountFormat(AmountFormatQueryBuilder.of(US).set(SYMBOL).build());
        MonetaryAmount amountUsd = Monetary.getDefaultAmountFactory().setCurrency("USD").setNumber(123456.561).create();
        assertEquals("$123,456.56", format.format(amountUsd));
    }

    /**
     * Test method for
     * {@link javax.money.format.MonetaryAmountFormat#print(java.lang.Appendable, javax.money.MonetaryAmount)}
     */
    @Test
    public void testPrint() throws IOException {
        StringBuilder b = new StringBuilder();
        MonetaryAmountFormat defaultFormat = MonetaryFormats.getAmountFormat(GERMANY);
        defaultFormat.print(b, Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(12.50).create());
        assertEquals("12,50 CHF", b.toString());
        b.setLength(0);
        defaultFormat.print(b, Monetary.getDefaultAmountFactory().setCurrency("INR")
                .setNumber(123456789101112.123456).create());
        assertEquals("123.456.789.101.112,12 INR", b.toString());
        b.setLength(0);
        defaultFormat = MonetaryFormats.getAmountFormat(new Locale("", "IN"));
        defaultFormat
                .print(b, Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(1211112.50).create());
        assertEquals("CHF 1,211,112.50", b.toString());
        b.setLength(0);
        defaultFormat.print(b, Monetary.getDefaultAmountFactory().setCurrency("INR")
                .setNumber(123456789101112.123456).create());
        assertEquals("INR 123,456,789,101,112.12", b.toString());
        b.setLength(0);
        // Locale india = new Locale("", "IN");
        // defaultFormat = MonetaryFormats.getAmountFormat(india)
        // .setNumberGroupSizes(3, 2).of();
        // defaultFormat.print(b, Monetary.getAmount("INR",
        // 123456789101112.123456));
        // assertEquals("INR 12,34,56,78,91,01,112.12",
        // b.toString());
    }

    /**
     * Test method for {@link javax.money.format.MonetaryAmountFormat#parse(java.lang.CharSequence)}
     */
    @Test
    public void testParse() throws ParseException {
        MonetaryAmountFormat defaultFormat = MonetaryFormats.getAmountFormat(GERMANY);
        MonetaryAmount amountEur = Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(new BigDecimal("12.50")).create();
        MonetaryAmount amountChf = Monetary.getDefaultAmountFactory().setCurrency("CHF").setNumber(new BigDecimal("12.50")).create();
        assertEquals(amountEur, defaultFormat.parse("12,50 EUR"));
        assertEquals(amountEur, defaultFormat.parse("\u00A0 \u202F \u2007 12,50 EUR"));
        assertEquals(amountEur, defaultFormat.parse("\u00A0 \u202F \u2007 12,50 \u00A0 \u202F \u2007 EUR  \t\n\n "));
        assertEquals(amountChf, defaultFormat.parse("12,50 CHF"));
    }

    @Test
    public void testWithCustomPatternAndRounding() {
        Money money = Money.of(12345.23456789, "EUR");
        // format with 5 digits after comma: note that formatted value was rounded to 5 digits
        testCustomFormat(money, "#,##0.00### ¤", "12.345,23456789 €", "12.345,23457 €", SYMBOL);
        // the same format but with currency symbol on start
        testCustomFormat(money, "¤ #,##0.00###", "€ 12.345,23456789", "€ 12.345,23457", SYMBOL);
    }

    @Test
    public void testWithCustomPattern() {
        Money money = Money.of(12345.23, "EUR");
        testCustomFormat(money, "#,##0.## ¤", "12.345,23 EUR", "12.345,23 EUR", CODE);
        testCustomFormat(money, "#,##0.##¤", "12.345,23EUR", "12.345,23EUR", CODE);
        testCustomFormat(money, "¤ #,##0.##", "EUR 12.345,23", "EUR 12.345,23", CODE);
        testCustomFormat(money, "¤#,##0.##", "EUR12.345,23", "EUR12.345,23", CODE);
        testCustomFormat(money, "LITERAL ¤#,##0.##", "LITERAL EUR12.345,23", "LITERAL EUR12.345,23", CODE);
        testCustomFormat(money, "LITERAL ¤ #,##0.##", "LITERAL EUR 12.345,23", "LITERAL EUR 12.345,23", CODE);
        testCustomFormat(money, "LITERAL ¤ #,##0.## LITERAL", "LITERAL EUR 12.345,23 LITERAL", "LITERAL EUR 12.345,23 LITERAL", CODE);
        testCustomFormat(money, "LITERAL #,##0.## ¤ LITERAL", "LITERAL 12.345,23 EUR LITERAL", "LITERAL 12.345,23 EUR LITERAL", CODE);
        testCustomFormat(money, "LITERAL #,##0.##¤ LITERAL", "LITERAL 12.345,23EUR LITERAL", "LITERAL 12.345,23EUR LITERAL", CODE);
    }

    @Test
    public void testWithCustomPattern_SYMBOL() {
        Money money = Money.of(12345.23, "EUR");
        testCustomFormat(money, "#,##0.## ¤", "12.345,23 €", "12.345,23 €", SYMBOL);
        testCustomFormat(money, "#,##0.##¤", "12.345,23€", "12.345,23€", SYMBOL);
        testCustomFormat(money, "¤ #,##0.##", "€ 12.345,23", "€ 12.345,23", SYMBOL);
        testCustomFormat(money, "¤#,##0.##", "€12.345,23", "€12.345,23", SYMBOL);
        testCustomFormat(money, "LITERAL ¤#,##0.##", "LITERAL €12.345,23", "LITERAL €12.345,23", SYMBOL);
        testCustomFormat(money, "LITERAL ¤ #,##0.##", "LITERAL € 12.345,23", "LITERAL € 12.345,23", SYMBOL);
        testCustomFormat(money, "LITERAL ¤ #,##0.## LITERAL", "LITERAL € 12.345,23 LITERAL", "LITERAL € 12.345,23 LITERAL", SYMBOL);
        testCustomFormat(money, "LITERAL #,##0.## ¤ LITERAL", "LITERAL 12.345,23 € LITERAL", "LITERAL 12.345,23 € LITERAL", SYMBOL);
        testCustomFormat(money, "LITERAL #,##0.##¤ LITERAL", "LITERAL 12.345,23€ LITERAL", "LITERAL 12.345,23€ LITERAL", SYMBOL);
    }

    /**
     * Testcase related to https://github.com/JavaMoney/jsr354-ri/issues/282
     */
    @Test
    public void testWithCustomPatternForNegativeAmount() {
        Money moneyNegative = Money.of(-12345.23, "EUR");
        testCustomFormat(moneyNegative, "#,##0.## ¤", "-12.345,23 EUR", "-12.345,23 EUR", CODE);
        testCustomFormat(moneyNegative, "#,##0.##¤", "-12.345,23EUR", "-12.345,23EUR", CODE);
//FIXME        testCustomFormat(moneyNegative, "¤ #,##0.##", "EUR -12.345,23", "EUR -12.345,23", CODE);
        testCustomFormat(moneyNegative, "¤#,##0.##", "EUR-12.345,23", "EUR-12.345,23", CODE);
        testCustomFormat(moneyNegative, "LITERAL ¤#,##0.##", "LITERAL EUR-12.345,23", "LITERAL EUR-12.345,23", CODE);
//FIXME        testCustomFormat(moneyNegative, "LITERAL ¤ #,##0.##", "LITERAL EUR -12.345,23", "LITERAL EUR -12.345,23", CODE);
//FIXME        testCustomFormat(moneyNegative, "LITERAL ¤ #,##0.## LITERAL", "LITERAL EUR -12.345,23 LITERAL", "LITERAL EUR -12.345,23 LITERAL", CODE);
//FIXME        testCustomFormat(moneyNegative, "LITERAL #,##0.## ¤ LITERAL", "LITERAL -12.345,23 EUR LITERAL", "LITERAL -12.345,23 EUR LITERAL", CODE);
//FIXME        testCustomFormat(moneyNegative, "LITERAL #,##0.##¤ LITERAL", "LITERAL -12.345,23EUR LITERAL", "LITERAL -12.345,23EUR LITERAL", CODE);
    }

    @Test
    public void testWithTwoCustomPatterns() {
        Money money = Money.of(12345.23, "EUR");
        Money moneyNegative = Money.of(-12345.23, "EUR");
        testCustomFormat(money, "#,##0.## ¤;#,##0.## ¤", "12.345,23 EUR", "12.345,23 EUR", CODE);
        testCustomFormat(moneyNegative, "#,##0.## ¤;#,##0.## ¤", "-12.345,23 EUR", "-12.345,23 EUR", CODE);
    }

    private void testCustomFormat(Money money, String pattern, String input, String expectedFormatted, CurrencyStyle style) {
        MonetaryAmountFormat formatWithLiteralOnStart = formatWithCustomPattern(pattern, style);
        assertEquals(formatWithLiteralOnStart.format(money), expectedFormatted);
        assertEquals(formatWithLiteralOnStart.parse(input), money);
    }

    private MonetaryAmountFormat formatWithCustomPattern(String pattern, CurrencyStyle code) {
        AmountFormatQuery formatQuery = AmountFormatQueryBuilder.of(GERMANY).set(code).set("pattern", pattern).build();
        return MonetaryFormats.getAmountFormat(formatQuery);
    }

    @Test
    public void testBulgarianLev(){
        MonetaryAmount money = Money.of(1123000.50, "BGN");
        Locale locale = new Locale("bg", "BG");
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(
                AmountFormatQueryBuilder.of(locale).set(SYMBOL)
                        .build());
        assertEquals(format.format(money), "1 123 000,50 лв.");

        format = MonetaryFormats.getAmountFormat(
                AmountFormatQueryBuilder.of(locale).set(CODE)
                        .build());
        assertEquals(format.format(money), "1 123 000,50 BGN");
    }
}
