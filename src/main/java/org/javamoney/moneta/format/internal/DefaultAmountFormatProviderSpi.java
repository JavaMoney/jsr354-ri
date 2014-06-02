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
package org.javamoney.moneta.format.internal;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;

import java.text.DecimalFormat;
import java.util.*;

import javax.money.format.AmountFormatContext;
import javax.money.format.MonetaryAmountFormat;
import javax.money.spi.DefaultServiceProvider;
import javax.money.spi.MonetaryAmountFormatProviderSpi;

/**
 * Default format provider, which mainly maps the existing JDK functionality into the JSR 354 logic.
 * 
 * @author Anatole Tresch
 */
public class DefaultAmountFormatProviderSpi implements
		MonetaryAmountFormatProviderSpi {

    private Set<Locale> supportedSets = new HashSet<>();

    public DefaultAmountFormatProviderSpi(){
        supportedSets.addAll(Arrays.asList(DecimalFormat.getAvailableLocales()));
        supportedSets = Collections.unmodifiableSet(supportedSets);
    }

    @Override
    public String getStyleId(){
        return "default";
    }

    /*
         * (non-Javadoc)
         * @see
         * javax.money.spi.MonetaryAmountFormatProviderSpi#getFormat(javax.money.format.AmountFormatContext)
         */
	@Override
	public MonetaryAmountFormat getAmountFormat(AmountFormatContext style) {
		Objects.requireNonNull(style, "AmountFormatContext required");
		return new DefaultMonetaryAmountFormat(style);
	}

    @Override
    public Set<Locale> getAvailableLocales(){
        return supportedSets;
    }

}
