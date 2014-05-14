package org.javamoney.moneta.internal;

import javax.money.CurrencyUnit;
import javax.money.MonetaryContext;
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