/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency
 * API ("Specification") Copyright (c) 2012-2014, Credit Suisse All rights
 * reserved.
 */
package org.javamoney.moneta;

import javax.money.AbstractQuery;
import javax.money.QueryType;
import javax.money.convert.ConversionQuery;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Enumeration with the most important query selectors.
 */
public final class QueryTypes {

    /**
     * Default QueryType for accessing an {@link javax.money.convert.ExchangeRate}.
     */
    public static final QueryType RATE_QUERY = QueryTypes.of("RateQuery", ConversionQuery.class, "base: CurrencyUnit", "term: CurrencyUnit");

    /**
     * Default QueryType for accessing an historic {@link javax.money.convert.ExchangeRate}.
     */
    public static final QueryType RATE_HIST_QUERY = QueryTypes.of("RateHistQuery", ConversionQuery.class, "base: CurrencyUnit", "term: CurrencyUnit", "timestamp: long/TemporalAccessor");

    /**
     * Default QueryType for accessing an {@link javax.money.convert.CurrencyConversion}.
     */
    public static final QueryType CONVERSION_QUERY = QueryTypes.of("ConversionQuery", ConversionQuery.class, "term: CurrencyUnit");

    /**
     * Default QueryType for accessing an historic {@link javax.money.convert.CurrencyConversion}.
     */
    public static final QueryType CONVERSION_HIST_QUERY = QueryTypes.of("ConversionHistQuery", ConversionQuery.class, "term: CurrencyUnit", "timestamp: long/TemporalAccessor");

    /**
     * Default QueryType for accessing an {@link javax.money.convert.ExchangeRate}.
     */
    public static final QueryType ROUNDING_CURRENCY_QUERY = QueryTypes.of("RoundingCurrencyQuery", ConversionQuery.class, "currencyUnit: CurrencyUnit", "OPT java.math.RoundingMode: RoundingMode");

    /**
     * Default QueryType for accessing an {@link javax.money.convert.ExchangeRate}.
     */
    public static final QueryType ROUNDING_MATH_QUERY = QueryTypes.of("RoundingMathQuery", ConversionQuery.class, "OPT java.math.MathContext: MathContext", "OPT java.math.RoundingMode: RoundingMode", "scale: int");

    /**
     * Default QueryType for accessing an {@link javax.money.convert.ExchangeRate}.
     */
    public static final QueryType ROUNDING_NAMED_QUERY = QueryTypes.of("RoundingCurrencyQuery", ConversionQuery.class, "roundingName: String");

    private QueryTypes() {
    }

    public static QueryType of(String name, Class<? extends AbstractQuery> queryType, String... params) {
        return new SimpleQueryType(name, queryType, params);
    }

    public static Set<QueryType> from(QueryType... queryTypes) {
        Set<QueryType> result = new HashSet<>();
        result.addAll(Arrays.asList(queryTypes));
        return Collections.unmodifiableSet(result);
    }

    private static final class SimpleQueryType implements QueryType {
        private String desc;

        SimpleQueryType(String name, Class<? extends AbstractQuery> queryType, String... params) {
            StringBuilder b = new StringBuilder("QueryType(").append(name).append(") -> ")
                    .append(queryType.getName()).append("[\n");
            for (String p : params) {
                b.append("   ").append(p).append('\n');
            }
            b.append(']');
            this.desc = b.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SimpleQueryType that = (SimpleQueryType) o;

            if (!desc.equals(that.desc)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return desc.hashCode();
        }

        @Override
        public String toString() {
            return desc;
        }
    }

}
