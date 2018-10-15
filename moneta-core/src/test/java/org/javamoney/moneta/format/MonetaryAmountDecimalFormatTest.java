/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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
import static org.testng.Assert.assertNotNull;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MoneyProducer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatContext;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryParseException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class MonetaryAmountDecimalFormatTest {

    private static final int EXAMPLE_VALUE = 10;

    private  MonetaryAmountFormat format;

    private Locale locale;

    private CurrencyUnit currencyUnit;

    private NumberFormat numberFormat;

    @BeforeMethod
    public void setup() {
        locale = Locale.US;
        currencyUnit = Monetary.getCurrency(locale);
        currencyUnit = Monetary.getCurrency(locale);
        format = MonetaryAmountDecimalFormatBuilder.of(locale).withProducer(new MoneyProducer())
                .withCurrencyUnit(currencyUnit).build();
        numberFormat = NumberFormat.getCurrencyInstance(locale);
    }

    @Test
    public void shouldReturnContext() {
        AmountFormatContext context = format.getContext();
        assertEquals(MonetaryAmountDecimalFormat.STYLE, context.getFormatName());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldReturnsErrorWhenAppendableIsNull() throws IOException {
        format.print(null, null);
    }

    @Test
    public void shouldPrintNullWhenMonetaryAmountIsNull() throws IOException {
        StringBuilder sb = new StringBuilder();
        format.print(sb, null);
        assertEquals(sb.toString(), "null");
    }

    @Test
    public void shouldPrintMonetaryAmount() throws IOException {
        StringBuilder sb = new StringBuilder();
        MonetaryAmount money = Money.of(EXAMPLE_VALUE, currencyUnit);
        format.print(sb, money);

        assertEquals(sb.toString(), numberFormat.format(EXAMPLE_VALUE));
    }

    @Test
    public void shouldQueryFromNullWhenMonetaryAmountIsNull() throws IOException {
        assertEquals(format.queryFrom(null), "null");
    }

    @Test
    public void shouldQueryFromMonetaryAmount() throws IOException {
        MonetaryAmount money = Money.of(EXAMPLE_VALUE, currencyUnit);
        assertEquals(format.queryFrom(money), numberFormat.format(EXAMPLE_VALUE));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldReturnErrorParseWhenMonetaryAmountIsNull() {
        format.parse(null);
    }

    @Test(expectedExceptions = MonetaryParseException.class)
    public void shouldReturnErrorParseWhenMonetaryAmountIsInvalid() {
        format.parse("ERROR");
    }

    @Test
    public void shouldParseMonetaryAmount() throws IOException {
        MonetaryAmount money = Money.of(EXAMPLE_VALUE, currencyUnit);
        String parse = format.queryFrom(money);
        assertEquals(format.parse(parse), money);
    }

    @Test
    public void shouldtoLocalizedPattern() {
        MonetaryAmountDecimalFormat f = MonetaryAmountDecimalFormat.class.cast(format);
        assertNotNull(f.toLocalizedPattern());
        assertNotNull(f.toPattern());
    }

    @Test
    public void shouldtoLocalizedPattern2() {
        String pattern = "#,##0.###";
        MonetaryAmountDecimalFormat f = (MonetaryAmountDecimalFormat) MonetaryAmountDecimalFormatBuilder.of(pattern).build();
        assertNotNull(f.toLocalizedPattern());
        assertNotNull(f.toPattern());
        assertEquals(pattern, f.toPattern());
    }

}
