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

import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.GroupMonetarySummaryStatistics;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.javamoney.moneta.function.MonetarySummaryStatistics;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryException;
import javax.money.convert.ExchangeRateProvider;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.javamoney.moneta.convert.StreamFactory.*;
import static org.javamoney.moneta.function.MonetaryFunctions.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Class to test the class MonetaryFunctions, but
 * just the aggregator methods
 *
 * @author otaviojava
 */
public class ConversionAggregatorTest {

	private ExchangeRateProvider provider;

    @BeforeMethod
    public void init() {
        provider = new ExchangeRateProviderMock();
    }

    @Test
    public void shouldSumCorrectly() {
        Stream<MonetaryAmount> stream = streamNormal();
        MonetaryAmount sum = stream.reduce(sum()).get();
        assertEquals(sum.getNumber().intValue(), 20);
    }

    @Test
    public void shouldSumExchangeCorrectly() {
        Stream<MonetaryAmount> stream = currencies();
        MonetaryAmount sum = stream.reduce(sum(provider, DOLLAR)).get();
        assertTrue(sum.getNumber().intValue() > 20);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldsumWithNPEWhenAnElementIsNull() {
        Stream<MonetaryAmount> stream = streamNull();
        stream.reduce(sum()).get();
    }

    @Test(expectedExceptions = MonetaryException.class)
    public void shouldSumMoneratyExceptionWhenHasDifferenctsCurrencies() {
        Stream<MonetaryAmount> stream = streamCurrencyDifferent();
        stream.reduce(sum()).get();
    }

    @Test
    public void shouldMinCorretly() {
        Stream<MonetaryAmount> stream = streamNormal();
        MonetaryAmount min = stream.reduce(min()).get();
        assertEquals(min.getNumber().intValue(), 0);
    }

    @Test
    public void shouldMinExchangeCorretly() {
        Stream<MonetaryAmount> stream = Stream.of(Money.of(7, EURO),
                Money.of(9, BRAZILIAN_REAL), Money.of(8, DOLLAR));
        MonetaryAmount min = stream.reduce(min(provider)).get();
        assertEquals(Money.of(9, BRAZILIAN_REAL), min);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldMinWithNPEWhenAnElementIsNull() {
        Stream<MonetaryAmount> stream = streamNull();
        stream.reduce(min()).get();
    }

    @Test(expectedExceptions = MonetaryException.class)
    public void shouldMinMoneratyExceptionWhenHasDifferenctsCurrencies() {
        Stream<MonetaryAmount> stream = streamCurrencyDifferent();
        stream.reduce(min()).get();
    }

    @Test
    public void shouldMaxExchangeCorrectly() {
        Stream<MonetaryAmount> stream = Stream.of(Money.of(7, EURO), Money.of(9, BRAZILIAN_REAL), Money.of(8, DOLLAR));
        MonetaryAmount max = stream.reduce(max(provider)).get();
        assertEquals(Money.of(7, EURO), max);
    }


    @Test(expectedExceptions = NullPointerException.class)
    public void shouldMaxWithNPEWhenAnElementIsNull() {
        Stream<MonetaryAmount> stream = streamNull();
        stream.reduce(max()).get();
    }

    @Test(expectedExceptions = MonetaryException.class)
    public void shouldMaxMoneratyExceptionWhenHasDifferenctsCurrencies() {
        Stream<MonetaryAmount> stream = streamCurrencyDifferent();
        stream.reduce(max()).get();
    }

    @Test
    public void groupByCurrencyUnitTest() {
        Map<CurrencyUnit, List<MonetaryAmount>> groupBy = currencies().collect(
                MonetaryFunctions.groupByCurrencyUnit());
        assertEquals(3, groupBy.entrySet().size());
        assertEquals(3, groupBy.get(BRAZILIAN_REAL).size());
        assertEquals(3, groupBy.get(DOLLAR).size());
        assertEquals(3, groupBy.get(EURO).size());
    }

    @Test
    public void summarizingMonetaryExchangeTest() {
        MonetarySummaryStatistics summary = currenciesToSummary().collect(
                ConversionOperators.summarizingMonetary(BRAZILIAN_REAL,
                        provider));

        assertEquals(10L, summary.getCount());
    }

    @Test
    public void groupBySummarizingMonetaryTest() {
        GroupMonetarySummaryStatistics group = currenciesToSummary().collect(
                groupBySummarizingMonetary());
        Map<CurrencyUnit, MonetarySummaryStatistics> mapSummary = group.get();
        assertEquals(mapSummary.keySet().size(), 3);
    }

}
