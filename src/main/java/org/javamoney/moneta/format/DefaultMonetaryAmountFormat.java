/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency
 * API ("Specification") Copyright (c) 2012-2013, Credit Suisse All rights
 * reserved.
 */
package org.javamoney.moneta.format;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmounts;
import javax.money.MonetaryContext;
import javax.money.format.CurrencyPlacement;
import javax.money.format.CurrencyStyle;
import javax.money.format.FormatStyle;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.ParseContext;
import javax.money.function.MonetaryRoundings;

/**
 * Formats instances of {@code MonetaryAmount} to a {@link String} or an
 * {@link Appendable}.
 * <p>
 * Instances of this class are not thread-safe. Basically when using
 * {@link MonetaryAmountFormat} instances a new instance should be created on
 * each access.
 * 
 * @author Anatole Tresch
 * @author Werner Keil
 * 
 */
final class DefaultMonetaryAmountFormat implements MonetaryAmountFormat {

	/** The tokens to be used for formatting/parsing. */
	private List<FormatToken> tokens = new ArrayList<FormatToken>();
	/**
	 * The {@link MonetaryContext} applied on creating a {@link MonetaryAmount}
	 * based on data parsed.
	 */
	private MonetaryContext monetaryContext;
	/** The formatting style to be used. */
	private FormatStyle numberStyle;
	/**
	 * The default {@link CurrencyUnit} used, if no currency can be evaluated
	 * from the parsing input.
	 */
	private CurrencyUnit defaultCurrency;

	/**
	 * Creates a new instance.
	 * 
	 * @param numberStyle
	 *            the base {@link FormatStyle}, not {@code null}.
	 * @param monetaryContext
	 *            the {@link MonetaryContext} to be used for creating
	 *            {@link MonetaryAmount} instances during parsing, not
	 *            {@code null}.
	 */
	public DefaultMonetaryAmountFormat(FormatStyle numberStyle,
			MonetaryContext monetaryContext, CurrencyUnit defaultCurrency) {
		if (tokens == null || tokens.isEmpty()) {
			throw new IllegalArgumentException(
					"tokens must not be null or empty.");
		}
		this.tokens.addAll(tokens);
		this.defaultCurrency = defaultCurrency;
		this.numberStyle = numberStyle;
		this.monetaryContext = monetaryContext;
	}

