/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE.
 * 
 * Specification: JSR-354 Money and Currency API ("Specification")
 * 
 * Copyright (c) 2012-2013, Credit Suisse All rights reserved.
 */
package javax.money.adapt;

import java.io.Serializable;
import java.util.Currency;

import javax.money.CurrencyUnit;

/**
 * Default implementation of a {@link CurrencyUnit} based on the using the JDK's
 * {@link MoneyCurrency}, but also extendable using a {@link Builder} instance.
 * 
 * @version 0.5.1
 * @author Anatole Tresch
 * @author Werner Keil
 */
final class JDKCurrencyAdapter implements CurrencyUnit, Serializable,
		Comparable<CurrencyUnit> {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -798486953910548549L;

	/** JDK currency instance. */
	private Currency baseCurrency;

	/**
	 * Private constructor, use a {@link Builder} for creating new instances.
	 * 
	 * @param code
	 *            the currency code, not {@code null} or empty.
	 * @param numCode
	 *            the numeric code, >= -1.
	 * @param fractionDigits
	 *            the fraction digits, >= -1.
	 */
	JDKCurrencyAdapter(Currency currency) {
		this.baseCurrency = currency;
	}

	/**
	 * Gets the unique currency code, the effective code depends on the
	 * currency.
	 * <p>
	 * Since each currency is identified by this code, the currency code is
	 * required to be defined for every {@link CurrencyUnit} and not
	 * {@code null} or empty.
	 * <p>
	 * For ISO codes the 3-letter ISO code should be returned. For non ISO
	 * currencies no constraints are defined.
	 * 
	 * @return the currency code, never {@code null}. For ISO-4217 this this
	 *         will be the three letter ISO-4217 code. However, alternate
	 *         currencies can have different codes. Also there is no constraint
	 *         about the formatting of alternate codes, despite they fact that
	 *         the currency codes must be unique.
	 * @see javax.money.CurrencyUnit#getCurrencyCode()
	 */
	public String getCurrencyCode() {
		return baseCurrency.getCurrencyCode();
	}

	/**
	 * Gets a numeric currency code. Within the ISO-4217 name space, this equals
	 * to the ISO numeric code. In other currency name spaces this number may be
	 * different, or even undefined (-1).
	 * <p>
	 * The numeric code is an optional alternative to the standard currency
	 * code. If defined, the numeric code is required to be unique.
	 * <p>
	 * This method matches the API of <type>java.util.Currency</type>.
	 * 
	 * @see CurrencyUnit#getNumericCode()
	 * @return the numeric currency code
	 */
	public int getNumericCode() {
		return baseCurrency.getNumericCode();
	}

	/**
	 * Gets the number of fractional digits typically used by this currency.
	 * <p>
	 * Different currencies have different numbers of fractional digits by
	 * default. * For example, 'GBP' has 2 fractional digits, but 'JPY' has
	 * zero. * virtual currencies or those with no applicable fractional are
	 * indicated by -1. *
	 * <p>
	 * This method matches the API of <type>java.util.Currency</type>.
	 * 
	 * @return the fractional digits, from 0 to 9 (normally 0, 2 or 3), or 0 for
	 *         pseudo-currencies.
	 * 
	 */
	public int getDefaultFractionDigits() {
		return baseCurrency.getDefaultFractionDigits();
	}

	/**
	 * Compares two instances, based on {@link #getCurrencyCode()}.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(CurrencyUnit currency) {
		return getCurrencyCode().compareTo(currency.getCurrencyCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((baseCurrency == null) ? 0 : baseCurrency.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CurrencyUnit other = (CurrencyUnit) obj;
		if (!getCurrencyCode().equals(other.getCurrencyCode()))
			return false;
		return true;
	}

	/**
	 * Returns {@link #getCurrencyCode()}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return baseCurrency.toString();
	}

}
