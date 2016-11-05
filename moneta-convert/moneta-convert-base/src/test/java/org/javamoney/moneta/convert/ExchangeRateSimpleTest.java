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
package org.javamoney.moneta.convert;

import static org.testng.Assert.assertEquals;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.convert.ExchangeRate;
import javax.money.convert.RateType;

import org.javamoney.moneta.convert.ExchangeRateBuilder;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.testng.annotations.Test;

public class ExchangeRateSimpleTest {
    private static final CurrencyUnit EUR = Monetary.getCurrency("EUR");
    private static final CurrencyUnit GBP = Monetary.getCurrency("GBP");

    @Test
    public void equalsTest() {
        DefaultNumberValue factor = new DefaultNumberValue(1.1);

        ExchangeRate rate1 = new ExchangeRateBuilder("myprovider", RateType.ANY)
                .setBase(EUR)
                .setTerm(GBP)
                .setFactor(factor)
                .build();

        ExchangeRate rate2 = new ExchangeRateBuilder("myprovider", RateType.ANY)
                .setBase(EUR)
                .setTerm(GBP)
                .setFactor(factor)
                .build();

        assertEquals(rate1, rate2);
    }
}
