package org.javamoney.moneta.extras.yhoo;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.convert.ConversionContextBuilder;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContext;
import javax.money.convert.RateType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.javamoney.moneta.CurrencyUnitBuilder;
import org.javamoney.moneta.ExchangeRateBuilder;
import org.javamoney.moneta.extras.yhoo.quote.YhooCurrencies;
import org.javamoney.moneta.extras.yhoo.quote.YhooField;
import org.javamoney.moneta.extras.yhoo.quote.YhooQuoteItem;
import org.javamoney.moneta.extras.yhoo.quote.YhooRoot;
import org.javamoney.moneta.spi.DefaultNumberValue;

/**
 * Event Handler which parse rates.
 * <p>
 * 
 * @author skosoy@gmail.com
 */
class YhooRateReadingHandler {
    private final String YHOO_CURRENCY = "price";
    private final String YHOO_CURRENCY_PAIR = "name";
    private final String YHOO_CURRENCY_UPDATE_TIME = "utctime";
    private final String YHOO_DTO_PACKAGE = "org.javamoney.moneta.extras.yhoo.quote";
    private final String UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private final String CST_ZONE = "America/Chicago";
    private final String USD_CURRENCY = "USD";

    //NOTE: IT MIGHT BE BETTER TO KEEP A TIMESTAMT AS A LONG KEY VALUE HERE
	private final Map<LocalDate, Map<String, ExchangeRate>> excangeRates;
	private final ProviderContext context;

	public YhooRateReadingHandler(final Map<LocalDate, Map<String, ExchangeRate>> excangeRates,
			final ProviderContext context) {
		this.excangeRates = excangeRates;
		this.context = context;
	}

	void parse(final InputStream stream) throws JAXBException, ParseException {
		final Unmarshaller u = JAXBContext.newInstance(YHOO_DTO_PACKAGE).createUnmarshaller();
		final YhooRoot root = (YhooRoot) u.unmarshal(stream);
		final YhooCurrencies currencies = root.getResources();

		final DateFormat utcFormatter = new SimpleDateFormat(UTC_DATE_FORMAT);
		utcFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	    
		for (YhooQuoteItem quote : currencies.getResource()) {
			int position = quote.getField().indexOf(new YhooField(YHOO_CURRENCY_PAIR));
			String currencyName = null;
			CurrencyUnit tgtCurrency = null;
			currencyName = quote.getField().get(position).getValue();

			if(currencyName.equals(USD_CURRENCY))//in order to not add BASE currency itself
				continue;

			//NOTE Yahoo responds with <SILVER 1 OZ 999 NY>, <ADIUM 1 OZ> and other similar items which are not currencies
			//NOTE Yahoo responds with <ECS> - ECUADORIAN_SUCRE which does not exist anymore
			//NOTE Yahoo responds with <CNH> - Chinese Yuan when trading offshore
			if(currencyName.length()>7){
				tgtCurrency = CurrencyUnitBuilder.of(currencyName, YHOORateProvider.PROVIDER).build();
			} else {
				//one currency with ending SPACE symbol was explored
				tgtCurrency = CurrencyUnitBuilder.of(currencyName.substring(4).trim(), YHOORateProvider.PROVIDER).build();
			}

			position = quote.getField().indexOf(new YhooField(YHOO_CURRENCY_UPDATE_TIME));
			final LocalDate currencyLastUpdateTime = LocalDateTime.ofInstant(
					utcFormatter.parse(quote.getField().get(position).getValue()).toInstant(), 
					ZoneId.of(CST_ZONE)).toLocalDate();//Central American Time
			
			position = quote.getField().indexOf(new YhooField(YHOO_CURRENCY));
			final YhooField field = quote.getField().get(position);
			addRate(tgtCurrency, currencyLastUpdateTime, 
					BigDecimal.valueOf(Double.parseDouble(field.getValue())));
		}
	}

    /**
     * Method to add a currency exchange rate.
     *
     * @param term      the term (target) currency, mapped from USD.
     * @param rate      The rate.
     */
    private void addRate(final CurrencyUnit term, final LocalDate localDate, final Number rate) {//TODO it might be generalised with other Provides
        final ExchangeRateBuilder builder = new ExchangeRateBuilder(
        		ConversionContextBuilder.create(context, RateType.DEFERRED).build());
        builder.setBase(YhooAbstractRateProvider.BASE_CURRENCY);
        builder.setTerm(term);
        builder.setFactor(DefaultNumberValue.of(rate));
        final ExchangeRate exchangeRate = builder.build();
        
        Map<String, ExchangeRate> rateMap = this.excangeRates.get(localDate);
        if (Objects.isNull(rateMap)) {
            synchronized (this.excangeRates) {
                rateMap = Optional.ofNullable(this.excangeRates.get(localDate)).orElse(new ConcurrentHashMap<>());
                this.excangeRates.putIfAbsent(localDate, rateMap);
            }
        }
        rateMap.put(term.getCurrencyCode(), exchangeRate);
    	
    }
	
}