package com.github.pablowyourmind.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Currency;
import org.hibernate.validator.constraints.pl.PESEL;

import java.math.BigDecimal;

public class RegistrationInfo {
    @PESEL
    private String pesel;
    @Size(min = 1, max = 100)
    private String name;
    @Size(min = 1, max = 100)
    private String surname;
    @DecimalMin(value = "0.00", inclusive = true)
    @Digits(integer = 100, fraction = 2)
    private BigDecimal initialAmount;
    @Pattern(regexp = "PLN|USD", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String firstSubaccountCurrency;
    @Pattern(regexp = "PLN|USD", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String secondSubaccountCurrency;


    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }

    public String getFirstSubaccountCurrency() {
        return firstSubaccountCurrency;
    }

    public void setFirstSubaccountCurrency(String firstSubaccountCurrency) {
        this.firstSubaccountCurrency = firstSubaccountCurrency;
    }

    public String getSecondSubaccountCurrency() {
        return secondSubaccountCurrency;
    }

    public void setSecondSubaccountCurrency(String secondSubaccountCurrency) {
        this.secondSubaccountCurrency = secondSubaccountCurrency;
    }
}
