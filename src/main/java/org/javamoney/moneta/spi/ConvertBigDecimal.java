package org.javamoney.moneta.spi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.money.NumberValue;

enum ConvertBigDecimal {
	INTEGER {
		@Override
		BigDecimal getDecimal(Number num) {
			return BigDecimal.valueOf(num.longValue());
		}
	}, FLUCTUAGE {
		@Override
		BigDecimal getDecimal(Number num) {
			return new BigDecimal(num.toString());
		}
	}, BIGINTEGER {
		@Override
		BigDecimal getDecimal(Number num) {
			return new BigDecimal((BigInteger) num);
		}
	}, NUMBERVALUE {
		@Override
		BigDecimal getDecimal(Number num) {
			BigDecimal result = ((NumberValue)num).numberValue(BigDecimal.class);
			return isScaleZero(result);
		}
	}, BIGDECIMAL {
		@Override
		BigDecimal getDecimal(Number num) {
			BigDecimal result = ((BigDecimal)num);
			return isScaleZero(result);
		}
	}, BIGDECIMAL_EXTENDS {
		@Override
		BigDecimal getDecimal(Number num) {
			BigDecimal result = ((BigDecimal)num).stripTrailingZeros();
			return isScaleZero(result);
		}
	},
	DEFAULT {
		@Override
		BigDecimal getDecimal(Number num) {
			BigDecimal result = null;
			try {
				result = new BigDecimal(num.toString());
			} catch (NumberFormatException e) {
			}
			result = Optional.ofNullable(result).orElse(
					BigDecimal.valueOf(num.doubleValue()));
			return isScaleZero(result);
		}
	};
	
	
	abstract BigDecimal getDecimal(Number num);
	
	static BigDecimal of(Number num) {
		Objects.requireNonNull(num, "Number is required.");
		return factory(num).getDecimal(num);
	}

	private static ConvertBigDecimal factory(Number num) {
		if (INSTEGERS.contains(num.getClass())) {
			return INTEGER;
		}

		if (FLOATINGS.contains(num.getClass())) {
			return FLUCTUAGE;
		}

		if (num instanceof BigInteger) {
			return BIGINTEGER;
		}

		if (num instanceof NumberValue) {
			return NUMBERVALUE;
		}
		if (BigDecimal.class.equals(num.getClass())) {
			return BIGDECIMAL;
		}

		if (num instanceof BigDecimal) {
			return BIGDECIMAL_EXTENDS;
		}
		return DEFAULT;
	}
	
	private static List<Class<? extends Number>> INSTEGERS = Arrays.asList(
			Long.class, Integer.class, Short.class, Byte.class,
			AtomicLong.class, AtomicInteger.class);
	
	private static List<Class<? extends Number>> FLOATINGS = Arrays.asList(
			Float.class, Double.class);
	
	private static BigDecimal isScaleZero(BigDecimal result) {
		if (result.signum() == 0) {
			return BigDecimal.ZERO;
		}
		if (result.scale() > 0) {
			return result.stripTrailingZeros();
		}
		return result;
	}
}
