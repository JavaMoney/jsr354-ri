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
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryCurrencies;
import javax.money.format.CurrencyStyle;
import javax.money.format.MonetaryParseException;

/**
 * Implements a {@link FormatToken} that adds a localizable {@link String}, read
 * by key from a {@link ResourceBundle}.
 * 
 * @author Anatole Tresch
 * 
 * @param <T>
 *            The concrete type.
 */
final class CurrencyToken implements FormatToken {
	/** The style defining, how the currency should be localized. */
	private CurrencyStyle style = CurrencyStyle.CODE;
	/** The target locale. */
	private Locale locale;

	/**
	 * Creates a new {@link CurrencyToken}.
	 * 
	 * @param style
	 *            The style defining, how the currency should be localized, not
	 *            {@code null}.
	 * @param locale
	 *            The target locale, not {@code null}.
	 */
	public CurrencyToken(CurrencyStyle style, Locale locale) {
		this.locale = locale;
	}

	/**
	 * Explicitly configure the {@link CurrencyStyle} to be used.
	 * 
	 * @param style
	 *            the {@link CurrencyStyle}, not {@code null}.
	 * @return this token instance, for chaining.
	 */
	public CurrencyToken setCurrencyStyle(CurrencyStyle displayType) {
		if (displayType == null) {
			throw new IllegalArgumentException("Display type null.");
		}
		this.style = displayType;
		return this;
	}

	/**
	 * Access the {@link CurrencyStyle} used for formatting.
	 * 
	 * @return the current {@link CurrencyStyle}, never {@code null}.
	 */
	public CurrencyStyle getCurrencyStyle() {
		return this.style;
	}

	/**
	 * Evaluate the formatted(localized) token.
	 * 
	 * @param amount
	 *            the {@link MonetaryAmount} containing the {@link CurrencyUnit}
	 *            to be formatted.
	 * @return the formatted currency.
	 */
	protected String getToken(MonetaryAmount amount) {
		switch (style) {
		case NUMERIC_CODE:
			return String.valueOf(amount.getCurrency()
					.getNumericCode());
		case NAME:
			return getCurrencyName(amount.getCurrency());
		case SYMBOL:
			return getCurrencySymbol(amount.getCurrency());
		default:
		case CODE:
			return amount.getCurrency().getCurrencyCode();
		}
	}

	/**
	 * This method tries to evaluate the localized display name for a
	 * {@link CurrencyUnit}. It uses {@link Currency#getDisplayName(Locale)} if
	 * the given currency code maps to a JDK {@link Currency} instance.
	 * <p>
	 * If not found {@code currency.getCurrencyCode()} is returned.
	 * 
	 * @param currency
	 *            The currency, not {@code null}
	 * @return the formatted currency name.
	 */
	private String getCurrencyName(CurrencyUnit currency) {
		Currency jdkCurrency = getCurrency(currency.getCurrencyCode());
		if (jdkCurrency != null) {
			return jdkCurrency.getDisplayName(locale);
		}
		return currency.getCurrencyCode();
	}

	private Currency getCurrency(String currencyCode) {
		try {
			return Currency.getInstance(currencyCode);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * This method tries to evaluate the localized symbol name for a
	 * {@link CurrencyUnit}. It uses {@link Currency#getSymbol(Locale)} if the
	 * given currency code maps to a JDK {@link Currency} instance.
	 * <p>
	 * If not found {@code currency.getCurrencyCode()} is returned.
	 * 
	 * @param currency
	 *            The currency, not {@code null}
	 * @return the formatted currency symbol.
	 */
	private String getCurrencySymbol(CurrencyUnit currency) {
		Currency jdkCurrency = getCurrency(currency.getCurrencyCode());
		if (jdkCurrency != null) {
			return jdkCurrency.getSymbol(locale);
		}
		return currency.getCurrencyCode();
	}

	/**
	 * Parses a currency from the given {@link ParseContext}. Depending on the
	 * current {@link CurrencyStyle} it interprets the next non empty token,
	 * either as
	 * <ul>
	 * <li>currency code
	 * <li>currency symbol
	 * </ul>
	 * Parsing of localized currency names or numeric code is not supported.
	 * 
	 * @throws UnsupportedOperationException
	 *             if the {@link CurrencyStyle} is configured to us currency
	 *             names, or numeric codes for formatting.
	 */
	@Override
	public void parse(ParseContext context)
			throws MonetaryParseException {
		String token = context.lookupNextToken();
		while (token != null) {
			if (token.trim().isEmpty()) {
				context.consume(token);
				token = context.lookupNextToken();
				continue;
			}
			break;
		}
		try {
			CurrencyUnit cur = null;
			switch (style) {
			case CODE:
				cur = MonetaryCurrencies.getCurrency(token);
				context.consume(token);
				break;
			case SYMBOL:
				if (token.startsWith("$")) {
					cur = MonetaryCurrencies.getCurrency("USD");
					context.consume("$");
				}
				else if (token.startsWith("€")) {
					cur = MonetaryCurrencies.getCurrency("EUR");
					context.consume("€");
				}
				else if (token.startsWith("£")) {
					cur = MonetaryCurrencies.getCurrency("GBP");
					context.consume("£");
				}
				cur = MonetaryCurrencies.getCurrency(token);
				context.consume(token);
				break;
			case NAME:
			case NUMERIC_CODE:
			default:
				throw new UnsupportedOperationException("Not yet implemented");
			}
			if (cur != null) {
				context.setParsedCurrency(cur);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the {@link CurrencyUnit} of the given {@link MonetaryAmount} to
	 * the given {@link Appendable}.
	 * 
	 * @throws IOException
	 *             may be thrown by the {@link Appendable}
	 */
	@Override
	public void print(Appendable appendable, MonetaryAmount amount)
			throws IOException {
		appendable.append(getToken(amount));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CurrencyToken [locale=" + locale + ", style=" + style + "]";
	}

}
