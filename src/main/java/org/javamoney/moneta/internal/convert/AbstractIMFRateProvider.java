package org.javamoney.moneta.internal.convert;

import java.io.InputStream;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.CurrencyContextBuilder;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContext;

import org.javamoney.moneta.CurrencyUnitBuilder;
import org.javamoney.moneta.internal.convert.RateIMFReadingHandler.RateIMFResult;
import org.javamoney.moneta.spi.AbstractRateProvider;
import org.javamoney.moneta.spi.LoaderService.LoaderListener;

abstract class AbstractIMFRateProvider extends AbstractRateProvider implements LoaderListener {


    private static final Logger LOG = Logger.getLogger(AbstractIMFRateProvider.class.getName());

	protected static final Map<String, CurrencyUnit> CURRENCIES_BY_NAME = new HashMap<>();

	protected static final CurrencyUnit SDR =
            CurrencyUnitBuilder.of("SDR", CurrencyContextBuilder.of(IMFRateProvider.class.getSimpleName()).build())
                    .setDefaultFractionDigits(3).build(true);

	protected Map<CurrencyUnit, List<ExchangeRate>> currencyToSdr = Collections.emptyMap();

	protected Map<CurrencyUnit, List<ExchangeRate>> sdrToCurrency = Collections.emptyMap();

	private final ProviderContext context;

	private final RateIMFReadingHandler handler;

	public AbstractIMFRateProvider(ProviderContext providerContext) {
		super(providerContext);
		this.context = providerContext;
		handler = new RateIMFReadingHandler(CURRENCIES_BY_NAME, context);
	}


    static {
        for (Currency currency : Currency.getAvailableCurrencies()) {
            CURRENCIES_BY_NAME.put(currency.getDisplayName(Locale.ENGLISH),
                    Monetary.getCurrency(currency.getCurrencyCode()));
        }
        CURRENCIES_BY_NAME.put("U.K. Pound Sterling", Monetary.getCurrency("GBP"));
        CURRENCIES_BY_NAME.put("U.S. Dollar", Monetary.getCurrency("USD"));
        CURRENCIES_BY_NAME.put("Bahrain Dinar", Monetary.getCurrency("BHD"));
        CURRENCIES_BY_NAME.put("Botswana Pula", Monetary.getCurrency("BWP"));
        CURRENCIES_BY_NAME.put("Czech Koruna", Monetary.getCurrency("CZK"));
        CURRENCIES_BY_NAME.put("Icelandic Krona", Monetary.getCurrency("ISK"));
        CURRENCIES_BY_NAME.put("Korean Won", Monetary.getCurrency("KRW"));
        CURRENCIES_BY_NAME.put("Rial Omani", Monetary.getCurrency("OMR"));
        CURRENCIES_BY_NAME.put("Nuevo Sol", Monetary.getCurrency("PEN"));
        CURRENCIES_BY_NAME.put("Qatar Riyal", Monetary.getCurrency("QAR"));
        CURRENCIES_BY_NAME.put("Saudi Arabian Riyal", Monetary.getCurrency("SAR"));
        CURRENCIES_BY_NAME.put("Sri Lanka Rupee", Monetary.getCurrency("LKR"));
        CURRENCIES_BY_NAME.put("Trinidad And Tobago Dollar", Monetary.getCurrency("TTD"));
        CURRENCIES_BY_NAME.put("U.A.E. Dirham", Monetary.getCurrency("AED"));
        CURRENCIES_BY_NAME.put("Peso Uruguayo", Monetary.getCurrency("UYU"));
        CURRENCIES_BY_NAME.put("Bolivar Fuerte", Monetary.getCurrency("VEF"));
    }


    @Override
    public void newDataLoaded(String resourceId, InputStream is) {
        try {
        	RateIMFResult result = handler.read(is);
        	 this.sdrToCurrency = result.getSdrToCurrency();
             this.currencyToSdr = result.getCurrencyToSdr();
        } catch (Exception e) {
        	LOG.log(Level.SEVERE, "Error", e);
        }
    }

}
