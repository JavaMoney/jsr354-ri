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
package org.javamoney.moneta.internal.format;

import org.javamoney.moneta.format.AmountFormatParams;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatContext;
import javax.money.format.MonetaryParseException;

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
        this.amountFormatContext = Optional.ofNullable(amountFormatContext)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "amountFormatContext is required."));
        this.partialNumberPattern = partialNumberPattern;
        initDecimalFormats();
    }

    private void initDecimalFormats() {
        formatFormat = (DecimalFormat) DecimalFormat.getInstance(amountFormatContext.get(Locale.class));
        parseFormat = (DecimalFormat) DecimalFormat.getInstance(amountFormatContext.get(Locale.class));
        DecimalFormatSymbols syms = amountFormatContext.get(DecimalFormatSymbols.class);
        if (Objects.nonNull(syms)) {
            formatFormat.setDecimalFormatSymbols(syms);
            parseFormat.setDecimalFormatSymbols(syms);
        }
        formatFormat.applyPattern(this.partialNumberPattern);
        parseFormat.applyPattern(this.partialNumberPattern.trim());
    }

    /**
     * Access the underlying amount fomat context.
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
        return this.partialNumberPattern;
    }

    @Override
    public void print(Appendable appendable, MonetaryAmount amount)
            throws IOException {
        if (amountFormatContext.get(AmountFormatParams.GROUPING_SIZES, int[].class) == null ||
                amountFormatContext.get(AmountFormatParams.GROUPING_SIZES, int[].class).length == 0) {
            appendable.append(this.formatFormat.format(amount.getNumber()
                    .numberValue(BigDecimal.class)));
            return;
        }
        this.formatFormat.setGroupingUsed(false);
        String preformattedValue = this.formatFormat.format(amount.getNumber()
                .numberValue(BigDecimal.class));
        String[] numberParts = splitNumberParts(this.formatFormat,
                preformattedValue);
        if (numberParts.length != 2) {
            appendable.append(preformattedValue);
        } else {
            if (Objects.isNull(numberGroup)) {
                char[] groupChars = amountFormatContext.get(AmountFormatParams.GROUPING_GROUPING_SEPARATORS, char[].class);
                if (groupChars == null || groupChars.length == 0) {
                    groupChars = new char[]{this.formatFormat
                            .getDecimalFormatSymbols().getGroupingSeparator()};
                }
                int[] groupSizes = amountFormatContext.get(AmountFormatParams.GROUPING_SIZES, int[].class);
                if (groupSizes == null) {
                    groupSizes = new int[0];
                }
                numberGroup = new StringGrouper(groupChars, groupSizes);
            }
            preformattedValue = numberGroup.group(numberParts[0])
                    + this.formatFormat.getDecimalFormatSymbols()
                    .getDecimalSeparator() + numberParts[1];
            appendable.append(preformattedValue);
        }
    }

    private String[] splitNumberParts(DecimalFormat format,
                                      String preformattedValue) {
        int index = preformattedValue.indexOf(format.getDecimalFormatSymbols()
                .getDecimalSeparator());
        if (index < 0) {
            return new String[]{preformattedValue};
        }
        return new String[]{preformattedValue.substring(0, index),
                preformattedValue.substring(index + 1)};
    }

    @Override
    public void parse(ParseContext context) throws MonetaryParseException {
        String token = context.lookupNextToken();
        if (Objects.nonNull(token) && !context.isComplete()) {
            parseToken(context, token);
            if (context.hasError()) {
                throw new MonetaryParseException(context.getErrorMessage(), context.getInput(), context.getIndex());
            }
        } else {
            context.setError();
            context.setErrorMessage("Number expected.");
        }
    }

    private void parseToken(ParseContext context, String token) {
        try {
            Number number = this.parseFormat.parse(token);
            if (Objects.nonNull(number)) {
                context.setParsedNumber(number);
                context.consume(token);
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).finest(
                    "Could not parse amount from: " + token);
            context.setError();
            context.setErrorMessage(e.getMessage());
        }
    }

}
