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
package org.javamoney.moneta;

import static java.util.Optional.ofNullable;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

import javax.money.Monetary;
import javax.money.MonetaryContext;
import javax.money.MonetaryContextBuilder;
import javax.money.MonetaryOperator;
import javax.money.RoundingQueryBuilder;

/**
 * Factory of default {@link RoundedMoney}
 * @see {@link RoundedMoney}
 * @see {@link RoundedMoney#divide(double)}
 * @author Otavio Santana
 */
enum RoundedMoneyMonetaryOperatorFactory {

INSTANCE;

	private static final int SCALE_DEFAULT = 2;

	MonetaryOperator getDefaultMonetaryOperator(MonetaryOperator rounding,
			MonetaryContext context,
			MonetaryContextBuilder monetaryContextBuilder) {

		if (Objects.nonNull(rounding)) {
			return rounding;
		}
		if (Objects.nonNull(context)) {
			return createUsingMonetaryContext(context, monetaryContextBuilder);
		} else {
			return Monetary.getDefaultRounding();
		}
	}

	private MonetaryOperator createUsingMonetaryContext(
			MonetaryContext context,
			MonetaryContextBuilder monetaryContextBuilder) {

		MathContext mathContext = context.get(MathContext.class);
		if (Objects.isNull(mathContext)) {

			RoundingMode roundingMode = context.get(RoundingMode.class);
			if (Objects.nonNull(roundingMode)) {

				int scale = ofNullable(context.getInt("scale")).orElse(SCALE_DEFAULT);

				monetaryContextBuilder.set(roundingMode);
				monetaryContextBuilder.set("scale", scale);
				return Monetary.getRounding(RoundingQueryBuilder.of()
						.setScale(scale).set(roundingMode).build());
			} else {
				return Monetary.getDefaultRounding();
			}
		} else {
			monetaryContextBuilder.set(mathContext.getRoundingMode());
			monetaryContextBuilder.set("scale", SCALE_DEFAULT);
			return Monetary.getRounding(RoundingQueryBuilder.of().set(mathContext)
					.setScale(SCALE_DEFAULT).build());
		}
	}
}
