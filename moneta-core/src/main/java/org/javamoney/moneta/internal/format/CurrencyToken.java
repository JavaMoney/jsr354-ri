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

import org.javamoney.moneta.format.CurrencyStyle;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.Monetary;
import javax.money.format.MonetaryParseException;
import java.io.IOException;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.FINEST;
import static org.javamoney.moneta.format.CurrencyStyle.CODE;

/**
 * Implements a {@link FormatToken} that adds a localizable {@link String}, read
 * by key from a {@link ResourceBundle}.
 *
 * @author Anatole Tresch
 */
final class CurrencyToken implements FormatToken {
    /**
     * The style defining, how the currency should be localized.
     */
    private CurrencyStyle style = CODE;
    /**
     * The target locale.
     */
    private final Locale locale;

    /**
     * Creates a new {@link CurrencyToken}.
     *
     * @param style  The style defining, how the currency should be localized, not
     *               {@code null}.
     * @param locale The target locale, not {@code null}.
     */
    CurrencyToken(CurrencyStyle style, Locale locale) {
        this.locale = requireNonNull(locale, "Locale null");
        if (Objects.nonNull(style)) {
            this.style = style;
        }
    }

    /**
     * Explicitly configure the {@link CurrencyStyle} to be used.
     *
     * @param style the {@link CurrencyStyle}, not {@code null}.
     * @return this token instance, for chaining.
     */
    public CurrencyToken setCurrencyStyle(CurrencyStyle style) {
        this.style = requireNonNull(style, "CurrencyStyle null");
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
     * @param amount the {@link MonetaryAmount} containing the {@link CurrencyUnit}
     *               to be formatted.
     * @return the formatted currency.
     */
    private String getToken(MonetaryAmount amount) {
        switch (style) {
            case NUMERIC_CODE:
                return String.valueOf(amount.getCurrency().getNumericCode());
            case NAME:
                return getCurrencyName(amount.getCurrency());
            case SYMBOL:
                return getCurrencySymbol(amount.getCurrency());
            case CODE:
                return amount.getCurrency().getCurrencyCode();
            default:
                throw new UnsupportedOperationException("Unexpected style " + style);
        }
    }

    /**
     * This method tries to evaluate the localized display name for a
     * {@link CurrencyUnit}. It uses {@link Currency#getDisplayName(Locale)} if
     * the given currency code maps to a JDK {@link Currency} instance.
     * <p>
     * If not found {@code currency.getCurrencyCode()} is returned.
     *
     * @param currency The currency, not {@code null}
     * @return the formatted currency name.
     */
    private String getCurrencyName(CurrencyUnit currency) {
        Currency jdkCurrency = getCurrency(currency.getCurrencyCode());
        if (Objects.nonNull(jdkCurrency)) {
            return jdkCurrency.getDisplayName(locale);
        }
        return currency.getCurrencyCode();
    }

    /**
     * Method to safely access a {@link java.util.Currency}.
     * If no such currency exists, null is returned.
     *
     * @param currencyCode the currency code , not null.
     * @return the corresponding currency instance, or null.
     */
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
     * @param currency The currency, not {@code null}
     * @return the formatted currency symbol.
     */
    private String getCurrencySymbol(CurrencyUnit currency) {
        Currency jdkCurrency = getCurrency(currency.getCurrencyCode());
        if (Objects.nonNull(jdkCurrency)) {
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
     * @throws MonetaryParseException on an error or if the {@link CurrencyStyle} is configured
     *         to non implemented currency name (NAME), or numeric codes (NUMERIC_CODE).
     */
    @Override
    public void parse(ParseContext context)
            throws MonetaryParseException {
        String token = context.lookupNextToken();
        while (Objects.nonNull(token)) {
            if (token.trim().isEmpty()) {
                context.consume(token);
                token = context.lookupNextToken();
                continue;
            }
            break;
        }
        if (token == null){
            throw new MonetaryParseException("Error parsing CurrencyUnit: no input.", "", -1);
        }
        try {
            CurrencyUnit cur;
            switch (style) {
                case CODE:
                    if (!Monetary.isCurrencyAvailable(token)) {
                        // Perhaps blank is missing between currency code and number...
                        String subCurrency = parseCurrencyCode(token);
                        cur = Monetary.getCurrency(subCurrency);
                        context.consume(subCurrency);
                    } else {
                        cur = Monetary.getCurrency(token);
                        context.consume(token);
                    }
                    break;
                case SYMBOL:
                    if (token.startsWith("$")) {
                        throw new MonetaryParseException("$ is not a unique currency symbol.", token,
                                context.getErrorIndex());
                    } else if (token.startsWith("€")) {
                        cur = Monetary.getCurrency("EUR");
                        context.consume("€");
                    } else if (token.startsWith("£")) {
                        cur = Monetary.getCurrency("GBP");
                        context.consume("£");
                    } else {
                        cur = Monetary.getCurrency(token);
                        context.consume(token);
                    }
                    context.setParsedCurrency(cur);
                    break;
                case NAME:
                case NUMERIC_CODE:
                default:
                    throw new UnsupportedOperationException("Not yet implemented");
            }
            if (Objects.nonNull(cur)) {
                context.setParsedCurrency(cur);
            }
        } catch (MonetaryParseException e) {
            context.setError();
            context.setErrorMessage(e.getMessage());
            throw e;
        } catch (Exception e) {
            context.setError();
            context.setErrorMessage(e.getMessage());
            Logger.getLogger(getClass().getName()).log(FINEST, "Could not parse CurrencyUnit from \"" + token + "\"", e);
            throw new MonetaryParseException("Could not parse CurrencyUnit. " + e.getMessage(), token, -1);
        }
    }

    /**
     * Tries to split up a letter based first part, e.g. for evaluating a ISO currency code from an input as
     * 'CHF100.34'.
     * @param token the input token
     * @return the first letter based part, or the full token.
     */
    private String parseCurrencyCode(String token) {
        int letterIndex = 0;
        for (char ch : token.toCharArray()) {
            if (Character.isLetter(ch)) {
                letterIndex++;
            } else {
                return token.substring(0, letterIndex);
            }
        }
        return token;
    }

    /**
     * Prints the {@link CurrencyUnit} of the given {@link MonetaryAmount} to
     * the given {@link Appendable}.
     *
     * @throws IOException may be thrown by the {@link Appendable}
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
        return "CurrencyToken [locale=" + locale + ", style=" + style + ']';
    }

}
