package com.bills.api.service;

import com.bills.api.domain.Coin;
import com.bills.api.util.CoinConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class CoinInventory {
    private Map<Coin, Integer> coinCount = new ConcurrentHashMap<>();

    private static List<Coin> coins = new ArrayList<>(EnumSet.allOf(Coin.class).stream()
            .sorted(Comparator.comparing(Coin::getCoinValue).reversed()).collect(Collectors.toList()));

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

    public Optional<Coin> getAvailableCoin(final BigDecimal amount) {
        return coins.stream()
                .filter(this::isCoinAvailable)
                .filter(coin -> isBillValueLessThanOrEqualToBalance(amount, coin.getCoinValue()))
                .findFirst();
    }

    private boolean isBillValueLessThanOrEqualToBalance(BigDecimal bal, BigDecimal billValue) {
        return billValue.compareTo(bal) == -1 || billValue.compareTo(bal) == 0;
    }


    public void debitCoinCount(Coin coin, Integer count)  {
        this.coinCount.computeIfPresent(coin, (k, v) -> v - count);
    }

    public Map<Coin, Integer> getCoinBalance() {
        return coinCount;
    }

    public void creditCoinCount(Map<Coin, Integer> coinDenominations) {
        for(Map.Entry<Coin, Integer> coins : coinDenominations.entrySet()) {
            this.coinCount.computeIfPresent(coins.getKey(), (k, v) -> v + coins.getValue());
        }
    }
}
