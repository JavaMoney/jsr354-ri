package org.javamoney.moneta;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;

import org.testng.annotations.Test;

public class NumberVerifierTest {

	@Test
	public void shouldIgnoreNumberWhenNumberIsNotInfinityOrNaN() {
		NumberVerifier.checkNoInfinityOrNaN(BigDecimal.ONE);
		NumberVerifier.checkNoInfinityOrNaN(1L);
		NumberVerifier.checkNoInfinityOrNaN(Float.MIN_NORMAL);
		NumberVerifier.checkNoInfinityOrNaN(Double.MIN_NORMAL);
	}

	@Test(expectedExceptions = ArithmeticException.class)
	public void shouldIReturnErrorWhenFloatIsNAN() {
		NumberVerifier.checkNoInfinityOrNaN(Float.NaN);
	}

	@Test(expectedExceptions = ArithmeticException.class)
	public void shouldIReturnErrorWhenFloatIsNegativeInfinite() {
		NumberVerifier.checkNoInfinityOrNaN(Float.NEGATIVE_INFINITY);
	}

	@Test(expectedExceptions = ArithmeticException.class)
	public void shouldIReturnErrorWhenFloatIsPositiveInfinite() {
		NumberVerifier.checkNoInfinityOrNaN(Float.POSITIVE_INFINITY);
	}

	@Test(expectedExceptions = ArithmeticException.class)
	public void shouldIReturnErrorWhenDoubleIsNAN() {
		NumberVerifier.checkNoInfinityOrNaN(Double.NaN);
	}

	@Test(expectedExceptions = ArithmeticException.class)
	public void shouldIReturnErrorWhenDoubleIsNegativeInfinite() {
		NumberVerifier.checkNoInfinityOrNaN(Double.NEGATIVE_INFINITY);
	}

	@Test(expectedExceptions = ArithmeticException.class)
	public void shouldIReturnErrorWhenDoubleIsPositiveInfinite() {
		NumberVerifier.checkNoInfinityOrNaN(Double.POSITIVE_INFINITY);
	}

	@Test
	public void shouldReturnsTrueFloatIsPositiveInfint() {
		assertTrue(NumberVerifier.isInfinityAndNotNaN(Float.POSITIVE_INFINITY));
	}

	@Test
	public void shouldReturnsTrueFloatIsNegativeInfint() {
		assertTrue(NumberVerifier.isInfinityAndNotNaN(Float.NEGATIVE_INFINITY));
	}
	@Test(expectedExceptions = ArithmeticException.class)
	public void shouldReturnsWhenFloatIsNAN() {
		assertTrue(NumberVerifier.isInfinityAndNotNaN(Float.NaN));
	}

	@Test
	public void shouldReturnsTrueDoubleIsPositiveInfint() {
		assertTrue(NumberVerifier.isInfinityAndNotNaN(Double.POSITIVE_INFINITY));
	}

	@Test
	public void shouldReturnsTrueDoubleIsNegativeInfint() {
		assertTrue(NumberVerifier.isInfinityAndNotNaN(Double.NEGATIVE_INFINITY));
	}
	@Test(expectedExceptions = ArithmeticException.class)
	public void shouldReturnsWhenDoubleIsNAN() {
		assertTrue(NumberVerifier.isInfinityAndNotNaN(Double.NaN));
	}

	@Test
	public void shouldReturnsFalseWhenIsNumberNotInfinity() {
		assertFalse(NumberVerifier.isInfinityAndNotNaN(Double.MAX_VALUE));
		assertFalse(NumberVerifier.isInfinityAndNotNaN(Float.MAX_VALUE));
		assertFalse(NumberVerifier.isInfinityAndNotNaN(10));
		assertFalse(NumberVerifier.isInfinityAndNotNaN(10L));
	}
}
