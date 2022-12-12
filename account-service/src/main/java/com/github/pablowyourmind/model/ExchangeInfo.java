package com.github.pablowyourmind.model;

import java.util.ArrayList;
import java.util.List;

public class ExchangeInfo {
    private String code;
    private List<Rate> rates = new ArrayList<>();

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
