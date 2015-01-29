/**
 * Copyright (c) 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.spi;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Test for the {@link org.javamoney.moneta.spi.MonetaryConfig} class.
 */
public class MonetaryConfigTest {

    @Test
    public void testGetConfig() throws Exception {
        assertNotNull(MonetaryConfig.getConfig());
        assertFalse(MonetaryConfig.getConfig().isEmpty());
    }

    @Test
    public void testConfigOverride() throws Exception {
        assertEquals(MonetaryConfig.getConfig().get("theWinner1"), "theWinner1");
        assertEquals(MonetaryConfig.getConfig().get("theWinner2"), "theWinner2");
        assertEquals(MonetaryConfig.getConfig().get("theWinner3"), "theWinner2");
    }

    @Test
    public void testConfigNormal() throws Exception {
        assertEquals(MonetaryConfig.getConfig().get("myTestValue"), "myTestValue");
    }
}