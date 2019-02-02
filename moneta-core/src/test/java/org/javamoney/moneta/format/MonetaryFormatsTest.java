/**
 * Copyright (c) 2012, 2019, Werner Keil and others by the @author tag.
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

import static java.util.Locale.CHINA;
import static java.util.Locale.FRANCE;
import static java.util.Locale.GERMANY;
import static org.javamoney.moneta.format.CurrencyStyle.CODE;
import static org.testng.Assert.assertEquals;

import java.util.Locale;

import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQuery;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.javamoney.moneta.Money;
import org.testng.annotations.Test;

public class MonetaryFormatsTest {
    private static final Locale DANISH = new Locale("da");
    private static final Locale BULGARIA = new Locale("bg", "BG");
    public static final Locale INDIA = new Locale("en, IN");

    @Test
    public void testParse_DKK_da() {
        AmountFormatQuery formatQuery = AmountFormatQueryBuilder.of(DANISH).set(CODE).build();
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(formatQuery);
        assertMoneyParse(format, "123 DKK", 123.0, "DKK");
        assertMoneyParse(format, "123,01 DKK", 123.01, "DKK");
        assertMoneyParse(format, "14.000,12 DKK", 14000.12, "DKK");
        assertMoneyParse(format, "14.000,12\u00A0DKK", 14000.12, "DKK");
    }

    @Test
    public void testFormat_DKK_da() {
        AmountFormatQuery formatQuery = AmountFormatQueryBuilder.of(DANISH).set(CODE).build();
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(formatQuery);
        assertMoneyFormat(format,  Money.of(123.01, "DKK"), "123,01 DKK");
        assertMoneyFormat(format, Money.of(14000.12, "DKK"), "14.000,12 DKK");
    }

    @Test
    public void testParse_EUR_fr_FR() {
        AmountFormatQuery formatQuery = AmountFormatQueryBuilder.of(FRANCE).set(CODE).build();
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(formatQuery);
        assertMoneyParse(format, "123 EUR", 123.0, "EUR");
        assertMoneyParse(format, "123,01 EUR", 123.01, "EUR");
        assertMoneyParse(format, "14 000,12 EUR", 14000.12, "EUR");
        assertMoneyParse(format, "14\u00A0000,12\u00A0EUR", 14000.12, "EUR");
        assertMoneyParse(format, "14\u202F000,12\u00A0EUR", 14000.12, "EUR");
    }

    @Test
    public void testFormat_EUR_fr_FR() {
        AmountFormatQuery formatQuery = AmountFormatQueryBuilder.of(FRANCE).set(CODE).build();
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(formatQuery);
        assertMoneyFormat(format,  Money.of(123.01, "EUR"), "123,01 EUR");
        assertMoneyFormat(format,  Money.of(14000.12, "EUR"), "14 000,12 EUR");
    }

    @Test
    public void testParse_BGN_bg_BG() {
        AmountFormatQuery formatQuery = AmountFormatQueryBuilder.of(BULGARIA).set(CODE).build();
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(formatQuery);
        assertMoneyParse(format, "123 BGN", 123.0, "BGN");
        assertMoneyParse(format, "123,01 BGN", 123.01, "BGN");
        assertMoneyParse(format, "14 000,12 BGN", 14000.12, "BGN");
        assertMoneyParse(format, "14\u00A0000,12\u00A0BGN", 14000.12, "BGN");
    }

    @Test
    public void testFormat_BGN_bg_BG() {
        AmountFormatQuery formatQuery = AmountFormatQueryBuilder.of(BULGARIA).set(CODE).build();
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(formatQuery);
        assertMoneyFormat(format,  Money.of(123.01, "BGN"), "123,01 BGN");
        assertMoneyFormat(format, Money.of(14000.12, "BGN"), "14 000,12 BGN");
    }

    @Test
    public void testParse_EUR_de_DE() {
        AmountFormatQuery formatQuery = AmountFormatQueryBuilder.of(GERMANY).set(CODE).build();
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(formatQuery);
        assertMoneyParse(format, "123 EUR", 123.0, "EUR");
        assertMoneyParse(format, "123,01 EUR", 123.01, "EUR");
        assertMoneyParse(format, "14.000,12 EUR", 14000.12, "EUR");
        assertMoneyParse(format, "14.000,12\u00A0EUR", 14000.12, "EUR");
    }

    @Test
    public void testFormat_EUR_de_DE() {
        AmountFormatQuery formatQuery = AmountFormatQueryBuilder.of(GERMANY).set(CODE).build();
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(formatQuery);
        assertMoneyFormat(format, Money.of(123.01, "EUR"), "123,01 EUR");
        assertMoneyFormat(format, Money.of(14000.12, "EUR"), "14.000,12 EUR");
    }

    @Test
    public void testParse_INR_en_IN() {
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(INDIA);
        assertMoneyParse(format, "INR 6,78,90,00,00,00,000.00", 67890000000000L, "INR");
    }

    @Test
    public void testFormat_INR_en_IN() {
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(INDIA);
        assertMoneyFormat(format, Money.of(67890000000000L, "INR"), "INR 67,890,000,000,000.00");
//TODO        assertMoneyFormat(format, Money.of(67890000000000L, "INR"), "INR 6,78,90,00,00,00,000.00");
    }

    @Test
    public void testParse_CNY_zh_CN() {
        AmountFormatQuery formatQuery = AmountFormatQueryBuilder.of(CHINA).set(CODE).build();
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(formatQuery);
        assertMoneyParse(format, "CNY123", 123.0, "CNY");
        assertMoneyParse(format, "CNY123.01", 123.01, "CNY");
        assertMoneyParse(format, "CNY14,000.12", 14000.12, "CNY");
        assertMoneyParse(format, "CNY 14,000.12", 14000.12, "CNY");
        assertMoneyParse(format, "CNY\u00A014,000.12", 14000.12, "CNY");
    }

    @Test
    public void testFormat_CNY_zh_CN() {
        AmountFormatQuery formatQuery = AmountFormatQueryBuilder.of(CHINA).set(CODE).build();
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(formatQuery);
        assertMoneyFormat(format, Money.of(123.01, "CNY"), "CNY123.01");
        assertMoneyFormat(format, Money.of(14000.12, "CNY"), "CNY14,000.12");
    }

    private void assertMoneyParse(MonetaryAmountFormat format, String text, double expected, String currencyCode) {
        MonetaryAmount amountInt = format.parse(text);
        assertEquals(amountInt.getNumber().doubleValueExact(), expected);
        assertEquals(amountInt.getCurrency().getCurrencyCode(), currencyCode);
    }

    private void assertMoneyFormat(MonetaryAmountFormat format, MonetaryAmount amount, String expected) {
        String formatted = format.format(amount);
        assertEquals(formatted, expected);
    }
}
