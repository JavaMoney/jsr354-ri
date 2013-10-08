/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
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
 * 
 * Contributors: Anatole Tresch - initial implementation Werner Keil -
 * extensions and adaptions.
 */
package org.javamoney.moneta.spi;

import java.util.ServiceLoader;
import java.util.Set;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAdjuster;
import javax.money.MonetaryAmount;

/**
 * This SPI allows to extends/override the roundings available for
 * {@link CurrencyUnit}. The JSRs implementation already provides default
 * roundings. By registering instances of this interface using the JDK
 * {@link ServiceLoader}, the default behaviour can be overridden and extended,
 * e.g. for supporting also special roundings.
 * <p>
 * Implementations of this interface must be
 * <ul>
 * <li>thread-safe
 * <li>not require loading of other resources.
 * </ul>
 * If required, it is possible to implement this interface in a contextual way,
 * e.g. providing different roundings depending on the current EE application
 * context. Though in most cases rounding should be a general concept that does
 * not require contextual handling.
 * 
 * @author Anatole Tresch
 */
public interface RoundingProviderSpi {

	/**
	 * Access the current valid rounding for the given {@link CurrencyUnit}.
	 * <p>
	 * Instances of {@link MonetaryAdjuster} returned, must be thread safe and
	 * immutable.
	 * 
	 * @param currency
	 *            the currency for which a rounding operator should be obtained,
	 *            not {@code null}.
	 * @return the corresponding rounding instance, or {@code null}.
	 */
	MonetaryAdjuster getRounding(CurrencyUnit currency);

	/**
	 * Access the rounding for the given {@link CurrencyUnit}, that was valid at
	 * the given timestamp.
	 * <p>
	 * Instances of {@link MonetaryAdjuster} returned, must be thread safe and
	 * immutable.
	 * 
	 * @param currency
	 *            the currency for which a rounding operator should be obtained,
	 *            not {@code null}.
	 * @param timestamp
	 *            the target UTC timestamp, when the rounding should be valid.
	 * @return the corresponding rounding instance, or {@code null}.
	 */
	MonetaryAdjuster getRounding(CurrencyUnit currency, long timestamp);

	/**
	 * Access the current valid rounding for the given {@link CurrencyUnit}.
	 * <p>
	 * Instances of {@link MonetaryAdjuster} returned, must be thread safe and
	 * immutable.
	 * 
	 * @param currency
	 *            the currency for which a rounding operator should be obtained,
	 *            not {@code null}.
	 * @return the corresponding rounding instance, or {@code null}.
	 */
	MonetaryAdjuster getCashRounding(CurrencyUnit currency);

	/**
	 * Access the cash rounding for the given {@link CurrencyUnit}, that was
	 * valid at the given timestamp.
	 * <p>
	 * Instances of {@link MonetaryAdjuster} returned, must be thread safe and
	 * immutable.
	 * 
	 * @param currency
	 *            the currency for which a rounding operator should be obtained,
	 *            not {@code null}.
	 * @param timestamp
	 *            the target UTC timestamp, when the rounding should be valid.
	 * @return the corresponding rounding instance, or {@code null}.
	 */
	MonetaryAdjuster getCashRounding(CurrencyUnit currency, long timestamp);

	/**
	 * Access an {@link MonetaryAdjuster} for custom rounding
	 * {@link MonetaryAmount} instances.
	 * 
	 * @param customRounding
	 *            The customRounding identifier.
	 * @return the corresponding {@link MonetaryAdjuster} implementing the
	 *         rounding, or {@code null}.
	 */
	MonetaryAdjuster getCustomRounding(String customRoundingId);

	/**
	 * Access the ids of the custom roundigs defined by this provider.
	 * 
	 * @return the ids of the defined custom roundings, never {@code null}.
	 */
	Set<String> getCustomRoundingIds();

}
