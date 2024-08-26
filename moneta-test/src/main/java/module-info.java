
module org.javamoney.moneta.test {
    requires java.money;
    requires org.javamoney.moneta;
    provides javax.money.spi.RoundingProviderSpi with org.javamoney.moneta.test.TestRoundingProvider;
}
