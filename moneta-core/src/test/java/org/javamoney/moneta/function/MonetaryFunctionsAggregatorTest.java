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
package org.javamoney.moneta.function;

import static org.javamoney.moneta.function.MonetaryFunctions.groupBySummarizingMonetary;
import static org.javamoney.moneta.function.MonetaryFunctions.isCurrency;
import static org.javamoney.moneta.function.MonetaryFunctions.max;
import static org.javamoney.moneta.function.MonetaryFunctions.min;
import static org.javamoney.moneta.function.MonetaryFunctions.sum;
import static org.javamoney.moneta.function.MonetaryFunctions.summarizingMonetary;
import static org.javamoney.moneta.function.StreamFactory.BRAZILIAN_REAL;
import static org.javamoney.moneta.function.StreamFactory.currencies;
import static org.javamoney.moneta.function.StreamFactory.currenciesToSummary;
import static org.javamoney.moneta.function.StreamFactory.streamCurrencyDifferent;
import static org.javamoney.moneta.function.StreamFactory.streamNormal;
import static org.javamoney.moneta.function.StreamFactory.streamNull;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryException;
import org.testng.annotations.Test;

/**
 * Class to test the class MonetaryFunctions, but
 * just the aggregator methods
 *
 * @author otaviojava
 * @author keilw
 */
public class MonetaryFunctionsAggregatorTest {

    @Test
    public void shouldSumCorrectly() {
        Stream<MonetaryAmount> stream = streamNormal();
        MonetaryAmount sum = stream.reduce(sum()).get();
        assertEquals(sum.getNumber().intValue(), 20);
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
    public void shouldMaxCorretly() {
        Stream<MonetaryAmount> stream = streamNormal();
        MonetaryAmount max = stream.reduce(max()).get();
        assertEquals(max.getNumber().intValue(), 10);
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
        assertEquals(3, groupBy.get(StreamFactory.DOLLAR).size());
        assertEquals(3, groupBy.get(StreamFactory.EURO).size());
    }

    @Test
    public void summarizingMonetaryTest() {

        MonetarySummaryStatistics summary = currenciesToSummary()
                .filter(
                        isCurrency(BRAZILIAN_REAL)).collect(
                        summarizingMonetary(BRAZILIAN_REAL));

        assertEquals(8L, summary.getCount());
        assertEquals(0L, summary.getMin().getNumber().longValue());
        assertEquals(10L, summary.getMax().getNumber().longValue());
        assertEquals(16L, summary.getSum().getNumber().longValue());
        assertEquals(2L, summary.getAverage().getNumber().longValue());

    }

    @Test
    public void groupBySummarizingMonetaryTest() {
        GroupMonetarySummaryStatistics group = currenciesToSummary().collect(
                groupBySummarizingMonetary());
        Map<CurrencyUnit, MonetarySummaryStatistics> mapSummary = group.get();
        assertEquals(mapSummary.keySet().size(), 3);
    }

}
