package org.javamoney.moneta.spi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
	
	class ConvertNumberValueBigDecimal implements ConvertNumberValueI<BigDecimal> {

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
	
	class ConvertNumberValueBigInteger implements ConvertNumberValueI<BigInteger> {

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
	
	class ConvertNumberValueDouble implements ConvertNumberValueI<Double> {

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
			return Double.valueOf(d);
		}

	}
	
	class ConvertNumberValueFloat implements ConvertNumberValueI<Float> {

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
			return Float.valueOf(f);
		}

	}
	
	class ConvertNumberValueLong implements ConvertNumberValueI<Long> {

		@Override
		public <E extends Number> Long convert(Class<E> numberType,
				Number number) {
			return number.longValue();
		}

		@Override
		public <E extends Number> Long convertExact(Class<E> numberType,
				Number number) {
			return Long.valueOf(ConvertBigDecimal.of(number).longValueExact());
		}

	}
	class ConvertNumberValueInteger implements ConvertNumberValueI<Integer> {

		@Override
		public <E extends Number> Integer convert(Class<E> numberType,
				Number number) {
			return number.intValue();
		}

		@Override
		public <E extends Number> Integer convertExact(Class<E> numberType,
				Number number) {
			
			return Integer.valueOf(ConvertBigDecimal.of(number).intValueExact());
		}

	}
	class ConvertNumberValueShort implements ConvertNumberValueI<Short> {

		@Override
		public <E extends Number> Short convert(Class<E> numberType,
				Number number) {
			return number.shortValue();
		}

		@Override
		public <E extends Number> Short convertExact(Class<E> numberType,
				Number number) {
			return Short.valueOf(ConvertBigDecimal.of(number).shortValueExact());
		}

	}
	class ConvertNumberValueByte implements ConvertNumberValueI<Byte> {

		@Override
		public <E extends Number> Byte convert(Class<E> numberType,
				Number number) {
			return number.byteValue();
		}

		@Override
		public <E extends Number> Byte convertExact(Class<E> numberType,
				Number number) {
			
			return Byte.valueOf(ConvertBigDecimal.of(number).byteValueExact());
		}

	}
	
	class ConvertNumberValueAtomicInteger implements ConvertNumberValueI<AtomicInteger> {

		@Override
		public <E extends Number> AtomicInteger convert(Class<E> numberType,
				Number number) {
			return new AtomicInteger(number.intValue());
		}

		@Override
		public <E extends Number> AtomicInteger convertExact(
				Class<E> numberType, Number number) {
			return new AtomicInteger(Integer.valueOf(ConvertBigDecimal.of(
					number).intValueExact()));
		}

	}
	
	class ConvertNumberValueAtomicLong implements ConvertNumberValueI<AtomicLong> {

		@Override
		public <E extends Number> AtomicLong convert(Class<E> numberType,
				Number number) {
			return new AtomicLong(number.longValue());
		}

		@Override
		public <E extends Number> AtomicLong convertExact(Class<E> numberType,
				Number number) {
			return new AtomicLong(Long.valueOf(ConvertBigDecimal.of(number).longValueExact()));
		}

	}
	
	@SuppressWarnings("rawtypes")
	private Map<Class<? extends Number>, ConvertNumberValueI> convertIMap;

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