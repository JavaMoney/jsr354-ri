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

import org.javamoney.moneta.function.FastMoneyProducer;
import org.javamoney.moneta.function.MoneyProducer;
import org.testng.annotations.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Locale;

import static org.testng.Assert.assertEquals;

public class MonetaryAmountDecimalFormatBuilderTest {

    @Test
    public void shouldCreateDefaultBuilder() {
        MonetaryAmountDecimalFormat format = (MonetaryAmountDecimalFormat) MonetaryAmountDecimalFormatBuilder.newInstance().build();
        assertEquals(format.getCurrencyUnit(), Monetary.getCurrency(Locale.getDefault()));
        assertEquals(format.getProducer().getClass(), MoneyProducer.class);
        assertEquals(format.getDecimalFormat().getCurrency().getCurrencyCode(),format.getCurrencyUnit().getCurrencyCode());
    }

    @Test
    public void shouldCreateSettingProducer() {
        MonetaryAmountDecimalFormat format = (MonetaryAmountDecimalFormat) MonetaryAmountDecimalFormatBuilder.newInstance().withProducer(new FastMoneyProducer())
                .build();
        assertEquals(format.getCurrencyUnit(), Monetary.getCurrency(Locale.getDefault()));
        assertEquals(format.getProducer().getClass(), FastMoneyProducer.class);
        assertEquals(format.getDecimalFormat().getCurrency().getCurrencyCode(),format.getCurrencyUnit().getCurrencyCode());
    }

    @Test
    public void shouldCreateSettingCurrencyUnit() {
        CurrencyUnit currencyUnit = Monetary.getCurrency("BRL");
        MonetaryAmountDecimalFormat format = (MonetaryAmountDecimalFormat) MonetaryAmountDecimalFormatBuilder.newInstance().withProducer(new FastMoneyProducer())
                .withCurrencyUnit(currencyUnit).build();
        assertEquals(format.getCurrencyUnit(), currencyUnit);
        assertEquals(format.getProducer().getClass(), FastMoneyProducer.class);
        assertEquals(format.getDecimalFormat().getCurrency().getCurrencyCode(),format.getCurrencyUnit().getCurrencyCode());
    }



}
