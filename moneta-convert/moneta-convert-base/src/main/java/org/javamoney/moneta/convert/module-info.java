import org.javamoney.moneta.convert.internal.DefaultMonetaryConversionsSingletonSpi;
import org.javamoney.moneta.convert.internal.IdentityRateProvider;

/*
Copyright (c) 2012, 2018, Anatole Tresch, Werner Keil and others by the @author tag.

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
module org.javamoney.moneta.convert {
    exports org.javamoney.moneta.convert;
    requires transitive org.javamoney.moneta;
    requires static org.osgi.core;
    requires static org.osgi.compendium;
    requires static org.osgi.annotation;
    provides javax.money.spi.MonetaryConversionsSingletonSpi with org.javamoney.moneta.convert.internal.DefaultMonetaryConversionsSingletonSpi;
    provides javax.money.convert.ExchangeRateProvider with org.javamoney.moneta.convert.internal.IdentityRateProvider;
    uses org.javamoney.moneta.spi.MonetaryAmountProducer;
    uses javax.money.convert.ExchangeRateProvider;
}