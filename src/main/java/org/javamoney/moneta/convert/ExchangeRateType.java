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

import javax.money.convert.ExchangeRateProviderSupplier;

import org.javamoney.moneta.internal.convert.ECBCurrentRateProvider;
import org.javamoney.moneta.internal.convert.ECBHistoric90RateProvider;
import org.javamoney.moneta.internal.convert.ECBHistoricRateProvider;
import org.javamoney.moneta.internal.convert.IMFHistoricRateProvider;
import org.javamoney.moneta.internal.convert.IMFRateProvider;
import org.javamoney.moneta.internal.convert.IdentityRateProvider;
import org.javamoney.moneta.internal.convert.USFederalReserveRateProvider;

/**
 * <p>
 * This enum contains all implementations by Moneta. Using this enum will easier
 * to choose an available implementation.
 * </p>
 * <code>ExchangeRateProvider provider = MonetaryConversions.getExchangeRateProvider(ExchangeRateType.ECB);<code>
 * @author otaviojava
 * @author Werner Keil
 * @since 1.0.1
 */
public enum ExchangeRateType implements ExchangeRateProviderSupplier {
    /**
     * Exchange rate to the European Central Bank. Uses the
     * {@link ECBCurrentRateProvider} implementation.
     */
    ECB("ECB", "Exchange rate to the European Central Bank."),
    /**
     * Exchange rate to the International Monetary Fond. Uses the
     * {@link IMFRateProvider} implementation.
     */
    IMF("IMF", "Exchange rate to the International Monetary Fond."),
    /**
     * Exchange rate to the International Monetary Fond from historic. Uses the
     * {@link IMFHistoricRateProvider} implementation.
     */
    IMF_HIST("IMF-HIST", "Exchange rate to the International Monetary Fond that retrieve historical information on lazy way."),
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
            "Exchange rate to the European Central Bank that loads all data up to 1999 into its historic data cache."),
    /**
     * Uses the {@link USFederalReserveRateProvider} implementation.
     */
    FRB("FRB", "Exchange rate to the Federal Reserve Bank of the United States, providing the prior week's Monday-Friday data."),
    /**
     * Uses the {@link IdentityRateProvider} implementation.
     */
    IDENTITY(
            "IDENT",
            "Exchange rate rate with factor one for identical base/term currencies");

    private final String type;

    private final String description;

    ExchangeRateType(String type, String description) {
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
