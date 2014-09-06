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
package org.javamoney.moneta.internal;

import org.javamoney.moneta.spi.ServicePriority;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests for DefaultMonetaryAmountsSingletonSpi.
 *
 * @author cm-rudolph
 */
public class DefaultMonetaryAmountsSingletonSpiTest{

    /**
     * Test method for
     * {@link org.javamoney.moneta.internal.DefaultMonetaryAmountsSingletonSpi#comparePriority(Object, Object)}
     * .
     */
    @Test
    public void testComparePriority(){
        Object service1 = new Service1();
        Object service2 = new Service2();
        assertTrue(DefaultMonetaryAmountsSingletonSpi.comparePriority(service1, service2) > 0);
        assertEquals(DefaultMonetaryAmountsSingletonSpi.comparePriority(service1, service1), 0);
        assertTrue(DefaultMonetaryAmountsSingletonSpi.comparePriority(service2, service1) < 0);
        assertTrue(DefaultMonetaryAmountsSingletonSpi.comparePriority(null, service2) > 0);
    }

    /**
     * Test class used, with prio 1.
     */
    @ServicePriority(1)
    private static class Service1{}

    /**
     * Test class used with prio 2.
     */
    @ServicePriority(2)
    private static class Service2{}
}