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
 */
package org.javamoney.moneta.function;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.CurrencyUnit;
import javax.money.MonetaryOperator;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.spi.RoundingProviderSpi;

/**
 * This class models the accessor for rounding instances, modeled by
 * {@link MonetaryOperator}.
 * <p>
 * This class is thread-safe.
 * 
 * @author Anatole Tresch
 * @author Werner Keil
 */
public final class MonetaryRoundings {
	/**
	 * An adaptive rounding instance that transparently looks up the correct
	 * rounding.
	 */
	private static final MonetaryOperator DEFAULT_ROUNDING = new DefaultCurrencyRounding();
	/**
	 * The internal fallback provider, if no registered
	 * {@link RoundingProviderSpi} could return a rounding.
	 */
	private static DefaultRoundingProvider defaultProvider = new DefaultRoundingProvider();
	/** Currently loaded SPIs. */
	private static ServiceLoader<RoundingProviderSpi> providerSpis = loadSpis();

	/**
	 * Private singleton constructor.
	 */
	private MonetaryRoundings() {
		// Singleton
	}

	private static ServiceLoader<RoundingProviderSpi> loadSpis() {
		try {
			return ServiceLoader.load(RoundingProviderSpi.class);
		} catch (Exception e) {
			Logger.getLogger(MonetaryRoundings.class.getName()).log(
					Level.SEVERE,
					"Error loading RoundingProviderSpi instances.", e);
			return null;
		}
	}

	/**
	 * Creates a rounding that can be added as {@link MonetaryOperator} to
	 * chained calculations. The instance will lookup the concrete
	 * {@link MonetaryOperator} instance from the {@link MonetaryRoundings}
	 * based on the input {@link MonetaryAmount}'s {@link CurrencyUnit}.
	 * 
	 * @return the (shared) default rounding instance.
	 */
	public static MonetaryOperator getRounding() {
		return DEFAULT_ROUNDING;
	}

	/**
	 * Creates an rounding instance.
	 * 
	 * @param mathContext
	 *            The {@link MathContext} to be used, not {@code null}.
	 */
	public static MonetaryOperator getRounding(int scale,
			RoundingMode roundingMode) {
		Objects.requireNonNull(roundingMode, "RoundingMode required.");
		return new DefaultRounding(scale, roundingMode);
	}

	/**
	 * Creates an {@link MonetaryOperator} for rounding {@link MonetaryAmount}
	 * instances given a currency.
	 * 
	 * @param currency
	 *            The currency, which determines the required precision. As
	 *            {@link RoundingMode}, by default, {@link RoundingMode#HALF_UP}
	 *            is sued.
	 * @return a new instance {@link MonetaryOperator} implementing the
	 *         rounding, never {@code null}.
	 */
	public static MonetaryOperator getRounding(CurrencyUnit currency) {
		Objects.requireNonNull(currency, "Currency required.");
		for (RoundingProviderSpi prov : providerSpis) {
			try {
				MonetaryOperator op = prov.getRounding(currency);
				if (op != null) {
					return op;
				}
			} catch (Exception e) {
				Logger.getLogger(MonetaryRoundings.class.getName()).log(
						Level.SEVERE,
						"Error loading RoundingProviderSpi from ptovider: "
								+ prov, e);
			}
		}
		return defaultProvider.getRounding(currency);
	}

	/**
	 * Creates an {@link MonetaryOperator} for rounding {@link MonetaryAmount}
	 * instances given a currency.
	 * 
	 * @param currency
	 *            The currency, which determines the required precision. As
	 *            {@link RoundingMode}, by default, {@link RoundingMode#HALF_UP}
	 *            is sued.
	 * @return a new instance {@link MonetaryOperator} implementing the
	 *         rounding, never {@code null}.
	 */
	public static MonetaryOperator getCashRounding(CurrencyUnit currency) {
		Objects.requireNonNull(currency, "Currency required.");
		for (RoundingProviderSpi prov : providerSpis) {
			try {
				MonetaryOperator op = prov.getCashRounding(currency);
				if (op != null) {
					return op;
				}
			} catch (Exception e) {
				Logger.getLogger(MonetaryRoundings.class.getName()).log(
						Level.SEVERE,
						"Error loading RoundingProviderSpi from ptovider: "
								+ prov, e);
			}
		}
		return defaultProvider.getCashRounding(currency);
	}

