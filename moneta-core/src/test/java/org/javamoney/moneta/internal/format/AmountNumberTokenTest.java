package org.javamoney.moneta.internal.format;

import org.testng.annotations.Test;

import javax.money.format.*;
import java.util.Locale;

import static java.util.Locale.*;
import static org.javamoney.moneta.format.CurrencyStyle.CODE;
import static org.testng.Assert.*;

public class AmountNumberTokenTest {
    private static final String DEFAULT_STYLE = "default";
    private static final String PARTIAL_NUMBER_PATTERN = "#,##0.00 ";

    @Test
    public void testToString_US() {
        AmountNumberToken token = new AmountNumberToken(contextForLocale(US), PARTIAL_NUMBER_PATTERN);
        assertEquals(token.toString(), "AmountNumberToken [locale=en_US, partialNumberPattern=#,##0.00 ]");
    }

    private AmountFormatContext contextForLocale(Locale locale) {
        AmountFormatQuery amountFormatQuery = AmountFormatQueryBuilder.of(locale).set(CODE).build();
        AmountFormatContextBuilder builder = AmountFormatContextBuilder.of(DEFAULT_STYLE);
        builder.setLocale(locale);
        builder.importContext(amountFormatQuery, false);
        builder.setMonetaryAmountFactory(amountFormatQuery.getMonetaryAmountFactory());
        AmountFormatContext amountFormatContext = builder.build();
        return amountFormatContext;
    }
}