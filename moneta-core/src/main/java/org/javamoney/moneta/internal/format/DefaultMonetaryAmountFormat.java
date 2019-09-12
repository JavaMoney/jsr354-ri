/*
 * Copyright (c) 2012, 2019, Anatole Tresch, Werner Keil and others by the @author tag.
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

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;
import javax.money.*;
import javax.money.format.AmountFormatContext;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryParseException;

import org.javamoney.moneta.format.AmountFormatParams;
import org.javamoney.moneta.format.CurrencyStyle;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.FINEST;
import static org.javamoney.moneta.format.AmountFormatParams.PATTERN;

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
 */
final class DefaultMonetaryAmountFormat implements MonetaryAmountFormat {

    /**
     * The international Unicode currency sign.
     */
    private static final char CURRENCY_SIGN = '¤';

    /**
     * Separates positive and negative subpatterns
     */
    private static final char SUBPATTERN_BOUNDARY = ';';

    /**
     * The tokens to be used for formatting/parsing of positive and zero numbers.
     */
    private List<FormatToken> positiveTokens;

    /**
     * The tokens to be used for formatting/parsing of negative numbers.
     */
    private List<FormatToken> negativeTokens;

    /**
     * The current {@link javax.money.format.AmountFormatContext}, never null.
     */
    private AmountFormatContext amountFormatContext;


    /**
     * Creates a new instance.
     *
     * @param amountFormatContext the {@link javax.money.format.AmountFormatContext} to be used, not {@code null}.
     */
    DefaultMonetaryAmountFormat(AmountFormatContext amountFormatContext) {
        Locale locale = amountFormatContext.getLocale();
        if(locale != null && locale.getCountry().equals("IN")){
            // Fix invalid JDK grouping for rupees...
            amountFormatContext = amountFormatContext.toBuilder().set(AmountFormatParams.GROUPING_SIZES, new int[]{3,2})
                                        .build();
        }
        setAmountFormatContext(amountFormatContext);
    }

