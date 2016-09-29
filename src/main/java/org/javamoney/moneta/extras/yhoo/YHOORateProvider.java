package org.javamoney.moneta.extras.yhoo;

import javax.money.convert.ConversionContext;
import javax.money.convert.ProviderContext;
import javax.money.convert.ProviderContextBuilder;
import javax.money.convert.RateType;

public class YHOORateProvider extends YhooAbstractRateProvider {

	public static final String PROVIDER = "YHOO";
	
	/**
	 * The data id used for the LoaderService.
	 */
	private static final String DATA_ID = YHOORateProvider.class
			.getSimpleName();
	/**
	 * The {@link ConversionContext} of this provider.
	 */
	private static final ProviderContext CONTEXT = ProviderContextBuilder
			.of(PROVIDER, RateType.DEFERRED)
			.set("providerDescription", "Yahoo currency rates").set("days", 1)
			.build();

	public YHOORateProvider() {
		super(CONTEXT);
	}

	@Override
	protected String getDataId() {
		return DATA_ID;
	}

}
