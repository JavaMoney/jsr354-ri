/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency
 * API ("Specification") Copyright (c) 2012-2013, Credit Suisse All rights
 * reserved.
 */
package javax.money.function;

import static java.text.NumberFormat.getPercentInstance;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Locale;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

/**
 * This class allows to extract the percentage of a {@link MonetaryAmount}
 * instance.
 * 
 * @version 0.5
 * @author Werner Keil
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Percent">Wikipedia: Percentage</a>
 */
final class Percent<T extends MonetaryAmount> implements MonetaryOperator {

	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100,
			MathContext.DECIMAL64);

	private final BigDecimal percentValue;

	/**
	 * Access the shared instance of {@link Percent} for use.
	 * 
	 * @return the shared instance, never {@code null}.
	 */
	Percent(final BigDecimal decimal) {
		percentValue = calcPercent(decimal);
	}

	/**
	 * Gets the percentage of the amount.
	 * <p>
	 * This returns the monetary amount in percent. For example, for 10% 'EUR
	 * 2.35' will return 0.235.
	 * <p>
	 * This is returned as a {@code MonetaryAmount}.
	 * 
	 * @return the percent result of the amount, never {@code null}
	 */
	@Override
	public MonetaryAmount apply(MonetaryAmount amount) {
		return amount.multiply(percentValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getPercentInstance().format(percentValue);
	}

	/**
	 * Format the percentage for a locale.
	 * 
	 * @param locale
	 *            the target locale
	 */
	public String getDisplayName(Locale locale) {
		return getPercentInstance(locale).format(percentValue);
	}

	/**
	 * Calculate a BigDecimal value for a Percent e.g. "3" (3 percent) will
	 * generate .03
	 * 
	 * @return java.math.BigDecimal
	 * @param decimal
	 *            java.math.BigDecimal
	 */
	private static final BigDecimal calcPercent(BigDecimal decimal) {
		return decimal.divide(ONE_HUNDRED, MathContext.DECIMAL64); // we now
																	// have
	}

}
