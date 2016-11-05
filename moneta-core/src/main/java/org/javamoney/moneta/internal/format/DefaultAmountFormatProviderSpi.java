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
package org.javamoney.moneta.internal.format;


import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import javax.money.format.AmountFormatContextBuilder;
import javax.money.format.AmountFormatQuery;
import javax.money.format.MonetaryAmountFormat;
import javax.money.spi.MonetaryAmountFormatProviderSpi;

/**
 * Default format provider, which mainly maps the existing JDK functionality into the JSR 354 logic.
 *
 * @author Anatole Tresch
 */
public class DefaultAmountFormatProviderSpi implements MonetaryAmountFormatProviderSpi {

    private static final String DEFAULT_STYLE = "default";
    private static final String PROVIDER_NAME = "default";

    private Set<Locale> supportedSets = new HashSet<>();
    private Set<String> formatNames = new HashSet<>();

    public DefaultAmountFormatProviderSpi() {
        supportedSets.addAll(Arrays.asList(DecimalFormat.getAvailableLocales()));
        supportedSets = Collections.unmodifiableSet(supportedSets);
        formatNames.add(DEFAULT_STYLE);
        formatNames = Collections.unmodifiableSet(formatNames);
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    /*
         * (non-Javadoc)
         * @see
         * javax.money.spi.MonetaryAmountFormatProviderSpi#getFormat(javax.money.format.AmountFormatContext)
         */
    @Override
    public Collection<MonetaryAmountFormat> getAmountFormats(AmountFormatQuery amountFormatQuery) {
        Objects.requireNonNull(amountFormatQuery, "AmountFormatContext required");
        if (!amountFormatQuery.getProviderNames().isEmpty() &&
                !amountFormatQuery.getProviderNames().contains(getProviderName())) {
            return Collections.emptySet();
        }
        if (!(amountFormatQuery.getFormatName() == null || DEFAULT_STYLE.equals(amountFormatQuery.getFormatName()))) {
            return Collections.emptySet();
        }
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(DEFAULT_STYLE);
        if (amountFormatQuery.getLocale() != null) {
            builder.setLocale(amountFormatQuery.getLocale());
        }
        builder.importContext(amountFormatQuery, false);
        builder.setMonetaryAmountFactory(amountFormatQuery.getMonetaryAmountFactory());
        return Arrays.asList(new MonetaryAmountFormat[]{new DefaultMonetaryAmountFormat(builder.build())});
    }

    @Override
    public Set<Locale> getAvailableLocales() {
        return supportedSets;
    }

    @Override
    public Set<String> getAvailableFormatNames() {
        return formatNames;
    }

}
