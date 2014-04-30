package org.javamoney.moneta.internal;

import org.javamoney.moneta.RoundedMoney;
import org.javamoney.moneta.spi.AbstractAmountFactory;

import javax.money.CurrencyUnit;
import javax.money.MonetaryContext;
import javax.money.AmountFlavor;
import java.math.RoundingMode;

/**
 * Implementation of {@link javax.money.MonetaryAmountFactory} creating instances of {@link org.javamoney.moneta
 * .RoundedMoney}.
 *
 * @author Anatole Tresch
 */
public class RoundedMoneyAmountFactory extends AbstractAmountFactory<RoundedMoney>{

    static final MonetaryContext DEFAULT_CONTEXT =
            new MonetaryContext.Builder(RoundedMoney.class).setPrecision(0).setObject(RoundingMode.HALF_EVEN)
                    .setFlavor(AmountFlavor.UNDEFINED).create();
    static final MonetaryContext MAX_CONTEXT =
            new MonetaryContext.Builder(RoundedMoney.class).setPrecision(0).setObject(RoundingMode.HALF_EVEN)
                    .setFlavor(AmountFlavor.UNDEFINED).create();

    /*
     * (non-Javadoc)
     * @see org.javamoney.moneta.spi.AbstractAmountFactory#create(javax.money.CurrencyUnit,
     * java.lang.Number, javax.money.MonetaryContext)
     */
    @Override
    protected RoundedMoney create(Number number, CurrencyUnit currency, MonetaryContext monetaryContext){
        return RoundedMoney.of(number, currency );
    }

    /*
     * (non-Javadoc)
     * @see javax.money.MonetaryAmountFactory#getAmountType()
     */
    @Override
    public Class<RoundedMoney> getAmountType(){
        return RoundedMoney.class;
    }

    /*
     * (non-Javadoc)
     * @see org.javamoney.moneta.spi.AbstractAmountFactory#loadDefaultMonetaryContext()
     */
    @Override
    protected MonetaryContext loadDefaultMonetaryContext(){
        return DEFAULT_CONTEXT;
    }

    /*
     * (non-Javadoc)
     * @see org.javamoney.moneta.spi.AbstractAmountFactory#loadMaxMonetaryContext()
     */
    @Override
    protected MonetaryContext loadMaxMonetaryContext(){
        return MAX_CONTEXT;
    }

}
