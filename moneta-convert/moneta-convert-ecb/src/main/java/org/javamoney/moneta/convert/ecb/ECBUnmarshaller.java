package org.javamoney.moneta.convert.ecb;

import org.javamoney.moneta.convert.ecb.model.Exchange;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by woorea on 03/04/2017.
 */
public class ECBUnmarshaller implements Function<String, List<Exchange>> {

  @Override
  public List<Exchange> apply(final String xml) {

    //InputSource inputSource = new InputSource (new StringReader(xml));
    //InputSource inputSource = new InputSource (xmlStream);
    // different encoding
    //inputSource.setEncoding(StandardCharsets.UTF_8.displayName());
    //inputSource.setCharacterStream();


    List<Exchange> exchanges = new ArrayList<>();

    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      //Document document = db.parse(inputSource);
      Document document = db.parse(new URL(xml).openStream());

      document.getDocumentElement().normalize();

      Node rootCube = document.getElementsByTagName("Cube").item(0);

      NodeList dates = rootCube.getChildNodes();

      for(int i = 0; i < dates.getLength(); i++) {
        Node dailyNode = dates.item(i);
        if(dailyNode.getNodeType() == Node.ELEMENT_NODE) {
          NamedNodeMap dailyNodeAttributes = dailyNode.getAttributes();
          String timeAttributeValue = dailyNodeAttributes.getNamedItem("time").getNodeValue();
          LocalDate localDate = LocalDate.parse(timeAttributeValue, DateTimeFormatter.ISO_LOCAL_DATE);
          NodeList exchangeNodeList = dailyNode.getChildNodes();
          for (int j = 0; j < exchangeNodeList.getLength(); j++) {
            Node exchangeNode = exchangeNodeList.item(j);
            if (exchangeNode.getNodeType() == Node.ELEMENT_NODE) {
              NamedNodeMap exchangeNodeAttributes = exchangeNode.getAttributes();
              String currency = exchangeNodeAttributes.getNamedItem("currency").getNodeValue();
              String value = exchangeNodeAttributes.getNamedItem("rate").getNodeValue();
              Exchange exchange = new Exchange(localDate, currency, new BigDecimal(value));
              exchanges.add(exchange);
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return exchanges;

  }

}
