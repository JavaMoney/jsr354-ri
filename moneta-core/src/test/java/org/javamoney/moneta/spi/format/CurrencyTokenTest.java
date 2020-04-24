/*
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

import org.javamoney.moneta.CurrencyUnitBuilder;
import org.javamoney.moneta.FastMoney;
import org.testng.annotations.Test;

import javax.money.CurrencyUnit;
import javax.money.format.AmountFormatContextBuilder;
import javax.money.format.MonetaryParseException;
import java.io.IOException;
import java.util.Locale;

import static java.util.Locale.*;
import static org.javamoney.moneta.format.CurrencyStyle.*;
import static org.testng.Assert.*;

public class CurrencyTokenTest {

    public static final CurrencyUnit BTC = CurrencyUnitBuilder.of("BTC", "crypto").build();


    @Test
    public void testParse_CODE() {
        CurrencyToken token = new CurrencyToken(CODE, AmountFormatContextBuilder.of(FRANCE).build());
        ParseContext context = new ParseContext("EUR");
        token.parse(context);
        assertEquals(context.getIndex(), 3);
    }

    @Test
    public void testParse_CODE_unknown() {
        CurrencyToken token = new CurrencyToken(CODE, AmountFormatContextBuilder.of(FRANCE).build());
        ParseContext context = new ParseContext("BTC");
        try {
            token.parse(context);
        } catch (MonetaryParseException e) {
            assertEquals(e.getInput(), "BTC");
            assertEquals(e.getErrorIndex(), -1);
            assertEquals(e.getMessage(), "Could not parse CurrencyUnit. Unknown currency code: BTC");
        }
        assertEquals(context.getIndex(), 0);
        assertFalse(context.isComplete());
        assertTrue(context.hasError());
        assertEquals(context.getErrorMessage(), "Unknown currency code: BTC");
    }

    @Test
    public void testParse_SYMBOL_EUR() {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(FRANCE).build());
        ParseContext context = new ParseContext("€");
        token.parse(context);
        assertEquals(context.getIndex(), 1);
    }

    @Test
    public void testParse_SYMBOL_GBP() {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(FRANCE).build());
        ParseContext context = new ParseContext("£");
        token.parse(context);
        assertEquals(context.getIndex(), 1);
    }

    @Test
    public void testParse_SYMBOL_ambiguous_dollar() {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(FRANCE).build());
        ParseContext context = new ParseContext("$");
        try {
            token.parse(context);
        } catch (MonetaryParseException e) {
            assertEquals(e.getInput(), "$");
            assertEquals(e.getErrorIndex(), -1);
            assertEquals(e.getMessage(), "$ is not a unique currency symbol.");
        }
        assertEquals(context.getIndex(), 0);
        assertFalse(context.isComplete());
        assertTrue(context.hasError());
        assertEquals(context.getErrorMessage(), "$ is not a unique currency symbol.");
    }

    @Test
    public void testParse_NUMERIC_CODE() {
        CurrencyToken token = new CurrencyToken(NUMERIC_CODE, AmountFormatContextBuilder.of(FRANCE).build());
        ParseContext context = new ParseContext("840");
        try {
            token.parse(context);
        } catch (MonetaryParseException e) {
            assertEquals(e.getInput(), "840");
            assertEquals(e.getErrorIndex(), -1);
            assertEquals(e.getMessage(), "Could not parse CurrencyUnit. Not yet implemented");
        }
        assertEquals(context.getIndex(), 0);
        assertFalse(context.isComplete());
        assertTrue(context.hasError());
        assertEquals(context.getErrorMessage(), "Not yet implemented");
    }

    @Test
    public void testParse_NAME() {
        CurrencyToken token = new CurrencyToken(NAME, AmountFormatContextBuilder.of(FRANCE).build());
        ParseContext context = new ParseContext("US Dollar");
        try {
            token.parse(context);
        } catch (MonetaryParseException e) {
            assertEquals(e.getInput(), "US"); //TODO US Dollar
            assertEquals(e.getErrorIndex(), -1);
            assertEquals(e.getMessage(), "Could not parse CurrencyUnit. Not yet implemented");
        }
        assertEquals(context.getIndex(), 0);
        assertFalse(context.isComplete());
        assertTrue(context.hasError());
        assertEquals(context.getErrorMessage(), "Not yet implemented");
    }

    @Test
    public void testPrint_CODE() throws IOException {
        CurrencyToken token = new CurrencyToken(CODE, AmountFormatContextBuilder.of(FRANCE).build());
        FastMoney amount = FastMoney.of(0, "EUR");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "EUR");
    }

    @Test
    public void testPrint_SYMBOL_USD() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(US).build());
        FastMoney amount = FastMoney.of(0, "USD");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "$");
    }

    @Test
    public void testPrint_SYMBOL_USD_for_France() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(FRANCE).build());
        FastMoney amount = FastMoney.of(0, "USD");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "$US");
    }

    @Test
    public void testPrint_SYMBOL_HKD() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(new Locale("en", "HK")).build());
        FastMoney amount = FastMoney.of(0, "HKD");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "HK$");
    }

    @Test
    public void testPrint_SYMBOL_HKD_for_US() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(US).build());
        FastMoney amount = FastMoney.of(0, "HKD");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "HK$");
    }

    @Test
    public void testPrint_SYMBOL_UAH() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(new Locale("uk", "UA")).build());
        FastMoney amount = FastMoney.of(0, "UAH");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "₴");
    }

    @Test
    public void testPrint_SYMBOL_UAH_for_US() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(US).build());
        FastMoney amount = FastMoney.of(0, "UAH");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "UAH");
    }

    @Test
    public void testPrint_SYMBOL_RUB() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(new Locale("ru", "RU")).build());
        FastMoney amount = FastMoney.of(0, "RUB");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "₽");
    }

    @Test
    public void testPrint_SYMBOL_RUB_for_US() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(US).build());
        FastMoney amount = FastMoney.of(0, "RUB");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "RUB");
    }

    @Test
    public void testPrint_SYMBOL_BGN() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(new Locale("bg", "BG")).build());
        FastMoney amount = FastMoney.of(0, "BGN");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "лв.");
    }

    @Test
    public void testPrint_SYMBOL_BGN_for_US() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(US).build());
        FastMoney amount = FastMoney.of(0, "BGN");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "BGN");
    }

    @Test
    public void testPrint_SYMBOL_EUR() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(FRANCE).build());
        FastMoney amount = FastMoney.of(0, "EUR");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "€");
    }

    @Test
    public void testPrint_SYMBOL_EUR_for_US() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(US).build());
        FastMoney amount = FastMoney.of(0, "EUR");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "€"); // for some reason here is returned a symbol € instead of EUR
    }

    @Test
    public void testPrint_SYMBOL_GBP() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(UK).build());
        FastMoney amount = FastMoney.of(0, "GBP");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "£");
    }

    @Test
    public void testPrint_SYMBOL_GBP_for_France() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(FRANCE).build());
        FastMoney amount = FastMoney.of(0, "GBP");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "£GB");
    }

    @Test
    public void testPrint_SYMBOL_BTC() throws IOException {
        CurrencyToken token = new CurrencyToken(SYMBOL, AmountFormatContextBuilder.of(FRANCE).build());
        FastMoney amount = FastMoney.of(0, BTC);
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "BTC"); // returned currency code itself
    }

    @Test
    public void testPrint_NAME_USD_for_US() throws IOException {
        CurrencyToken token = new CurrencyToken(NAME, AmountFormatContextBuilder.of(US).build());
        FastMoney amount = FastMoney.of(0, "USD");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "US Dollar");
    }

    @Test
    public void testPrint_NAME_USD_for_FR() throws IOException {
        CurrencyToken token = new CurrencyToken(NAME, AmountFormatContextBuilder.of(FRANCE).build());
        FastMoney amount = FastMoney.of(0, "USD");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "dollar des États-Unis");
    }

    @Test
    public void testPrint_NAME_BTC() throws IOException {
        CurrencyToken token = new CurrencyToken(NAME, AmountFormatContextBuilder.of(FRANCE).build());
        FastMoney amount = FastMoney.of(0, BTC);
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "BTC"); // returned currency code itself
    }

    @Test
    public void testPrint_NUMERIC_CODE() throws IOException {
        CurrencyToken token = new CurrencyToken(NUMERIC_CODE, AmountFormatContextBuilder.of(FRANCE).build());
        FastMoney amount = FastMoney.of(0, "USD");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), "840");
    }

    @Test
    public void testToString() {
        CurrencyToken token = new CurrencyToken(CODE, AmountFormatContextBuilder.of(FRANCE).build());
        assertEquals(token.toString(), "CurrencyToken [locale=fr_FR, style=CODE]");
    }
}