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
import java.util.Currency;

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
		// Check for ISO currency format
		String pattern = this.style.getDecimalFormat().toPattern();
		if (pattern.contains("¤")) {
			if (MoneyCurrency.isJavaCurrency(amount.getCurrency()
					.getCurrencyCode())) {
				this.style.getDecimalFormat().setCurrency(
						Currency.getInstance(amount.getCurrency()
								.getCurrencyCode()));
			}
			else {
				this.style.getDecimalFormat().setCurrency(
						Currency.getInstance("XXX"));
			}
		}
		if (this.style.getNumberGroupSizes().length == 0) {
			appendable.append(this.style.getDecimalFormat().format(
					Money.from(amount).asType(BigDecimal.class)));
			return;
		}
		this.style.getDecimalFormat().setGroupingUsed(false);
		String preformattedValue = this.style.getDecimalFormat().format(
				Money.from(amount).asType(BigDecimal.class));
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
			appendable.append(numberGroup.group(numberParts[0])
					+ this.style.getDecimalFormat().getDecimalFormatSymbols()
							.getDecimalSeparator()
					+ numberParts[1]);
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
		Number number = this.style.getDecimalFormat().parse(
				context.getInput().toString(), pos);
		context.setParsedNumber(number);
	}

}
