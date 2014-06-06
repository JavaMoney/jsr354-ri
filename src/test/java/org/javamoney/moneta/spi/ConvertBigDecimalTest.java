package org.javamoney.moneta.spi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.testng.Assert;
import org.testng.annotations.Test;




public class ConvertBigDecimalTest {

	private BigDecimal expectValue = BigDecimal.TEN;
	
	@Test
	public void ofIntegerTest() {
		Assert.assertEquals(ConvertBigDecimal.of(10), expectValue);
	}

	@Test
	public void ofLongTest() {
		Assert.assertEquals(ConvertBigDecimal.of(10l), expectValue);
	}

	@Test
	public void ofShortTest() {
		Assert.assertEquals(ConvertBigDecimal.of((short) 10), expectValue);
	}

	@Test
	public void ofByteTest() {
		Assert.assertEquals(ConvertBigDecimal.of((byte) 10), expectValue);
	}
	
	@Test
	public void ofAtomicLongTest() {
		Assert.assertEquals(ConvertBigDecimal.of(new AtomicLong(10l)), expectValue);
	}
	
	@Test
	public void ofAtomicIntegerTest() {
		Assert.assertEquals(ConvertBigDecimal.of(new AtomicInteger(10)), expectValue);
	}
	
	@Test
	public void ofFloatTest() {
		Assert.assertEquals(ConvertBigDecimal.of(10f).setScale(0), expectValue);
	}
	
	@Test
	public void ofDoubleTest() {
		Assert.assertEquals(ConvertBigDecimal.of(10d).setScale(0), expectValue);
	}
	@Test
	public void ofDefaultNumberValueTest() {
		Assert.assertEquals(ConvertBigDecimal.of(new DefaultNumberValue(10)).setScale(0), expectValue);
	}
	@Test
	public void ofBigIntegerTest() {
		Assert.assertEquals(ConvertBigDecimal.of(BigInteger.valueOf(10l)), expectValue);
	}
}
