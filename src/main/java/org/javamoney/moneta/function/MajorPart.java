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

import javax.money.MonetaryAmount;
import javax.money.MonetaryContext;
import javax.money.MonetaryOperator;
import javax.money.function.MonetaryRoundings;

/**
 * This class allows to extract the major part of a {@link MonetaryAmount}
 * instance.
 * 
 * @author Anatole Tresch
 */
final class MajorPart implements MonetaryOperator {

	private MonetaryOperator downRounding = MonetaryRoundings
			.getRounding(new MonetaryContext.Builder(Object.class)
					.setMaxScale(0).setAttribute(RoundingMode.DOWN).build());

	/**
	 * Access the shared instance of {@link MajorPart} for use.
	 * 
	 * @return the shared instance, never {@code null}.
	 */
	MajorPart() {
	}

	/**
	 * Gets the amount in major units as a {@code MonetaryAmount} with scale 0.
	 * <p>
	 * This returns the monetary amount in terms of the major units of the
	 * currency, truncating the amount if necessary. For example, 'EUR 2.35'
	 * will return 'EUR 2', and 'BHD -1.345' will return 'BHD -1'.
	 * <p>
	 * This is returned as a {@code MonetaryAmount} rather than a
	 * {@code BigInteger} . This is to allow further calculations to be
	 * performed on the result. Should you need a {@code BigInteger}, simply
	 * call {@code asType(BigInteger.class)}.
	 * 
	 * @return the major units part of the amount, never {@code null}
	 */
	@Override
	public <T extends MonetaryAmount<T>> T apply(T amount) {
		Objects.requireNonNull(amount, "Amount required.");
		return amount.with(downRounding);
	}

}