    /**
     * Formats a value of {@code T} to a {@code String}. {@link java.util.Locale}
     * passed defines the overall target {@link Locale}. This locale state, how the
     * {@link MonetaryAmountFormat} should generally behave. The
     * {@link java.util.Locale} allows to configure the formatting and parsing
     * in arbitrary details. The attributes that are supported are determined by
     * the according {@link MonetaryAmountFormat} implementation:
     *
     * @param amount the amount to print, not {@code null}
     * @return the string printed using the settings of this formatter
     * @throws UnsupportedOperationException if the formatter is unable to print
     */
    @Override
    public String format(MonetaryAmount amount) {
        StringBuilder builder = new StringBuilder();
        try {
            print(builder, amount);
        } catch (IOException e) {
            throw new IllegalStateException("Error formatting of " + amount, e);
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
     * @param appendable the appendable to add to, not null
     * @param amount     the amount to print, not null
     * @throws IOException if an IO error occurs
     */
    @Override
    public void print(Appendable appendable, MonetaryAmount amount) throws IOException {
        List<FormatToken> tokens = amount.isNegative() ? negativeTokens : positiveTokens;
        for (FormatToken token : tokens) {
            token.print(appendable, amount);
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
     * @param text the text to parse, not null
     * @return the parsed value, never {@code null}
     * @throws UnsupportedOperationException             if the formatter is unable to parse
     * @throws javax.money.format.MonetaryParseException if there is a problem while parsing
     */
    @Override
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
            if (log.isLoggable(FINEST)) {
                log.log(FINEST, "Failed to parse positive pattern, trying negative for: " + text, e);
            }
            for (FormatToken token : this.negativeTokens) {
                token.parse(ctx);
            }
        }
        CurrencyUnit unit = ctx.getParsedCurrency();
        if (Objects.isNull(unit)) {
            unit = this.amountFormatContext.get(CurrencyUnit.class);
        }
        if (Objects.isNull(unit)) {
            throw new MonetaryParseException("Failed to parse currency. Is currency sign ¤ present in pattern?", text.toString(), -1);
        }
        Number num = ctx.getParsedNumber();
        if (Objects.isNull(num)) {
            throw new MonetaryParseException("Failed to parse amount", text.toString(), -1);
        }
        MonetaryAmountFactory<?> factory = this.amountFormatContext.getParseFactory();
        if (factory == null) {
            factory = Monetary.getDefaultAmountFactory();
        }
        return factory.setCurrency(unit).setNumber(num).create();
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
    public AmountFormatContext getContext() {
        return this.amountFormatContext;
    }

    private void setAmountFormatContext(AmountFormatContext amountFormatContext) {
        this.amountFormatContext = requireNonNull(amountFormatContext);
        String pattern = resolvePattern(amountFormatContext);
        String[] plusMinusPatterns = splitIntoPlusMinusPatterns(amountFormatContext, pattern);
        String positivePattern = plusMinusPatterns[0];
        this.positiveTokens = initPattern(positivePattern, amountFormatContext);
        if (plusMinusPatterns.length > 1) { // if negative pattern is specified
            String negativePattern = plusMinusPatterns[1];
            String pattern1 = negativePattern.replace("-", "");
            this.negativeTokens = initPattern(pattern1, amountFormatContext);
        } else { // only positive patter is specified
            this.negativeTokens = this.positiveTokens;
        }
    }

    private String resolvePattern(AmountFormatContext amountFormatContext) {
        String pattern = amountFormatContext.getText(PATTERN);
        if (pattern == null) {
            DecimalFormat currencyDecimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance(amountFormatContext.getLocale());
            pattern = currencyDecimalFormat.toPattern();
        }
        return pattern;
    }

    /**
     * Split into (potential) plus, minus patterns
     */
    private String[] splitIntoPlusMinusPatterns(AmountFormatContext amountFormatContext, String pattern) {
        DecimalFormatSymbols decimalFormatSymbols = amountFormatContext.get(DecimalFormatSymbols.class);
        char patternSeparator = decimalFormatSymbols != null ? decimalFormatSymbols.getPatternSeparator() : SUBPATTERN_BOUNDARY;
        return pattern.split(String.valueOf(patternSeparator));
    }

    private List<FormatToken> initPattern(String pattern, AmountFormatContext context) {
        int currencySignPos = pattern.indexOf(CURRENCY_SIGN);
        Locale locale = context.get(Locale.class);
        CurrencyStyle currencyStyle = context.get(CurrencyStyle.class);
        if (currencySignPos > 0) { // currency placement after, between
            String p1 = pattern.substring(0, currencySignPos);
            String p2 = pattern.substring(currencySignPos + 1);
            List<FormatToken> tokens = new ArrayList<>(3);
            if (isLiteralPattern(p1)) {
                tokens.add(new LiteralToken(p1));
                tokens.add(new CurrencyToken(currencyStyle, locale));
            } else {
                tokens.add(new AmountNumberToken(context, p1));
                tokens.add(new CurrencyToken(currencyStyle, locale));
            }
            if (!p2.isEmpty()) {
                if (isLiteralPattern(p2)) {
                    tokens.add(new LiteralToken(p2));
                } else {
                    tokens.add(new AmountNumberToken(context, p2));
                }
            }
            return tokens;
        } else if (currencySignPos == 0) { // currency placement before
            String patternWithoutCurrencySign = pattern.substring(1);
            List<FormatToken> tokens = asList(
                    new CurrencyToken(currencyStyle, locale),
                    new AmountNumberToken(context, patternWithoutCurrencySign));
            return tokens;
        }
        // no currency
        List<FormatToken> tokens = asList(new AmountNumberToken(context, pattern));
        return tokens;
    }

    private boolean isLiteralPattern(String pattern) {
        // TODO implement better here
        return !(pattern.contains("#") || pattern.contains("0"));
    }

}
