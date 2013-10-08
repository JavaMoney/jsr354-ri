/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
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
package org.javamoney.moneta.format;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAdjuster;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.MonetaryAmountFormat.Builder;
import org.javamoney.moneta.function.MonetaryRoundings;

/**
 * Formats instances of {@code MonetaryAmount} to a {@link String} or an
 * {@link Appendable}.
 * <p>
 * Instances of this class are not thread-safe. Basically when using
 * {@link MonetaryAmountFormat} instances a new instance should be created on
 * each access.
 */
public final class MonetaryAmountFormat {

	public static enum CurrencyStyle {
		CODE, NAME, NUMERIC_CODE, SYMBOL
	}

	/** The tokens to be used for formatting/parsing. */
	private List<MonetaryAmountFormatToken> tokens = new ArrayList<MonetaryAmountFormatToken>();

	private CurrencyUnit defaultCurrency;

	public AmountStyle getAmountStyle() {
		AmountNumberToken numberToken = getNumberToken();
		if (numberToken == null) {
			throw new IllegalStateException(
					"This format has no numer value attached.");
		}
		return numberToken.getAmountStyle();
	}

	private CurrencyToken getCurrencyToken() {
		for (MonetaryAmountFormatToken t : this.tokens) {
			if (t instanceof CurrencyToken) {
				return (CurrencyToken) t;
			}
		}
		return null;
	}

