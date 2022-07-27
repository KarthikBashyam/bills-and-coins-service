package com.bills.api.service;

import com.bills.api.domain.Coin;
import com.bills.api.util.CoinConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CoinInventory {
    private Map<Coin, Integer> coinCount = new ConcurrentHashMap<>();

    @Autowired
    public CoinInventory(CoinConfig coinConfig) {
        coinCount.put(Coin.QUARTER,coinConfig.getQuarterCount());
        coinCount.put(Coin.CENT,coinConfig.getCentCount());
        coinCount.put(Coin.DIME,coinConfig.getDimeCount());
        coinCount.put(Coin.NICKEL,coinConfig.getNickelCount());
    }

    public boolean isCoinAvailable(Coin coinType) {
        return coinCount.get(coinType) > 0;
    }

    public Integer getCount(Coin coin) {
        return coinCount.get(coin);
    }

    public void reduceCoinCount(Coin coin, Integer count) {
        coinCount.computeIfPresent(coin, (k,v) -> v - count);
    }

    public Map<Coin, Integer> getCoinBalance() {
        return coinCount;
    }
}