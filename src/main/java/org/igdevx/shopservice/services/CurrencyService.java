package org.igdevx.shopservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.igdevx.shopservice.dtos.CurrencyRequest;
import org.igdevx.shopservice.dtos.CurrencyResponse;
import org.igdevx.shopservice.exceptions.DuplicateResourceException;
import org.igdevx.shopservice.exceptions.ResourceNotFoundException;
import org.igdevx.shopservice.mappers.CurrencyMapper;
import org.igdevx.shopservice.models.Currency;
import org.igdevx.shopservice.repositories.CurrencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Transactional(readOnly = true)
    public List<CurrencyResponse> getAllCurrencies() {
        log.debug("Fetching all currencies");
        return currencyRepository.findAll()
                .stream()
                .map(currencyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CurrencyResponse getCurrencyById(Long id) {
        log.debug("Fetching currency with id: {}", id);
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));
        return currencyMapper.toResponse(currency);
    }

    @Transactional(readOnly = true)
    public CurrencyResponse getCurrencyByCode(String code) {
        log.debug("Fetching currency with code: {}", code);
        Currency currency = currencyRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with code: " + code));
        return currencyMapper.toResponse(currency);
    }

    @Transactional
    public CurrencyResponse createCurrency(CurrencyRequest request) {
        log.debug("Creating new currency with code: {}", request.getCode());

        if (currencyRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Currency already exists with code: " + request.getCode());
        }

        Currency currency = currencyMapper.toEntity(request);
        Currency savedCurrency = currencyRepository.save(currency);
        log.info("Currency created with id: {}", savedCurrency.getId());
        return currencyMapper.toResponse(savedCurrency);
    }

    @Transactional
    public CurrencyResponse updateCurrency(Long id, CurrencyRequest request) {
        log.debug("Updating currency with id: {}", id);

        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));

        // Check if code is being changed and if the new code already exists
        if (!currency.getCode().equals(request.getCode()) && currencyRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Currency already exists with code: " + request.getCode());
        }

        currencyMapper.updateEntity(currency, request);
        Currency updatedCurrency = currencyRepository.save(currency);
        log.info("Currency updated with id: {}", id);
        return currencyMapper.toResponse(updatedCurrency);
    }

    @Transactional
    public void deleteCurrency(Long id) {
        log.debug("Soft deleting currency with id: {}", id);

        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));

        currency.softDelete();
        currencyRepository.save(currency);
        log.info("Currency soft deleted with id: {}", id);
    }

    @Transactional
    public void restoreCurrency(Long id) {
        log.debug("Restoring currency with id: {}", id);

        Currency currency = currencyRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));

        if (!currency.getIsDeleted()) {
            throw new IllegalStateException("Currency with id " + id + " is not deleted");
        }

        currency.restore();
        currencyRepository.save(currency);
        log.info("Currency restored with id: {}", id);
    }

    @Transactional
    public void hardDeleteCurrency(Long id) {
        log.debug("Hard deleting currency with id: {}", id);

        currencyRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found with id: " + id));

        currencyRepository.hardDeleteById(id);
        log.info("Currency hard deleted with id: {}", id);
    }

    @Transactional(readOnly = true)
    public List<CurrencyResponse> getAllDeletedCurrencies() {
        log.debug("Fetching all deleted currencies");
        return currencyRepository.findAllDeleted()
                .stream()
                .map(currencyMapper::toResponse)
                .collect(Collectors.toList());
    }
}
