package org.javamoney.moneta.spi;

import static junit.framework.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;

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


}
