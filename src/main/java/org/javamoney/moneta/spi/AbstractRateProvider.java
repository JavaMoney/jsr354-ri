package org.javamoney.moneta.spi;

import java.util.Objects;
import java.util.logging.Logger;

import javax.money.CurrencyUnit;
import javax.money.MonetaryCurrencies;
import javax.money.convert.ConversionContext;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.CurrencyConversionException;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;

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
	 * org.javamoney.moneta.conversion.internal.AbstractRateProvider#isAvailable
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
	 * org.javamoney.moneta.conversion.internal.AbstractRateProvider#getExchangeRate
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

}
