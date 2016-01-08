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
package org.javamoney.moneta.format;

import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.format.MonetaryAmountFormat;

/**
 * This class represents symbols to be used on {@link MonetaryAmountFormatSymbols}, this class decorate the
 * {@link DecimalFormatSymbols}
 * @see {@link DecimalFormatSymbols}
 * @see {@link MonetaryAmountFormat}
 * @see {@link MonetaryAmountFormatSymbols}
 * @author Otavio Santana
 * @deprecated
 */
@Deprecated
public final class MonetaryAmountSymbols {

	private final DecimalFormatSymbols formatSymbols;
	/**
	 * Create a MonetaryAmountFormatSymbols object for the given locale
	 * @see {@link DecimalFormatSymbols#DecimalFormatSymbols(Locale)}
	 * @param locale
	 */
	public MonetaryAmountSymbols(Locale locale) {
		this.formatSymbols = new DecimalFormatSymbols(locale);
	}
	/**
	 * Create a MonetaryAmountFormatSymbols object for the default FORMAT locale.
	 * @see {@link Locale.getDefault(Locale.Category.FORMAT)}
	 * {@link DecimalFormatSymbols#DecimalFormatSymbols()}
	 */
	public MonetaryAmountSymbols() {
		this.formatSymbols = new DecimalFormatSymbols();
	}
	/**
	 *
	 * @return
	 */
	public CurrencyUnit getCurrency() {
		return Monetary.getCurrency(formatSymbols.getCurrency().getCurrencyCode());
	}

	/**
	 * Sets the currency of these MonetaryAmountFormatSymbols. This also sets the currency symbol attribute to the
	 * currency's symbol in the MonetaryAmountFormatSymbols' locale, and the international currency symbol attribute
	 *  to the currency's ISO 4217 currency code.
	 * @param currency
	 * @throws NullPointerException if currency is null
	 */
	public void setCurrency(CurrencyUnit currency) {
	    Objects.requireNonNull(currency);
		formatSymbols.setCurrency(Currency.getInstance(currency.getCurrencyCode()));
	}
	/**
	 * @return Returns the currency symbol for the currency of these MonetaryAmountFormatSymbols in their locale.
	 */
	public String getCurrencySymbol() {
		return formatSymbols.getCurrencySymbol();
	}
	/**
	 * Sets the currency symbol for the currency of these MonetaryAmountFormatSymbols in their locale.
	 * @param currencySymbol
	 */
	public void setCurrencySymbol(String currencySymbol) {
		formatSymbols.setCurrencySymbol(currencySymbol);
	}
	/**
	 * Gets the character used for decimal sign.
	 * @return
	 */
	public char getDecimalSeparator() {
		return formatSymbols.getDecimalSeparator();
	}

