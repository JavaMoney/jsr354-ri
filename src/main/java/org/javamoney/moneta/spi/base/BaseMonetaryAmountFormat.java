/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.moneta.spi.base;

import javax.money.MonetaryAmount;
import javax.money.format.MonetaryAmountFormat;
import java.io.IOException;

/**
 * <p>
 * Formats instances of {@code MonetaryAmount} to a {@link String} or an {@link Appendable}.
 * </p>
 * <p>
 * To obtain a <code>MonetaryAmountFormat</code> for a specific locale, including the default
 * locale, call {@link javax.money.format.MonetaryFormats#getAmountFormat(java.util.Locale, String...)}.
 *
 * More complex formatting scenarios can be implemented by registering instances of {@link javax.money.spi
 * .MonetaryAmountFormatProviderSpi}.
 * The spi implementation creates new instances of {@link BaseMonetaryAmountFormat} based on the
 * <i>styleId</i> and <i> (arbitrary) attributes</i> passed within the {@link javax.money.format.AmountFormatContext}.
 * </p>
 * <p>In general, do prefer
 * accessing <code>MonetaryAmountFormat</code> instances from the {@link javax.money.format.MonetaryFormats} singleton,
 * instead of instantiating implementations directly, since the <code>MonetaryFormats</code> factory
 * method may return different subclasses or may implement contextual behaviour (in a EE context).
 * If you need to customize the format object, do something like this:
 * <p>
 * <blockquote>
 * <p>
 * <pre>
 * MonetaryAmountFormat f = MonetaryFormats.getInstance(loc);
 * f.setStyle(f.getStyle().toBuilder().setPattern(&quot;###.##;(###.##)&quot;).build());
 * </pre>
 * <p>
 * </blockquote>
 * <p>
 * <h4>Special Values</h4>
 * <p>
 * <p>
 * Negative zero (<code>"-0"</code>) should always parse to
 * <ul>
 * <li><code>0</code></li>
 * </ul>
 * <p>
 * <h4><a name="synchronization">Synchronization</a></h4>
 * <p>
 * <p>
 * Instances of this class are not required to be thread-safe. It is recommended to of separate
 * format instances for each thread. If multiple threads access a format concurrently, it must be
 * synchronized externally.
 * <p>
 * <h4>Example</h4>
 * <p>
 * <blockquote>
 * <p>
 * <pre>
 * <strong>// Print out a number using the localized number, currency,
 * // for each locale</strong>
 * Locale[] locales = MonetaryFormats.getAvailableLocales();
 * MonetaryAmount amount = ...;
 * MonetaryAmountFormat form;
 *     System.out.println("FORMAT");
 *     for (int i = 0; i < locales.length; ++i) {
 *         if (locales[i].getCountry().length() == 0) {
 *            continue; // Skip language-only locales
 *         }
 *         System.out.print(locales[i].getDisplayName());
 *         form = MonetaryFormats.getInstance(locales[i]);
 *         System.out.print(": " + form.getStyle().getPattern());
 *         String myAmount = form.format(amount);
 *         System.out.print(" -> " + myAmount);
 *         try {
 *             System.out.println(" -> " + form.parse(form.format(myAmount)));
 *         } catch (ParseException e) {}
 *     }
 * }
 * </pre>
 * <p>
 * </blockquote>
 * @deprecated This functionality is moved into Java 8 default methods.
 */
@Deprecated
public abstract class BaseMonetaryAmountFormat implements MonetaryAmountFormat {

    /**
     * Formats the given {@link javax.money.MonetaryAmount} to a String.
     *
     * @param amount the amount to format, not {@code null}
     * @return the string printed using the settings of this formatter
     * @throws UnsupportedOperationException if the formatter is unable to print
     */
    public String format(MonetaryAmount amount){
        StringBuilder b = new StringBuilder();
        try{
            print(b, amount);
        }
        catch(IOException e){
            throw new IllegalStateException("Formatting error.", e);
        }
        return b.toString();
    }

}
