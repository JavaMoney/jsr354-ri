package org.javamoney.moneta.spi;

import org.javamoney.moneta.FastMoney;
import org.testng.annotations.Test;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryException;
import java.math.BigDecimal;

import static java.math.BigDecimal.TEN;
import static org.javamoney.moneta.spi.MoneyUtils.*;
import static org.testng.Assert.*;

public class MoneyUtilsTest {

    @Test
    public void testGetBigDecimal_long() {
        assertEquals(getBigDecimal(10), TEN);
    }

    @Test
    public void testGetBigDecimal_double() {
        expectThrows(ArithmeticException.class, () -> getBigDecimal(Double.NaN));
        expectThrows(ArithmeticException.class, () -> getBigDecimal(Double.POSITIVE_INFINITY));
        expectThrows(ArithmeticException.class, () -> getBigDecimal(Double.NEGATIVE_INFINITY));
        assertEquals(getBigDecimal(0.24D), new BigDecimal("0.24"));
        assertEquals(getBigDecimal(0.25F), new BigDecimal("0.25"));
    }

    @Test
    public void testGetBigDecimal_Number() {
        expectThrows(ArithmeticException.class, () -> getBigDecimal(Float.valueOf(Float.NaN)));
        expectThrows(ArithmeticException.class, () -> getBigDecimal(Float.valueOf(Float.POSITIVE_INFINITY)));
        expectThrows(ArithmeticException.class, () -> getBigDecimal(Float.valueOf(Float.NEGATIVE_INFINITY)));
        assertEquals(getBigDecimal(Byte.valueOf((byte) 10)), TEN);
        assertEquals(getBigDecimal(Short.valueOf((short) 10)), TEN);
        assertEquals(getBigDecimal(Float.valueOf(0.24F)), new BigDecimal("0.24"));
    }

    @Test
    public void testCheckAmountParameter() {
        CurrencyUnit dollar = Monetary.getCurrency("USD");
        CurrencyUnit real = Monetary.getCurrency("BRL");
        checkAmountParameter(FastMoney.of(0, "USD"), dollar);
        expectThrows(MonetaryException.class, () -> checkAmountParameter(FastMoney.of(0, "USD"), real));
    }

    @Test
    public void testReplaceNbspWithSpace() {
        assertEquals(replaceNbspWithSpace("14\u202F000,12\u00A0EUR"), "14 000,12 EUR");
    }

}