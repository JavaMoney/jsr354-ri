package org.javamoney.moneta.convert.ecb;

import javax.money.convert.ExchangeRateProviderSupplier;

/**
 * <p>
 * This enum contains all implementations by moneta-convert-ECB. Using this enum will easier
 * to choose an available implementation.
 * </p>
 * <code>ExchangeRateProvider provider = MonetaryConversions.getExchangeRateProvider(ECBExchangeRateType.ECB);<code>
 *
 * @author otaviojava
 */
public enum ECBExchangeRateType implements ExchangeRateProviderSupplier {
    /**
     * Exchange rate to the European Central Bank. Uses the
     * {@link ECBCurrentRateProvider} implementation.
     */
    ECB("ECB", "Exchange rate to the European Central Bank."),
        /**
     * Exchange rate to European Central Bank (last 90 days). Uses the
     * {@link ECBHistoric90RateProvider} implementation.
     */
    ECB_HIST90("ECB-HIST90",
            "Exchange rate to European Central Bank (last 90 days)."),
    /**
     * Uses the {@link ECBHistoricRateProvider} implementation.
     */
    ECB_HIST(
            "ECB-HIST",
            "Exchange rate to the European Central Bank that loads all data up to 1999 into its historic data cache.");

    private final String type;

    private final String description;

    ECBExchangeRateType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    @Override
    public String get() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}