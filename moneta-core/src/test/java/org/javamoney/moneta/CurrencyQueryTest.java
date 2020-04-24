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
package org.javamoney.moneta;

import static java.util.stream.Collectors.toSet;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import javax.money.CurrencyQuery;
import javax.money.CurrencyQueryBuilder;
import javax.money.CurrencyUnit;
import javax.money.Monetary;

import org.testng.annotations.Test;

/**
 * @author Philippe Marschall
 */
public class CurrencyQueryTest {

    /**
     * Tests that searching currencies by regex is supported.
     */
    @Test
    public void testSeachByRegex() {
        String dollarRegex = "\\p{Upper}{2}D";
        Pattern dollarPattern = Pattern.compile(dollarRegex);
        Collection<CurrencyUnit> allCurrencies = Monetary.getCurrencies(CurrencyQueryBuilder.of().build());
        Set<String> availableDollarCodes = allCurrencies.stream()
                                                        .map(CurrencyUnit::getCurrencyCode)
                                                        .filter(currencyCode -> dollarPattern.matcher(currencyCode).matches())
                                                        .collect(toSet());

        assertFalse(availableDollarCodes.isEmpty());

        CurrencyQuery dollarQuery = CurrencyQueryBuilder.of().setCurrencyCodes(dollarRegex).build();
        Collection<CurrencyUnit> dollarCurrencies = Monetary.getCurrencies(dollarQuery);
        for (CurrencyUnit dollarCurrency : dollarCurrencies) {
            availableDollarCodes.remove(dollarCurrency.getCurrencyCode());
        }

        assertTrue(availableDollarCodes.isEmpty());
    }

}
