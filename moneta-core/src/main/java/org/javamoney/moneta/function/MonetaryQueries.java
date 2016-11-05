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
package org.javamoney.moneta.function;

import javax.money.MonetaryAmount;
import javax.money.MonetaryQuery;

/**
 * <p>This class has utility queries, {@link MonetaryQuery}, to {@link MonetaryAmount}.</p>
 *
 * <pre>
 * {@code
 * 	MonetaryAmount monetaryAmount = Money.parse(&quot;EUR 2.35&quot;);
 * 	Long result = monetaryAmount.query(query);// 2L
 * }
 * </pre>
 * <p> Or using: </p>
 * <pre>
 * {@code
 * 	MonetaryAmount monetaryAmount = Money.parse(&quot;EUR 2.35&quot;);
 * 	Long result = query.queryFrom(monetaryAmount);// 2L
 * }
 * </pre>
 * @see {@link MonetaryAmount#query(MonetaryQuery)}
 * @see {@link MonetaryQuery}
 * @see {@link MonetaryQuery#queryFrom(MonetaryAmount)}
 * @author Otavio Santana
 * @since 1.0.1
 */
public final class MonetaryQueries {

	private static final ExtractorMajorPartQuery EXTRACTOR_MAJOR_PART = new ExtractorMajorPartQuery();

	private static final ConvertMinorPartQuery CONVERT_MINOR_PART = new ConvertMinorPartQuery();

	private static final ExtractorMinorPartQuery EXTRACTOR_MINOR_PART = new ExtractorMinorPartQuery();

	private MonetaryQueries() {
	}

	/**
	 * Allows to extract the major part of a {@link MonetaryAmount} instance.
	 * Gets the amount in major units as a {@code long}.
	 * <p>
	 * For example, 'EUR 2.35' will return 2, and 'BHD -1.345' will return -1.
	 * </p>
	 *
	 * <pre>
	 * {
	 * 	@code
	 * 	MonetaryAmount monetaryAmount = Money.parse(&quot;EUR 2.35&quot;);
	 * 	Long result = monetaryAmount.query(MonetaryQueries.majorPart());// 2L
	 * }
	 * </pre>
	 */
	public static MonetaryQuery<Long> extractMajorPart() {
		return EXTRACTOR_MAJOR_PART;
	}

	/**
	 * Convert to minor part a {@link MonetaryAmount} instance.
	 * <p>
	 * This returns the monetary amount in terms of the minor units of the
	 * currency, truncating the amount if necessary. For example, 'EUR 2.35'
	 * will return 235, and 'BHD -1.345' will return -1345.
	 * </p>
	 * </p>
	 *
	 * <pre>
	 * {
	 * 	@code
	 * 	MonetaryAmount monetaryAmount = Money.parse(&quot;EUR 2.35&quot;);
	 * 	Long result = monetaryAmount.query(MonetaryQueries.convertMinorPart());// 235L
	 * }
	 * </pre>
	 *
	 */
	public static MonetaryQuery<Long> convertMinorPart() {
		return CONVERT_MINOR_PART;
	}

	/**
	 * Convert to minor part a {@link MonetaryAmount} instance.
	 * <p>
	 * This returns the monetary amount in terms of the minor units of the
	 * currency, truncating the whole part if necessary. For example, 'EUR 2.35'
	 * will return 35, and 'BHD -1.345' will return -345.
	 * </p>
	 *
	 * <pre>
	 * {
	 * 	@code
	 * 	MonetaryAmount monetaryAmount = Money.parse(&quot;EUR 2.35&quot;);
	 * 	Long result = monetaryAmount.query(MonetaryQueries.convertMinorPart());// 35L
	 * }
	 * </pre>
	 *
	 */
	public static MonetaryQuery<Long> extractMinorPart() {
		return EXTRACTOR_MINOR_PART;
	}
}
