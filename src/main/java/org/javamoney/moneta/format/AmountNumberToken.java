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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.logging.Logger;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.format.FormatStyle;
import javax.money.format.ParseContext;

/**
 * {@link FormatToken} which allows to format a {@link Number} type.
 * 
 * @author Anatole Tresch
 * 
 * @param <T>
 *            The item type.
 */
final class AmountNumberToken implements
		FormatToken {

	private static final Logger LOG = Logger.getLogger(AmountNumberToken.class
			.getName());

	private FormatStyle style;
	private StringGrouper numberGroup;

	public AmountNumberToken(FormatStyle style) {
		if (style == null) {
			throw new IllegalArgumentException("style is required.");
		}
		this.style = style;
	}

	public FormatStyle getAmountStyle() {
		return style;
	}

	@Override
	public void print(Appendable appendable, MonetaryAmount amount)
			throws IOException {
		int digits = amount.getCurrency()
				.getDefaultFractionDigits();
		this.style.getDecimalFormat().setMinimumFractionDigits(digits);
		this.style.getDecimalFormat().setMaximumFractionDigits(digits);
		CurrencyUnit cur = amount.getCurrency();
		if (this.style.getNumberGroupSizes().length == 0) {
			appendable.append(this.style.getDecimalFormat().format(
					amount.getNumber(BigDecimal.class)));
			return;
		}
		this.style.getDecimalFormat().setGroupingUsed(false);
		String preformattedValue = this.style.getDecimalFormat().format(
				amount.getNumber(BigDecimal.class));
		String[] numberParts = splitNumberParts(this.style.getDecimalFormat(),
				preformattedValue);
		if (numberParts.length != 2) {
			appendable.append(preformattedValue);
		}
		else {
			if (numberGroup == null) {
				char[] groupChars = style.getNumberGroupChars();
				if (groupChars.length == 0) {
					groupChars = new char[] { this.style.getDecimalFormat()
							.getDecimalFormatSymbols().getGroupingSeparator() };
				}
				numberGroup = new StringGrouper(groupChars,
						style.getNumberGroupSizes());
			}
			preformattedValue = numberGroup.group(numberParts[0])
					+ this.style.getDecimalFormat().getDecimalFormatSymbols()
							.getDecimalSeparator()
					+ numberParts[1];
			appendable.append(preformattedValue);
		}
	}

	private String[] splitNumberParts(DecimalFormat format,
			String preformattedValue) {
		int index = preformattedValue.indexOf(format
				.getDecimalFormatSymbols().getDecimalSeparator());
		if (index < 0) {
			return new String[] { preformattedValue };
		}
		return new String[] { preformattedValue.substring(0, index),
				preformattedValue.substring(index + 1) };
	}

	@Override
	public void parse(ParseContext context) throws ParseException {
		ParsePosition pos = new ParsePosition(0);
		String token = context.lookupNextToken();
		while (token != null && !context.isComplete()) {
			parseToken(context, token);
			token = context.lookupNextToken();
		}
	}

	private void parseToken(ParseContext context, String token) {
		try {
			Number number = this.style.getDecimalFormat().parse(
					token);
			if (number != null) {
				context.setParsedNumber(number);
				context.consume(token);
			}
		} catch (Exception e) {
			LOG.finest("Could not parse amount from: " + token);
		}
	}

}
