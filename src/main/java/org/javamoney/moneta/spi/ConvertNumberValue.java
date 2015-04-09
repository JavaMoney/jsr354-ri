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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility for converting numeric types.
 */
enum ConvertNumberValue {
	INSTANCE;

	public static <T extends Number> T of(Class<T> numberType, Number number) {
		return INSTANCE.convert(numberType, number);
	}
	
	public static <T extends Number> T ofExact(Class<T> numberType, Number number) {
		return INSTANCE.convertExact(numberType, number);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Number> T convert(Class<T> numberType, Number number) {
		
		return (T) Optional.ofNullable(convertIMap.get(numberType)).orElseThrow(() -> new IllegalArgumentException("Unsupported numeric type: "
				+ numberType)).convert(numberType, number);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Number> T convertExact(Class<T> numberType, Number number) {
		return (T) Optional.ofNullable(convertIMap.get(numberType)).orElseThrow(() -> new IllegalArgumentException("Unsupported numeric type: "
				+ numberType)).convertExact(numberType, number);
	}
	
	interface ConvertNumberValueI<T extends Number> {
		<E extends Number> T convert(Class<E> numberType, Number number);
		<E extends Number> T convertExact(Class<E> numberType, Number number);
	}
	
	static class ConvertNumberValueBigDecimal implements ConvertNumberValueI<BigDecimal> {

		@Override
		public <E extends Number> BigDecimal convert(Class<E> numberType,
				Number number) {
			return ConvertBigDecimal.of(number);
		}

		@Override
		public <E extends Number> BigDecimal convertExact(Class<E> numberType,
				Number number) {
			return ConvertBigDecimal.of(number);
		}

	}
	
	static class ConvertNumberValueBigInteger implements ConvertNumberValueI<BigInteger> {

		@Override
		public <E extends Number> BigInteger convert(Class<E> numberType,
				Number number) {
			return ConvertBigDecimal.of(number).toBigInteger();
		}

		@Override
		public <E extends Number> BigInteger convertExact(Class<E> numberType,
				Number number) {
			return ConvertBigDecimal.of(number).toBigIntegerExact();
		}

	}
	
	static class ConvertNumberValueDouble implements ConvertNumberValueI<Double> {

		@Override
		public <E extends Number> Double convert(Class<E> numberType,
				Number number) {
			return number.doubleValue();
		}

		@Override
		public <E extends Number> Double convertExact(Class<E> numberType,
				Number number) {
			double d = number.doubleValue();
			if (d == Double.NEGATIVE_INFINITY || d == Double.POSITIVE_INFINITY) {
				throw new ArithmeticException(
						"Value not exact mappable to double: " + number);
			}
			return d;
		}

	}
	
	static class ConvertNumberValueFloat implements ConvertNumberValueI<Float> {

		@Override
		public <E extends Number> Float convert(Class<E> numberType,
				Number number) {
			return number.floatValue();
		}

		@Override
		public <E extends Number> Float convertExact(Class<E> numberType,
				Number number) {
			float f = number.floatValue();
			if (f == Float.NEGATIVE_INFINITY || f == Float.POSITIVE_INFINITY) {
				throw new ArithmeticException(
						"Value not exact mappable to float: " + number);
			}
			return f;
		}

	}
	
	static class ConvertNumberValueLong implements ConvertNumberValueI<Long> {

		@Override
		public <E extends Number> Long convert(Class<E> numberType,
				Number number) {
			return number.longValue();
		}

		@Override
		public <E extends Number> Long convertExact(Class<E> numberType,
				Number number) {
			return ConvertBigDecimal.of(number).longValueExact();
		}

	}
	static class ConvertNumberValueInteger implements ConvertNumberValueI<Integer> {

		@Override
		public <E extends Number> Integer convert(Class<E> numberType,
				Number number) {
			return number.intValue();
		}

		@Override
		public <E extends Number> Integer convertExact(Class<E> numberType,
				Number number) {
			
			return ConvertBigDecimal.of(number).intValueExact();
		}

	}
	static class ConvertNumberValueShort implements ConvertNumberValueI<Short> {

		@Override
		public <E extends Number> Short convert(Class<E> numberType,
				Number number) {
			return number.shortValue();
		}

		@Override
		public <E extends Number> Short convertExact(Class<E> numberType,
				Number number) {
			return ConvertBigDecimal.of(number).shortValueExact();
		}

	}
	static class ConvertNumberValueByte implements ConvertNumberValueI<Byte> {

		@Override
		public <E extends Number> Byte convert(Class<E> numberType,
				Number number) {
			return number.byteValue();
		}

		@Override
		public <E extends Number> Byte convertExact(Class<E> numberType,
				Number number) {
			
			return ConvertBigDecimal.of(number).byteValueExact();
		}

	}
	
	static class ConvertNumberValueAtomicInteger implements ConvertNumberValueI<AtomicInteger> {

		@Override
		public <E extends Number> AtomicInteger convert(Class<E> numberType,
				Number number) {
			return new AtomicInteger(number.intValue());
		}

		@Override
		public <E extends Number> AtomicInteger convertExact(
				Class<E> numberType, Number number) {
			return new AtomicInteger(ConvertBigDecimal.of(
                    number).intValueExact());
		}

	}
	
	static class ConvertNumberValueAtomicLong implements ConvertNumberValueI<AtomicLong> {

		@Override
		public <E extends Number> AtomicLong convert(Class<E> numberType,
				Number number) {
			return new AtomicLong(number.longValue());
		}

		@Override
		public <E extends Number> AtomicLong convertExact(Class<E> numberType,
				Number number) {
			return new AtomicLong(ConvertBigDecimal.of(number).longValueExact());
		}

	}
	
	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends Number>, ConvertNumberValueI> convertIMap;

	{
		convertIMap = new HashMap<>();
		convertIMap.put(BigDecimal.class, new ConvertNumberValueBigDecimal());
		convertIMap.put(BigInteger.class, new ConvertNumberValueBigInteger());
		convertIMap.put(Float.class, new ConvertNumberValueFloat());
		convertIMap.put(Double.class, new ConvertNumberValueDouble());
		convertIMap.put(Long.class, new ConvertNumberValueLong());
		convertIMap.put(Integer.class, new ConvertNumberValueInteger());
		convertIMap.put(Short.class, new ConvertNumberValueShort());
		convertIMap.put(Byte.class, new ConvertNumberValueByte());
		convertIMap.put(AtomicInteger.class, new ConvertNumberValueAtomicInteger());
		convertIMap.put(AtomicLong.class, new ConvertNumberValueAtomicLong());
	}
}