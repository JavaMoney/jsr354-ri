package org.javamoney.moneta.spi;

import org.testng.annotations.Test;

import javax.money.convert.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static org.testng.Assert.*;

public class CompoundRateProviderTest {

    @Test
    public void testGetExchangeRate() {
        String baseCurrency = "EUR";
        String termCurrency = "GBP";
        LocalDate date = lastWeekTuesday();
        ConversionQuery conversionQuery = ConversionQueryBuilder.of()
                .setBaseCurrency(baseCurrency)
                .setTermCurrency(termCurrency)
                .set(date)
                .build();

        // Need to cast to CompoundRateProvider to access the getExchangeRate method that takes a boolean parameter
        CompoundRateProvider compoundRateProvider = (CompoundRateProvider) MonetaryConversions.getExchangeRateProvider("ECB", "ECB-HIST90");
        assertNotNull(compoundRateProvider.getExchangeRate(conversionQuery, false));
    }

    @Test
    public void testGetExchangeRateAllProvidersFail() {
        String baseCurrency = "EUR";
        String termCurrency = "GBP";
        LocalDate date = lastWeekTuesday();
        ConversionQuery conversionQuery = ConversionQueryBuilder.of()
                .setBaseCurrency(baseCurrency)
                .setTermCurrency(termCurrency)
                .set(date)
                .build();

        // Need to cast to CompoundRateProvider to access the getExchangeRate method that takes a boolean parameter
        CompoundRateProvider compoundRateProvider = (CompoundRateProvider) MonetaryConversions.getExchangeRateProvider("ECB", "IMF");
        assertThrows(CurrencyConversionException.class, () -> compoundRateProvider.getExchangeRate(conversionQuery, false));
    }

    @Test
    public void testGetExchangeRateFailingFast() {
        String baseCurrency = "EUR";
        String termCurrency = "GBP";
        LocalDate date = lastWeekTuesday();
        ConversionQuery conversionQuery = ConversionQueryBuilder.of()
                .setBaseCurrency(baseCurrency)
                .setTermCurrency(termCurrency)
                .set(date)
                .build();

        ExchangeRateProvider compoundRateProvider = MonetaryConversions.getExchangeRateProvider("ECB", "ECB-HIST90");
        assertThrows(CurrencyConversionException.class, () -> compoundRateProvider.getExchangeRate(conversionQuery));
    }

    private LocalDate lastWeekTuesday() {
        LocalDate today = LocalDate.now();
        LocalDate lastTuesday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.TUESDAY));
        return lastTuesday.minusWeeks(1);
    }
}