	/**
	 * Creates a new instance.
	 * 
	 * @param style
	 *            the {@link FormatStyle} to be used, not {@code null}.
	 * @param currencyStyle
	 *            the style defining how the {@link CurrencyUnit} should be
	 *            formatted.
	 * @param currencyPlacement
	 *            Defines how where the {@link CurrencyUnit} should be placed in
	 *            relation to the numeric part or the {@link MonetaryAmount}.
	 * @param monetaryContext
	 *            The {@link MonetaryContext} used for creating of
	 *            {@link MonetaryAmount} instances during parsing.
	 * @param defaultCurrency
	 *            The default {@link CurrencyUnit} used, when no currency
	 *            information can be extracted from the parse input.
	 */
	public DefaultMonetaryAmountFormat(FormatStyle style,
			CurrencyStyle currencyStyle, CurrencyPlacement currencyPlacement,
			MonetaryContext monetaryContext, CurrencyUnit defaultCurrency) {

		switch (currencyPlacement) {
		case AFTER:
			this.tokens.add(new AmountNumberToken(style));
			this.tokens.add(new LiteralToken(" "));
			this.tokens
					.add(new CurrencyToken(currencyStyle, style.getLocale()));
			break;
		case OMIT:
			this.tokens.add(new AmountNumberToken(style));
		default:
		case BEFORE:
			this.tokens
					.add(new CurrencyToken(currencyStyle, style.getLocale()));
			this.tokens.add(new LiteralToken(" "));
			this.tokens.add(new AmountNumberToken(style));
			break;
		}
		this.defaultCurrency = defaultCurrency;
		this.numberStyle = style;
		this.monetaryContext = monetaryContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.format.MonetaryAmountFormat#getDefaultCurrency()
	 */
	@Override
	public CurrencyUnit getDefaultCurrency() {
		return defaultCurrency;
	}

	/**
	 * Formats a value of {@code T} to a {@code String}. The {@link Locale}
	 * passed defines the overall target {@link Locale}, whereas the
	 * {@link LocalizationStyle} attached with the instances configures, how the
	 * {@link MonetaryAmountFormat} should generally behave. The
	 * {@link LocalizationStyle} allows to configure the formatting and parsing
	 * in arbitrary details. The attributes that are supported are determined by
	 * the according {@link MonetaryAmountFormat} implementation:
	 * <ul>
	 * <li>When the {@link MonetaryAmountFormat} was created using the
	 * {@link Builder} , all the {@link FormatToken}, that model the overall
	 * format, and the {@link ItemFactory}, that is responsible for extracting
	 * the final parsing result, returned from a parsing call, are all possible
	 * recipients for attributes of the configuring {@link LocalizationStyle}.
	 * <li>When the {@link MonetaryAmountFormat} was provided by an instance of
	 * {@link ItemFormatFactorySpi} the {@link MonetaryAmountFormat} returned
	 * determines the capabilities that can be configured.
	 * </ul>
	 * 
	 * So, regardless if an {@link MonetaryAmountFormat} is created using the
	 * fluent style {@link Builder} pattern, or provided as preconfigured
	 * implementation, {@link LocalizationStyle}s allow to configure them both
	 * effectively.
	 * 
	 * @param amount
	 *            the amount to print, not {@code null}
	 * @return the string printed using the settings of this formatter
	 * @throws UnsupportedOperationException
	 *             if the formatter is unable to print
	 */
	public String format(MonetaryAmount<?> amount) {
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
	public void print(Appendable appendable, MonetaryAmount<?> amount)
			throws IOException {
		for (FormatToken token : tokens) {
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
		for (FormatToken token : tokens) {
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
		return MonetaryAmounts.getAmount(unit, num);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.format.MonetaryAmountFormat#getMonetaryContext()
	 */
	@Override
	public MonetaryContext getMonetaryContext() {
		return monetaryContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.format.MonetaryAmountFormat#getFormatStyle()
	 */
	@Override
	public FormatStyle getFormatStyle() {
		return numberStyle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryQuery#queryFrom(javax.money.MonetaryAmount)
	 */
	@Override
	public String queryFrom(MonetaryAmount<?> amount) {
		return format(amount);
	}

	/**
	 * This class implements a builder that allows creating of
	 * {@link MonetaryAmountFormat} instances programmatically using a fluent
	 * API. The formatting hereby is modeled by a concatenation of
	 * {@link FormatToken} instances. The same {@link FormatToken} instances
	 * also are responsible for implementing the opposite, parsing, of an item
	 * from an input character sequence. Each {@link FormatToken} gets access to
	 * the current parsing location, and the original and current character
	 * input sequence, modeled by the {@link ParseContext}. Finally if parsing of
	 * a part failed, a {@link FormatToken} throws an {@link ItemParseException}
	 * describing the problem.
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

		/**
		 * The default currency, used, when parsing amounts, where no currency
		 * is available.
		 */
		private CurrencyUnit defaultCurrency;

		private FormatStyle style;

		private CurrencyStyle currencyStyle = CurrencyStyle.CODE;

		private CurrencyPlacement currencyPlacement = CurrencyPlacement.BEFORE;

		private MonetaryContext monetaryContext = MonetaryAmounts
				.getDefaultMonetaryContext();

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
			setDefaultFormatStyle(locale);
		}

		public Builder setDefaultCurrency(CurrencyUnit currency) {
			this.defaultCurrency = currency;
			return this;
		}

		/**
		 * Sets the default {@link FormatStyle} for the given {@link Locale}.
		 * for the {@link #locale} is used, and the number is rounded with the
		 * currencies, default rounding as returned by
		 * {@link MonetaryRoundings#getRounding()}.
		 * 
		 * @param locale
		 *            the {@link Locale} to set.
		 * @return the builder, for chaining.
		 */
		public Builder setDefaultFormatStyle(Locale locale) {
			Objects.requireNonNull(locale);
			this.style = new FormatStyle.Builder(locale).setRounding(
					MonetaryRoundings.getRounding()).build();
			return this;
		}

		/**
		 * Sets the {@link FormatStyle} used explicitly.
		 * 
		 * @param style
		 *            the {@link FormatStyle} to be used.
		 * @return the builder, for chaining.
		 */
		public Builder setFormatStyle(FormatStyle style) {
			Objects.requireNonNull(style);
			this.style = style;
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
		public Builder setCurrencyStyle(CurrencyStyle currencyStyle) {
			Objects.requireNonNull(currencyStyle);
			this.currencyStyle = currencyStyle;
			return this;
		}

		/**
		 * Defines the {@link CurrencyPlacement} for the format instance.
		 * 
		 * @param style
		 *            the style to be used, not {@code null}.
		 * @return the builder, for chaining.
		 */
		public Builder setCurrencyPlacement(CurrencyPlacement currencyPlacement) {
			Objects.requireNonNull(currencyPlacement);
			this.currencyPlacement = currencyPlacement;
			return this;
		}

		/**
		 * Set the {@link MonetaryContext} class
		 * 
		 * @param monetaryContext
		 */
		public Builder setMonetaryContext(MonetaryContext monetaryContext) {
			Objects.requireNonNull(monetaryContext);
			this.monetaryContext = monetaryContext;
			return this;
		}

		/**
		 * This method creates an {@link MonetaryAmountFormat} based on this
		 * instance, hereby using the given a {@link ItemFactory} to extract the
		 * item to be returned from the {@link ParseContext}'s results.
		 * 
		 * @return the {@link MonetaryAmountFormat} instance, never null.
		 */
		public MonetaryAmountFormat build() {
			return new DefaultMonetaryAmountFormat(style, currencyStyle,
					currencyPlacement,
					monetaryContext, defaultCurrency);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "MonetaryAmountFormat.Builder [style=" + style
					+ ", currencyStyle=" + currencyStyle
					+ ", currencyPlacement=" + currencyPlacement
					+ ", monetaryContext=" + monetaryContext + "]";
		}

	}

}
