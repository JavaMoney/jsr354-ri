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
import javax.money.MonetaryOperator;
import javax.money.MonetaryQuery;

/**
 * Interface defining a monetary amount. The effective format representation of an amount may vary
 * depending on the implementation used. JSR 354 explicitly supports different types of monetary
 * amounts to be implemented and used. Reason behind is that the requirements to an implementation
 * heavily vary for different usage scenarios. E.g. product calculations may require high precision
 * and scale, whereas low latency order and trading systems require high calculation performance for
 * algorithmic operations.
 * <p>
 * Each instance of an amount provides additional meta-data in form of a {@link javax.money.MonetaryContext}.
 * This context contains detailed information on the numeric capabilities, e.g. the supported
 * precision and maximal scale, as well as the common implementation flavor.
 *
 * Also a {@link BaseMonetaryAmount} provides a {@link javax.money.NumberValue}, which allows easily to extract the
 * numeric value, of the amount. And finally {@link #getFactory()} provides a
 * {@link javax.money.MonetaryAmountFactory}, which allows to of instances of {@link BaseMonetaryAmount} based
 * on the same numeric implementation.
 * <p>
 * This JSR additionally recommends to consider the following aspects:
 * <ul>
 * <li>Arithmetic operations should throw an {@link ArithmeticException}, if performing arithmetic
 * operations between amounts exceeds the capabilities of the numeric representation type used. Any
 * implicit truncating, that would lead to complete invalid and useless results, should be avoided.
 * This recommendation does not affect format rounding, as required by the format numeric
 * representation of a monetary amount.
 * <li>Monetary amounts should allow numbers as argument for arithmetic operations like division and
 * multiplication. Adding or subtracting of amounts must only be possible by passing instances of
 * {@link BaseMonetaryAmount}.</li>
 * <li>Nevertheless numeric truncation is also explicitly supported when calling
 * {@link javax.money.NumberValue#numberValue(Class)}, whereas the <i>exact</i> counterpart,
 * {@link javax.money.NumberValue#numberValueExact(Class)}, works similar to
 * {@link java.math.BigDecimal#longValueExact()}.
 * <li>Since implementations are recommended to be immutable, an operation should never change any
 * format state of an instance. Given an instance, all operations are required to be fully
 * reproducible.</li>
 * <li>Finally the result of calling {@link #with(javax.money.MonetaryOperator)} must be of the same type as
 * type on which {@code with} was called. The {@code with} method also defines additional
 * interoperability requirements that are important to enable this invariant.</li>
 * <li>To enable further interoperability a static method {@code from(MonetaryAmount)} is
 * recommended to be implemented on each implementation class, that allows conversion of a
 * {@code MonetaryAmount} to a concrete instance. E.g.a class {@code MyMoney extends MonetaryAmount}
 * would contain the following method:
 *
 * <blockquote>
 * <p>
 * <pre>
 * public final class MyMoney implements MonetaryAmount{
 *   ...
 *   public static MyMoney from(MonetaryAmount amount)(...)
 * }
 * </pre>
 * <p>
 * </blockquote></li>
 * </ul>
 * <h4>Implementation specification</h4>
 * Implementations of this interface must be
 * <ul>
 * <li>thread-safe</li>
 * </ul>
 * Implementations of this interface should be
 * <ul>
 * <li>final</li>
 * <li>serializable, hereby writing the numeric value, the {@link javax.money.MonetaryContext} and a serialized
 * {@link javax.money.CurrencyUnit}.</li>
 * </ul>
 * Implementations of this interface must be
 * <ul>
 * <li>thread-safe</li>
 * <li>immutable</li>
 * <li>comparable</li>
 * <li>must implement {@code equals/hashCode}, hereby considering
 * <ul>
 * <li>Implementation type
 * <li>CurrencyUnit
 * <li>Numeric value.
 * </ul>
 * This also means that two different implementations types with the same currency and numeric value
 * are NOT equal.</li>
 * </ul>
 * <p>
 *
 * @author Anatole Tresch
 * @author Werner Keil
 * @version 0.8.2
 * @see #with(javax.money.MonetaryOperator)
 * @deprecated This functionality is moved into Java 8 default methods.
 */
@Deprecated
public abstract class BaseMonetaryAmount implements MonetaryAmount {

    /**
     * Queries this monetary amount for a value.
     * <p>
     * This queries this amount using the specified query strategy object.
     * <p>
     * Implementations must ensure that no observable state is altered when this read-only method is
     * invoked.
     *
     * @param <R>   the type of the result
     * @param query the query to invoke, not null
     * @return the query result, null may be returned (defined by the query)
     */
    public <R> R query(MonetaryQuery<R> query){
        return query.queryFrom(this);
    }

    /**
     * Returns an operated object <b>of the same type</b> as this object with the operation made.
     * Hereby returning an instance <b>of the same type</b> is very important to prevent
     * uncontrolled mixup of implementations. Switching between implementations is still easily
     * possible, e.g. by using according {@link javax.money.MonetaryAmountFactory} instances: <blockquote>
     * <p>
     * <pre>
     * // converting from Money to MyMoney
     * Money m = ...;
     * MonetaryAmountFactory<MyMoney> f = Monetary.queryAmountFactory(MyMoney.class);
     * MyMoney myMoney = f.setAmount(m).of();
     * </blockquote>
     * </pre>
     * <p>
     * This converts this monetary amount according to the rules of the specified operator. A
     * typical operator will change the amount and leave the currency unchanged. A more complex
     * operator might also change the currency.
     * <p>
     * Some example code indicating how and why this method is used:
     * <p>
     * <blockquote>
     * <p>
     * <pre>
     * MonetaryAmount money = money.with(amountMultipliedBy(2));
     * money = money.with(amountRoundedToNearestWholeUnit());
     * </pre>
     * <p>
     * </blockquote>
     * <p>
     * Hereby also the method signature on the implementation type must return the concrete type, to
     * enable a fluent API, e.g.
     * <p>
     * <blockquote>
     * <p>
     * <pre>
     * public final class MyMoney implements MonetaryAmount{
     *   ...
     *   public MyMoney with(MonetaryOperator operator){
     *     ...
     *   }
     *
     *   ...
     * }
     * </pre>
     * <p>
     * </blockquote>
     *
     * @param operator the operator to use, not null
     * @return an object of the same type with the specified conversion made, not null
     */
    public MonetaryAmount with(MonetaryOperator operator){
        return operator.apply(this);
    }


    /**
     * Checks if a {@code MonetaryAmount} is negative.
     *
     * @return {@code true} if {@link #signum()} < 0.
     */
    public boolean isNegative(){
        return signum() < 0;
    }

    /**
     * Checks if a {@code MonetaryAmount} is negative or zero.
     *
     * @return {@code true} if {@link #signum()} <= 0.
     */
    public boolean isNegativeOrZero(){
        return signum() <= 0;
    }

    /**
     * Checks if a {@code MonetaryAmount} is positive.
     *
     * @return {@code true} if {@link #signum()} > 0.
     */
    public boolean isPositive(){
        return signum() > 0;
    }

    /**
     * Checks if a {@code MonetaryAmount} is positive or zero.
     *
     * @return {@code true} if {@link #signum()} >= 0.
     */
    public boolean isPositiveOrZero(){
        return signum() >= 0;
    }

    /**
     * Checks if an {@code MonetaryAmount} is zero.
     *
     * @return {@code true} if {@link #signum()} == 0.
     */
    public boolean isZero(){
        return signum() == 0;
    }

}
