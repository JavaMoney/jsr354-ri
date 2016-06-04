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
package org.javamoney.moneta.spi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * Tests for {@link org.javamoney.moneta.spi.ConvertNumberValue}.
 */
public class ConvertNumberValueTest {

	
	@Test
	public void integerTest() {
		Number valueTest = 20d;
		Integer expectedValue = 20;
		Integer number = ConvertNumberValue.of(Integer.class, valueTest);
		Integer numberExact = ConvertNumberValue.ofExact(Integer.class, valueTest);
		
		Assert.assertTrue(number != null);
		Assert.assertTrue(numberExact != null);
		
		Assert.assertEquals(expectedValue, number);
		Assert.assertEquals(expectedValue, numberExact);
	}
	
	@Test
	public void longTest() {
		Number valueTest = 20d;
		Long expectedValue = 20l;
		Long number = ConvertNumberValue.of(Long.class, valueTest);
		Long numberExact = ConvertNumberValue.ofExact(Long.class, valueTest);
		
		Assert.assertTrue(number != null);
		Assert.assertTrue(numberExact != null);
		
		Assert.assertEquals(expectedValue, number);
		Assert.assertEquals(expectedValue, numberExact);
	}
	
	@Test
	public void byteTest() {
		Number valueTest = 20d;
		Byte expectedValue = 20;
		Byte number = ConvertNumberValue.of(Byte.class, valueTest);
		Byte numberExact = ConvertNumberValue.ofExact(Byte.class, valueTest);
		
		Assert.assertTrue(number != null);
		Assert.assertTrue(numberExact != null);
		
		Assert.assertEquals(expectedValue, number);
		Assert.assertEquals(expectedValue, numberExact);
	}
	
	@Test
	public void shortTest() {
		Number valueTest = 20d;
		Short expectedValue = 20;
		Short number = ConvertNumberValue.of(Short.class, valueTest);
		Short numberExact = ConvertNumberValue.ofExact(Short.class, valueTest);
		
		Assert.assertTrue(number != null);
		Assert.assertTrue(numberExact != null);
		
		Assert.assertEquals(expectedValue, number);
		Assert.assertEquals(expectedValue, numberExact);
	}
	
	@Test
	public void floatTest() {
		Number valueTest = 20.2f;
		Float expectedValue = 20.2f;
		Float number = ConvertNumberValue.of(Float.class, valueTest);
		Float numberExact = ConvertNumberValue.ofExact(Float.class, valueTest);
		
		Assert.assertTrue(number != null);
		Assert.assertTrue(numberExact != null);
		
		Assert.assertEquals(expectedValue, number);
		Assert.assertEquals(expectedValue, numberExact);
	}
	
	@Test
	public void doubleTest() {
		Number valueTest = 20.3456d;
		Double expectedValue = 20.3456d;
		Double number = ConvertNumberValue.of(Double.class, valueTest);
		Double numberExact = ConvertNumberValue.ofExact(Double.class, valueTest);
		
		Assert.assertTrue(number != null);
		Assert.assertTrue(numberExact != null);
		
		Assert.assertEquals(expectedValue, number);
		Assert.assertEquals(expectedValue, numberExact);
	}
	
	@Test
	public void bigIntegerTest() {
		Number valueTest = BigInteger.TEN;
		BigInteger expectedValue = BigInteger.TEN;
		BigInteger number = ConvertNumberValue.of(BigInteger.class, valueTest);
		BigInteger numberExact = ConvertNumberValue.ofExact(BigInteger.class, valueTest);
		
		Assert.assertTrue(number != null);
		Assert.assertTrue(numberExact != null);
		
		Assert.assertEquals(expectedValue, number);
		Assert.assertEquals(expectedValue, numberExact);
	}
	
	@Test
	public void bigDecimalTest() {
		Number valueTest = BigDecimal.valueOf(20.3456d);
		BigDecimal expectedValue = BigDecimal.valueOf(20.3456d);
		BigDecimal number = ConvertNumberValue.of(BigDecimal.class, valueTest);
		BigDecimal numberExact = ConvertNumberValue.ofExact(BigDecimal.class, valueTest);
		
		Assert.assertTrue(number != null);
		Assert.assertTrue(numberExact != null);
		
		Assert.assertEquals(expectedValue, number);
		Assert.assertEquals(expectedValue, numberExact);
	}
	
	@Test
	public void atomicIntegerTest() {
		Number valueTest = 20d;
		AtomicInteger expectedValue = new AtomicInteger(20);
		AtomicInteger number = ConvertNumberValue.of(AtomicInteger.class, valueTest);
		AtomicInteger numberExact = ConvertNumberValue.ofExact(AtomicInteger.class, valueTest);
		
		Assert.assertTrue(number != null);
		Assert.assertTrue(numberExact != null);
		
		Assert.assertEquals(expectedValue.get(), number.get());
		Assert.assertEquals(expectedValue.get(), numberExact.get());
	}
	
	@Test
	public void atomicLongTest() {
		Number valueTest = 20d;
		AtomicLong expectedValue = new AtomicLong(20l);
		AtomicLong number = ConvertNumberValue.of(AtomicLong.class, valueTest);
		AtomicLong numberExact = ConvertNumberValue.ofExact(AtomicLong.class, valueTest);
		
		Assert.assertTrue(number != null);
		Assert.assertTrue(numberExact != null);
		
		Assert.assertEquals(expectedValue.get(), number.get());
		Assert.assertEquals(expectedValue.get(), numberExact.get());
	}
}
