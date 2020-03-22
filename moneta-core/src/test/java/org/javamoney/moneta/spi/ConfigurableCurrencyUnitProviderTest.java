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
package org.javamoney.moneta.spi;

import static org.testng.Assert.assertEquals;

import javax.money.CurrencyQuery;
import javax.money.CurrencyQueryBuilder;
import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.javamoney.moneta.CurrencyUnitBuilder;
import org.testng.annotations.Test;

/**
 * @author Philippe Marschall
 */
public class ConfigurableCurrencyUnitProviderTest {
    
    /**
     * Tests that searching by numeric code is supported by {@link ConfigurableCurrencyUnitProvider}.
     */
    @Test
    public void testSearchByNumericCurrencyCode() {
        CurrencyUnit usd = CurrencyUnitBuilder.of("USD", "search-test")
                        .setNumericCode(840)
                        .setDefaultFractionDigits(2)
                        .build(false);
        CurrencyUnit eur = CurrencyUnitBuilder.of("EUR", "search-test")
                        .setNumericCode(978)
                        .setDefaultFractionDigits(2)
                        .build(false);
        
        ConfigurableCurrencyUnitProvider.registerCurrencyUnit(usd);
        ConfigurableCurrencyUnitProvider.registerCurrencyUnit(eur);
        try {
            CurrencyQuery query = CurrencyQueryBuilder.of()
                .setProviderName(ConfigurableCurrencyUnitProvider.class.getSimpleName())
                .setNumericCodes(840)
                .build();
            CurrencyUnit currency = Monetary.getCurrency(query);
            assertEquals(usd, currency);
        } finally {
            ConfigurableCurrencyUnitProvider.removeCurrencyUnit(usd.getCurrencyCode());
            ConfigurableCurrencyUnitProvider.removeCurrencyUnit(eur.getCurrencyCode());
        }
    }
}
