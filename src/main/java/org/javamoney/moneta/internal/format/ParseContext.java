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

import java.text.ParsePosition;
import java.util.Objects;
import java.util.Optional;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;

/**
 * Context passed along to each {@link FormatToken} in-line, when parsing an
 * input stream using a {@link MonetaryAmountFormat}. It allows to inspect the
 * next tokens, the whole input String, or just the current input substring,
 * based on the current parsing position etc.
 * <p>
 * This class is mutable and intended for use by a single thread. A new instance
 * is created for each parse.
 */
final class ParseContext {
    /**
     * The current position of parsing.
     */
    private int index;
    /**
     * The error index position.
     */
    private int errorIndex = -1;
    /**
     * The full input.
     */
    private final CharSequence originalInput;
    /**
     * The currency parsed, used for creation of the {@link MonetaryAmount}.
     */
    private CurrencyUnit parsedCurrency;
    /**
     * The numeric part of the {@link MonetaryAmount} parsed.
     */
    private Number parsedNumber;
    /**
     * The parse error message.
     */
    private String errorMessage;

    /**
     * Creates a new {@link ParseContext} with the given input.
     *
     * @param text The test to be parsed.
     */
    ParseContext(CharSequence text) {
        this.originalInput = Optional.ofNullable(text).orElseThrow(
                () -> new IllegalArgumentException("text is required"));
    }

    /**
     * Method allows to determine if the item being parsed is available from the
     * {@link ParseContext}.
     *
     * @return true, if the item is available.
     */
    public boolean isComplete() {
        return Objects.nonNull(parsedNumber) && Objects.nonNull(parsedCurrency);
    }

    /**
     * Get the parsed item.
     *
     * @return the item parsed.
     */
    public Number getParsedNumber() {
        return parsedNumber;
    }

    /**
     * Consumes the given token. If the current residual text to be parsed
     * starts with the parsing index is increased by {@code token.size()}.
     *
     * @param token The token expected.
     * @return true, if the token could be consumed and the index was increased
     * by {@code token.size()}.
     */
    public boolean consume(String token) {
        if (getInput().toString().startsWith(token)) {
            index += token.length();
            return true;
        }
        return false;
    }

    /**
     * Tries to consume one single character.
     *
     * @param c the next character being expected.
     * @return true, if the character matched and the index could be increased
     * by one.
     */
    public boolean consume(char c) {
        if (originalInput.charAt(index) == c) {
            index++;
            return true;
        }
        return false;
    }

    /**
     * Skips all whitespaces until a non whitespace character is occurring. If
     * the next character is not whitespace this method does nothing.
     *
     * @return the new parse index after skipping any whitespaces.
     * @see Character#isWhitespace(char)
     */
    public int skipWhitespace() {
        for (int i = index; i < originalInput.length(); i++) {
            if (Character.isWhitespace(originalInput.charAt(i))) {
                index++;
            } else {
                break;
            }
        }
        return index;
    }

    /**
     * Gets the error index.
     *
     * @return the error index, negative if no error
     */
    public int getErrorIndex() {
        return errorIndex;
    }

    /**
     * Sets the error index.
     *
     * @param index the error index
     */
    public void setErrorIndex(int index) {
        this.errorIndex = index;
    }

    /**
     * Get the stored error message.
     *
     * @return the stored error message, or null.
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Sets the error index from the current index.
     */
    public void setError() {
        this.errorIndex = index;
    }

    /**
     * Gets the current parse position.
     *
     * @return the current parse position within the input.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets the residual input text starting from the current parse position.
     *
     * @return the residual input text
     */
    public CharSequence getInput() {
        return originalInput.subSequence(index, originalInput.length());
    }

    /**
     * Gets the full input text.
     *
     * @return the full input.
     */
    public String getOriginalInput() {
        return originalInput.toString();
    }

    /**
     * Resets this instance; this will resetToFallback the parsing position, the error
     * index and also all containing results.
     */
    public void reset() {
        this.index = 0;
        this.errorIndex = -1;
        this.parsedNumber = null;
        this.parsedCurrency = null;
    }

    /**
     * Sets the parsed numeric value into the context.
     *
     * @param number The result number
     */
    public void setParsedNumber(Number number) {
        this.parsedNumber = number;
    }

    /**
     * Set the parsed currency into the context.
     *
     * @param currency The parsed currency
     */
    public void setParsedCurrency(CurrencyUnit currency) {
        this.parsedCurrency = currency;
    }

    /**
     * Checks if the parse has found an error.
     *
     * @return whether a parse error has occurred
     */
    public boolean hasError() {
        return errorIndex >= 0;
    }

    /**
     * Checks if the text has been fully parsed such that there is no more text
     * to parse.
     *
     * @return true if fully parsed
     */
    public boolean isFullyParsed() {
        return index == this.originalInput.length();
    }

    /**
     * This method skips all whitespaces and returns the full text, until
     * another whitespace area or the end of the input is reached. The method
     * will not update any index pointers.
     *
     * @return the next token found, or null.
     */
    public String lookupNextToken() {
        skipWhitespace();
        int start = index;
        for (int end = index; end < originalInput.length(); end++) {
            if (Character.isWhitespace(originalInput.charAt(end))) {
                if (end > start) {
                    return originalInput.subSequence(start, end).toString();
                }
                return null;
            }
        }
        if (start < originalInput.length()) {
            return originalInput.subSequence(start, originalInput.length())
                    .toString();
        }
        return null;
    }

    /**
     * Converts the indexes to a parse position.
     *
     * @return the parse position, never null
     */
    public ParsePosition toParsePosition() {
        return new ParsePosition(index);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ParseContext [index=" + index + ", errorIndex=" + errorIndex
                + ", originalInput='" + originalInput + "', parsedNumber="
                + parsedNumber + "', parsedCurrency=" + parsedCurrency
                + ']';
    }

    public CurrencyUnit getParsedCurrency() {
        return parsedCurrency;
    }

    public void setErrorMessage(String message) {
        Objects.requireNonNull(message);
        this.errorMessage = message;
    }
}
