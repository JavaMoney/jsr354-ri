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
package org.javamoney.moneta;

import java.util.Locale;
import java.util.Objects;

import javax.money.CurrencyUnit;

import org.javamoney.moneta.internal.ConfigurableCurrencyUnitProvider;

public final class BuildableCurrencyUnit implements CurrencyUnit, Comparable<CurrencyUnit> {

	private String currencyCode;
	private int numericCode;
	private int defaultFractionDigits;

	private BuildableCurrencyUnit(Builder builder) {
		Objects.requireNonNull(builder.currencyCode, "currencyCode required");
		if (builder.numericCode < -1) {
			throw new IllegalArgumentException("numericCode must be >= -1");
		}
		if (builder.defaultFractionDigits < 0) {
			throw new IllegalArgumentException(
					"defaultFractionDigits must be >= 0");
		}
		this.defaultFractionDigits = builder.defaultFractionDigits;
		this.numericCode = builder.numericCode;
		this.currencyCode = builder.currencyCode;
	}

	@Override
	public String getCurrencyCode() {
		return currencyCode;
	}

	@Override
	public int getNumericCode() {
		return numericCode;
	}

	@Override
	public int getDefaultFractionDigits() {
		return defaultFractionDigits;
	}
	
	@Override
	public int compareTo(CurrencyUnit o) {
		return this.currencyCode.compareTo(o.getCurrencyCode());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currencyCode == null) ? 0 : currencyCode.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		BuildableCurrencyUnit other = (BuildableCurrencyUnit) obj;
		if (currencyCode == null) {
			if (other.currencyCode != null)
				return false;
		} else if (!currencyCode.equals(other.currencyCode))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BuildableCurrencyUnit [currencyCode=" + currencyCode
				+ ", numericCode=" + numericCode + ", defaultFractionDigits="
				+ defaultFractionDigits + "]";
	}



	public static final class Builder {
		private String currencyCode;
		private int numericCode = -1;
		private int defaultFractionDigits = 2;

		public Builder(String currencyCode) {
			Objects.requireNonNull(currencyCode, "currencyCode required");
			this.currencyCode = currencyCode;
		}

		public Builder setCurrencyCode(String currencyCode) {
			Objects.requireNonNull(currencyCode, "currencyCode required");
			this.currencyCode = currencyCode;
			return this;
		}

		public Builder setNumericCode(int numericCode) {
			if (numericCode < -1) {
				throw new IllegalArgumentException("numericCode must be >= -1");
			}
			this.numericCode = numericCode;
			return this;
		}

		public Builder setDefaultFractionDigits(int defaultFractionDigits) {
			if (defaultFractionDigits < 0) {
				throw new IllegalArgumentException(
						"defaultFractionDigits must be >= 0");
			}
			this.defaultFractionDigits = defaultFractionDigits;
			return this;
		}

		public BuildableCurrencyUnit create() {
			return build(false);
		}
		
		public BuildableCurrencyUnit build(boolean register) {
			BuildableCurrencyUnit cu = new BuildableCurrencyUnit(this);
			if(register){
				ConfigurableCurrencyUnitProvider.registerCurrencyUnit(cu);
			}
			return cu;
		}
		
		public BuildableCurrencyUnit build(boolean register, Locale locale) {
			BuildableCurrencyUnit cu = new BuildableCurrencyUnit(this);
			if(register){
				ConfigurableCurrencyUnitProvider.registerCurrencyUnit(cu);
				ConfigurableCurrencyUnitProvider.registerCurrencyUnit(cu, locale);
			}
			return cu;
		}
	}

}
