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
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.NumberValue;

/**
 * Default implementation of {@link NumberValue} based on {@link BigDecimal}.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public final class DefaultNumberValue extends NumberValue {

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
        this.number = Objects.requireNonNull(number, "Number required");
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
		return ConvertBigDecimal.of(number).scale();
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
		return ConvertBigDecimal.of(number).intValueExact();
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
		return ConvertBigDecimal.of(number).longValueExact();
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

    	return ConvertBigDecimal.of(number).setScale(0, RoundingMode.DOWN).longValueExact();
    }

    /*
     * (non-Javadoc)
	 * @see javax.money.NumberValue#getAmountFractionDenominator()
     */
	@Override
	public long getAmountFractionDenominator() {
		BigDecimal value = ConvertBigDecimal.of(number);
		return value.setScale(value.scale(), RoundingMode.DOWN)
				.remainder(BigDecimal.ONE).movePointRight(value.scale())
				.longValue();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.money.NumberValue#getNumberValue(java.lang.Class)
	 */
	@Override
	public <T extends Number> T numberValue(Class<T> numberType) {
		return ConvertNumberValue.of(numberType, number);
	}

    /*
     * (non-Javadoc)
	 * @see javax.money.NumberValue#round(java.math.MathContext)
	 */
    @Override
    public NumberValue round(MathContext mathContext) {
        if (this.number instanceof BigDecimal) {
            return new DefaultNumberValue(((BigDecimal) this.number).round(mathContext));
        }
        return new DefaultNumberValue(new BigDecimal(this.number.toString()).round(mathContext));
    }

    /*
     * (non-Javadoc)
     * @see javax.money.NumberValue#numberValueExact(java.lang.Class)
     */
    @Override
	public <T extends Number> T numberValueExact(Class<T> numberType) {
		return ConvertNumberValue.ofExact(numberType, number);
	}

	/**
	 * Creates a {@link BigDecimal} from the given {@link Number} doing the valid conversion
	 * depending the type given.
	 *
	 * @param num
	 *            the number type
	 * @return the corresponding {@link BigDecimal}
	 * @deprecated will be removed.
	 */
	@Deprecated
	protected static BigDecimal getBigDecimal(Number num) {
		return ConvertBigDecimal.of(num);
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.valueOf(number);
	}

}
