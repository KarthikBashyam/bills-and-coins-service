package com.bills.api.service;

import com.bills.api.domain.Coin;
import com.bills.api.exceptions.CoinNotAvailableException;
import com.bills.api.util.CoinConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

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


    public void reduceCoinCount(Coin coin, Integer count)  {
        // Check and update coin balance before atomically.
        this.coinCount.computeIfPresent(coin, updateCoinCountAtomically(count));
    }

    private BiFunction<Coin, Integer, Integer> updateCoinCountAtomically(Integer count) {
        return (k, v) -> {
            if(v >= count) {
                System.out.println("Thread:"+Thread.currentThread().getName());
                return v - count;
            } else {
              System.out.println("Else Thread:"+ Thread.currentThread().getName());
             throw new CoinNotAvailableException("Coins are not available for " + k);
            }
        };
    }

    public Map<Coin, Integer> getCoinBalance() {
        return coinCount;
    }

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        CoinConfig coinConfig = new CoinConfig();
        coinConfig.setCentCount(20);
        coinConfig.setNickelCount(20);
        coinConfig.setQuarterCount(20);
        coinConfig.setDimeCount(20);
        CoinInventory coinInventory = new CoinInventory(coinConfig);

            Runnable task1 = () -> {
                try {
                    coinInventory.reduceCoinCount(Coin.QUARTER,20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            Runnable task2 = () -> {
                try {
                    coinInventory.reduceCoinCount(Coin.QUARTER,20);
                } catch (Exception e) {
                  e.printStackTrace();
                }
            };
            executorService.submit(task1);
            executorService.submit(task2);



        System.out.println(coinInventory.coinCount);

        executorService.shutdown();

    }

}
