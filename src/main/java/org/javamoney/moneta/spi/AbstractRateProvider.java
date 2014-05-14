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
package org.javamoney.moneta.spi;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;
import java.util.logging.Logger;

import javax.money.CurrencyUnit;
import javax.money.MonetaryCurrencies;
import javax.money.NumberValue;
import javax.money.convert.ConversionContext;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.CurrencyConversionException;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;

/**
 * Abstract base class for {@link ExchangeRateProvider} implementations.
 * 
 * @author Anatole Tresch
 * @author Werner Keil
 *
 */
public abstract class AbstractRateProvider implements ExchangeRateProvider {

	/** The logger used. */
	protected final Logger LOGGER = Logger.getLogger(getClass().getName());

	/** The {@link ConversionContext} of this provider. */
	private ProviderContext providerContext;

	/**
	 * Constructor.
	 * 
	 * @param providerContext
	 *            the {@link ProviderContext}, not null.
	 */
	public AbstractRateProvider(ProviderContext providerContext) {
		Objects.requireNonNull(providerContext);
		this.providerContext = providerContext;
	}

	protected abstract ExchangeRate getExchangeRateInternal(CurrencyUnit base,
			CurrencyUnit term, ConversionContext context);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.convert.spi.ExchangeRateProviderSpi#getExchangeRateType
	 * ()
	 */
	@Override
	public ProviderContext getProviderContext() {
		return providerContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#isAvailable(javax.money.CurrencyUnit
	 * , javax.money.CurrencyUnit)
	 */
	@Override
	public boolean isAvailable(CurrencyUnit src, CurrencyUnit target) {
		return getExchangeRate(src, target) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#getExchangeRate(javax.money.
	 * CurrencyUnit, javax.money.CurrencyUnit)
	 */
	@Override
	public ExchangeRate getExchangeRate(CurrencyUnit source, CurrencyUnit target) {
		return getExchangeRate(source, target, ConversionContext.of());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#getReversed(javax.money.convert
	 * .ExchangeRate)
	 */
	@Override
	public ExchangeRate getReversed(ExchangeRate rate) {
		if (isAvailable(rate.getTerm(), rate.getBase(),
				rate.getConversionContext())) {
			return getExchangeRate(rate.getTerm(), rate.getBase(),
					rate.getConversionContext());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#getCurrencyConversion(javax.
	 * money.CurrencyUnit)
	 */
	@Override
	public CurrencyConversion getCurrencyConversion(CurrencyUnit termCurrency) {
		return new LazyBoundCurrencyConversion(termCurrency, this,
				ConversionContext.of());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#getCurrencyConversion(javax.
	 * money.CurrencyUnit, javax.money.convert.ConversionContext)
	 */
	@Override
	public CurrencyConversion getCurrencyConversion(CurrencyUnit term,
			ConversionContext conversionContext) {
		return new LazyBoundCurrencyConversion(term, this, conversionContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#isAvailable(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public boolean isAvailable(String baseCode, String termCode) {
		return isAvailable(MonetaryCurrencies.getCurrency(baseCode),
				MonetaryCurrencies.getCurrency(termCode),
				ConversionContext.of());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#getExchangeRate(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public ExchangeRate getExchangeRate(String baseCode, String termCode) {
		return getExchangeRate(MonetaryCurrencies.getCurrency(baseCode),
				MonetaryCurrencies.getCurrency(termCode),
				ConversionContext.of());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#getCurrencyConversion(java.lang
	 * .String)
	 */
	@Override
	public CurrencyConversion getCurrencyConversion(String termCode) {
		return getCurrencyConversion(MonetaryCurrencies.getCurrency(termCode));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#getCurrencyConversion(java.lang
	 * .String, javax.money.convert.ConversionContext)
	 */
	@Override
	public CurrencyConversion getCurrencyConversion(String termCode,
			ConversionContext conversionContext) {
		return getCurrencyConversion(MonetaryCurrencies.getCurrency(termCode),
				conversionContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#isAvailable(java.lang.String,
	 * java.lang.String, javax.money.convert.ConversionContext)
	 */
	@Override
	public boolean isAvailable(String baseCode, String termCode,
			ConversionContext conversionContext) {
		return isAvailable(MonetaryCurrencies.getCurrency(baseCode),
				MonetaryCurrencies.getCurrency(termCode), conversionContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.money.convert.ExchangeRateProvider#getExchangeRate(java.lang.String
	 * , java.lang.String, javax.money.convert.ConversionContext)
	 */
	@Override
	public ExchangeRate getExchangeRate(String baseCode, String termCode,
			ConversionContext conversionContext) {
		return getExchangeRate(MonetaryCurrencies.getCurrency(baseCode),
				MonetaryCurrencies.getCurrency(termCode), conversionContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javamoney.moneta.convert.internal.AbstractRateProvider#isAvailable
	 * (javax.money.CurrencyUnit, javax.money.CurrencyUnit,
	 * javax.money.convert.ConversionContext)
	 */
	@Override
	public boolean isAvailable(CurrencyUnit base, CurrencyUnit term,
			ConversionContext conversionContext) {
		return getExchangeRateInternal(base, term,
				conversionContext) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javamoney.moneta.convert.internal.AbstractRateProvider#getExchangeRate
	 * (javax.money.CurrencyUnit, javax.money.CurrencyUnit,
	 * javax.money.convert.ConversionContext)
	 */
	@Override
	public ExchangeRate getExchangeRate(CurrencyUnit base, CurrencyUnit term,
			ConversionContext conversionContext) {
		ExchangeRate rate = getExchangeRateInternal(base, term,
				conversionContext);
		if (rate == null) {
			throw new CurrencyConversionException(base, term, conversionContext);
		}
		return rate;
	}
	
	/**
	 * A protected helper method to multiply 2 {@link NumberValue} types.<br>
	 * If either of the values is <code>null</code> an {@link ArithmeticException} is thrown.
	 * 
	 * @param multiplicand the first value to be multiplied
	 * @param multiplier the second value to be multiplied
	 * @return the result of the multiplication as {@link NumberValue}
	 */
	protected static final NumberValue multiply(NumberValue multiplicand, NumberValue multiplier) {
		if (multiplicand == null) {
			throw new ArithmeticException("The multiplicand cannot be null");
		}
		if (multiplier == null) {
			throw new ArithmeticException("The multiplier cannot be null");
		}
		return new DefaultNumberValue(
				multiplicand.numberValue(BigDecimal.class).multiply(
						multiplier.numberValue(BigDecimal.class))); // TODO should we use numberValueExact?
	}
	
	/**
	 * A protected helper method to divide 2 {@link NumberValue} types.<br>
	 * If either of the values is <code>null</code> an {@link ArithmeticException} is thrown.
	 * 
	 * @param dividend the first value to be divided
	 * @param divisor the value to be divided by
	 * @return the result of the division as {@link NumberValue}
	 */
	protected static final NumberValue divide(NumberValue dividend, NumberValue divisor) {
		if (dividend == null) {
			throw new ArithmeticException("The dividend cannot be null");
		}
		if (divisor == null) {
			throw new ArithmeticException("The divisor cannot be null");
		}
		return new DefaultNumberValue(
				dividend.numberValue(BigDecimal.class).divide(
						divisor.numberValue(BigDecimal.class))); // TODO should we use numberValueExact?
	}
	
	/**
	 * A protected helper method to divide 2 {@link NumberValue} types.<br>
	 * If either of the values is <code>null</code> an {@link ArithmeticException} is thrown.
	 * 
	 * @param dividend the first value to be divided
	 * @param divisor the value to be divided by
	 * @param context the {@link MathContext} to use
	 * @return the result of the division as {@link NumberValue}
	 */
	protected static final NumberValue divide(NumberValue dividend, NumberValue divisor, MathContext context) {
		if (dividend == null) {
			throw new ArithmeticException("The dividend cannot be null");
		}
		if (divisor == null) {
			throw new ArithmeticException("The divisor cannot be null");
		}
		return new DefaultNumberValue(
				dividend.numberValue(BigDecimal.class).divide(
						divisor.numberValue(BigDecimal.class), context)); // TODO should we use numberValueExact?
	}
}
