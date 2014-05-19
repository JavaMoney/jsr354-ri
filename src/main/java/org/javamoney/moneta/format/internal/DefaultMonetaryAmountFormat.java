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
package org.javamoney.moneta.format.internal;

import org.javamoney.moneta.format.CurrencyStyle;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmounts;
import javax.money.MonetaryContext;
import javax.money.format.AmountFormatContext;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryParseException;

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

	/** The international Unicode currency sign. */
	private static final char CURRENCY_SIGN = '\u00A4';

	/**
	 * The tokens to be used for formatting/parsing of positive and zero
	 * numbers.
	 */
	private List<FormatToken> positiveTokens;

	/**
	 * The tokens to be used for formatting/parsing of positive and zero
	 * numbers.
	 */
	private List<FormatToken> negativeTokens;

	/**
	 * The {@link MonetaryContext} applied on creating a {@link MonetaryAmount}
	 * based on data parsed.
	 */
	private MonetaryContext monetaryContext = MonetaryAmounts
			.getAmountFactory().getDefaultMonetaryContext();

	/** Currency used, when no currency was on the input parsed. */
	private CurrencyUnit defaultCurrency;

	/** The current {@link javax.money.format.AmountFormatContext}, never null. */
	private AmountFormatContext amountFormatContext;

	/**
	 * Creates a new instance.
	 * 
	 * @param style
	 *            the {@link javax.money.format.AmountFormatContext} to be used, not {@code null}.
	 * @param style
	 *            the style defining how the {@link CurrencyUnit} should be
	 *            formatted.
	 */
	DefaultMonetaryAmountFormat(AmountFormatContext style) {
		setAmountFormatContext(style);
	}

	private void initPattern(String pattern, List<FormatToken> tokens,
			AmountFormatContext style) {
		int index = pattern.indexOf(CURRENCY_SIGN);
		if (index > 0) { // currency placement after, between
			String p1 = pattern.substring(0, index);
			String p2 = pattern.substring(index + 1);
			if (isLiteralPattern(p1)) {
				tokens.add(new LiteralToken(p1));
				tokens.add(new CurrencyToken(style.getAttribute(CurrencyStyle.class), style.
                                             getAttribute(Locale.class)));
			} else {
				tokens.add(new AmountNumberToken(style, p1));
				tokens.add(new CurrencyToken(style.getAttribute(CurrencyStyle.class), style.getAttribute(Locale.class)));
			}
			if (!p2.isEmpty()) {
				if (isLiteralPattern(p2)) {
					tokens.add(new LiteralToken(p2));
				} else {
					tokens.add(new AmountNumberToken(style, p2));
				}
			}
		} else if (index == 0) { // currency placement before
			tokens.add(new CurrencyToken(style.getAttribute(CurrencyStyle.class), style
					.getAttribute(Locale.class)));
			tokens.add(new AmountNumberToken(style, pattern.substring(1)));
		} else { // no currency
			tokens.add(new AmountNumberToken(style, pattern));
		}
	}

	private boolean isLiteralPattern(String pattern) {
		// TODO implement better here
		if (pattern.contains("#") || pattern.contains("0")) {
			return false;
		}
		return true;
	}

	/**
	 * Formats a value of {@code T} to a {@code String}. {@link java.util.Locale}
     * passed defines the overall target {@link Locale}. This locale state, how the
	 * {@link MonetaryAmountFormat} should generally behave. The
	 * {@link java.util.Locale} allows to configure the formatting and parsing
	 * in arbitrary details. The attributes that are supported are determined by
	 * the according {@link MonetaryAmountFormat} implementation:
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
	 * @param amount
	 *            the amount to print, not null
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void print(Appendable appendable, MonetaryAmount amount)
			throws IOException {
		if (amount.isNegative()) {
			for (FormatToken token : negativeTokens) {
				token.print(appendable, amount);
			}
		} else {
			for (FormatToken token : positiveTokens) {
				token.print(appendable, amount);
			}
		}
	}

	/**
	 * Fully parses the text into an instance of {@code MonetaryAmount}.
	 * <p>
	 * The parse must complete normally and parse the entire text. If the parse
	 * completes without reading the entire length of the text, an exception is
	 * thrown. If any other problem occurs during parsing, an exception is
	 * thrown.
	 *
	 * @param text
	 *            the text to parse, not null
	 * @return the parsed value, never {@code null}
	 * @throws UnsupportedOperationException
	 *             if the formatter is unable to parse
	 * @throws javax.money.format.MonetaryParseException
	 *             if there is a problem while parsing
	 */
	public MonetaryAmount parse(CharSequence text)
			throws MonetaryParseException {
		ParseContext ctx = new ParseContext(text);
		try {
			for (FormatToken token : this.positiveTokens) {
				token.parse(ctx);
			}
		} catch (Exception e) {
			// try parsing negative...
			Logger log = Logger.getLogger(getClass().getName());
			if (log.isLoggable(Level.FINEST)){
				log.log(Level.FINEST,
						"Failed to parse positive pattern, trying negative for: "
								+ text, e);
			}
			for (FormatToken token : this.negativeTokens) {
				token.parse(ctx);
			}
		}
		CurrencyUnit unit = ctx.getParsedCurrency();
		Number num = ctx.getParsedNumber();
		if (unit == null) {
			unit = defaultCurrency;
		}
		if (num == null) {
			throw new MonetaryParseException(text.toString(), -1);
		}
		Class<? extends MonetaryAmount> type = MonetaryAmounts
				.queryAmountType(this.monetaryContext);
		if (type == null) {
			Logger log = Logger.getLogger(getClass().getName());
			if (log.isLoggable(Level.WARNING)){
				log.warning("Required moneterayContext was not resolvable, using default, required="
						+ this.monetaryContext);
			}
			type = MonetaryAmounts.getDefaultAmountType();
		}
		return MonetaryAmounts.getAmountFactory(type)
				.setContext(monetaryContext).setCurrency(unit)
				.setNumber(num)
				.create();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.MonetaryQuery#queryFrom(javax.money.MonetaryAmount)
	 */
	@Override
	public String queryFrom(MonetaryAmount amount) {
		return format(amount);
	}

	@Override
	public AmountFormatContext getAmountFormatContext() {
		return this.amountFormatContext;
	}

	private void setAmountFormatContext(AmountFormatContext style) {
		Objects.requireNonNull(style);
		this.amountFormatContext = style;
		this.positiveTokens = new ArrayList<>();
		this.negativeTokens = new ArrayList<>();
		String pattern = style.getText("pattern");
        if(pattern==null){
            pattern = ((DecimalFormat)DecimalFormat.getCurrencyInstance(style.getLocale())).toPattern();
        }
		if (pattern.indexOf(CURRENCY_SIGN) < 0) {
			this.positiveTokens.add(new AmountNumberToken(style, pattern));
			this.negativeTokens = positiveTokens;
		} else {
			// split into (potential) plus, minus patterns
			char patternSeparator = ';';
			if (style.getAttribute(DecimalFormatSymbols.class) != null) {
				patternSeparator = style.getAttribute(DecimalFormatSymbols.class).getPatternSeparator();
			}
			String[] plusMinusPatterns = pattern.split(String.valueOf(patternSeparator));
			initPattern(plusMinusPatterns[0], this.positiveTokens, style);
			if (plusMinusPatterns.length > 1) {
				initPattern(plusMinusPatterns[1], this.negativeTokens, style);
			} else {
				this.negativeTokens = this.positiveTokens;
			}
		}
	}


}
