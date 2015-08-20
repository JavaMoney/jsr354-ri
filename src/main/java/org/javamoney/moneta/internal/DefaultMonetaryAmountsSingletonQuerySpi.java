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

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryAmountFactoryQuery;
import javax.money.MonetaryContext;
import javax.money.MonetaryContextBuilder;
import javax.money.MonetaryException;
import javax.money.spi.Bootstrap;
import javax.money.spi.MonetaryAmountFactoryProviderSpi;
import javax.money.spi.MonetaryAmountFactoryProviderSpi.QueryInclusionPolicy;
import javax.money.spi.MonetaryAmountsSingletonQuerySpi;

/**
 * Default implementation ot {@link javax.money.spi.MonetaryAmountsSingletonSpi} loading the SPIs on startup
 * initially once, using the
 * JSR's {@link javax.money.spi.Bootstrap} mechanism.
 */
public class DefaultMonetaryAmountsSingletonQuerySpi implements MonetaryAmountsSingletonQuerySpi{

    private static final Comparator<MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount>> CONTEXT_COMPARATOR =
            (f1, f2) -> {
                int compare = 0;
                MonetaryContext c1 = f1.getMaximalMonetaryContext();
                MonetaryContext c2 = f2.getMaximalMonetaryContext();
                if(c1.getPrecision() == 0 && c2.getPrecision() != 0){
                    compare = -1;
                }
                if(compare == 0 && c2.getPrecision() == 0 && c1.getPrecision() != 0){
                    compare = 1;
                }
                if(compare == 0 && c1.getPrecision() != 0 && c2.getPrecision() > c1.getPrecision()){
                    compare = 1;
                }
                if(compare == 0 && c2.getPrecision() != 0 && c2.getPrecision() < c1.getPrecision()){
                    compare = -1;
                }
                if(compare == 0 && (c1.getMaxScale() > c2.getMaxScale())){
                    compare = -1;
                }
                if(compare == 0 && (c1.getMaxScale() < c2.getMaxScale())){
                    compare = 1;
                }
                return compare;
            };


    /**
     * (non-Javadoc)
     *
     * @see javax.money.spi.MonetaryAmountsSingletonQuerySpi#getAmountFactories(javax.money.MonetaryAmountFactoryQuery)
     */
    @Override
    public Collection<MonetaryAmountFactory<?>> getAmountFactories(MonetaryAmountFactoryQuery factoryQuery){
        Objects.requireNonNull(factoryQuery);
        List<MonetaryAmountFactory<?>> factories = new ArrayList<>();
        // first check for explicit type
        for(@SuppressWarnings("unchecked") MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount> factory : Bootstrap
                .getServices(MonetaryAmountFactoryProviderSpi.class)){
            if(factory.getQueryInclusionPolicy() == QueryInclusionPolicy.NEVER){
                continue;
            }
            if(factoryQuery.getTargetType() == factory.getAmountType()){
                if(isPrecisionOK(factoryQuery, factory.getMaximalMonetaryContext())){
                    factories.add(factory.createMonetaryAmountFactory());
                }else{
                    throw new MonetaryException("Incompatible context required=" + factoryQuery + ", maximal=" +
                                                        factory.getMaximalMonetaryContext());
                }
            }
        }
        List<MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount>> selection = new ArrayList<>();
        for(@SuppressWarnings("unchecked") MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount> factory : Bootstrap
                .getServices(MonetaryAmountFactoryProviderSpi.class)){
            if(factory.getQueryInclusionPolicy() == QueryInclusionPolicy.DIRECT_REFERENCE_ONLY ||
                    factory.getQueryInclusionPolicy() == QueryInclusionPolicy.NEVER){
                continue;
            }
            if(isPrecisionOK(factoryQuery, factory.getMaximalMonetaryContext())){
                selection.add(factory);
            }
        }
        if(selection.isEmpty()){
            // fall back, add all selections, ignore flavor
            for(@SuppressWarnings("unchecked") MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount> factory : Bootstrap
                    .getServices(MonetaryAmountFactoryProviderSpi.class)){
                if(factory.getQueryInclusionPolicy() == QueryInclusionPolicy.DIRECT_REFERENCE_ONLY ||
                        factory.getQueryInclusionPolicy() == QueryInclusionPolicy.NEVER){
                    continue;
                }
                if(isPrecisionOK(factoryQuery, factory.getMaximalMonetaryContext())){
                    selection.add(factory);
                }
            }
        }
        if(selection.size() == 1){
            factories.add(selection.get(0).createMonetaryAmountFactory());
        }

		MonetaryContext context = createContext(factoryQuery);

		factories.forEach(f -> f.setContext(context));
        Collections.sort(selection, CONTEXT_COMPARATOR);
        factories.add(selection.get(0).createMonetaryAmountFactory());
        return factories;
    }

	private MonetaryContext createContext(MonetaryAmountFactoryQuery factoryQuery) {
		MonetaryContextBuilder contextBuilder = MonetaryContextBuilder.of();

		if (Objects.nonNull(factoryQuery.getPrecision())) {
			contextBuilder.setPrecision(factoryQuery.getPrecision());
		}
		if (Objects.nonNull(factoryQuery.get(RoundingMode.class))) {
			contextBuilder.set(factoryQuery.get(RoundingMode.class));
		}
		return contextBuilder.build();
	}

    private boolean isPrecisionOK(MonetaryAmountFactoryQuery requiredContext, MonetaryContext maxMonetaryContext){
        if(maxMonetaryContext.getPrecision() == 0){
            return true;
        }
        if(requiredContext.getPrecision() != null){
            if(requiredContext.getPrecision() == 0){
                return false;
            }
            if(requiredContext.getPrecision() > maxMonetaryContext.getPrecision()){
                return false;
            }
        }
        return null == requiredContext.getMaxScale() ||
                requiredContext.getMaxScale() <= maxMonetaryContext.getMaxScale();
    }

}
