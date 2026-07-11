/*
  Copyright (c) 2012, 2026, Werner Keil and others by the @author tag.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
 */
package org.javamoney.moneta;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;

import javax.money.*;

import org.javamoney.moneta.spi.DefaultNumberValue;
import org.testng.annotations.Test;

public class MoneyTrailingZerosTest {

    private static final CurrencyUnit USD = Monetary.getCurrency("USD");

    @Test
    public void preservesScaleFromBigDecimal() {
        Money m = Money.of(new BigDecimal("1.420"), USD);
        assertEquals(m.getScale(), 3);
        assertEquals(m.getPrecision(), 4);
        assertEquals(m.getNumber().getScale(), 3);
        assertEquals(m.getNumber().getPrecision(), 4);
        assertEquals(m.getNumber().numberValue(BigDecimal.class), new BigDecimal("1.420"));
        assertEquals(m.getNumber().numberValueExact(BigDecimal.class), new BigDecimal("1.420"));
    }

    @Test
    public void preservesScaleThroughParseRoundTrip() {
        Money m = Money.of(new BigDecimal("1.420"), USD);
        Money parsed = Money.parse(m.toString());
        assertEquals(parsed.getScale(), 3);
        assertEquals(parsed.getNumber().numberValue(BigDecimal.class), new BigDecimal("1.420"));
    }

    @Test
    public void preservesScaleViaNumberOverload() {
        Number number = new BigDecimal("1.420");
        Money m = Money.of(number, USD);
        assertEquals(m.getNumber().numberValue(BigDecimal.class), new BigDecimal("1.420"));
    }

    @Test
    public void preservesScaleFromNumberValueInput() {
        Money m = Money.of(DefaultNumberValue.of(new BigDecimal("1.420")), USD);
        assertEquals(m.getNumber().numberValue(BigDecimal.class), new BigDecimal("1.420"));
    }

    @Test
    public void preservesScaleViaCurrencyCode() {
        Money byCode = Money.of(new BigDecimal("1.420"), "USD");
        assertEquals(byCode.getNumber().numberValue(BigDecimal.class), new BigDecimal("1.420"));
    }

    @Test
    public void preservesScaleViaExplicitContext() {
        MonetaryContext ctx = MonetaryContextBuilder.of(Money.class).setPrecision(64).build();
        Money withCtx = Money.of(new BigDecimal("1.420"), "USD", ctx);
        assertEquals(withCtx.getNumber().numberValue(BigDecimal.class), new BigDecimal("1.420"));
    }

    @Test
    public void preservesScaleViaTypedFactory() {
        MonetaryAmountFactory<Money> factory = Monetary.getAmountFactory(Money.class);
        Money m = factory.setCurrency(USD).setNumber(new BigDecimal("1.420")).create();
        assertEquals(m.getNumber().numberValue(BigDecimal.class), new BigDecimal("1.420"));
    }

    @Test
    public void preservesScaleThroughGetFactoryRoundTrip() {
        Money m = Money.of(new BigDecimal("1.420"), USD);
        Money rebuilt = m.getFactory().create();
        assertEquals(rebuilt.getNumber().numberValue(BigDecimal.class), new BigDecimal("1.420"));
    }

    @Test
    public void preservesScaleFromOtherMonetaryAmount() {
        RoundedMoney source = RoundedMoney.of(new BigDecimal("1.420"), USD);
        Money m = Money.from(source);
        assertEquals(m.getNumber().numberValue(BigDecimal.class), new BigDecimal("1.420"));
    }

    @Test
    public void preservesScaleFromOfMinor() {
        Money m = Money.ofMinor(USD, 1200, 2);
        assertEquals(m.getScale(), 2);
        assertEquals(m.getNumber().numberValue(BigDecimal.class), new BigDecimal("12.00"));
        assertEquals(m.toString(), "USD 12.00");
    }

    @Test
    public void preservesScaleOfArithmeticResult() {
        Money product = Money.of(new BigDecimal("2.50"), USD).multiply(new BigDecimal("2"));
        assertEquals(product.getNumber().numberValue(BigDecimal.class), new BigDecimal("5.00"));
    }

    @Test
    public void fractionPartsReflectPreservedScale() {
        NumberValue value = Money.of(new BigDecimal("1.420"), USD).getNumber();
        assertEquals(value.getAmountFractionNumerator(), 420L);
        assertEquals(value.getAmountFractionDenominator(), 1000L);
    }

    @Test
    public void equalityAndComparisonIgnoreTrailingZeros() {
        Money withZeros = Money.of(new BigDecimal("1.420"), USD);
        Money withoutZeros = Money.of(new BigDecimal("1.42"), USD);

        assertEquals(withZeros, withoutZeros);
        assertEquals(withZeros.hashCode(), withoutZeros.hashCode());
        assertTrue(withZeros.isEqualTo(withoutZeros));
        assertEquals(withZeros.compareTo(withoutZeros), 0);
    }
}
