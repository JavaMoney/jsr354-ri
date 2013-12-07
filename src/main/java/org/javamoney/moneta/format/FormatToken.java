package org.javamoney.moneta.format;

import java.io.IOException;
import java.text.ParseException;

import javax.money.MonetaryAmount;
import javax.money.format.ParseContext;

public interface FormatToken {

	public void parse(ParseContext context) throws ParseException;

	public void print(Appendable appendable, MonetaryAmount amount) throws IOException;

}
