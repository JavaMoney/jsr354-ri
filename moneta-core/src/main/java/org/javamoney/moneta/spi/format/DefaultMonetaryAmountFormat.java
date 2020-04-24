/*
 * Copyright (c) 2012, 2020, Anatole Tresch, Werner Keil and others by the @author tag.
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
package org.javamoney.moneta.spi.format;

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
import javax.money.format.AmountFormatContextBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryParseException;

import org.javamoney.moneta.format.AmountFormatParams;
import org.javamoney.moneta.format.CurrencyStyle;
import org.javamoney.moneta.spi.MoneyUtils;

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
 * When parsing currencies this format supports an optional {@code currencyProviderName}
 * context parameter on the {@link AmountFormatContext}. This name will be passed as a currency trarget provider when
 * resolving currency codes and symbols.
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
        if(locale != null && locale.getCountry().equals("IN")
        && amountFormatContext.get(AmountFormatParams.GROUPING_SIZES, int[].class)==null){
            // Fix invalid JDK grouping for rupees...
            amountFormatContext = amountFormatContext.toBuilder().set(AmountFormatParams.GROUPING_SIZES, new int[]{3,2})
                                        .build();
        }
        if(locale != null && locale.getCountry().equals("BG")){
            AmountFormatContextBuilder builder = amountFormatContext.toBuilder();
            if(amountFormatContext.get(AmountFormatParams.GROUPING_SIZES, int[].class)==null) {
                // Fix invalid JDK grouping for leva...
                builder.set(AmountFormatParams.GROUPING_SIZES, new int[]{3}).build();
            }
            if(amountFormatContext.get(AmountFormatParams.GROUPING_GROUPING_SEPARATORS, int[].class)==null) {
                // Fix invalid JDK grouping for leva...
                builder.set(AmountFormatParams.GROUPING_GROUPING_SEPARATORS, new String[]{"\u00A0"}).build();
            }
            amountFormatContext = builder.build();
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
        text = MoneyUtils.replaceNbspWithSpace(text.toString()).trim();
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
            pattern = MoneyUtils.replaceNbspWithSpace(currencyDecimalFormat.toPattern());
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
        Locale locale = context.get(Locale.class);
        DecimalFormat format = (DecimalFormat)DecimalFormat.getCurrencyInstance(locale);
        CurrencyStyle currencyStyle = context.get(CurrencyStyle.class);
        List<String> patternParts = tokenizePattern(pattern, format);
        List<FormatToken> tokens = new ArrayList<>(3);
        for(String p:patternParts){
            if (isNumberToken(p)) {
                tokens.add(new AmountNumberToken(context, p.substring(4)));
            } else if(isCurrencyToken(p)){
                tokens.add(new CurrencyToken(currencyStyle, context));
            } else{
                if(!p.isEmpty()) {
                    tokens.add(new LiteralToken(p));
                }
            }
        }
        return tokens;
    }

    private boolean isNumberToken(String token) {
        return token.startsWith("NUM:");
    }

    private boolean isCurrencyToken(String token) {
        return token.length()==0 || token.charAt(0)==CURRENCY_SIGN;
    }

    private List<String> tokenizePattern(String pattern, DecimalFormat format) {
        List<String> result = splitPatternForCurrency(pattern);
        return splitNumberPattern(result, format);
    }

    /**
     * Splits the given pattern into a prefix, a currency token and a postfix token.
     * @param pattern
     * @return
     */
    private List<String> splitPatternForCurrency(String pattern) {
        List<String> result = new ArrayList();
        int index = pattern.indexOf(CURRENCY_SIGN);
        if(index<0){
            result.add(pattern);
        }else {
            String p = pattern.substring(0, index);
            if (!p.isEmpty()) {
                result.add(p);
            }
            result.add("" + CURRENCY_SIGN);
            p = pattern.substring(index + 1);
            if (!p.isEmpty()) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * Splits away the number pattern for targeting an AmountToken, if possible.
     * @param tokens the token identified so far.
     * @param format the target locale.
     * @return the tokenized list.
     */
    private List<String> splitNumberPattern(List<String> tokens, DecimalFormat format){
        List<String> result = new ArrayList();
        String numberPattern = format.toLocalizedPattern()
                .replace(""+CURRENCY_SIGN, "").trim();
        for(String token:tokens){
            int index = token.indexOf(numberPattern);
            if(index>0){
                String part0 = token.substring(0, index);
                if(!part0.isEmpty()){
                    result.add(part0);
                }
                result.add("NUM:"+numberPattern);
                String part1 = token.substring(index+numberPattern.length());
                if(!part1.isEmpty()){
                    result.add(part1);
                }
            }else{
                result.add(token);
            }
        }
        if(result.size() == tokens.size()){
            result.clear();
            // we have to check each token for a number pattern manually...
            for(String token:tokens){
                numberPattern = getNumberPattern(token, format);
                if(numberPattern==null) {
                    result.add(token);
                }else {
                    int index = token.indexOf(numberPattern);
                    String part0 = token.substring(0, index);
                    if (!part0.isEmpty()) {
                        result.add(part0);
                    }
                    result.add("NUM:" + numberPattern);
                    String part1 = token.substring(index + numberPattern.length());
                    if (!part1.isEmpty()) {
                        result.add(part1);
                    }
                }
            }
        }
        return result;
    }

    private String getNumberPattern(String token, DecimalFormat format) {
        // Parse the token for
        int first = -1;
        int last = -1;
        DecimalFormatSymbols syms = format.getDecimalFormatSymbols();
        char[] chars = token.toCharArray();
        int nonMatching = 0;
        for(int i=0; i<chars.length;i++){
            if(chars[i] ==syms.getMonetaryDecimalSeparator() ||
                    chars[i] ==syms.getMonetaryDecimalSeparator() ||
                    chars[i] ==syms.getDecimalSeparator() ||
                    chars[i] ==syms.getGroupingSeparator() ||
                    chars[i] ==syms.getMinusSign() ||
                    chars[i] ==syms.getPercent() ||
                    chars[i] ==syms.getPerMill() ||
                    chars[i] ==syms.getZeroDigit() ||
                    chars[i] ==syms.getDigit()){
                if(first<0)first = i;
                last = i;
                nonMatching = 0;
            }else{
                nonMatching++;
            }
            if(last!=-1 && first<last && nonMatching>2){
                break;
            }
        }
        if(last!=-1 && first<last){
            return token.substring(first, last+1);
        }
        return null;
    }

    private boolean isLiteralPattern(String pattern) {
        // TODO implement better here
        return !(pattern.contains("#") || pattern.contains("0"));
    }

}
