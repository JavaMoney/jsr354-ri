package org.javamoney.moneta.format;

import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
/**
 *The {@link MonetaryAmountFormat} that uses the {@link MonetaryAmountSymbols} to format {@link MonetaryAmount}.
 * @author Otavio Santana
 * @see {@link MonetaryAmountSymbols}
 * @see {@link MonetaryAmountFormat}
 */
public interface MonetaryAmountFormatSymbols extends MonetaryAmountFormat {

	/**
	 * Gets the {@link MonetaryAmountSymbols} used in this {@link MonetaryAmountFormatSymbols}
	 * @return
	 */
	MonetaryAmountSymbols getAmountSymbols();
}