	private AmountNumberToken getNumberToken() {
		for (MonetaryAmountFormatToken t : this.tokens) {
			if (t instanceof CurrencyToken) {
				return (AmountNumberToken) t;
			}
		}
		return null;
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param buildItemFormat
	 *            the base buildItemFormat, not null.
	 * @param itemFactory
	 *            the itemFactory to be used, not null.
	 */
	private MonetaryAmountFormat(
			List<MonetaryAmountFormatToken> tokens, CurrencyUnit defaultCurrency) {
		if (tokens == null || tokens.isEmpty()) {
			throw new IllegalArgumentException(
					"tokens must not be null or empty.");
		}
		this.tokens.addAll(tokens);
		this.defaultCurrency = defaultCurrency;
	}

	public CurrencyUnit getDefaultCurrency() {
		return defaultCurrency;
	}

	/**
	 * Formats a value of {@code T} to a {@code String}. The {@link Locale}
	 * passed defines the overall target {@link Locale}, whereas the
	 * {@link LocalizationStyle} attached with the instances configures, how the
	 * {@link MonetaryFormat} should generally behave. The
	 * {@link LocalizationStyle} allows to configure the formatting and parsing
	 * in arbitrary details. The attributes that are supported are determined by
	 * the according {@link MonetaryFormat} implementation:
	 * <ul>
	 * <li>When the {@link MonetaryFormat} was created using the {@link Builder}
	 * , all the {@link MonetaryAmountFormatToken}, that model the overall format, and the
	 * {@link ItemFactory}, that is responsible for extracting the final parsing
	 * result, returned from a parsing call, are all possible recipients for
	 * attributes of the configuring {@link LocalizationStyle}.
	 * <li>When the {@link MonetaryFormat} was provided by an instance of
	 * {@link ItemFormatFactorySpi} the {@link MonetaryFormat} returned
	 * determines the capabilities that can be configured.
	 * </ul>
	 * 
	 * So, regardless if an {@link MonetaryFormat} is created using the fluent
	 * style {@link Builder} pattern, or provided as preconfigured
	 * implementation, {@link LocalizationStyle}s allow to configure them both
	 * effectively.
	 * 
	 * @param amount
	 *            the amount to print, not {@code null}
	 * @return the string printed using the settings of this formatter
	 * @throws UnsupportedOperationException
	 *             if the formatter is unable to print
	 */
	public String format(MonetaryAmount amount) {
		StringBuilder builder = new StringBuilder();
		try {
			print(builder, amount);
		} catch (IOException e) {
			throw new IllegalStateException("Error foratting of " + amount, e);
		}
		return builder.toString();
	}

	/**
	 * Prints a item value to an {@code Appendable}.
	 * <p>
	 * Example implementations of {@code Appendable} are {@code StringBuilder},
	 * {@code StringBuffer} or {@code Writer}. Note that {@code StringBuilder}
	 * and {@code StringBuffer} never throw an {@code IOException}.
	 * 
	 * @param appendable
	 *            the appendable to add to, not null
	 * @param item
	 *            the item to print, not null
	 * @param locale
	 *            the main target {@link Locale} to be used, not {@code null}
	 * @throws UnsupportedOperationException
	 *             if the formatter is unable to print
	 * @throws ItemFormatException
	 *             if there is a problem while printing
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void print(Appendable appendable, MonetaryAmount amount)
			throws IOException {
		for (MonetaryAmountFormatToken token : tokens) {
			token.print(appendable, amount);
		}
	}

	/**
	 * Fully parses the text into an instance of {@code T}.
	 * <p>
	 * The parse must complete normally and parse the entire text. If the parse
	 * completes without reading the entire length of the text, an exception is
	 * thrown. If any other problem occurs during parsing, an exception is
	 * thrown.
	 * <p>
	 * This method uses a {@link Locale} as an input parameter. Additionally the
	 * {@link ItemFormatException} instance is configured by a
	 * {@link LocalizationStyle}. {@link LocalizationStyle}s allows to configure
	 * formatting input in detail. This allows to implement complex formatting
	 * requirements using this interface.
	 * 
	 * @param text
	 *            the text to parse, not null
	 * @param locale
	 *            the main target {@link Locale} to be used, not {@code null}
	 * @return the parsed value, never {@code null}
	 * @throws UnsupportedOperationException
	 *             if the formatter is unable to parse
	 * @throws ItemParseException
	 *             if there is a problem while parsing
	 */
	public MonetaryAmount parse(CharSequence text)
			throws ParseException {
		ParseContext ctx = new ParseContext(text);
		for (MonetaryAmountFormatToken token : tokens) {
			token.parse(ctx);
		}
		CurrencyUnit unit = ctx.getParsedCurrency();
		Number num = ctx.getParsedNumber();
		if (unit == null) {
			unit = defaultCurrency;
		}
		if (num == null) {
			throw new ParseException(text.toString(), -1);
		}
		return Money.of(unit, num);
	}

	/**
	 * This class implements a builder that allows creating of
	 * {@link MonetaryFormat} instances programmatically using a fluent API. The
	 * formatting hereby is modeled by a concatenation of {@link MonetaryAmountFormatToken}
	 * instances. The same {@link MonetaryAmountFormatToken} instances also are responsible
	 * for implementing the opposite, parsing, of an item from an input
	 * character sequence. Each {@link MonetaryAmountFormatToken} gets access to the current
	 * parsing location, and the original and current character input sequence,
	 * modeled by the {@link ParseContext}. Finall if parsing of a part failed,
	 * a {@link MonetaryAmountFormatToken} throws an {@link ItemParseException} describing the
	 * problem.
	 * <p>
	 * This class is not thread-safe and therefore should not be shared among
	 * different threads.
	 * 
	 * @author Anatole Tresch
	 * 
	 * @param <T>
	 *            the target type.
	 */
	public static final class Builder {
		/** The tokens to be used for formatting/parsing. */
		private List<MonetaryAmountFormatToken> tokens = new ArrayList<MonetaryAmountFormatToken>();

		private Locale locale;

		/**
		 * The default currency, used, when parsing amounts, where no currency
		 * is available.
		 */
		private CurrencyUnit defaultCurrency;

		private char[] groupChars;

		private int[] groupSizes;

		/**
		 * Creates a new Builder.
		 * 
		 * @param targetType
		 *            the target class.
		 */
		public Builder(Locale locale) {
			if (locale == null) {
				throw new IllegalArgumentException("Locale required.");
			}
			this.locale = locale;
		}

		public Builder withDefaultCurrency(CurrencyUnit currency) {
			this.defaultCurrency = currency;
			return this;
		}

		/**
		 * Add a {@link MonetaryAmountFormatToken} to the token list.
		 * 
		 * @param token
		 *            the token to add.
		 * @return the builder, for chaining.
		 */
		public Builder appendAmount(AmountStyle style) {
			this.tokens.add(new AmountNumberToken(style));
			return this;
		}

		/**
		 * Add the amount to the given format. Hereby the number default style
		 * for the {@link #locale} is used, and the number is rounded with the
		 * currencies, default rounding as returned by
		 * {@link MonetaryRoundings#getRounding()}.
		 * 
		 * @param token
		 *            the token to add.
		 * @return the builder, for chaining.
		 */
		public Builder appendAmount() {
			AmountStyle style = new AmountStyle.Builder(locale).withFormattingAdjuster(
					MonetaryRoundings.getRounding()).build();
			this.tokens.add(new AmountNumberToken(style));
			return this;
		}

		/**
		 * Adds a currency unit to the format using the given
		 * {@link CurrencyStyle}.
		 * 
		 * @param style
		 *            the style to be used, not {@code null}.
		 * @return the builder, for chaining.
		 */
		public Builder appendCurrency(CurrencyStyle style) {
			this.tokens.add(new CurrencyToken(style, locale));
			return this;
		}

		/**
		 * Adds a currency to the format printing using the currency code.
		 * 
		 * @return the builder, for chaining.
		 */
		public Builder appendCurrency() {
			return appendCurrency(CurrencyStyle.CODE);
		}

		/**
		 * Add a {@link MonetaryAmountFormatToken} to the token list.
		 * 
		 * @param literal
		 *            the literal to add, not {@code null}.
		 * @return the builder, for chaining.
		 */
		public Builder appendLiteral(String literal) {
			this.tokens.add(new LiteralToken(literal));
			return this;
		}

		/**
		 * This method creates an {@link MonetaryFormat} based on this instance,
		 * hereby using the given a {@link ItemFactory} to extract the item to
		 * be returned from the {@link ParseContext}'s results.
		 * 
		 * @return the {@link MonetaryFormat} instance, never null.
		 */
		public MonetaryAmountFormat build() {
			if (tokens.isEmpty()) {
				// create default JDK currency format
				this.tokens.add(new AmountNumberToken(new AmountStyle.Builder(
						locale).withCurrencyFormat(locale)
						.withNumberGroupChars(groupChars)
						.withNumberGroupSizes(groupSizes).build()));
			}
			return new MonetaryAmountFormat(tokens, defaultCurrency);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "MonetaryAmountFormat.Builder [tokens=" + tokens + "]";
		}

		public Builder withNumberGroupSizes(int... groupSizes) {
			this.groupSizes = groupSizes.clone();
			return this;
		}

		public Builder withNumberGroupChars(char... groupChars) {
			this.groupChars = groupChars.clone();
			return this;
		}

	}
}
