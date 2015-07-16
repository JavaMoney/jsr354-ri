/**
 * Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.internal.convert.ecb;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Objects;

import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;

import org.javamoney.moneta.convert.ExchangeRateType;
import org.javamoney.moneta.internal.convert.IdentityRateProvider;
import org.testng.annotations.Test;

public class ExchangeRateTypeTest {

    @Test
    public void shouldReturnsECBCurrentRateProvider() {
        ExchangeRateProvider prov = MonetaryConversions
                .getExchangeRateProvider(ExchangeRateType.ECB);
        assertTrue(Objects.nonNull(prov));
        assertEquals(ECBCurrentRateProvider.class, prov.getClass());
    }

    @Test
    public void shouldReturnsECBHistoricRateProvider() {
        ExchangeRateProvider prov = MonetaryConversions
                .getExchangeRateProvider(ExchangeRateType.ECB_HIST);
        assertTrue(Objects.nonNull(prov));
        assertEquals(ECBHistoricRateProvider.class, prov.getClass());
    }

    @Test
    public void shouldReturnsECBHistoric90RateProvider() {
        ExchangeRateProvider prov = MonetaryConversions
                .getExchangeRateProvider(ExchangeRateType.ECB_HIST90);
        assertTrue(Objects.nonNull(prov));
        assertEquals(ECBHistoric90RateProvider.class, prov.getClass());
    }


    @Test
    public void shouldReturnsIdentityRateProvider() {
        ExchangeRateProvider prov = MonetaryConversions
                .getExchangeRateProvider(ExchangeRateType.IDENTITY);
        assertTrue(Objects.nonNull(prov));
        assertEquals(IdentityRateProvider.class, prov.getClass());
    }

}
