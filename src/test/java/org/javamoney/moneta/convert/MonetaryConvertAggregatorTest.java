package org.javamoney.moneta.convert;

import static org.javamoney.moneta.function.MonetaryFunctions.isCurrency;
import static org.javamoney.moneta.function.MonetaryFunctions.groupBySummarizingMonetary;
import static org.javamoney.moneta.function.MonetaryFunctions.summarizingMonetary;
import static org.javamoney.moneta.convert.ConversionOperators.max;
import static org.javamoney.moneta.convert.ConversionOperators.min;
import static org.javamoney.moneta.convert.ConversionOperators.sum;
import static org.javamoney.moneta.convert.ConversionOperators.sortValuable;
import static org.javamoney.moneta.convert.ConversionOperators.sortValuableDesc;
import static org.javamoney.moneta.function.StreamFactory.BRAZILIAN_REAL;
import static org.javamoney.moneta.function.StreamFactory.DOLLAR;
import static org.javamoney.moneta.function.StreamFactory.EURO;
import static org.javamoney.moneta.function.StreamFactory.currencies;
import static org.javamoney.moneta.function.StreamFactory.currenciesToSummary;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.convert.ExchangeRateProvider;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.convert.ExchangeRateProviderMock;
import org.javamoney.moneta.function.GroupMonetarySummaryStatistics;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.javamoney.moneta.function.MonetarySummaryStatistics;
import org.javamoney.moneta.function.StreamFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Class to test the class ConversionOperators, but
 * just the aggregator methods
 *
 * @author otaviojava
 */
public class MonetaryConvertAggregatorTest {

    private ExchangeRateProvider provider;

    @Test
    public void init() {
        provider = new ExchangeRateProviderMock();
    }

    @Test
    public void shouldSumExchangeCorrectly() {
        Stream<MonetaryAmount> stream = currencies();
        MonetaryAmount sum = stream.reduce(sum(provider, DOLLAR)).get();
        Assert.assertTrue(sum.getNumber().intValue() > 20);
    }

  

    @Test
    public void shouldMinExchangeCorretly() {
        Stream<MonetaryAmount> stream = Stream.of(Money.of(7, EURO),
                Money.of(9, BRAZILIAN_REAL), Money.of(8, DOLLAR));
        MonetaryAmount min = stream.reduce(min(provider)).get();
        Assert.assertEquals(Money.of(9, BRAZILIAN_REAL), min);
    }
    
    @Test
    public void shouldMaxExchangeCorrectly() {
        Stream<MonetaryAmount> stream = Stream.of(Money.of(7, EURO), Money.of(9, BRAZILIAN_REAL), Money.of(8, DOLLAR));
        MonetaryAmount max = stream.reduce(max(provider)).get();
        Assert.assertEquals(Money.of(7, EURO), max);
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

//    @Test
//    public void summarizingMonetaryExchangeTest() {
//        MonetarySummaryStatistics summary = currenciesToSummary().collect(
//                summarizingMonetary(BRAZILIAN_REAL,
//                        provider));
//
//        assertEquals(10L, summary.getCount());
//    }

    @Test
    public void groupBySummarizingMonetaryTest() {
        GroupMonetarySummaryStatistics group = currenciesToSummary().collect(
                groupBySummarizingMonetary());
        Map<CurrencyUnit, MonetarySummaryStatistics> mapSummary = group.get();
        assertEquals(mapSummary.keySet().size(), 3);
    }
    
    @Test
    public void shouldExecuteValiableOrder() {

        Stream<MonetaryAmount> stream = Stream.of(Money.of(7, EURO),
                Money.of(9, BRAZILIAN_REAL), Money.of(8, DOLLAR));
        List<MonetaryAmount> list = stream.sorted(
                sortValuable(provider)).collect(
                Collectors.toList());

        Assert.assertEquals(Money.of(9, BRAZILIAN_REAL), list.get(0));
        Assert.assertEquals(Money.of(8, DOLLAR), list.get(1));
        Assert.assertEquals(Money.of(7, EURO), list.get(2));
    }

    @Test
    public void shouldExecuteValiableOrderDesc() {

        Stream<MonetaryAmount> stream = Stream.of(Money.of(7, EURO),
                Money.of(9, BRAZILIAN_REAL), Money.of(8, DOLLAR));
        List<MonetaryAmount> list = stream.sorted(
                sortValuableDesc(provider)).collect(
                Collectors.toList());

        Assert.assertEquals(Money.of(7, EURO), list.get(0));
        Assert.assertEquals(Money.of(8, DOLLAR), list.get(1));
        Assert.assertEquals(Money.of(9, BRAZILIAN_REAL), list.get(2));

    }

}
