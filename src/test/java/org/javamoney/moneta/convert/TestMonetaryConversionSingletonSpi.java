/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta.convert;

import java.util.Arrays;
import java.util.List;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.convert.ConversionContext;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;
import javax.money.convert.RateType;
import javax.money.spi.MonetaryConversionsSingletonSpi;

public class TestMonetaryConversionSingletonSpi implements MonetaryConversionsSingletonSpi{

	private ExchangeRateProvider dummyProvider = new DummyRateProvider();
	private CurrencyConversion dummyConversion = new DummyConversion();

	@Override
	public ExchangeRateProvider getExchangeRateProvider(String... providers) {
		if ("test".equals(providers[0])) {
			return dummyProvider;
		}
		return null;
	}

	@Override
	public CurrencyConversion getConversion(CurrencyUnit termCurrency,
			ConversionContext context, String... providers) {
		if ("test".equals(providers[0])) {
			return dummyConversion;
		}
		return null;
	}

	@Override
	public List<String> getProviderNames() {
		return Arrays.asList(new String[] { "test" });
	}

	@Override
	public boolean isProviderAvailable(String provider) {
		return "test".equals(provider);
	}

	@Override
	public List<String> getDefaultProviderChain() {
		return Arrays.asList(new String[] { "test" });
	}

	@Override
	public ProviderContext getProviderContext(String provider) {
		return ProviderContext.of("test");
	}
	
	public final class DummyRateProvider implements ExchangeRateProvider {

		@Override
		public ProviderContext getProviderContext() {
			return ProviderContext.of("test", RateType.ANY);
		}

		@Override
		public boolean isAvailable(CurrencyUnit src, CurrencyUnit target) {
			return false;
		}

		@Override
		public ExchangeRate getExchangeRate(CurrencyUnit source,
				CurrencyUnit target) {
			return null;
		}

		@Override
		public ExchangeRate getReversed(ExchangeRate rate) {
			return null;
		}

		@Override
		public CurrencyConversion getCurrencyConversion(CurrencyUnit currency) {
			return dummyConversion;
		}

		@Override
		public boolean isAvailable(CurrencyUnit base, CurrencyUnit term,
				ConversionContext conversionContext) {
			return false;
		}

		@Override
		public ExchangeRate getExchangeRate(CurrencyUnit base,
				CurrencyUnit term, ConversionContext conversionContext) {
			return null;
		}

		@Override
		public CurrencyConversion getCurrencyConversion(CurrencyUnit term,
				ConversionContext conversionContext) {
			return null;
		}

		@Override
		public boolean isAvailable(String baseCode, String termCode) {
			return false;
		}

		@Override
		public boolean isAvailable(String baseCode, String termCode,
				ConversionContext conversionContext) {
			return false;
		}

		@Override
		public ExchangeRate getExchangeRate(String baseCode, String termCode) {
			return null;
		}

		@Override
		public ExchangeRate getExchangeRate(String baseCode, String termCode,
				ConversionContext conversionContext) {
			return null;
		}

		@Override
		public CurrencyConversion getCurrencyConversion(String termCode) {
			return null;
		}

		@Override
		public CurrencyConversion getCurrencyConversion(String termCode,
				ConversionContext conversionContext) {
			return null;
		}

	}

	public final class DummyConversion implements CurrencyConversion {

		@Override
		public <T extends MonetaryAmount> T apply(T value) {
			return null;
		}

		@Override
		public CurrencyUnit getTermCurrency() {
			return null;
		}

		@Override
		public ConversionContext getConversionContext() {
			return ConversionContext.of("test", RateType.ANY);
		}

		@Override
		public ExchangeRate getExchangeRate(MonetaryAmount sourceAmount) {
			return null;
		}

		@Override
		public CurrencyConversion with(ConversionContext conversionContext) {
			return this;
		}

	}

}
