
module org.javamoney.moneta.test_extension {
    requires java.money;
    requires org.javamoney.moneta;
    provides javax.money.spi.RoundingProviderSpi with org.javamoney.moneta.test.TestRoundingProvider;
}
