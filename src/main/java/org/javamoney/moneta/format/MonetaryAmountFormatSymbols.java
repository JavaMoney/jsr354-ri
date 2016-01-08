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

import java.text.DecimalFormat;
import java.util.Objects;

import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;

import org.javamoney.moneta.spi.MonetaryAmountProducer;
import org.javamoney.moneta.spi.MoneyProducer;


/**
 *The {@link MonetaryAmountFormat} that uses the {@link MonetaryAmountSymbols} to format {@link MonetaryAmount}.
 * @author Otavio Santana
 * @see {@link MonetaryAmountSymbols}
 * @see {@link MonetaryAmountFormat}
 * @see {@link MonetaryAmountNumericInformation}
 * @deprecated
 */
@Deprecated
public interface MonetaryAmountFormatSymbols extends MonetaryAmountFormat {

	/**
	 * Gets the {@link MonetaryAmountSymbols} used in this {@link MonetaryAmountFormatSymbols}
	 * @return symbols
	 */
	MonetaryAmountSymbols getAmountSymbols();
	/**
	 * Gets the {@link MonetaryAmountNumericInformation} used in this {@link MonetaryAmountFormatSymbols}
	 * @return numeric information
	 */
	MonetaryAmountNumericInformation getNumericInformation();

	/**
	 * Creates {@link MonetaryAmountSymbols} using the symbols and producer
	 * @param symbols
	 * @param producer
	 * @return the {@link MonetaryAmountSymbols}
	 * @see {@link MonetaryAmountSymbols}
	 * @see {@link MonetaryAmountFormatSymbols}
	 * @see {@link MonetaryAmountProducer}
	 */
	static MonetaryAmountFormatSymbols of(MonetaryAmountSymbols symbols, MonetaryAmountProducer producer) {
		return new DefaultMonetaryAmountFormatSymbols(Objects.requireNonNull(symbols), Objects.requireNonNull(producer));
	}
	/**
	 * Creates {@link MonetaryAmountSymbols} using the pattern and symbol.
	 * @param pattern
	 * @param symbols
	 * @param producer
	 * @return the {@link MonetaryAmountSymbols}
	 * @see {@link MonetaryAmountSymbols}
	 * @see {@link MonetaryAmountFormatSymbols}
	 * @see {@link MonetaryAmountProducer}
	 * @see {@link DecimalFormat}
	 */
	static MonetaryAmountFormatSymbols of(String pattern, MonetaryAmountSymbols symbols, MonetaryAmountProducer producer) {
		return new DefaultMonetaryAmountFormatSymbols(
				Objects.requireNonNull(pattern),
				Objects.requireNonNull(symbols),
				Objects.requireNonNull(producer));
	}
	/**
	 * Creates a default {@link MonetaryAmountSymbols}.
	 * @return the {@link MonetaryAmountSymbols}
	 * @see {@link MonetaryAmountSymbols}
	 * @see {@link MonetaryAmountFormatSymbols}
	 * @see {@link MonetaryAmountProducer}
	 * @see {@link DecimalFormat}
	 */
	static MonetaryAmountFormatSymbols getDefafult() {
		return new DefaultMonetaryAmountFormatSymbols(new MonetaryAmountSymbols(), new MoneyProducer());
	}

}
