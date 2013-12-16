package org.javamoney.moneta.function;

import javax.money.CurrencyUnit;

/**
 * Represents a supplier of {@link CurrencyUnit}-valued results. This is the
 * {@link CurrencyUnit}-producing primitive specialization of {@code Supplier}.
 * 
 * <p>
 * There is no requirement that a distinct result be returned each time the
 * supplier is invoked.
 * 
 * <p>
 * This is a <a href="package-summary.html">functional interface</a> whose
 * functional method is {@link #getCurrency()}.
 * 
 * @see Supplier
 */
// @FunctionalInterface
public interface CurrencySupplier {

	/**
	 * Gets a result.
	 * 
	 * @return a result
	 */
	CurrencyUnit getCurrency();
}