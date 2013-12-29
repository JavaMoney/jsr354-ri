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
import java.util.Objects;

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

/**
 * This class allows to extract the reciprocal value (multiplcative inversion)
 * of a {@link MonetaryAmount} instance.
 * 
 * @author Anatole Tresch
 */
final class Reciprocal implements MonetaryOperator {

	/**
	 * Access the shared instance of {@link Reciprocal} for use.
	 * 
	 * @return the shared instance, never {@code null}.
	 */
	Reciprocal() {
	}

	/**
	 * Gets the amount as reciprocal / multiplcative inversed value (1/n).
	 * <p>
	 * E.g. 'EUR 2.0' will be converted to 'EUR 0.5'.
	 * 
	 * @return
	 * 
	 * @return the reciprocal / multiplcative inversed of the amount
	 * @throws ArithmeticException
	 *             if the arithmetic operation failed
	 */
	// unchecked cast {@code (T)amount.with(MonetaryOperator)} is
	// safe, if the operator is implemented as specified by this JSR.
	@SuppressWarnings("unchecked")
	@Override
	public <T extends MonetaryAmount> T apply(T amount) {
		Objects.requireNonNull(amount, "Amount required.");
		return (T)amount.getFactory().with(BigDecimal.ONE.divide(
				amount.getNumber().numberValue(BigDecimal.class))).create();
	}

}
