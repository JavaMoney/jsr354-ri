import org.javamoney.moneta.spi.loader.LoaderService;

/*
Copyright (c) 2012, 2025, Werner Keil and others by the @author tag.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
*/
module org.javamoney.moneta.convert.imf {
    exports org.javamoney.moneta.convert.imf;
    exports org.javamoney.moneta.convert.imf.defaults;

    opens org.javamoney.moneta.convert.imf;
    opens org.javamoney.moneta.convert.imf.defaults;
	requires org.javamoney.moneta.convert;
    requires static osgi.core;
    requires static osgi.annotation;
    requires okhttp3;
    provides javax.money.convert.ExchangeRateProvider with
            org.javamoney.moneta.convert.imf.IMFRateProvider, org.javamoney.moneta.convert.imf.IMFHistoricRateProvider;
    uses LoaderService;
    uses org.javamoney.moneta.spi.MonetaryAmountProducer;
}