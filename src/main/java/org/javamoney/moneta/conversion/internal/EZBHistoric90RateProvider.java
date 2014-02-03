/*
 * Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
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
 * 
 * Contributors: Anatole Tresch - initial implementation.
 */
package org.javamoney.moneta.conversion.internal;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.CurrencyUnit;
import javax.money.MonetaryCurrencies;
import javax.money.convert.ConversionContext;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;
import javax.money.convert.RateType;
import javax.money.spi.Bootstrap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.javamoney.moneta.conversion.LazyBoundCurrencyConversion;
import org.javamoney.moneta.spi.LoaderService;
import org.javamoney.moneta.spi.LoaderService.LoaderListener;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class implements an {@link ExchangeRateProviderSpi} that loads data from
 * the European Central Bank data feed (XML). It loads the current exchange
 * rates, as well as historic rates for the past 90 days. By calling
 * {@link #loadHistoric()} the provider loads all data up to 1999 into its
 * historic data cache.
 * 
 * @author Anatole Tresch
 * @author Werner Keil
 */
public class EZBHistoric90RateProvider implements ExchangeRateProvider,
		LoaderListener {
	/** The data id used for the LoaderService. */
	private static final String DATA_ID = EZBHistoric90RateProvider.class.getSimpleName();

	private static final String BASE_CURRENCY_CODE = "EUR";
	/** Base currency of the loaded rates is always EUR. */
	public static final CurrencyUnit BASE_CURRENCY = MonetaryCurrencies
			.getCurrency(BASE_CURRENCY_CODE);
	/** The logger used. */
	private static final Logger LOGGER = Logger
			.getLogger(EZBHistoric90RateProvider.class.getName());

	/** Historic exchange rates, rate timestamp as UTC long. */
	private Map<Long, Map<String, ExchangeRate>> rates = new ConcurrentHashMap<Long, Map<String, ExchangeRate>>();
	/** Parser factory. */
	private SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	/** The {@link ConversionContext} of this provider. */
	private static final ProviderContext CONTEXT = new ProviderContext.Builder(
			"EZB-HIST90").setRateTypes(RateType.HISTORIC, RateType.DEFERRED)
			.set("European Central Bank (last 90 days)", "providerDescription")
			.set(90, "days").create();

	/**
	 * Constructor, also loads initial data.
	 * 
	 * @throws MalformedURLException
	 */
	public EZBHistoric90RateProvider() throws MalformedURLException {
		saxParserFactory.setNamespaceAware(false);
		saxParserFactory.setValidating(false);
		LoaderService loader = Bootstrap.getService(LoaderService.class);
		loader.addLoaderListener(this, DATA_ID);
		loader.loadDataAsynch(DATA_ID);
	}


	/**
	 * (Re)load the given data feed.
	 * 
	 * @throws IOException
	 * 
	 * @throws SAXException
	 * 
	 * @throws ParserConfigurationException
	 */
	@Override
	public void newDataLoaded(String data, InputStream is) {
		final int oldSize = this.rates.size();
		try {
			SAXParser parser = saxParserFactory.newSAXParser();
			parser.parse(is, new RateReadingHandler());
		} catch (Exception e) {
			LOGGER.log(Level.FINEST, "Error during data load.", e);
		}
		int newSize = this.rates.size();
		LOGGER.info("Loaded " + DATA_ID + " exchange rates for days:"
				+ (newSize - oldSize));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.money.convert.spi.ExchangeRateProviderSpi#getExchangeRateType
	 * ()
	 */
	@Override
	public ProviderContext getProviderContext() {
		return CONTEXT;
	}

	protected ExchangeRate getExchangeRateInternal(CurrencyUnit base,
			CurrencyUnit term, Long timestamp) {
		ExchangeRate sourceRate = null;
		ExchangeRate target = null;
		ExchangeRate.Builder builder = new ExchangeRate.Builder(
				ConversionContext.of(CONTEXT.getProviderName(),
						RateType.HISTORIC, timestamp));
		if (timestamp == null) {
			return null;
		} else {
			if (rates.isEmpty()) {
				return null;
			}
			final Calendar cal = new GregorianCalendar(
					TimeZone.getTimeZone("UTC"));
			if (timestamp != null) {
				cal.setTimeInMillis(timestamp);
			}
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Long targetTS = Long.valueOf(cal.getTimeInMillis());

			builder.setBase(base);
			builder.setTerm(term);
			Map<String, ExchangeRate> targets = this.rates.get(targetTS);
			if (targets == null) {
				return null;
			}
			sourceRate = targets.get(base.getCurrencyCode());
			target = targets.get(term.getCurrencyCode());
		}
		if (BASE_CURRENCY_CODE.equals(base.getCurrencyCode())
				&& BASE_CURRENCY_CODE.equals(term.getCurrencyCode())) {
			builder.setFactor(BigDecimal.ONE);
			return builder.create();
		} else if (BASE_CURRENCY_CODE.equals(term.getCurrencyCode())) {
			if (sourceRate == null) {
				return null;
			}
			return reverse(sourceRate);
		} else if (BASE_CURRENCY_CODE.equals(base.getCurrencyCode())) {
			return target;
		} else {
			// Get Conversion base as derived rate: base -> EUR -> term
			ExchangeRate rate1 = getExchangeRateInternal(base,
					MonetaryCurrencies.getCurrency(BASE_CURRENCY_CODE),
					timestamp);
			ExchangeRate rate2 = getExchangeRateInternal(
					MonetaryCurrencies.getCurrency(BASE_CURRENCY_CODE), term,
					timestamp);
			if (rate1 != null || rate2 != null) {
				builder.setFactor(rate1.getFactor().multiply(rate2.getFactor()));
				builder.setRateChain(rate1, rate2);
				return builder.create();
			}
			return null;
		}
	}

	private static ExchangeRate reverse(ExchangeRate rate) {
		if (rate == null) {
			throw new IllegalArgumentException("Rate null is not reversable.");
		}
		return rate
				.toBuilder()
				.setBase(rate.getTerm())
				.setTerm(rate.getBase())
				.setFactor(
						BigDecimal.ONE.divide(rate.getFactor(),
								MathContext.DECIMAL64)).create();
	}

	/**
	 * SAX Event Handler that reads the quotes.
	 * <p>
	 * Format: <gesmes:Envelope
	 * xmlns:gesmes="http://www.gesmes.org/xml/2002-08-01"
	 * xmlns="http://www.ecb.int/vocabulary/2002-08-01/eurofxref">
	 * <gesmes:subject>Reference rates</gesmes:subject> <gesmes:Sender>
	 * <gesmes:name>European Central Bank</gesmes:name> </gesmes:Sender> <Cube>
	 * <Cube time="2013-02-21">...</Cube> <Cube time="2013-02-20">...</Cube>
	 * <Cube time="2013-02-19"> <Cube currency="USD" rate="1.3349"/> <Cube
	 * currency="JPY" rate="124.81"/> <Cube currency="BGN" rate="1.9558"/> <Cube
	 * currency="CZK" rate="25.434"/> <Cube currency="DKK" rate="7.4599"/> <Cube
	 * currency="GBP" rate="0.8631"/> <Cube currency="HUF" rate="290.79"/> <Cube
	 * currency="LTL" rate="3.4528"/> ...
	 * 
	 * @author Anatole Tresch
	 */
	private class RateReadingHandler extends DefaultHandler {

		/** Date parser. */
		private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		/** Current timestamp for the given section. */
		private Long timestamp;

		/** Flag, if current or historic data is loaded. */
		// private boolean loadCurrent;

		/**
		 * Creates a new parser.
		 * 
		 * @param loadCurrent
		 *            Flag, if current or historic data is loaded.
		 */
		public RateReadingHandler() {
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			try {
				if ("Cube".equals(qName)) {
					if (attributes.getValue("time") != null) {
						Date date = dateFormat.parse(attributes
								.getValue("time"));
						timestamp = Long.valueOf(date.getTime());
					} else if (attributes.getValue("currency") != null) {
						// read data <Cube currency="USD" rate="1.3349"/>
						CurrencyUnit tgtCurrency = MonetaryCurrencies
								.getCurrency(attributes.getValue("currency"));
						addRate(tgtCurrency, timestamp,
								BigDecimal.valueOf(Double
										.parseDouble(attributes
												.getValue("rate"))));
					}
				}
				super.startElement(uri, localName, qName, attributes);
			} catch (ParseException e) {
				throw new SAXException("Failed to read.", e);
			}
		}

	}

	/**
	 * Method to add a currency exchange rate.
	 * 
	 * @param term
	 *            the term (target) currency, mapped from EUR.
	 * @param timestamp
	 *            The target day.
	 * @param rate
	 *            The rate.
	 * @param loadCurrent
	 *            Flag, if current or historic data is loaded.
	 */
	void addRate(CurrencyUnit term, Long timestamp, BigDecimal rate) {
		ExchangeRate.Builder builder = null;
		RateType rateType = RateType.HISTORIC;
		if (timestamp != null) {
			if (timestamp > System.currentTimeMillis()) {
				rateType = RateType.DEFERRED;
			}
			builder = new ExchangeRate.Builder(ConversionContext.of(
					CONTEXT.getProviderName(), rateType, timestamp));
		} else {
			builder = new ExchangeRate.Builder(ConversionContext.of(
					CONTEXT.getProviderName(), rateType));
		}
		builder.setBase(BASE_CURRENCY);
		builder.setTerm(term);
		builder.setFactor(rate);
		ExchangeRate exchangeRate = builder.create();
		Map<String, ExchangeRate> rateMap = this.rates.get(timestamp);
		if (rateMap == null) {
			synchronized (this.rates) {
				rateMap = this.rates.get(timestamp);
				if (rateMap == null) {
					rateMap = new ConcurrentHashMap<String, ExchangeRate>();
					this.rates.put(timestamp, rateMap);
				}
			}
		}
		rateMap.put(term.getCurrencyCode(), exchangeRate);
	}

	@Override
	public boolean isAvailable(CurrencyUnit src, CurrencyUnit target) {
		return getExchangeRate(src, target) != null;
	}

	@Override
	public ExchangeRate getExchangeRate(CurrencyUnit source, CurrencyUnit target) {
		return getExchangeRateInternal(source, target, null);
	}

	@Override
	public ExchangeRate getReversed(ExchangeRate rate) {
		return getExchangeRateInternal(rate.getTerm(), rate.getBase(), rate
				.getConversionContext().getTimestamp());
	}

	@Override
	public CurrencyConversion getCurrencyConversion(CurrencyUnit termCurrency) {
		return new LazyBoundCurrencyConversion(termCurrency, this,
				ConversionContext.of());
	}

	@Override
	public boolean isAvailable(CurrencyUnit base, CurrencyUnit term,
			ConversionContext conversionContext) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ExchangeRate getExchangeRate(CurrencyUnit base, CurrencyUnit term,
			ConversionContext conversionContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CurrencyConversion getCurrencyConversion(CurrencyUnit term,
			ConversionContext conversionContext) {
		// TODO Auto-generated method stub
		return null;
	}
}