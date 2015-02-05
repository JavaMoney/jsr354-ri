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

import org.javamoney.moneta.Money;
import org.testng.annotations.Test;

import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Test that tries to compare the value returned by IMF and ECB provider.
 */
public class CurrencyProviderTest {

    @Test
    public void testECB() {
        ExchangeRateProvider ecbRateProvider = MonetaryConversions.getExchangeRateProvider("ECB");
        ExchangeRateProvider imfRateProvider = MonetaryConversions.getExchangeRateProvider("IMF");

        CurrencyConversion ecbDollarConvertion = ecbRateProvider.getCurrencyConversion("USD");
        CurrencyConversion imfDollarConversion = imfRateProvider.getCurrencyConversion("USD");

        try {
            // Wait for IMF provider to load
            Thread.sleep(10000L);
            for (String currency : new String[]{"INR", "CHF", "BRL"}) {
                Money money = Money.of(10, currency);
                System.out.println("ECB : " + money.with(ecbDollarConvertion));
                System.out.println("IMF : " + money.with(imfDollarConversion));
                assertEquals(money.with(ecbDollarConvertion).getNumber().doubleValue(), money.with(imfDollarConversion).getNumber().doubleValue(), 0.1d);
            }
        } catch (InterruptedException e) {
            // This test may fail, if the network is slow or not available, so only write the exception as of now...
            e.printStackTrace();
        }

    }
}
