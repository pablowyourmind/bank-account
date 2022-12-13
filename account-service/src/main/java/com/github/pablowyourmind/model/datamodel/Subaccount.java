package com.github.pablowyourmind.model.datamodel;

import com.github.pablowyourmind.model.types.CurrencyType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

@Entity
public class Subaccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private CurrencyType currency;
    @DecimalMin(value = "0.00", inclusive = true, message = "Wartość nie może być ujemna")
    @Digits(integer = 100, fraction = 2, message = "Nieprawidłowa wartość")
    private BigDecimal amount;

    public Subaccount() {
    }

    public Subaccount(CurrencyType currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