	/**
	 * Creates an {@link MonetaryOperator} for rounding {@link MonetaryAmount}
	 * instances given a currency, hereby the rounding must be valid for the
	 * given timestamp.
	 * 
	 * @param currency
	 *            The currency, which determines the required precision. As
	 *            {@link RoundingMode}, by default, {@link RoundingMode#HALF_UP}
	 *            is used.
	 * @param timestamp
	 *            the UTC timestamp.
	 * @return a new instance {@link MonetaryOperator} implementing the
	 *         rounding, or {@code null}.
	 */
	public static MonetaryOperator getRounding(CurrencyUnit currency,
			long timestamp) {
		Objects.requireNonNull(currency, "Currency required.");
		for (RoundingProviderSpi prov : providerSpis) {
			try {
				MonetaryOperator op = prov.getRounding(currency, timestamp);
				if (op != null) {
					return op;
				}
			} catch (Exception e) {
				Logger.getLogger(MonetaryRoundings.class.getName()).log(
						Level.SEVERE,
						"Error loading RoundingProviderSpi from provider: "
								+ prov, e);
			}
		}
		return defaultProvider.getRounding(currency, timestamp);
	}

	/**
	 * Creates an {@link MonetaryOperator} for rounding {@link MonetaryAmount}
	 * instances given a currency, hereby the rounding must be valid for the
	 * given timestamp.
	 * 
	 * @param currency
	 *            The currency, which determines the required precision. As
	 *            {@link RoundingMode}, by default, {@link RoundingMode#HALF_UP}
	 *            is sued.
	 * @param timestamp
	 *            the UTC timestamp.
	 * @return a new instance {@link MonetaryOperator} implementing the
	 *         rounding, or {@code null}.
	 */
	public static MonetaryOperator getCashRounding(CurrencyUnit currency,
			long timestamp) {
		Objects.requireNonNull(currency, "Currency required.");
		for (RoundingProviderSpi prov : providerSpis) {
			try {
				MonetaryOperator op = prov.getCashRounding(currency, timestamp);
				if (op != null) {
					return op;
				}
			} catch (Exception e) {
				Logger.getLogger(MonetaryRoundings.class.getName()).log(
						Level.SEVERE,
						"Error loading RoundingProviderSpi from ptovider: "
								+ prov, e);
			}
		}
		return defaultProvider.getCashRounding(currency, timestamp);
	}

	/**
	 * Access an {@link MonetaryOperator} for custom rounding
	 * {@link MonetaryAmount} instances.
	 * 
	 * @param customRounding
	 *            The customRounding identifier.
	 * @return the corresponding {@link MonetaryOperator} implementing the
	 *         rounding, never {@code null}.
	 * @throws IllegalArgumentException
	 *             if no such rounding is registered using a
	 *             {@link RoundingProviderSpi} instance.
	 */
	public static MonetaryOperator getRounding(String customRoundingId) {
		Objects.requireNonNull(customRoundingId, "CustomRoundingId required.");
		for (RoundingProviderSpi prov : providerSpis) {
			try {
				MonetaryOperator op = prov.getCustomRounding(customRoundingId);
				if (op != null) {
					return op;
				}
			} catch (Exception e) {
				Logger.getLogger(MonetaryRoundings.class.getName()).log(
						Level.SEVERE,
						"Error loading RoundingProviderSpi from provider: "
								+ prov, e);
			}
		}
		return defaultProvider.getCustomRounding(customRoundingId);
	}

	/**
	 * Allows to access the identifiers of the current defined custom roundings.
	 * 
	 * @return the set of custom rounding ids, never {@code null}.
	 */
	public static Set<String> getCustomRoundingIds() {
		Set<String> result = new HashSet<String>();
		for (RoundingProviderSpi prov : providerSpis) {
			try {
				result.addAll(prov.getCustomRoundingIds());
			} catch (Exception e) {
				Logger.getLogger(MonetaryRoundings.class.getName()).log(
						Level.SEVERE,
						"Error loading RoundingProviderSpi from provider: "
								+ prov, e);
			}
		}
		return result;
	}

	/**
	 * Platform RI: Default Rounding that rounds a {@link MonetaryAmount} based
	 * on tis {@link Currency}.
	 * 
	 * @author Anatole Tresch
	 */
	private static final class DefaultCurrencyRounding implements
			MonetaryOperator {

		@Override
		public MonetaryAmount apply(MonetaryAmount amount) {
			MonetaryOperator r = MonetaryRoundings.getRounding(amount
					.getCurrency());
			return r.apply(amount);
		}

	}

	private static final class DefaultRoundingProvider implements
			RoundingProviderSpi {

		@Override
		public MonetaryOperator getRounding(CurrencyUnit currency) {
			return new DefaultRounding(currency);
		}

		@Override
		public MonetaryOperator getRounding(CurrencyUnit currency,
				long timestamp) {
			return null;
		}

		@Override
		public MonetaryOperator getCashRounding(CurrencyUnit currency) {
			return new DefaultCashRounding(currency);
		}

		@Override
		public MonetaryOperator getCashRounding(CurrencyUnit currency,
				long timestamp) {
			return null;
		}

		@Override
		public Set<String> getCustomRoundingIds() {
			return Collections.emptySet();
		}

		@Override
		public MonetaryOperator getCustomRounding(String customRoundingId) {
			throw new IllegalArgumentException("No such custom rounding: "
					+ customRoundingId);
		}

	}
}