	/**
	 * Sets the character used for decimal sign.
	 * @param decimalSeparator
	 */
	public void setDecimalSeparator(char decimalSeparator) {
		formatSymbols.setDecimalSeparator(decimalSeparator);
	}
	/**
	 * @return Gets the character used for a digit in a pattern.
	 */
	public char getDigit() {
		return formatSymbols.getDigit();
	}
	/**
	 * Sets the character used for a digit in a pattern.
	 * @param digit
	 */
	public void setDigit(char digit) {
		formatSymbols.setDigit(digit);
	}
	/**
	 * @return Returns the string used to separate the mantissa from the exponent. Examples: "x10^" for 1.23x10^4,
	 * "E" for 1.23E4.
	 */
	public String getExponentSeparator() {
		return formatSymbols.getExponentSeparator();
	}
	/**
	 * Sets the string used to separate the mantissa from the exponent. Examples: "x10^" for 1.23x10^4, "E" for 1.23E4.
	 * @param exponentSeparator
	 */
	public void setExponentSeparator(String exponentSeparator) {
		formatSymbols.setExponentSeparator(exponentSeparator);
	}
	/**
	 * @return Gets the character used for thousands separator.
	 */
	public char getGroupingSeparator() {
		return formatSymbols.getGroupingSeparator();
	}
	/**
	 * Sets the character used for thousands separator.
	 * @param groupingSeparator
	 */
	public void setGroupingSeparator(char groupingSeparator) {
		formatSymbols.setGroupingSeparator(groupingSeparator);
	}
	/**
	 * @return Gets the string used to represent infinity. Almost always left unchanged.
	 */
	public String getInfinity() {
		return formatSymbols.getInfinity();
	}
	/**
	 * Sets the string used to represent infinity. Almost always left unchanged.
	 * @param infinity
	 */
	public void setInfinity(String infinity) {
		formatSymbols.setInfinity(infinity);
	}
	/**
	 * @return the ISO 4217 currency code of the currency of these MonetaryAmountFormatSymbols.
	 */
	public String getInternationalCurrencySymbol() {
		return formatSymbols.getInternationalCurrencySymbol();
	}
	/**
	 * Sets the ISO 4217 currency code of the currency of these MonetaryAmountFormatSymbols.
	 * @param internationalCurrencySymbol
	 */
	public void setInternationalCurrencySymbol(String internationalCurrencySymbol) {
		Objects.requireNonNull(internationalCurrencySymbol);
		Currency.getInstance(internationalCurrencySymbol);
		formatSymbols.setInternationalCurrencySymbol(internationalCurrencySymbol);
	}
	/**
	 * Gets the character used to represent minus sign. If no explicit negative format is specified, one is
	 * formed by prefixing minusSign to the positive format.
	 * @return
	 */
	public char getMinusSign() {
		return formatSymbols.getMinusSign();
	}
	/**
	 * Sets the character used to represent minus sign. If no explicit negative format is specified, one is
	 * formed by prefixing minusSign to the positive format.
	 * @param minusSign
	 */
	public void setMinusSign(char minusSign) {
		formatSymbols.setMinusSign(minusSign);
	}
	/**
	 * @return the monetary decimal separator.
	 */
	public char getMonetaryDecimalSeparator() {
		return formatSymbols.getMonetaryDecimalSeparator();
	}
	/**
	 * Sets the monetary decimal separator.
	 * @param monetaryDecimalSeparator
	 */
	public void setMonetaryDecimalSeparator(char monetaryDecimalSeparator) {
		formatSymbols.setMonetaryDecimalSeparator(monetaryDecimalSeparator);
	}
	/**
	 * @return the string used to represent "not a number". Almost always left unchanged.
	 */
	public String getNaN() {
		return formatSymbols.getNaN();
	}
	/**
	 * Sets the string used to represent "not a number". Almost always left unchanged.
	 * @param naN
	 */
	public void setNaN(String naN) {
		formatSymbols.setNaN(naN);
	}
	/**
	 * @return the character used to separate positive and negative subpatterns in a pattern.
	 */
	public char getPatternSeparator() {
		return formatSymbols.getPatternSeparator();
	}
	/**
	 * Sets the character used to separate positive and negative subpatterns in a pattern.
	 * @param patternSeparator
	 */
	public void setPatternSeparator(char patternSeparator) {
		formatSymbols.setPatternSeparator(patternSeparator);
	}
	/**
	 * @return the character used for percent sign.
	 */
	public char getPercent() {
		return formatSymbols.getPercent();
	}
	/**
	 * Sets the character used for percent sign.
	 * @param percent
	 */
	public void setPercent(char percent) {
		formatSymbols.setPercent(percent);
	}
	/**
	 * @return the character used for per mille sign.
	 */
	public char getPerMill() {
		return formatSymbols.getPerMill();
	}
	/**
	 * Sets the character used for per mille sign.
	 * @param perMill
	 */
	public void setPerMill(char perMill) {
		formatSymbols.setPerMill(perMill);
	}
	/**
	 * @return Gets the character used for zero.
	 */
	public char getZeroDigit() {
		return formatSymbols.getZeroDigit();
	}
	/**
	 * Sets the character used for zero.
	 * @param zeroDigit
	 */
	public void setZeroDigit(char zeroDigit) {
		formatSymbols.setZeroDigit(zeroDigit);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(MonetaryAmountSymbols.class.isInstance(obj)) {
			MonetaryAmountSymbols other = MonetaryAmountSymbols.class.cast(obj);
			return Objects.equals(other.formatSymbols, formatSymbols);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Objects.hash(formatSymbols);
	}

	DecimalFormatSymbols getFormatSymbol() {
		return formatSymbols;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName()).append('{');
		sb.append(" Currency: ").append(formatSymbols.getCurrency()).append(',');
		sb.append(" currencySymbol: ").append(formatSymbols.getCurrencySymbol()).append(',');
		sb.append(" decimalSeparator: ").append(formatSymbols.getDecimalSeparator()).append(',');
		sb.append(" digit: ").append(formatSymbols.getDigit()).append(',');
		sb.append(" exponentSeparator: ").append(formatSymbols.getExponentSeparator()).append(',');
		sb.append(" groupingSeparator: ").append(formatSymbols.getGroupingSeparator()).append(',');
		sb.append(" infinity: ").append(formatSymbols.getInfinity()).append(',');
		sb.append(" internationalCurrencySymbol: ").append(formatSymbols.getInternationalCurrencySymbol()).append(',');
		sb.append(" minusSign: ").append(formatSymbols.getMinusSign()).append(',');
		sb.append(" monetaryDecimalSeparator: ").append(formatSymbols.getMonetaryDecimalSeparator()).append(',');
		sb.append(" naN: ").append(formatSymbols.getNaN()).append(',');
		sb.append(" patternSeparator: ").append(formatSymbols.getPatternSeparator()).append(',');
		sb.append(" percent: ").append(formatSymbols.getPercent()).append(',');
		sb.append(" perMill: ").append(formatSymbols.getPerMill()).append(',');
		sb.append(" zeroDigit: ").append(formatSymbols.getZeroDigit()).append('}');
		return sb.toString();
	}
}