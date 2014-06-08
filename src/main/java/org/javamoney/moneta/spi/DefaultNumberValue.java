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
import java.util.Objects;

import javax.money.NumberValue;

/**
 * Default implementation of {@link NumberValue} based on {@link BigDecimal}.
 * 
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class DefaultNumberValue extends NumberValue {
	
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	/** The numeric value. */
	private final Number number;

    /**
     * The value 1, with a scale of 0.<br>
     * Backed by {@link BigDecimal#ONE}
     *
     * @since  0.8
     */
	public static final NumberValue ONE = new DefaultNumberValue(BigDecimal.ONE);
	
	public DefaultNumberValue(Number number) {
		Objects.requireNonNull(number, "Number required");
		this.number = number;
	}
	
	/**
	 * Creates a new instance of {@link NumberValue}, using the given number.
	 * 
	 * @param number
	 *            The numeric part, not null.
	 * @return A new instance of {@link NumberValue}.
	 */
	public static NumberValue of(Number number) {
		return new DefaultNumberValue(number);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.NumberValue#getNumberType()
	 */
	@Override
	public Class<?> getNumberType() {
		return this.number.getClass();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.NumberValue#getPrecision()
	 */
	@Override
	public int getPrecision() {
		return numberValue(BigDecimal.class).precision();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.NumberValue#getScale()
	 */
	@Override
	public int getScale() {
		return getBigDecimal(number).scale();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.NumberValue#getIntValue()
	 */
	@Override
	public int intValue() {
		return this.number.intValue();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.NumberValue#getIntValueExact()
	 */
	@Override
	public int intValueExact() {
		return getBigDecimal(number).intValueExact();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.NumberValue#getLongValue()
	 */
	@Override
	public long longValue() {
		return this.number.longValue();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.NumberValue#getLongValueExact()
	 */
	@Override
	public long longValueExact() {
		return getBigDecimal(number).longValueExact();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.NumberValue#getFloatValue()
	 */
	@Override
	public float floatValue() {
		return this.number.floatValue();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.NumberValue#getDoubleValue()
	 */
	@Override
	public double doubleValue() {
		return this.number.doubleValue();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.NumberValue#getDoubleValueExact()
	 */
	@Override
	public double doubleValueExact() {
		double d = this.number.doubleValue();
		if (d == Double.NEGATIVE_INFINITY || d == Double.POSITIVE_INFINITY) {
			throw new ArithmeticException("Unable to convert to double: "
					+ this.number);
		}
		return d;
	}

    /*
     * (non-Javadoc)
	 * @see javax.money.NumberValue#getAmountFractionNumerator()
     */
    @Override
    public long getAmountFractionNumerator(){
        BigDecimal bd = getBigDecimal(number).remainder(BigDecimal.ONE);
        return bd.movePointRight(getScale()).longValueExact();
    }

    /*
     * (non-Javadoc)
	 * @see javax.money.NumberValue#getAmountFractionDenominator()
     */
    @Override
    public long getAmountFractionDenominator(){
        return BigDecimal.valueOf(10).pow(getScale()).longValueExact();
    }

	/*
	 * (non-Javadoc)
	 * @see javax.money.NumberValue#getNumberValue(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Number> T numberValue(Class<T> numberType) {
		if (BigDecimal.class == numberType) {
			return (T) getBigDecimal(number);
		}
		else if (BigInteger.class == numberType) {
			return (T) getBigDecimal(number).toBigInteger();
		}
		else if (Double.class == numberType) {
			return (T) Double.valueOf(this.number.doubleValue());
		}
		else if (Float.class == numberType) {
			return (T) Float.valueOf(this.number.floatValue());
		}
		else if (Long.class == numberType) {
			return (T) Long.valueOf(this.number.longValue());
		}
		else if (Integer.class == numberType) {
			return (T) Integer.valueOf(this.number.intValue());
		}
		else if (Short.class == numberType) {
			return (T) Short.valueOf(this.number.shortValue());
		}
		else if (Byte.class == numberType) {
			return (T) Byte.valueOf(this.number.byteValue());
		}
		throw new IllegalArgumentException("Unsupported numeric type: "
				+ numberType);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.NumberValue#numberValueExact(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Number> T numberValueExact(Class<T> numberType) {
		if (BigDecimal.class == numberType) {
			return (T) getBigDecimal(number);
		}
		else if (BigInteger.class == numberType) {
			return (T) getBigDecimal(number).toBigIntegerExact();
		}
		else if (Double.class == numberType) {
			double d = this.number.doubleValue();
			if (d == Double.NEGATIVE_INFINITY || d == Double.POSITIVE_INFINITY) {
				throw new ArithmeticException(
						"Value not exact mappable to double: " + this.number);
			}
			return (T) Double.valueOf(d);
		}
		else if (Float.class == numberType) {
			float f = this.number.floatValue();
			if (f == Float.NEGATIVE_INFINITY || f == Float.POSITIVE_INFINITY) {
				throw new ArithmeticException(
						"Value not exact mappable to float: " + this.number);
			}
			return (T) Float.valueOf(f);
		}
		else if (Long.class == numberType) {
			return (T) Long.valueOf(getBigDecimal(number).longValueExact());
		}
		else if (Integer.class == numberType) {
			return (T) Integer.valueOf(getBigDecimal(number).intValueExact());
		}
		else if (Short.class == numberType) {
			return (T) Short.valueOf(getBigDecimal(number).shortValueExact());
		}
		else if (Byte.class == numberType) {
			return (T) Short.valueOf(getBigDecimal(number).byteValueExact());
		}
		throw new IllegalArgumentException("Unsupported numeric type: "
				+ numberType);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.valueOf(number);
	}

	/**
	 * Creates a {@link BigDecimal} from the given {@link Number} doing the valid conversion
	 * depending the type given.
	 * 
	 * @param num
	 *            the number type
	 * @return the corresponding {@link BigDecimal}
	 */
	protected static BigDecimal getBigDecimal(Number num) {
		return ConvertBigDecimal.of(num);
	}

}
