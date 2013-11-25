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
 * Contributors: Anatole Tresch - initial implementation Wernner Keil -
 * extensions and adaptions.
 */
package org.javamoney.moneta.function;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import javax.money.CurrencyUnit;
import javax.money.MonetaryOperator;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.spi.RoundingProviderSpi;

public class TestRoundingProvider implements RoundingProviderSpi {

	private Set<String> customIds = new HashSet<>();

	public TestRoundingProvider() {
		customIds.add("zero");
		customIds.add("minusOne");
		customIds.add("CHF-cash");
	}

	private MonetaryOperator zeroRounding = new MonetaryOperator() {

		@Override
		public MonetaryAmount apply(MonetaryAmount amount) {
			return Money.ofZero(amount.getCurrency());
		}

	};

	private MonetaryOperator minusOneRounding = new MonetaryOperator() {

		@Override
		public MonetaryAmount apply(MonetaryAmount amount) {
			return Money.of(amount.getCurrency(), -1);
		}

	};

	private MonetaryOperator chfCashRounding = new MonetaryOperator() {

		private MonetaryOperator minorRounding = MonetaryRoundings.getRounding(
				2, RoundingMode.HALF_UP);

		@Override
		public MonetaryAmount apply(MonetaryAmount amount) {
			MonetaryAmount amt = amount.with(minorRounding);
			Money mp = Money.from(amt.with(MonetaryFunctions.minorPart()));
			BigDecimal delta = null;
			if (mp.isGreaterThanOrEqualTo(Money.of(amount.getCurrency(), 0.03))) {
				// add
				return Money.from(amt).add(
						Money.of(amt.getCurrency(), new BigDecimal("0.05"))
								.subtract(mp));
			}
			else {
				// subtract
				return Money.from(amt).subtract(mp);
			}
		}
	};

	@Override
	public MonetaryOperator getRounding(CurrencyUnit currency) {
		if (currency.getCurrencyCode().equals("XXX")) {
			return zeroRounding;
		}
		return null;
	}

	@Override
	public MonetaryOperator getRounding(CurrencyUnit currency, long timestamp) {
		if (currency.getCurrencyCode().equals("XXX")) {
			if (timestamp > System.currentTimeMillis()) {
				return minusOneRounding;
			}
			return zeroRounding;
		}
		return null;
	}

	@Override
	public MonetaryOperator getCashRounding(CurrencyUnit currency) {
		if (currency.getCurrencyCode().equals("CHF")) {
			return chfCashRounding;
		}
		else if (currency.getCurrencyCode().equals("XXX")) {
			return zeroRounding;
		}
		return null;
	}

	@Override
	public MonetaryOperator getCashRounding(CurrencyUnit currency,
			long timestamp) {
		if (currency.getCurrencyCode().equals("CHF")) {
			if (timestamp > System.currentTimeMillis()) {
				return minusOneRounding;
			}
			return chfCashRounding;
		}
		return null;
	}

	@Override
	public MonetaryOperator getCustomRounding(String customRoundingId) {
		if ("CHF-cash".equals(customRoundingId)) {
			return chfCashRounding;
		}
		else if ("zero".equals(customRoundingId)) {
			return zeroRounding;
		}
		else if ("minusOne".equals(customRoundingId)) {
			return minusOneRounding;
		}
		return null;
	}

	@Override
	public Set<String> getCustomRoundingIds() {
		return customIds;
	}

}
