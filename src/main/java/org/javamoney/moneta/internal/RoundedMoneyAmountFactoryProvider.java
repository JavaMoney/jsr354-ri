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

import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryContext;
import javax.money.spi.MonetaryAmountFactoryProviderSpi;

import org.javamoney.moneta.RoundedMoney;

/**
 * Implementation of {@link MonetaryAmountFactoryProviderSpi} creating instances of
 * {@link RoundedMoneyAmountBuilder}.
 *
 * @author Anatole Tresch
 */
public final class RoundedMoneyAmountFactoryProvider implements MonetaryAmountFactoryProviderSpi<RoundedMoney>{

    @Override
    public Class<RoundedMoney> getAmountType(){
        return RoundedMoney.class;
    }

    @Override
    public MonetaryAmountFactory<RoundedMoney> createMonetaryAmountFactory(){
        return new RoundedMoneyAmountBuilder();
    }

    /*
     * (non-Javadoc)
     * @see javax.money.spi.MonetaryAmountFactoryProviderSpi#getQueryInclusionPolicy()
     */
    @Override
    public QueryInclusionPolicy getQueryInclusionPolicy(){
        return QueryInclusionPolicy.DIRECT_REFERENCE_ONLY;
    }

    @Override
    public MonetaryContext getDefaultMonetaryContext(){
        return RoundedMoneyAmountBuilder.DEFAULT_CONTEXT;
    }

    @Override
    public MonetaryContext getMaximalMonetaryContext(){
        return RoundedMoneyAmountBuilder.MAX_CONTEXT;
    }

}
