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
import java.io.Serializable;
import java.text.ParseException;

import javax.money.MonetaryAmount;
import javax.money.format.ParseContext;

/**
 * {@link FormatToken} which adds an arbitrary literal constant value to the
 * output.
 * <p>
 * This class is thread safe, immutable and serializable.
 * 
 * @author Anatole Tresch
 * @author Werner Keil
 * 
 * @param <T>
 *            The item type.
 */
final class LiteralToken implements FormatToken, Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2528757575867480018L;
	/**
	 * The literal part.
	 */
	private String token;

	/**
	 * Creates a new {@link LiteralToken}.
	 * 
	 * @param token
	 *            The literal token part.
	 */
	public LiteralToken(String token) {
		if (token == null) {
			throw new IllegalArgumentException("Token is required.");
		}
		this.token = token;
	}

	/**
	 * Parses the literal from the current {@link ParseContext}.
	 * 
	 * @see javax.money.format.FormatToken#parse(javax.money.format.ParseContext,
	 *      java.util.Locale, javax.money.format.LocalizationStyle)
	 */
	@Override
	public void parse(ParseContext context)
			throws ParseException {
		if (!context.consume(token)) {
			throw new ParseException(context.getOriginalInput(),
					context.getErrorIndex());
		}
	}

	/**
	 * Prints the amount to the {@link Appendable} given.
	 * 
	 * @see javax.money.format.FormatToken#print(java.lang.Appendable,
	 *      java.lang.Object, java.util.Locale,
	 *      javax.money.format.LocalizationStyle)
	 */
	@Override
	public void print(Appendable appendable, MonetaryAmount amount)
			throws IOException {
		appendable.append(this.token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LiteralToken [token=" + token + "]";
	}

}
