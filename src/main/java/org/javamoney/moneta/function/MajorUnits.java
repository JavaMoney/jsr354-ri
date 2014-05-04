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

import java.math.RoundingMode;
import java.util.Objects;

import javax.money.*;

/**
 * This class allows to extract the major part of a {@link MonetaryAmount}
 * instance.
 * 
 * @author Anatole Tresch
 */
final class MajorUnits implements MonetaryQuery<Long> {

	private MonetaryOperator downRounding = MonetaryRoundings
			.getRounding(new RoundingContext.Builder()
					.setAttribute("scale", 0).setObject(RoundingMode.DOWN).build());

	/**
	 * Access the shared instance of {@link MajorUnits} for use.
	 * 
	 * @return the shared instance, never {@code null}.
	 */
	MajorUnits() {
	}

	/**
	 * Gets the amount in major units as a {@code long}.
	 * <p>
	 * This returns the monetary amount in terms of the major units of the
	 * currency, truncating the amount if necessary. For example, 'EUR 2.35'
	 * will return 2, and 'BHD -1.345' will return -1.
	 * <p>
	 * This method matches the API of {@link java.math.BigDecimal}.
	 * 
	 * @return the major units part of the amount
	 * @throws ArithmeticException
	 *             if the amount is too large for a {@code long}
	 */
	@Override
	public Long queryFrom(MonetaryAmount amount) {
		Objects.requireNonNull(amount, "Amount required.");
		return Long.valueOf(amount.with(downRounding)
				.getNumber().longValueExact());
	}
}
