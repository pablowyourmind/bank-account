package com.github.pablowyourmind.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pablowyourmind.model.types.CurrencyType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import org.hibernate.validator.constraints.pl.PESEL;

import java.math.BigDecimal;

public class ExchangeInfo {
    @PESEL
    private String pesel;

    @JsonProperty("kwotabazowa")
    @DecimalMin(value = "0.00", inclusive = true, message = "Wartość nie może być ujemna")
    @Digits(integer = 100, fraction = 2, message = "Nieprawidłowa wartość")
    private BigDecimal howMuch;

    @JsonProperty("walutabazowa")
    private CurrencyType sourceCurrency;

    @JsonProperty("walutadocelowa")
    private CurrencyType targetCurrency;

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public BigDecimal getHowMuch() {
        return howMuch;
    }

    public void setHowMuch(BigDecimal howMuch) {
        this.howMuch = howMuch;
    }

    public CurrencyType getSourceCurrency() {
        return sourceCurrency;
    }

    public void setSourceCurrency(CurrencyType sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public CurrencyType getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(CurrencyType targetCurrency) {
        this.targetCurrency = targetCurrency;
    }
}
