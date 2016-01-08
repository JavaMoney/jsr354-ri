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
package org.javamoney.moneta.spi;

import static junit.framework.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.money.NumberValue;

import org.testng.annotations.Test;

public class DefaultNumberValueTest {

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnNPEWhenUseNullOnOfMethod() {
		DefaultNumberValue.of(null);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void shouldReturnNPEWhenUseNullOnConstructor() {
		new DefaultNumberValue(null);
	}

	@Test
	public void shouldReturnNumberType() {
		NumberValue numberValueLong = DefaultNumberValue.of(10L);
		NumberValue numberValueInteger = DefaultNumberValue.of(10);
		NumberValue numberValueDouble = DefaultNumberValue.of(10D);
		NumberValue numberValueBigDecimal = DefaultNumberValue.of(BigDecimal.TEN);
		NumberValue numberValueBigInteger = DefaultNumberValue.of(BigInteger.TEN);

		assertEquals(numberValueLong.getNumberType(), Long.class);
		assertEquals(numberValueInteger.getNumberType(), Integer.class);
		assertEquals(numberValueDouble.getNumberType(), Double.class);
		assertEquals(numberValueBigDecimal.getNumberType(), BigDecimal.class);
		assertEquals(numberValueBigInteger.getNumberType(), BigInteger.class);
	}

	@Test
	public void shouldReturnScale() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(132.21));
		assertEquals(numberValue.getScale(), 2);
	}

	@Test
	public void shouldReturnPrecision() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(132.21));
		assertEquals(numberValue.getPrecision(), 5);
	}

	@Test
	public void shouldReturnIntValue() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(132.21));
		assertEquals(numberValue.intValue(), 132);
	}

	@Test
	public void shouldReturnIntValueExact() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(132));
		assertEquals(numberValue.intValueExact(), 132);
	}

	@Test(expectedExceptions = ArithmeticException.class)
	public void shouldReturnErrorWhenIntValueExactMustBeTruncated() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(132.12));
		numberValue.intValueExact();
	}

	@Test
	public void shouldReturnLong() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(132.21));
		assertEquals(numberValue.longValue(), 132L);
	}

	@Test
	public void shouldReturnLongValueExact() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(132));
		assertEquals(numberValue.longValueExact(), 132L);
	}

	@Test(expectedExceptions = ArithmeticException.class)
	public void shouldReturnErrorWhenLongValueExactMustBeTruncated() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(132.12));
		numberValue.longValueExact();
	}

	@Test
	public void shouldReturnFloat() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(132.21));
		assertEquals(numberValue.floatValue(), 132.21F);
	}


	@Test
	public void shouldReturnDouble() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(132.21));
		assertEquals(numberValue.doubleValue(), 132.21);
	}

	@Test
	public void shouldReturnDoubleExact() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(132.21));
		assertEquals(numberValue.doubleValueExact(), 132.21);
	}

	@Test
	public void shouldReturnAmountFractionNumerator() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(132.21));
		assertEquals(132L, numberValue.getAmountFractionNumerator());
	}

	@Test
	public void shouldReturnAmountFractionDenominator() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(132.21));
		assertEquals(21L, numberValue.getAmountFractionDenominator());
	}

	@Test
	public void shouldReturnNumberValue() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(21));

		assertEquals(Long.valueOf(21L), numberValue.numberValue(Long.class));
		assertEquals(Integer.valueOf(21),numberValue.numberValue(Integer.class));
		assertEquals(Float.valueOf(21),numberValue.numberValue(Float.class));
		assertEquals(Byte.valueOf((byte)21),numberValue.numberValue(Byte.class));
		assertEquals(Short.valueOf((short)21),numberValue.numberValue(Short.class));
		assertEquals(new AtomicLong(21L).get(),numberValue.numberValue(AtomicLong.class).get());
		assertEquals(new AtomicInteger(21).get(),numberValue.numberValue(AtomicInteger.class).get());
		assertEquals(BigDecimal.valueOf(21L),numberValue.numberValue(BigDecimal.class));
		assertEquals(BigInteger.valueOf(21L),numberValue.numberValue(BigInteger.class));

	}

	@Test
	public void shouldReturnNumberValueExact() {
		NumberValue numberValue = DefaultNumberValue.of(BigDecimal.valueOf(21));

		assertEquals(Long.valueOf(21L), numberValue.numberValueExact(Long.class));
		assertEquals(Integer.valueOf(21),numberValue.numberValueExact(Integer.class));
		assertEquals(Float.valueOf(21),numberValue.numberValueExact(Float.class));
		assertEquals(Byte.valueOf((byte)21),numberValue.numberValueExact(Byte.class));
		assertEquals(Short.valueOf((short)21),numberValue.numberValueExact(Short.class));
		assertEquals(new AtomicLong(21L).get(),numberValue.numberValueExact(AtomicLong.class).get());
		assertEquals(new AtomicInteger(21).get(),numberValue.numberValueExact(AtomicInteger.class).get());
		assertEquals(BigDecimal.valueOf(21L),numberValue.numberValueExact(BigDecimal.class));
		assertEquals(BigInteger.valueOf(21L),numberValue.numberValueExact(BigInteger.class));

	}


}

