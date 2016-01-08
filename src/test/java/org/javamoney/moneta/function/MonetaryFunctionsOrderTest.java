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
package org.javamoney.moneta.function;

import static org.javamoney.moneta.function.MonetaryFunctions.sortCurrencyUnit;
import static org.javamoney.moneta.function.MonetaryFunctions.sortCurrencyUnitDesc;
import static org.javamoney.moneta.function.MonetaryFunctions.sortNumber;
import static org.javamoney.moneta.function.MonetaryFunctions.sortNumberDesc;
import static org.javamoney.moneta.function.StreamFactory.BRAZILIAN_REAL;
import static org.javamoney.moneta.function.StreamFactory.DOLLAR;
import static org.javamoney.moneta.function.StreamFactory.currencies;

import java.math.BigDecimal;
import javax.money.MonetaryAmount;
import junit.framework.Assert;

import org.testng.annotations.Test;

public class MonetaryFunctionsOrderTest {
	
    @Test
    public void sortCurrencyUnitTest() {
        MonetaryAmount money = currencies().sorted(sortCurrencyUnit())
                .findFirst().get();
        Assert.assertEquals(BRAZILIAN_REAL, money.getCurrency());
    }

    @Test
    public void sortCurrencyUnitDescTest() {
        MonetaryAmount money = currencies().sorted(sortCurrencyUnitDesc())
                .findFirst().get();
        Assert.assertEquals(DOLLAR, money.getCurrency());
    }

    @Test
    public void sortorderNumberTest() {
        MonetaryAmount money = currencies().sorted(sortNumber())
                .findFirst().get();
        Assert.assertEquals(BigDecimal.ZERO, money.getNumber().numberValue(BigDecimal.class));
    }

    @Test
    public void sortorderNumberDescTest() {
        MonetaryAmount money = currencies().sorted(sortNumberDesc())
                .findFirst().get();
        Assert.assertEquals(BigDecimal.TEN, money.getNumber().numberValue(BigDecimal.class));
    }

    @Test
    public void sortCurrencyUnitAndNumberTest() {
        MonetaryAmount money = currencies().sorted(sortCurrencyUnit().thenComparing(sortNumber()))
                .findFirst().get();

        Assert.assertEquals(BRAZILIAN_REAL, money.getCurrency());
        Assert.assertEquals(BigDecimal.ZERO, money.getNumber().numberValue(BigDecimal.class));
    }
}
