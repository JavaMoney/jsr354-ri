/*
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
package org.javamoney.moneta.spi;

import static org.javamoney.moneta.spi.AbstractCurrencyConversion.KEY_SCALE;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import javax.money.NumberValue;
import javax.money.convert.ConversionContext;
import javax.money.convert.ConversionQuery;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;
import javax.money.convert.RateType;

/**
 * Abstract base class for {@link ExchangeRateProvider} implementations.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public abstract class AbstractRateProvider implements ExchangeRateProvider {

    /**
     * The {@link ConversionContext} of this provider.
     */
    private final ProviderContext context;

    @Deprecated
    protected final Logger log = Logger.getLogger(getClass().getName());

    /**
     * Constructor.
     *
     * @param providerContext the {@link ProviderContext}, not null.
     */
    public AbstractRateProvider(ProviderContext providerContext) {
        Objects.requireNonNull(providerContext);
        this.context = providerContext;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.money.convert.spi.ExchangeRateProviderSpi#getExchangeRateType
     * ()
     */
    @Override
    public ProviderContext getContext() {
        return context;
    }

    @Override
    public abstract ExchangeRate getExchangeRate(ConversionQuery conversionQuery);

    @Override
    public CurrencyConversion getCurrencyConversion(ConversionQuery conversionQuery) {
        if (getContext().getRateTypes().size() == 1) {
            return new LazyBoundCurrencyConversion(conversionQuery, this, ConversionContext
                    .of(getContext().getProviderName(), getContext().getRateTypes().iterator().next()));
        }
        return new LazyBoundCurrencyConversion(conversionQuery, this,
                ConversionContext.of(getContext().getProviderName(), RateType.ANY));
    }


    /**
     * A protected helper method to multiply 2 {@link NumberValue} types.<br>
     * If either of the values is <code>null</code> an {@link ArithmeticException} is thrown.
     *
     * @param multiplicand the first value to be multiplied
     * @param multiplier   the second value to be multiplied
     * @return the result of the multiplication as {@link NumberValue}
     */
    protected static NumberValue multiply(NumberValue multiplicand, NumberValue multiplier) {
        if (Objects.isNull(multiplicand)) {
            throw new ArithmeticException("The multiplicand cannot be null");
        }
        if (Objects.isNull(multiplier)) {
            throw new ArithmeticException("The multiplier cannot be null");
        }
        return new DefaultNumberValue(
                multiplicand.numberValueExact(BigDecimal.class).multiply(multiplier.numberValue(BigDecimal.class)));
    }

    /**
     * A protected helper method to divide 2 {@link NumberValue} types.<br>
     * If either of the values is <code>null</code> an {@link ArithmeticException} is thrown.
     *
     * @param dividend the first value to be divided
     * @param divisor  the value to be divided by
     * @return the result of the division as {@link NumberValue}
     */
    protected static NumberValue divide(NumberValue dividend, NumberValue divisor) {
        if (Objects.isNull(dividend)) {
            throw new ArithmeticException("The dividend cannot be null");
        }
        if (Objects.isNull(divisor)) {
            throw new ArithmeticException("The divisor cannot be null");
        }
        return new DefaultNumberValue(
                dividend.numberValueExact(BigDecimal.class).divide(divisor.numberValue(BigDecimal.class),
                        MathContext.DECIMAL64));
    }

    /**
     * A protected helper method to divide 2 {@link NumberValue} types.<br>
     * If either of the values is <code>null</code> an {@link ArithmeticException} is thrown.
     *
     * @param dividend the first value to be divided
     * @param divisor  the value to be divided by
     * @param context  the {@link MathContext} to use
     * @return the result of the division as {@link NumberValue}
     */
    protected static NumberValue divide(NumberValue dividend, NumberValue divisor, MathContext context) {
        if (Objects.isNull(dividend)) {
            throw new ArithmeticException("The dividend cannot be null");
        }
        if (Objects.isNull(divisor)) {
            throw new ArithmeticException("The divisor cannot be null");
        }
        return new DefaultNumberValue(
                dividend.numberValueExact(BigDecimal.class).divide(divisor.numberValue(BigDecimal.class), context));
    }

    protected int getScale(String key) {
        String string = MonetaryConfig.getConfig().get(key);
        if (string == null || string.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    protected ConversionContext getExchangeContext(String key) {
		int scale = getScale(key);
        if(scale < 0) {
          return ConversionContext.of(this.context.getProviderName(), RateType.HISTORIC);
        } else {
        	return ConversionContext.of(this.context.getProviderName(), RateType.HISTORIC).toBuilder().set(KEY_SCALE, scale).build();
        }
	}

    protected LocalDate[] getQueryDates(ConversionQuery query) {

        if (Objects.nonNull(query.get(LocalDate.class)) || Objects.nonNull(query.get(LocalDateTime.class))) {
        	LocalDate localDate = Optional.ofNullable(query.get(LocalDate.class)).orElseGet(() -> query.get(LocalDateTime.class).toLocalDate());
        	return new LocalDate[]{localDate};
        } else if(Objects.nonNull(query.get(LocalDate[].class))) {
        	return query.get(LocalDate[].class);
        }
        return null;
    }
}
