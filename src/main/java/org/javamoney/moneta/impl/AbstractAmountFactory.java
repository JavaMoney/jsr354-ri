package org.javamoney.moneta.impl;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.MonetaryAmounts;
import javax.money.MonetaryContext;
import javax.money.MonetaryCurrencies;
import javax.money.MonetaryException;
import javax.money.UnknownCurrencyException;

public abstract class AbstractAmountFactory<T extends MonetaryAmount<T>>
		implements MonetaryAmountFactory<T> {

	/**
	 * The default {@link MonetaryContext} applied, if not set explicitly on
	 * creation.
	 */
	private MonetaryContext<T> DEFAULT_MONETARY_CONTEXT = loadDefaultMonetaryContext();

	/**
	 * The default {@link MonetaryContext} applied, if not set explicitly on
	 * creation.
	 */
	private MonetaryContext<T> MAX_MONETARY_CONTEXT = loadMaxMonetaryContext();

	/**
	 * Creates a new instance of {@link MonetaryAmount}, using the default
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currency
	 *            currency unit, not {@code null}.
	 * @return a {@code MonetaryAmount} combining the numeric value and currency
	 *         unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the default
	 *             {@link MonetaryContext} used.
	 */
	@Override
	public T getAmount(CurrencyUnit currency, long number) {
		return getAmount(currency, number, DEFAULT_MONETARY_CONTEXT);
	}

	protected abstract MonetaryContext<T> loadDefaultMonetaryContext();

	protected abstract MonetaryContext<T> loadMaxMonetaryContext();

	/**
	 * Creates a new instance of {@link MonetaryAmount}, using the default
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currency
	 *            currency unit, not {@code null}.
	 * @return a {@code MonetaryAmount} combining the numeric value and currency
	 *         unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the default
	 *             {@link MonetaryContext} used.
	 */
	@Override
	public T getAmount(CurrencyUnit currency,
			double number) {
		return getAmount(currency, number, DEFAULT_MONETARY_CONTEXT);
	}

	/**
	 * Creates a new instance of {@link MonetaryAmount}, using the default
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currency
	 *            currency unit, not {@code null}.
	 * @return a {@code MonetaryAmount} combining the numeric value and currency
	 *         unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the default
	 *             {@link MonetaryContext} used.
	 */
	@Override
	public T getAmount(CurrencyUnit currency,
			Number number) {
		return getAmount(currency, number, DEFAULT_MONETARY_CONTEXT);
	}

	/**
	 * Creates a new instance of {@link MonetaryAmounts}, using the default
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currencyCode
	 *            currency code, not {@code null}.
	 * @return a {@code Money} combining the numeric value and currency unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the default
	 *             {@link MonetaryContext} used.
	 * @throws UnknownCurrencyException
	 *             if the currency code can not be resolved to
	 *             {@link CurrencyUnit}.
	 */
	@Override
	public T getAmount(String currencyCode, long number) {
		return getAmount(MonetaryCurrencies.getCurrency(currencyCode), number,
				DEFAULT_MONETARY_CONTEXT);
	}

	/**
	 * Creates a new instance of {@link MonetaryAmounts}, using the default
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currencyCode
	 *            currency code, not {@code null}.
	 * @return a {@code Money} combining the numeric value and currency unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the default
	 *             {@link MonetaryContext} used.
	 * @throws UnknownCurrencyException
	 *             if the currency code can not be resolved to
	 *             {@link CurrencyUnit}.
	 */
	@Override
	public T getAmount(String currencyCode, double number) {
		return getAmount(MonetaryCurrencies.getCurrency(currencyCode), number,
				DEFAULT_MONETARY_CONTEXT);
	}

	/**
	 * Creates a new instance of {@link MonetaryAmounts}, using the default
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currencyCode
	 *            currency code, not {@code null}.
	 * @return a {@code Money} combining the numeric value and currency unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the default
	 *             {@link MonetaryContext} used.
	 * @throws UnknownCurrencyException
	 *             if the currency code can not be resolved to
	 *             {@link CurrencyUnit}.
	 */
	@Override
	public T getAmount(String currencyCode, Number number) {
		return getAmount(MonetaryCurrencies.getCurrency(currencyCode), number,
				DEFAULT_MONETARY_CONTEXT);
	}

	/**
	 * Creates a new instance of {@link MonetaryAmounts}, using the default
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currency
	 *            currency unit, not {@code null}.
	 * @param context
	 *            the {@link MonetaryContext} required.
	 * @return a {@code Money} combining the numeric value and currency unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the default
	 *             {@link MonetaryContext} used.
	 * @throws UnknownCurrencyException
	 *             if the currency code can not be resolved to
	 *             {@link CurrencyUnit}.
	 */
	@Override
	public T getAmount(String currencyCode, long number,
			MonetaryContext<?> context) {
		return getAmount(MonetaryCurrencies.getCurrency(currencyCode), number,
				context);
	}

	/**
	 * Creates a new instance of {@link MonetaryAmounts}, using the default
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currencyCode
	 *            currency unit, not {@code null}.
	 * @param context
	 *            the {@link MonetaryContext} required.
	 * @return a {@code Money} combining the numeric value and currency unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the default
	 *             {@link MonetaryContext} used.
	 * @throws UnknownCurrencyException
	 *             if the currency code can not be resolved to
	 *             {@link CurrencyUnit}.
	 */
	@Override
	public T getAmount(String currencyCode,
			double number,
			MonetaryContext<?> context) {
		CurrencyUnit currency = MonetaryCurrencies.getCurrency(currencyCode);
		return getAmount(currency, number, context);
	}

	/**
	 * Creates a new instance of {@link MonetaryAmounts}, using the default
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currencyCode
	 *            currency unit, not {@code null}.
	 * @param context
	 *            the {@link MonetaryContext} required.
	 * @return a {@code Money} combining the numeric value and currency unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the default
	 *             {@link MonetaryContext} used.
	 * @throws UnknownCurrencyException
	 *             if the currency code can not be resolved to
	 *             {@link CurrencyUnit}.
	 */
	@Override
	public T getAmount(String currencyCode,
			Number number,
			MonetaryContext<?> monetaryContext) {
		CurrencyUnit currency = MonetaryCurrencies.getCurrency(currencyCode);
		return getAmount(currency, number, monetaryContext);
	}

	/**
	 * Creates a new instance of {@link MonetaryAmounts}, using an explicit
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currency
	 *            currency unit, not {@code null}.
	 * @param monetaryContext
	 *            the {@link MonetaryContext} to be used, if {@code null} the
	 *            default {@link MonetaryContext} is used.
	 * @return a {@code Money} instance based on the monetary context with the
	 *         given numeric value, currency unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the
	 *             {@link MonetaryContext} used.
	 */
	@Override
	public abstract T getAmount(CurrencyUnit currency,
			long number,
			MonetaryContext<?> monetaryContext);

	/**
	 * Creates a new instance of {@link MonetaryAmounts}, using an explicit
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currency
	 *            currency unit, not {@code null}.
	 * @param monetaryContext
	 *            the {@link MonetaryContext} to be used, if {@code null} the
	 *            default {@link MonetaryContext} is used.
	 * @return a {@code Money} instance based on the monetary context with the
	 *         given numeric value, currency unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the
	 *             {@link MonetaryContext} used.
	 */
	@Override
	public abstract T getAmount(CurrencyUnit currency,
			double number,
			MonetaryContext<?> monetaryContext);

	/**
	 * Creates a new instance of {@link MonetaryAmounts}, using an explicit
	 * {@link MonetaryContext}.
	 * 
	 * @param number
	 *            numeric value, not {@code null}.
	 * @param currency
	 *            currency unit, not {@code null}.
	 * @param monetaryContext
	 *            the {@link MonetaryContext} to be used, if {@code null} the
	 *            default {@link MonetaryContext} is used.
	 * @return a {@code Money} instance based on the monetary context with the
	 *         given numeric value, currency unit.
	 * @throws ArithmeticException
	 *             If the number exceeds the capabilities of the
	 *             {@link MonetaryContext} used.
	 */
	@Override
	public abstract T getAmount(CurrencyUnit currency,
			Number number,
			MonetaryContext<?> monetaryContext);

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency 
	 * 			the target {@link CurrencyUnit} of the {@link MonetaryAmount} being created, not {@code null}.
	 * @return a new Money instance of zero, with a default {@link MonetaryContext}.
	 */
	@Override
	public T getAmountZero(CurrencyUnit currency) {
		return getAmount(currency, BigDecimal.ZERO, DEFAULT_MONETARY_CONTEXT);
	}

/**
	 * Factory method creating a zero instance with the given {@code currencyCode);
	 * @param currencyCode
	 * 			the currency code to determine the {@link CurrencyUnit} of the {@link MonetaryAmount} being created.
	 * @return a new Money instance of zero, with a default {@link MonetaryContext}.
	 * @throws UnknownCurrencyException
	 *             if the currency code can not be resolved to
	 *             {@link CurrencyUnit}.
	 */
	@Override
	public T getAmountZero(
			String currencyCode) {
		return getAmount(MonetaryCurrencies.getCurrency(currencyCode),
				BigDecimal.ZERO,
				DEFAULT_MONETARY_CONTEXT);
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currency 
	 * 			the target currency of the amount being created, not {@code null}.
	 * @return a new Money instance of zero, with a default {@link MonetaryContext}.
	 */
	@Override
	public T getAmountZero(CurrencyUnit currency,
			MonetaryContext<?> monetaryContext) {
		return getAmount(currency, BigDecimal.ZERO, monetaryContext);
	}

/**
	 * Factory method creating a zero instance with the given {@code currency);
	 * @param currencyCode
	 * 			the target currency code to determine the {@link CurrencyUnit} of the {@link MonetaryAmount} being created.
	 * @return a new {@link MonetaryAmount} instance of zero, with a default {@link MonetaryContext}.
	 * @throws UnknownCurrencyException
	 *             if the currency code can not be resolved to
	 *             {@link CurrencyUnit}.
	 */
	@Override
	public T getAmountZero(String currencyCode,
			MonetaryContext<?> monetaryContext) {
		return getAmount(MonetaryCurrencies.getCurrency(currencyCode),
				BigDecimal.ZERO,
				monetaryContext);
	}

	/**
	 * Returns the default {@link MonetaryContext} used, when no
	 * {@link MonetaryContext} is provided.
	 * 
	 * @return the default {@link MonetaryContext}, never {@code null}.
	 */
	@Override
	public MonetaryContext<T> getDefaultMonetaryContext() {
		return DEFAULT_MONETARY_CONTEXT;
	}

	/**
	 * Returns the maximal {@link MonetaryContext} supported.
	 * 
	 * @return the maximal {@link MonetaryContext}, never {@code null}.
	 */
	@Override
	public MonetaryContext<T> getMaximalMonetaryContext() {
		return MAX_MONETARY_CONTEXT;
	}

	/**
	 * Converts (if necessary) the given {@link MonetaryAmount} to a new
	 * {@link MonetaryAmount} instance, hereby supporting the
	 * {@link MonetaryContext} given.
	 * 
	 * @param amt
	 *            the amount to be converted, if necessary.
	 * @param monetaryContext
	 *            the {@link MonetaryContext} to be used, if {@code null} the
	 *            default {@link MonetaryContext} is used.
	 * @return an according Money instance.
	 */
	@Override
	public T getAmountFrom(
			MonetaryAmount<?> amt,
			MonetaryContext<?> monetaryContext) {
		throw new MonetaryException(
				"Unsupported MonetaryAmount requested: "
						+ amt + "(" + monetaryContext + ")");
	}

	// private methods

	// /**
	// * Loader method, executed on startup once.
	// *
	// * @return the {@link CurrencyProviderSpi} loaded.
	// */
	// private static Collection<MonetaryAmountProviderSpi>
	// loadMonetaryAmountProviderSpis() {
	// List<MonetaryAmountProviderSpi> spis = new
	// ArrayList<MonetaryAmountProviderSpi>();
	// try {
	// for (MonetaryAmountProviderSpi spi : ServiceLoader
	// .load(MonetaryAmountProviderSpi.class)) {
	// spis.add(spi);
	// }
	// } catch (Exception e) {
	// Logger.getLogger(MonetaryAmounts.class.getName()).log(
	// Level.SEVERE,
	// "Error loading CurrencyProviderSpi instances.", e);
	// return null;
	// }
	// Collections.sort(spis, new PriorityComparator());
	// return spis;
	// }

	// /**
	// * Evaluates the default {@link MonetaryContext} to be used for the
	// * {@link MonetaryAmounts} singleton. The default {@link MonetaryContext}
	// * can be configured by adding a file {@code /javamoney.properties} from
	// the
	// * classpath with the following content:
	// *
	// * <pre>
	// * # Default MathContext for Money
	// * #-------------------------------
	// * # Custom MathContext, overrides entries from
	// org.javamoney.moneta.Money.mathContext
	// * # RoundingMode hereby is optional (default = HALF_EVEN)
	// * Money.defaults.precision=256
	// * Money.defaults.scale=-1
	// * Money.defaults.type=java.math.BigDecimal
	// * Money.attributes.java.math.RoundingMode=RoundingMode.HALF_EVEN
	// * </pre>
	// *
	// * @TODO implement the concept as outlined above...
	// * @return default MonetaryContext, never {@code null}.
	// */
	// private static MonetaryContext<?> initDefaultMathContext() {
	// InputStream is = null;
	// try {
	// Properties props = new Properties();
	// URL url = MonetaryAmounts.class
	// .getResource("/javamoney.properties");
	// if (url != null) {
	// is = url
	// .openStream();
	// props.load(is);
	// String value = props
	// .getProperty("org.javamoney.moneta.Money.defaults.precision");
	// if (value != null) {
	// int prec = Integer.parseInt(value);
	// value = props
	// .getProperty("org.javamoney.moneta.Money.defaults.roundingMode");
	// RoundingMode rm = value != null ? RoundingMode
	// .valueOf(value
	// .toUpperCase(Locale.ENGLISH))
	// : RoundingMode.HALF_UP;
	// MonetaryContext<?> mc = new MonetaryContext.Builder().setPrecision(prec)
	// .setAttribute(rm).build();
	// Logger.getLogger(MonetaryAmounts.class.getName()).info(
	// "Using custom MathContext: precision=" + prec
	// + ", roundingMode=" + rm);
	// return mc;
	// }
	// else {
	// MonetaryContext.Builder builder = new MonetaryContext.Builder();
	// value = props
	// .getProperty("org.javamoney.moneta.Money.defaults.mathContext");
	// if (value != null) {
	// switch (value.toUpperCase(Locale.ENGLISH)) {
	// case "DECIMAL32":
	// Logger.getLogger(MonetaryAmounts.class.getName())
	// .info(
	// "Using MathContext.DECIMAL32");
	// builder.setAttribute(MathContext.DECIMAL32);
	// break;
	// case "DECIMAL64":
	// Logger.getLogger(MonetaryAmounts.class.getName())
	// .info(
	// "Using MathContext.DECIMAL64");
	// builder.setAttribute(MathContext.DECIMAL64);
	// break;
	// case "DECIMAL128":
	// Logger.getLogger(MonetaryAmounts.class.getName())
	// .info(
	// "Using MathContext.DECIMAL128");
	// builder.setAttribute(MathContext.DECIMAL128);
	// break;
	// case "UNLIMITED":
	// Logger.getLogger(MonetaryAmounts.class.getName())
	// .info(
	// "Using MathContext.UNLIMITED");
	// builder.setAttribute(MathContext.UNLIMITED);
	// break;
	// }
	// }
	// return builder.build();
	// }
	// }
	// MonetaryContext.Builder builder = new MonetaryContext.Builder();
	// Logger.getLogger(MonetaryAmounts.class.getName()).info(
	// "Using default MathContext.DECIMAL64");
	// builder.setAttribute(MathContext.DECIMAL64);
	// return builder.build();
	// } catch (Exception e) {
	// Logger.getLogger(MonetaryAmounts.class.getName())
	// .log(Level.SEVERE,
	// "Error evaluating default NumericContext, using default (NumericContext.NUM64).",
	// e);
	// return new MonetaryContext.Builder().setAttribute(MathContext.DECIMAL64)
	// .build();
	// } finally {
	// if (is != null) {
	// try {
	// is.close();
	// } catch (IOException e) {
	// Logger.getLogger(MonetaryAmounts.class.getName())
	// .log(Level.WARNING,
	// "Error closing InputStream after evaluating default NumericContext.",
	// e);
	// }
	// }
	// }
	// }

}
