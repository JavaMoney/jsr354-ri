package org.javamoney.moneta.convert.imf;

import javax.money.convert.ExchangeRateProviderSupplier;

/**
 * <p>
 * This enum contains all implementations by moneta-convert-IMF. Using this enum will easier
 * to choose an available implementation.
 * </p>
 * <code>ExchangeRateProvider provider = MonetaryConversions.getExchangeRateProvider(IMFExchangeRateType.IMF);<code>
 *
 * @author otaviojava
 */
public enum IMFExchangeRateType implements ExchangeRateProviderSupplier {

    /**
     * Exchange rate to the International Monetary Fond. Uses the
     * {@link IMFRateProvider} implementation.
     */
    IMF("IMF", "Exchange rate to the International Monetary Fond."),
    /**
     * Exchange rate to the International Monetary Fond from historic. Uses the
     * {@link IMFHistoricRateProvider} implementation.
     */
    IMF_HIST("IMF-HIST", "Exchange rate to the International Monetary Fond that retrieve historical information on lazy way.");

    private final String type;

    private final String description;

    IMFExchangeRateType(String type, String description) {
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