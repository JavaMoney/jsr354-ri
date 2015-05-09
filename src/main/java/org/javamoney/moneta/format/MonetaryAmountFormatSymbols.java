package org.javamoney.moneta.format;

import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.format.MonetaryAmountFormat;

/**
 * This class represents symbols to be used on {@link MonetaryAmountFormat}, this class decorate the
 * {@link DecimalFormatSymbols}
 * @author Otavio Santana
 * @see {@link DecimalFormatSymbols}
 */
public final class MonetaryAmountFormatSymbols {

	private final DecimalFormatSymbols formatSymbols;

	public MonetaryAmountFormatSymbols(Locale locale) {
		this.formatSymbols = new DecimalFormatSymbols(locale);
	}

	public MonetaryAmountFormatSymbols() {
		this.formatSymbols = new DecimalFormatSymbols();
	}

	public CurrencyUnit getCurrency() {
		return Monetary.getCurrency(formatSymbols.getCurrency().getCurrencyCode());
	}

	public void setCurrency(CurrencyUnit currency) {
	    Objects.requireNonNull(currency);
		formatSymbols.setCurrency(Currency.getInstance(currency.getCurrencyCode()));
	}

	public String getCurrencySymbol() {
		return formatSymbols.getCurrencySymbol();
	}

	public void setCurrencySymbol(String currencySymbol) {
		formatSymbols.setCurrencySymbol(currencySymbol);
	}

	public char getDecimalSeparator() {
		return formatSymbols.getDecimalSeparator();
	}

	public void setDecimalSeparator(char decimalSeparator) {
		formatSymbols.setDecimalSeparator(decimalSeparator);
	}

	public char getDigit() {
		return formatSymbols.getDigit();
	}

	public void setDigit(char digit) {
		formatSymbols.setDigit(digit);
	}

	public String getExponentSeparator() {
		return formatSymbols.getExponentSeparator();
	}

	public void setExponentSeparator(String exponentSeparator) {
		formatSymbols.setExponentSeparator(exponentSeparator);
	}

	public char getGroupingSeparator() {
		return formatSymbols.getGroupingSeparator();
	}

	public void setGroupingSeparator(char groupingSeparator) {
		formatSymbols.setGroupingSeparator(groupingSeparator);
	}

	public String getInfinity() {
		return formatSymbols.getInfinity();
	}

	public void setInfinity(String infinity) {
		formatSymbols.setInfinity(infinity);
	}

	public String getInternationalCurrencySymbol() {
		return formatSymbols.getInternationalCurrencySymbol();
	}

	public void setInternationalCurrencySymbol(String internationalCurrencySymbol) {
		formatSymbols.setInternationalCurrencySymbol(internationalCurrencySymbol);
	}

	public char getMinusSign() {
		return formatSymbols.getMinusSign();
	}

	public void setMinusSign(char minusSign) {
		formatSymbols.setMinusSign(minusSign);
	}

	public char getMonetaryDecimalSeparator() {
		return formatSymbols.getMonetaryDecimalSeparator();
	}

	public void setMonetaryDecimalSeparator(char monetaryDecimalSeparator) {
		formatSymbols.setMonetaryDecimalSeparator(monetaryDecimalSeparator);
	}

	public String getNaN() {
		return formatSymbols.getNaN();
	}

	public void setNaN(String naN) {
		formatSymbols.setNaN(naN);
	}

	public char getPatternSeparator() {
		return formatSymbols.getPatternSeparator();
	}

	public void setPatternSeparator(char patternSeparator) {
		formatSymbols.setPatternSeparator(patternSeparator);
	}

	public char getPercent() {
		return formatSymbols.getPercent();
	}

	public void setPercent(char percent) {
		formatSymbols.setPercent(percent);
	}

	public char getPerMill() {
		return formatSymbols.getPerMill();
	}

	public void setPerMill(char perMill) {
		formatSymbols.setPerMill(perMill);
	}

	public char getZeroDigit() {
		return formatSymbols.getZeroDigit();
	}

	public void setZeroDigit(char zeroDigit) {
		formatSymbols.setZeroDigit(zeroDigit);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(MonetaryAmountFormatSymbols.class.isInstance(formatSymbols)) {
			MonetaryAmountFormatSymbols other = MonetaryAmountFormatSymbols.class.cast(obj);
			return Objects.equals(other.formatSymbols, formatSymbols);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(formatSymbols);
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