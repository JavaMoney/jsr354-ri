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

import javax.money.MonetaryAmount;
import javax.money.MonetaryOperator;

/**
 * This class allows to extract the minor part of a {@link MonetaryAmount}
 * instance.
 * 
 * @author Anatole Tresch
 */
final class MinorPart implements MonetaryOperator {

	/**
	 * Private constructor, there is only one instance of this class, accessible
	 * calling {@link #of()}.
	 */
	MinorPart() {
	}

	/**
	 * Gets the minor part of a {@code MonetaryAmount} with the same scale.
	 * <p>
	 * This returns the monetary amount in terms of the minor units of the
	 * currency, truncating the whole part if necessary. For example, 'EUR 2.35'
	 * will return 'EUR 0.35', and 'BHD -1.345' will return 'BHD -0.345'.
	 * <p>
	 * This is returned as a {@code MonetaryAmount} rather than a
	 * {@code BigDecimal} . This is to allow further calculations to be
	 * performed on the result. Should you need a {@code BigDecimal}, simply
	 * call {@code asType(BigDecimal.class)}.
	 * 
	 * @return the minor units part of the amount, never {@code null}
	 */
	@Override
	public <T extends MonetaryAmount<T>> T apply(T amount) {
		Objects.requireNonNull(amount, "Amount required.");
		BigDecimal number = amount.getNumber(BigDecimal.class);
		BigDecimal wholes = number.setScale(0, RoundingMode.DOWN);
		return amount.subtract(amount.with(amount.getCurrency(), wholes));
	}

}
