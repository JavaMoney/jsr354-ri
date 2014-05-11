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

import javax.money.AmountFlavor;
import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.MonetaryException;
import javax.money.spi.Bootstrap;
import javax.money.spi.MonetaryAmountFactoryProviderSpi;
import javax.money.spi.MonetaryAmountFactoryProviderSpi.QueryInclusionPolicy;
import javax.money.spi.MonetaryAmountsSingletonQuerySpi;
import javax.money.spi.MonetaryAmountsSingletonSpi;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Default implementation ot {@link javax.money.spi.MonetaryAmountsSingletonSpi} loading the SPIs on startup
 * initially once, using the
 * JSR's {@link javax.money.spi.Bootstrap} mechanism.
 */
public class DefaultMonetaryAmountsSingletonQuerySpi implements MonetaryAmountsSingletonQuerySpi{

    private static final Comparator<MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount>> CONTEXT_COMPARATOR =
            new Comparator<MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount>>(){

                @Override
                public int compare(MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount> f1,
                                   MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount> f2){
                    int compare = 0;
                    MonetaryContext c1 = f1.getMaximalMonetaryContext();
                    MonetaryContext c2 = f2.getMaximalMonetaryContext();
                    if(c1.getAmountFlavor() == AmountFlavor.PRECISION &&
                            c2.getAmountFlavor() != AmountFlavor.PRECISION){
                        compare = -1;
                    }
                    if(compare == 0 && c2.getAmountFlavor() == AmountFlavor.PRECISION &&
                            c1.getAmountFlavor() != AmountFlavor.PRECISION){
                        compare = 1;
                    }
                    if(compare == 0 && c1.getPrecision() == 0 && c2.getPrecision() != 0){
                        compare = -1;
                    }
                    if(compare == 0 && c2.getPrecision() == 0 && c1.getPrecision() != 0){
                        compare = 1;
                    }
                    if(compare == 0 && (c1.getMaxScale() > c2.getMaxScale())){
                        compare = -1;
                    }
                    if(compare == 0 && (c1.getMaxScale() < c2.getMaxScale())){
                        compare = 1;
                    }
                    return compare;
                }
            };


    /**
     * (non-Javadoc)
     *
     * @see javax.money.spi.MonetaryAmountsSingletonQuerySpi#queryAmountType(javax.money.spi.MonetaryAmountsSingletonSpi, javax.money.MonetaryContext)
     */
    @Override
    public Class<? extends MonetaryAmount> queryAmountType(MonetaryAmountsSingletonSpi amountSpi, MonetaryContext requiredContext){
        if(requiredContext == null){
            return amountSpi.getDefaultAmountType();
        }
        // first check for explicit type
        for(@SuppressWarnings("unchecked") MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount> f : Bootstrap
                .getServices(MonetaryAmountFactoryProviderSpi.class)){
            if(f.getQueryInclusionPolicy() == QueryInclusionPolicy.NEVER){
                continue;
            }
            if(requiredContext.getAmountType() == f.getAmountType()){
                if(isPrecisionOK(requiredContext, f.getMaximalMonetaryContext())){
                    return f.getAmountType();
                }else{
                    throw new MonetaryException("Incompatible context required=" + requiredContext + ", maximal=" +
                                                        f.getMaximalMonetaryContext()
                    );
                }
            }
        }
        // Select on required flavor
        List<MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount>> selection = new ArrayList<>();
        for(@SuppressWarnings("unchecked") MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount> f : Bootstrap
                .getServices(MonetaryAmountFactoryProviderSpi.class)){
            if(f.getDefaultMonetaryContext().getAmountFlavor() == AmountFlavor.UNDEFINED){
                if(f.getQueryInclusionPolicy() == QueryInclusionPolicy.DIRECT_REFERENCE_ONLY ||
                        f.getQueryInclusionPolicy() == QueryInclusionPolicy.NEVER){
                    continue;
                }
                if(isPrecisionOK(requiredContext, f.getMaximalMonetaryContext())){
                    selection.add(f);
                }
            }else if(requiredContext.getAmountFlavor() == f.getDefaultMonetaryContext().getAmountFlavor()){
                if(isPrecisionOK(requiredContext, f.getMaximalMonetaryContext())){
                    selection.add(f);
                }
            }
        }
        if(selection.isEmpty()){
            // fall back, add all selections, ignore flavor
            for(@SuppressWarnings("unchecked") MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount> f : Bootstrap
                    .getServices(MonetaryAmountFactoryProviderSpi.class)){
                if(f.getQueryInclusionPolicy() == QueryInclusionPolicy.DIRECT_REFERENCE_ONLY ||
                        f.getQueryInclusionPolicy() == QueryInclusionPolicy.NEVER){
                    continue;
                }
                if(isPrecisionOK(requiredContext, f.getMaximalMonetaryContext())){
                    selection.add(f);
                }
            }
        }
        if(selection.size() == 1){
            return selection.get(0).getAmountType();
        }else{
            // several matches, check for required flavor
            for(@SuppressWarnings(
                    "unchecked") MonetaryAmountFactoryProviderSpi<? extends MonetaryAmount> f : selection){
                if(f.getDefaultMonetaryContext().getAmountFlavor().equals(requiredContext.getAmountFlavor())){
                    return f.getAmountType();
                }
            }
        }
        Collections.sort(selection, CONTEXT_COMPARATOR);
        return selection.get(0).getAmountType();
    }

    private boolean isPrecisionOK(MonetaryContext requiredContext, MonetaryContext maximalMonetaryContext){
        if(maximalMonetaryContext.getPrecision() == 0){
            return true;
        }
        if(requiredContext.getPrecision() == 0 && maximalMonetaryContext.getPrecision() != 0){
            return false;
        }
        if(requiredContext.getPrecision() > maximalMonetaryContext.getPrecision()){
            return false;
        }
        if(requiredContext.getMaxScale() > maximalMonetaryContext.getMaxScale()){
            return false;
        }
        return true;
    }

}
