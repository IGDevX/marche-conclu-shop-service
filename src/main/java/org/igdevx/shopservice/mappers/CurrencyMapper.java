package org.igdevx.shopservice.mappers;

import org.igdevx.shopservice.dtos.CurrencyRequest;
import org.igdevx.shopservice.dtos.CurrencyResponse;
import org.igdevx.shopservice.models.Currency;
import org.springframework.stereotype.Component;

@Component
public class CurrencyMapper {

    public CurrencyResponse toResponse(Currency currency) {
        if (currency == null) {
            return null;
        }

        return CurrencyResponse.builder()
                .id(currency.getId())
                .code(currency.getCode())
                .label(currency.getLabel())
                .usdExchangeRate(currency.getUsdExchangeRate())
                .createdAt(currency.getCreatedAt())
                .updatedAt(currency.getUpdatedAt())
                .isDeleted(currency.getIsDeleted())
                .build();
    }

    public Currency toEntity(CurrencyRequest request) {
        if (request == null) {
            return null;
        }

        return Currency.builder()
                .code(request.getCode())
                .label(request.getLabel())
                .usdExchangeRate(request.getUsdExchangeRate())
                .build();
    }

    public void updateEntity(Currency currency, CurrencyRequest request) {
        if (currency == null || request == null) {
            return;
        }

        currency.setCode(request.getCode());
        currency.setLabel(request.getLabel());
        currency.setUsdExchangeRate(request.getUsdExchangeRate());
    }
}
