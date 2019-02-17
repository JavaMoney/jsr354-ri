/*
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
package org.javamoney.moneta.internal.format;

import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatContext;
import javax.money.format.MonetaryParseException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;
import static org.javamoney.moneta.format.AmountFormatParams.GROUPING_GROUPING_SEPARATORS;
import static org.javamoney.moneta.format.AmountFormatParams.GROUPING_SIZES;
import static org.javamoney.moneta.spi.MoneyUtils.NBSP;
import static org.javamoney.moneta.spi.MoneyUtils.NNBSP;
import static org.javamoney.moneta.spi.MoneyUtils.replaceNbspWithSpace;

/**
 * {@link FormatToken} which allows to format a {@link MonetaryAmount} type.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
final class AmountNumberToken implements FormatToken {

    private final AmountFormatContext amountFormatContext;
    private final String partialNumberPattern;
    private DecimalFormat parseFormat;
    private DecimalFormat formatFormat;
    private StringGrouper numberGroup;

    AmountNumberToken(AmountFormatContext amountFormatContext, String partialNumberPattern) {
        requireNonNull(amountFormatContext, "amountFormatContext is required.");
        requireNonNull(partialNumberPattern, "partialNumberPattern is required.");
        this.amountFormatContext = amountFormatContext;
        this.partialNumberPattern = replaceNbspWithSpace(partialNumberPattern);
        initDecimalFormats();
    }

    private void initDecimalFormats() {
        Locale locale = amountFormatContext.get(Locale.class);
        formatFormat = (DecimalFormat) DecimalFormat.getInstance(locale);
        parseFormat = (DecimalFormat) formatFormat.clone();
        DecimalFormatSymbols syms = amountFormatContext.get(DecimalFormatSymbols.class);
        if (Objects.nonNull(syms)) {
            syms = (DecimalFormatSymbols) syms.clone();
        } else {
            syms = formatFormat.getDecimalFormatSymbols();
        }
        fixThousandsSeparatorWithSpace(syms);
        formatFormat.setDecimalFormatSymbols(syms);
        parseFormat.setDecimalFormatSymbols(syms);

        formatFormat.applyPattern(partialNumberPattern);
        parseFormat.applyPattern(partialNumberPattern.trim());
    }

    private void fixThousandsSeparatorWithSpace(DecimalFormatSymbols syms) {
        if (syms.getGroupingSeparator() == NBSP || syms.getGroupingSeparator() == NNBSP) {
            syms.setGroupingSeparator(' ');
        }
    }

    /**
     * Access the underlying amount format context.
     *
     * @return the {@link javax.money.format.AmountFormatContext}.
     */
    public AmountFormatContext getAmountFormatContext() {
        return amountFormatContext;
    }

    /**
     * Get the number pattern used by this {@link AmountNumberToken} token. This
     * pattern can be a partial pattern, of the full pattern in place.
     *
     * @return the number pattern used, never {@code null}.
     */
    public String getNumberPattern() {
        return partialNumberPattern;
    }

    @Override
    public void print(Appendable appendable, MonetaryAmount amount)
            throws IOException {
        int[] groupSizes = amountFormatContext.get(GROUPING_SIZES, int[].class);
        if (groupSizes == null || groupSizes.length == 0) {
            String preformattedValue = formatFormat.format(amount.getNumber().numberValue(BigDecimal.class));
            appendable.append(preformattedValue);
            return;
        }
        formatFormat.setGroupingUsed(false);
        String preformattedValue = formatFormat.format(amount.getNumber().numberValue(BigDecimal.class));
        String[] numberParts = splitNumberParts(formatFormat, preformattedValue);
        if (numberParts.length != 2) {
            appendable.append(preformattedValue);
        } else {
            if (Objects.isNull(numberGroup)) {
                char[] groupChars = amountFormatContext.get(GROUPING_GROUPING_SEPARATORS, char[].class);
                if (groupChars == null || groupChars.length == 0) {
                    char groupingSeparator = formatFormat.getDecimalFormatSymbols().getGroupingSeparator();
                    groupChars = new char[]{groupingSeparator};
                }
                numberGroup = new StringGrouper(groupChars, groupSizes);
            }
            preformattedValue = numberGroup.group(numberParts[0])
                    + formatFormat.getDecimalFormatSymbols()
                    .getDecimalSeparator() + numberParts[1];
            appendable.append(preformattedValue);
        }
    }

    private String[] splitNumberParts(DecimalFormat format,
                                      String preformattedValue) {
        char decimalSeparator = format.getDecimalFormatSymbols().getDecimalSeparator();
        int index = preformattedValue.indexOf(decimalSeparator);
        if (index < 0) {
            return new String[]{preformattedValue};
        }
        String beforeSeparator = preformattedValue.substring(0, index);
        String afterSeparator = preformattedValue.substring(index + 1);
        return new String[]{beforeSeparator, afterSeparator};
    }

    @Override
    public void parse(ParseContext context) throws MonetaryParseException {
        context.skipWhitespace();
        if (!context.isFullyParsed()) {
            parseToken(context);
            if (context.hasError()) {
                throw new MonetaryParseException(context.getErrorMessage(), context.getInput(), context.getIndex());
            }
        } else {
            context.setError();
            context.setErrorMessage("Number expected.");
        }
    }

    private void parseToken(ParseContext context) {
        ParsePosition pos = new ParsePosition(context.getIndex());
        Number number = parseFormat.parse(context.getOriginalInput(), pos);
        if (Objects.nonNull(number)) {
            context.setParsedNumber(number);
            String consumedToken = context.getOriginalInput().substring(context.getIndex(), pos.getIndex());
            context.consume(consumedToken);
        } else {
            Logger.getLogger(getClass().getName()).finest("Could not parse amount from: " + context.getOriginalInput());
            context.setError();
            context.setErrorIndex(pos.getErrorIndex());
            context.setErrorMessage("Unparseable number: \"" + context.getOriginalInput() + "\"");
        }
    }

    @Override
    public String toString() {
        Locale locale = amountFormatContext.getLocale();
        return "AmountNumberToken [locale=" + locale + ", partialNumberPattern=" + partialNumberPattern + ']';
    }
}
