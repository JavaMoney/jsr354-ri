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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.logging.Logger;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;
import org.javamoney.moneta.MoneyCurrency;

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

	private AmountStyle style;
	private StringGrouper numberGroup;

	public AmountNumberToken(AmountStyle style) {
		if (style == null) {
			throw new IllegalArgumentException("style is required.");
		}
		this.style = style;
	}

	public AmountStyle getAmountStyle() {
		return style;
	}

	@Override
	public void print(Appendable appendable, MonetaryAmount amount)
			throws IOException {
		int digits = MoneyCurrency.from(amount.getCurrency())
				.getDefaultFractionDigits();
		this.style.getDecimalFormat().setMinimumFractionDigits(digits);
		this.style.getDecimalFormat().setMaximumFractionDigits(digits);
		MoneyCurrency cur = MoneyCurrency.from(amount.getCurrency());
		if (this.style.getNumberGroupSizes().length == 0) {
			switch (this.style.getCurrencyPlacement()) {
			case OMIT:
				appendable.append(this.style.getDecimalFormat().format(
						Money.from(amount).asType(BigDecimal.class)));
				break;
			default:
			case LEADING:
				appendable.append(cur.getCurrencyCode()).append(" ");
				appendable.append(this.style.getDecimalFormat().format(
						Money.from(amount).asType(BigDecimal.class)));
				break;
			case TRAILING:
				appendable.append(this.style.getDecimalFormat().format(
						Money.from(amount).asType(BigDecimal.class)));
				appendable.append(" ").append(cur.getCurrencyCode());
			}
			return;
		}
		this.style.getDecimalFormat().setGroupingUsed(false);
		String preformattedValue = this.style.getDecimalFormat().format(
				Money.from(amount).asType(BigDecimal.class));
		String[] numberParts = splitNumberParts(this.style.getDecimalFormat(),
				preformattedValue);
		if (numberParts.length != 2) {
			switch (this.style.getCurrencyPlacement()) {
			case OMIT:
				appendable.append(preformattedValue);
				break;
			default:
			case LEADING:
				appendable.append(cur.getCurrencyCode()).append(" ");
				appendable.append(preformattedValue);
				break;
			case TRAILING:
				appendable.append(preformattedValue);
				appendable.append(" ").append(cur.getCurrencyCode());
			}
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
			switch (this.style.getCurrencyPlacement()) {
			case OMIT:
				appendable.append(preformattedValue);
				break;
			default:
			case LEADING:
				appendable.append(cur.getCurrencyCode()).append(" ");
				appendable.append(preformattedValue);
				break;
			case TRAILING:
				appendable.append(preformattedValue);
				appendable.append(" ").append(cur.getCurrencyCode());
			}
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
				return;
			}
		} catch (Exception e) {
			LOG.finest("Could not parse amount from: " + token);
		}
		try {
			MoneyCurrency currency = MoneyCurrency.of(token);
			context.setParsedCurrency(currency);
			context.consume(token);
			return;
		} catch (Exception e) {
			LOG.finest("Could not parse currency from: " + token);
		}
	}

}
