package org.javamoney.moneta.convert.ecb;

import org.javamoney.moneta.convert.ecb.model.Exchange2;

import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for ECB XML Feed
 * */
public class ExchangeRateParser {

    private static final Logger LOGGER = Logger.getLogger(ExchangeRateParser.class.getName());

    /**
     * Use STaX events to parse the XML feed
     * @param xml InputStream of XML test
     * @param currency String of currency to find in the XML
     * @return List of Exchange2 items
     */
    public List<Exchange2> parseRates(InputStream xml, String currency) {
        List<Exchange2> rates = new ArrayList<>();

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader r = factory.createXMLStreamReader(xml);
            try {
                int event = r.getEventType();
                String date = null;
                boolean matchCurrency = false;
                boolean continueParse = true;
                while (continueParse) {
                    if (event == XMLStreamConstants.START_ELEMENT) {
                        // Both the date and rates use the Cube element
                        if (r.getLocalName().equals("Cube")) {
                            for(int i = 0, n = r.getAttributeCount(); i < n; ++i) {
                                // First mark the date
                                if (r.getAttributeLocalName(i).equals("time")) {
                                    date = r.getAttributeValue(i);
                                }

                                // Now get the currency
                                if ((r.getAttributeLocalName(i).equals("currency")) && r.getAttributeValue(i).equals(currency)) {
                                    matchCurrency = true;
                                }

                                // Finally, get the rate and add to the list
                                if (r.getAttributeLocalName(i).equals("rate")) {
                                    if (matchCurrency) {
                                        Exchange2 rate = new Exchange2(date, currency, Double.parseDouble(r.getAttributeValue(i)));
                                        rates.add(rate);
                                        matchCurrency = false;
                                    }

                                }
                            }
                        }
                    }

                    if (!r.hasNext()) {
                        continueParse = false;
                    } else {
                        event = r.next();
                    }
                }
            } finally {
                r.close();
            }
        } catch (Exception e) {
            LOGGER.severe("Error parsing XML: " + e.getMessage());
        }

        return rates;
    }
}
