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
package org.javamoney.moneta.internal;

import javax.money.CurrencyUnit;
import javax.money.MonetaryOperator;
import javax.money.RoundingContext;
import javax.money.spi.RoundingProviderSpi;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Set;

/**
 * Defaulr implementation of a {@link javax.money.spi.RoundingProviderSpi} that creates instances of {@link org
 * .javamoney.moneta.internal.DefaultRounding} that relies on the default fraction units defined by {@link java.util
 * .Currency#getDefaultFractionDigits()}.
 */
public class DefaultRoundingProvider implements RoundingProviderSpi{

    public MonetaryOperator getRounding(RoundingContext context){
        if("default".equals(context.getRoundingId())){
            CurrencyUnit currency = context.getCurrencyUnit();
                    // RoundingMode rm = monetaryContext.getAttribute(RoundingMode.class, RoundingMode.HALF_EVEN);
            if(currency!=null){
                if(context.getNamedAttribute("cashRounding", Boolean.class, Boolean.FALSE)){
                    if("CHF".equals(currency.getCurrencyCode())){
                        return new DefaultCashRounding(currency, RoundingMode.HALF_UP,5);
                    }
                }
                return new DefaultRounding(currency);
            }
            Integer scale = context.getNamedAttribute("scale", Integer.class);
            if(scale!=null){
                RoundingMode mode = context.getAttribute(RoundingMode.class,
                                                              RoundingMode.HALF_EVEN);
                return new DefaultRounding(scale, mode);
            }
        }
        return null;
    }


    @Override
    public Set<String> getRoundingIds(){
        return Collections.emptySet();
    }

}