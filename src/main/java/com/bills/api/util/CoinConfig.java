package com.bills.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(value = "coin.count.*")
public class CoinConfig {

    @Value("${coin.count.quarter}")
    private int quarterCount;
    @Value("${coin.count.dime}")
    private int dimeCount;
    @Value("${coin.count.cent}")
    private int centCount;
    @Value("${coin.count.nickel}")
    private int nickelCount;

    public int getQuarterCount() {
        return quarterCount;
    }

    public void setQuarterCount(int quarterCount) {
        this.quarterCount = quarterCount;
    }

    public int getDimeCount() {
        return dimeCount;
    }

    public void setDimeCount(int dimeCount) {
        this.dimeCount = dimeCount;
    }

    public int getCentCount() {
        return centCount;
    }

    public void setCentCount(int centCount) {
        this.centCount = centCount;
    }

    public int getNickelCount() {
        return nickelCount;
    }

    public void setNickelCount(int nickelCount) {
        this.nickelCount = nickelCount;
    }
}
