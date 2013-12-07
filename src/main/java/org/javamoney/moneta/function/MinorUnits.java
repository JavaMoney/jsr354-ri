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
package org.javamoney.moneta.function;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryQuery;

/**
 * This class allows to extract the minor units of a {@link MonetaryAmount}
 * instance.
 * 
 * @author Anatole Tresch
 */
final class MinorUnits implements MonetaryQuery<Long> {

	/**
	 * Private constructor, there is only one instance of this class, accessible
	 * calling {@link #of()}.
	 */
	MinorUnits() {
	}

	/**
	 * Gets the amount in minor units as a {@code long}.
	 * <p>
	 * This returns the monetary amount in terms of the minor units of the
	 * currency, truncating the amount if necessary. For example, 'EUR 2.35'
	 * will return 235, and 'BHD -1.345' will return -1345.
	 * <p>
	 * This method matches the API of {@link java.math.BigDecimal}.
	 * 
	 * @return the minor units part of the amount
	 * @throws ArithmeticException
	 *             if the amount is too large for a {@code long}
	 */
	@Override
	public Long queryFrom(MonetaryAmount<?> amount) {
		Objects.requireNonNull(amount, "Amount required.");
		BigDecimal number = amount.getNumber(BigDecimal.class);
		CurrencyUnit cur = amount.getCurrency();
		int scale = cur.getDefaultFractionDigits();
		if(scale<0){
			scale = 0;
		}
		number = number.setScale(scale, RoundingMode.DOWN);
		return number.movePointRight(number.scale()).longValueExact();
	}

}
