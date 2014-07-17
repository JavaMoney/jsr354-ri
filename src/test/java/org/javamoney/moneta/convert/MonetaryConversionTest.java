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
package org.javamoney.moneta.convert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.Objects;

import javax.money.MonetaryCurrencies;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;

import org.javamoney.moneta.convert.internal.ECBCurrentRateProvider;
import org.javamoney.moneta.spi.CompoundRateProvider;
import org.testng.annotations.Test;

public class MonetaryConversionTest {

    @Test
    public void testGetExchangeRateDefault() {
        ExchangeRateProvider prov = MonetaryConversions
                .getExchangeRateProvider();
        assertTrue(Objects.nonNull(prov));
        ExchangeRate rate = prov.getExchangeRate("CHF", "EUR");
        assertNotNull(rate);
    }

	@Test
	public void testGetExchangeRateProvider() {
		ExchangeRateProvider prov = MonetaryConversions
				.getExchangeRateProvider("ECB");
		assertTrue(Objects.nonNull(prov));
		assertEquals(ECBCurrentRateProvider.class, prov.getClass());
	}

	@Test
	public void testGetExchangeRateProvider_Chained() throws InterruptedException {
		ExchangeRateProvider prov = MonetaryConversions
				.getExchangeRateProvider("ECB", "IMF");
		assertTrue(Objects.nonNull(prov));
		assertEquals(CompoundRateProvider.class, prov.getClass());
		// Test rate provided by IMF (derived)
		Thread.sleep(5000L); // wait for provider to load...
		ExchangeRate r = prov.getExchangeRate(
				MonetaryCurrencies.getCurrency("USD"),
				MonetaryCurrencies.getCurrency("INR"));
		assertTrue(Objects.nonNull(r));
		assertTrue(r.isDerived());
		// Test rate provided by ECB
		r = prov.getExchangeRate(MonetaryCurrencies.getCurrency("EUR"),
				MonetaryCurrencies.getCurrency("CHF"));
		assertTrue(Objects.nonNull(r));
		assertFalse(r.isDerived());
	}

	@Test
	public void testGetSupportedProviderContexts() {
		Collection<String> types = MonetaryConversions.getProviderNames();
		assertNotNull(types);
		assertTrue(types.size() >= 1);
		assertTrue(types.contains("IMF"));
		assertTrue(types.contains("ECB"));
	}

}
