/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE.
 * 
 * Specification: JSR-354 Money and Currency API ("Specification")
 * 
 * Copyright (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.format;

import java.io.IOException;
import java.text.ParseException;
import java.util.Locale;

import javax.money.MonetaryAmount;

/**
 * Formats instances of {@code T} to a {@link String} or an {@link Appendable}.
 * Instances of {@link FormatToken} can be added to a {@link ItemFormatBuilder}
 * to assemble a complex input/output {@link ItemFormat} using a programmatic
 * fluent API. Hereby each {@link FormatToken} instance represent a part of the
 * overall formatted String. Similarly when parsing an input by calling
 * {@link ItemFormat#parse(CharSequence, Locale)} each {@link FormatToken} can
 * read and forward the current {@link ParseContext}, or through an error, if
 * the input does not provide a parseable input for the given
 * {@link FormatToken}.
 * <p>
 * Classes implementing this interface are required to be thread-safe and
 * immutable.
 * 
 * @author Anatole Tresch
 */
public interface FormatToken {

	/**
	 * Prints an item value to an {@code Appendable}.
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
	 *            the {@link Locale} to be used, not null.
	 * @throws UnsupportedOperationException
	 *             if the formatter is unable to print
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public void print(Appendable appendable, MonetaryAmount amount)
			throws IOException;

	/**
	 * Parses an item from the given {@link ParseContext}. Any parsed item can
	 * be added to the {@link ParseContext} using
	 * {@link ParseContext#addParseResult(Object, Object)} as results. At the
	 * end of the parsing process an instance of {@link ItemFactory} is
	 * transferring the results parsed into the target item to be parsed.
	 * 
	 * @param context
	 *            the parse context
	 * @param locale
	 *            the {@link Locale} to be used, not null.
	 * @param style
	 *            the {@link LocalizationStyle} to be used.
	 * @throws ParseException
	 *             thrown, if parsing fails.
	 */
	public void parse(ParseContext context)
			throws ParseException;

}
