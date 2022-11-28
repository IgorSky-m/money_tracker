package com.bot.calculation.transport.com.exchangerate;

import java.util.Date;
import java.util.Map;

/**
 * www.exchangerate-api.com
 */
public class ExchangeRateCurrency {
    private String result;
    private Date timeLastUpdateUtc;
    private String baseCode;
    private Map<String, Double> conversionRates;


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getTimeLastUpdateUtc() {
        return timeLastUpdateUtc;
    }

    public void setTimeLastUpdateUtc(Date timeLastUpdateUtc) {
        this.timeLastUpdateUtc = timeLastUpdateUtc;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public Map<String, Double> getConversionRates() {
        return conversionRates;
    }

    public void setConversionRates(Map<String, Double> conversionRates) {
        this.conversionRates = conversionRates;
    }
}
