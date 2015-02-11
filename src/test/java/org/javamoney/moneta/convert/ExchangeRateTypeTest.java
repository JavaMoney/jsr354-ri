package org.javamoney.moneta.convert;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Objects;

import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.MonetaryConversions;

import org.javamoney.moneta.ExchangeRateType;
import org.javamoney.moneta.convert.internal.ECBCurrentRateProvider;
import org.javamoney.moneta.convert.internal.ECBHistoric90RateProvider;
import org.javamoney.moneta.convert.internal.ECBHistoricRateProvider;
import org.javamoney.moneta.convert.internal.IMFRateProvider;
import org.javamoney.moneta.convert.internal.IdentityRateProvider;
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
    public void shouldReturnsIMFRateProvider() {
        ExchangeRateProvider prov = MonetaryConversions
                .getExchangeRateProvider(ExchangeRateType.IMF);
        assertTrue(Objects.nonNull(prov));
        assertEquals(IMFRateProvider.class, prov.getClass());
    }

    @Test
    public void shouldReturnsIdentityRateProvider() {
        ExchangeRateProvider prov = MonetaryConversions
                .getExchangeRateProvider(ExchangeRateType.IDENTITY);
        assertTrue(Objects.nonNull(prov));
        assertEquals(IdentityRateProvider.class, prov.getClass());
    }

}
