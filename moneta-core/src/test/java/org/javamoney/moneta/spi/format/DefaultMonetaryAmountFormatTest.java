/**
 * Copyright (c) 2012, 2020, Anatole Tresch, Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.spi.format;

import org.javamoney.moneta.FastMoney;
import org.testng.annotations.Test;

import javax.money.*;
import javax.money.format.*;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;

import static java.util.Locale.US;
import static org.testng.Assert.*;
import static org.testng.AssertJUnit.fail;

public class DefaultMonetaryAmountFormatTest {
    private static final Logger LOG = Logger.getLogger(DefaultMonetaryAmountFormatTest.class.getName());

    @Test
    public void testFormat() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        String formatted = format.format(FastMoney.of(1000.42, "USD"));
        assertEquals(formatted, "USD1,000.42");
    }

    @Test
    public void testFormat_with_custom_pattern() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00 ¤");
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        String formatted = format.format(FastMoney.of(1000.42, "USD"));
        assertEquals(formatted, "1000.42 USD");
    }

    @Test
    public void testFormat_with_two_patterns() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00 ¤;0.0 ¤");
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        String formatted = format.format(FastMoney.of(1000.42, "USD"));
        assertEquals(formatted, "1000.42 USD");
        formatted = format.format(FastMoney.of(-1000.42, "USD"));
        assertEquals(formatted, "-1000.4 USD");
    }

    @Test
    public void testFormat_with_two_patterns_without_currency() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00;0.0");
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        String formatted = format.format(FastMoney.of(1000.42, "USD"));
        assertEquals(formatted, "1000.42");
        formatted = format.format(FastMoney.of(-1000.42, "USD"));
        assertEquals(formatted, "-1000.4");
    }

    @Test
    public void testParse() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        MonetaryAmount parsedAmount = format.parse("USD1,000.42");
        assertEquals(parsedAmount.getCurrency().getCurrencyCode(), "USD");
        assertEquals(parsedAmount.getNumber().doubleValueExact(), 1000.42D);
        assertEquals(parsedAmount.toString(), "USD 1000.42");
    }

    @Test
    public void testParse_with_custom_pattern() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00 ¤");
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        MonetaryAmount parsedAmount = format.parse("0.01 USD");
        assertEquals(parsedAmount.getCurrency().getCurrencyCode(), "USD");
        assertEquals(parsedAmount.getNumber().doubleValueExact(), 0.01D);
        assertEquals(parsedAmount.toString(), "USD 0.01");
    }

    @Test
    public void testParse_pattern_without_currency_sign() {
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00");
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        try {
            format.parse("1,000.42");
        } catch (MonetaryParseException e) {
            assertEquals(e.getMessage(), "Failed to parse currency. Is currency sign ¤ present in pattern?");
            assertEquals(e.getErrorIndex(), -1);
        }
    }

    @Test
    public void testParse_pattern_without_currency_sign_but_with_currency_in_context() {
        CurrencyUnit usd = Monetary.getCurrency("USD");
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00");
        builder.set(CurrencyUnit.class, usd);
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        MonetaryAmount parsedAmount = format.parse("0.01");
        assertSame(parsedAmount.getCurrency(), usd);
        assertEquals(parsedAmount.getNumber().doubleValueExact(), 0.01D);
        assertEquals(parsedAmount.toString(), "USD 0.01");
    }

    /**
     * Test related to https://github.com/JavaMoney/jsr354-ri/issues/294
     */
    @Test
    public void testParse_pattern_with_currency_sign_and_with_currency_in_context_but_amount_is_without_currency_code() {
        CurrencyUnit usd = Monetary.getCurrency("USD");
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(US);
        builder.set("pattern", "0.00 ¤");
        builder.set(CurrencyUnit.class, usd);
        AmountFormatContext context = builder.build();
        DefaultMonetaryAmountFormat format = new DefaultMonetaryAmountFormat(context);
        try {
            MonetaryAmount parsedAmount = format.parse("0.01");
        } catch (MonetaryParseException e) {
            e.printStackTrace();
            assertEquals(e.getMessage(), "Error parsing CurrencyUnit: no input.");
            assertEquals(e.getErrorIndex(), -1);
        }
//FIXME        assertSame(parsedAmount.getCurrency(), usd);
//FIXME        assertEquals(parsedAmount.getNumber().doubleValueExact(), 0.01D);
//FIXME        assertEquals(parsedAmount.toString(), "USD 0.01");
    }

    @Test(description =
            "Test formats and parses (round-trip) any supported amount type for each supported Locale, " +
                    "using different format queries.")
    public void testParseDifferentStyles() {
        final String[] skipLangArray = {"as", "ar", "bn", "ckb", "dz", "fa", "ig", "ks", "lrc",
                "mni", "mr", "my", "mzn", "ne", "pa", "ps", "sa", "sat", "sd", "th", "ur", "uz", "raj", "bgc", "bho"};
        final Set<String> SKIPPED_LANGUAGES = new HashSet<>(Arrays.asList(skipLangArray));
        final Locale[] locArray = new Locale[]{new Locale("dz", "BT")
        };
        final Set<Locale> SKIPPED_LOCALES = new HashSet<>(Arrays.asList(locArray));

        for (Locale locale : MonetaryFormats.getAvailableLocales()) {
            if (SKIPPED_LANGUAGES.contains(locale.getLanguage()) ||
                    SKIPPED_LOCALES.contains(locale)) {
                continue;
            }
            LOG.finer("Locale: " + locale);
            for (Class clazz : Monetary.getAmountTypes()) {
                MonetaryAmountFactory fact = Monetary.getAmountFactory(clazz);
                AmountFormatQuery query = AmountFormatQueryBuilder.of(locale).setMonetaryAmountFactory(fact).build();
                final MonetaryAmount amt = fact.setCurrency("USD").setNumber(10.5).create();
                final NumberFormat jdkFormat = NumberFormat.getCurrencyInstance(locale);
                final String jdkProduced = jdkFormat.format(10.5);
                final MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(query);
                final String formatProduced = format.format(amt);
                LOG.finer(String.format("Formatted: %s (%s)", formatProduced, jdkProduced));
                assertNotNull(formatProduced, "No MonetaryAmountFormat returned from MonetaryFormats." +
                        "getMonetaryFormat(Locale,String...) with supported Locale: " + locale);
                assertFalse(formatProduced.isEmpty(), "MonetaryAmountFormat returned empty String for " + amt);
                try {
                    final MonetaryAmount amtParsed = format.parse(formatProduced);
                    assertNotNull(amtParsed, "Reverse-parsing of MonetaryAmount failed for '" + formatProduced +
                            "' using MonetaryAmountFormat: " + format);
                    assertEquals(amtParsed.getClass(), clazz,
                            "Reverse-parsing of MonetaryAmount failed for '" + formatProduced +
                                    "' using MonetaryAmountFormat(invalid type " +
                                    amtParsed.getClass().getName() + ") for format: " + format);
                } catch (MonetaryException e) {
                    try {
                        final Number jdkNum = jdkFormat.parse(jdkProduced);
                        LOG.finer(String.format("Parsed (JDK): %s (%s)", jdkNum, jdkProduced));
                        final Currency jdkCur = Currency.getInstance(locale);
                        final String jdkTweaked = jdkProduced.replace(jdkCur.getSymbol(), "USD");
                        LOG.finer(String.format("Trying to parse: %s", jdkTweaked));
                        final Number parsedNum = jdkFormat.parse(jdkTweaked);
                        LOG.finer(String.format("Parsed: %s (%s)", parsedNum, jdkTweaked));

                        fail("Reverse-parsing of MonetaryAmount failed for '" + formatProduced +
                                "' using MonetaryAmountFormat: " + format.getClass().getName() + " for Locale: " + locale);
                    } catch (ParseException pe) {
                        fail("Reverse-parsing via JDK failed for '" + jdkProduced + " with Locale: " + locale);
                    }
                }
            }
        }
    }
}