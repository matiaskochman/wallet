package com.wallet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

import com.wallet.entity.Wallet;
import com.wallet.service.WalletService;

@Configurable
/**
 * A central place to register application converters and formatters. 
 */
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

	@Override
	protected void installFormatters(FormatterRegistry registry) {
		super.installFormatters(registry);
		// Register application converters and formatters
	}

	@Autowired
    WalletService walletService;

	public Converter<Wallet, String> getWalletToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<com.wallet.entity.Wallet, java.lang.String>() {
            public String convert(Wallet wallet) {
                return new StringBuilder().append(wallet.getAvailableMoney()).toString();
            }
        };
    }

	public Converter<Long, Wallet> getIdToWalletConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, com.wallet.entity.Wallet>() {
            public com.wallet.entity.Wallet convert(java.lang.Long id) {
                return walletService.findWallet(id);
            }
        };
    }

	public Converter<String, Wallet> getStringToWalletConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, com.wallet.entity.Wallet>() {
            public com.wallet.entity.Wallet convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Wallet.class);
            }
        };
    }

	public void installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getWalletToStringConverter());
        registry.addConverter(getIdToWalletConverter());
        registry.addConverter(getStringToWalletConverter());
    }

	public void afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
}
