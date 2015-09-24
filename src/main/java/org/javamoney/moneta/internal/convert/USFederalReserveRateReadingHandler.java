package org.javamoney.moneta.internal.convert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryException;
import javax.money.convert.ConversionContextBuilder;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ProviderContext;
import javax.money.convert.RateType;

import org.javamoney.moneta.convert.ExchangeRateBuilder;
import org.javamoney.moneta.spi.DefaultNumberValue;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX Event Handler that reads the quotes.
 * <p>
 * RDF format: <item rdf:about="http://www.federalreserve.gov/releases/H10#16"> <title>US: H10 0.7100 2015-08-31 FRB
 * Australia Dollar (USD per AUD)</title> <link>http://www.federalreserve.gov/releases/H10#16</link>
 * <description>Australia Dollar (USD per AUD)</description> <dc:date>2015-08-31T12:00:00-05:00</dc:date>
 * <dc:language>en</dc:language> <dc:creator>FRB</dc:creator> <cb:statistics> <cb:country>US</cb:country>
 * <cb:institutionAbbrev>FRB</cb:institutionAbbrev> <cb:otherStatistic> <cb:value decimals="4" unit_mult="1"
 * units="Currency:_Per_AUD">0.7100</cb:value> <cb:topic>H10</cb:topic> <cb:coverage>Australia Dollar (USD per
 * AUD)</cb:coverage> <cb:observationPeriod frequency="business">2015-08-31</cb:observationPeriod> <cb:dataType/>
 * </cb:otherStatistic> </cb:statistics> </item>
 */
class USFederalReserveRateReadingHandler extends DefaultHandler {
    private LocalDate localDate;
    private String currencyCode;
    private String description;

    private boolean dcDateNode = false;
    private boolean cbValueNode = false;
    private boolean descriptionNode = false;

    private final Map<LocalDate, Map<String, ExchangeRate>> historicRates;

    private final ProviderContext context;

    private static final Pattern unitsPattern = Pattern.compile("^Currency:_Per_(\\w{3})$");

    private static final Map<String, CurrencyUnit> CURRENCIES_BY_NAME;

    static {
        Map<String, CurrencyUnit> currenciesByName = new HashMap<>();
        for (Currency currency : Currency.getAvailableCurrencies()) {
            currenciesByName.put(currency.getDisplayName(Locale.ENGLISH),
                Monetary.getCurrency(currency.getCurrencyCode()));
        }
        currenciesByName.put("Brazil Real", Monetary.getCurrency("BRL"));
        currenciesByName.put("Canada Dollar", Monetary.getCurrency("CAD"));
        currenciesByName.put("China, P.R. Yuan", Monetary.getCurrency("CNY"));
        currenciesByName.put("Denmark Krone", Monetary.getCurrency("DKK"));
        currenciesByName.put("EMU member countries Euro", Monetary.getCurrency("EUR"));        
        currenciesByName.put("India Rupee", Monetary.getCurrency("INR"));
        currenciesByName.put("Japan Yen", Monetary.getCurrency("JPY"));
        currenciesByName.put("Malaysia Ringgit", Monetary.getCurrency("MYR"));
        currenciesByName.put("Mexico Peso", Monetary.getCurrency("MXN"));
        currenciesByName.put("Norway Krone", Monetary.getCurrency("NOK"));
        currenciesByName.put("South Africa Rand", Monetary.getCurrency("ZAR"));
        currenciesByName.put("South Korea Won", Monetary.getCurrency("KRW"));
        currenciesByName.put("Sri Lanka Rupee", Monetary.getCurrency("LKR"));
        currenciesByName.put("Sweden Krona", Monetary.getCurrency("SEK"));
        currenciesByName.put("Switzerland Franc", Monetary.getCurrency("CHF"));
        currenciesByName.put("Thailand Baht", Monetary.getCurrency("THB"));
        currenciesByName.put("Taiwan Dollar", Monetary.getCurrency("TWD"));           
        currenciesByName.put("United Kingdom Pound", Monetary.getCurrency("GBP"));        
        currenciesByName.put("Venezuela Bolivar", Monetary.getCurrency("VEF"));
        CURRENCIES_BY_NAME = Collections.unmodifiableMap(currenciesByName);
    }

    /**
     * Creates a new handler.
     * 
     * @param historicRates
     *            the rates, not null.
     * @param context
     *            the context, not null.
     */
    USFederalReserveRateReadingHandler(Map<LocalDate, Map<String, ExchangeRate>> historicRates, ProviderContext context) {
        this.historicRates = historicRates;
        this.context = context;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("description".equals(qName)) {
            descriptionNode = true;
        } else if ("dc:date".equals(qName)) {
            dcDateNode = true;
        } else if ("cb:value".equals(qName)) {
            cbValueNode = true;
            String units = attributes.getValue("units");
            if (attributes.getValue("units") != null) {
                Matcher m = unitsPattern.matcher(units);
                if (m.find()) {
                    try {
                        this.currencyCode = m.group(1);
                    } catch (MonetaryException me) {
                        // ignore...currency index not an actual currency
                    }
                }
            }
        }
        super.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        dcDateNode = false;
        cbValueNode = false;
        descriptionNode = false;
        super.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        if (this.descriptionNode) {
            this.description = new String(ch, start, length);
        } else if (this.dcDateNode) {
            this.localDate = OffsetDateTime.parse(new String(ch, start, length)).toLocalDate();
        } else if (this.cbValueNode && this.currencyCode != null) {
            String rateStr = new String(ch, start, length);
            CurrencyUnit currencyUnit = null;
            boolean inverse = false;
            if(USFederalReserveRateProvider.BASE_CURRENCY_CODE.equals(this.currencyCode)) {
                currencyUnit = CURRENCIES_BY_NAME.get(description);                
            } else {
                currencyUnit = Monetary.getCurrency(this.currencyCode);
                inverse = true;
            }
            if(currencyUnit!=null) {
                addRate(currencyUnit, this.localDate, BigDecimal.valueOf(Double.parseDouble(rateStr)), inverse);
            }
            this.currencyCode = null;
            this.localDate = null;
            this.description = null;
        }
        super.characters(ch, start, length);
    }

    private void addRate(CurrencyUnit term, LocalDate localDate, Number rate, boolean inverse) {
        RateType rateType = RateType.HISTORIC;
        ExchangeRateBuilder builder =
            new ExchangeRateBuilder(ConversionContextBuilder.create(context, rateType).set(localDate).build());
        builder.setBase(inverse ? term : USFederalReserveRateProvider.BASE_CURRENCY);
        builder.setTerm(inverse ? USFederalReserveRateProvider.BASE_CURRENCY : term);
        builder.setFactor(DefaultNumberValue.of(rate));
        ExchangeRate exchangeRate = builder.build();
        if(inverse) {
            exchangeRate = USFederalReserveRateProvider.reverse(exchangeRate);
        }
        Map<String, ExchangeRate> rateMap = this.historicRates.get(localDate);
        if (Objects.isNull(rateMap)) {
            synchronized (this.historicRates) {
                rateMap = Optional.ofNullable(this.historicRates.get(localDate)).orElse(new ConcurrentHashMap<>());
                this.historicRates.putIfAbsent(localDate, rateMap);
            }
        }
        rateMap.put(term.getCurrencyCode(), exchangeRate);
    }

}
