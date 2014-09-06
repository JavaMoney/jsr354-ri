package org.javamoney.moneta.internal;

import org.javamoney.moneta.ServicePriority;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class DefaultMonetaryAmountsSingletonSpiTest {
    /**
     * Test method for
     * {@link org.javamoney.moneta.internal.DefaultMonetaryAmountsSingletonSpi#comparePriority(Object, Object)}
     * .
     */
    @Test
    public void testComparePriority() {
        Object service1 = new Service1();
        Object service2 = new Service2();
        assertTrue(DefaultMonetaryAmountsSingletonSpi.comparePriority(service1, service2) > 0);
        assertTrue(DefaultMonetaryAmountsSingletonSpi.comparePriority(service1, service1) == 0);
        assertTrue(DefaultMonetaryAmountsSingletonSpi.comparePriority(service2, service1) < 0);
        assertTrue(DefaultMonetaryAmountsSingletonSpi.comparePriority(null, service2) > 0);
    }

    @ServicePriority(1)
    private static class Service1 {
    }

    @ServicePriority(2)
    private static class Service2 {
    }
}
