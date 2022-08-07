package com.bills.api.service;

import com.bills.api.domain.Bill;
import com.bills.api.domain.Coin;
import com.bills.api.dto.ChangeResponseDTO;
import com.bills.api.dto.CoinBalanceResponseDTO;
import com.bills.api.exceptions.CoinNotAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class ChangeCalculatorService {

    private final Logger LOGGER = LoggerFactory.getLogger(ChangeCalculatorService.class);

    private CoinInventory coinInventory;

    private static List<Bill> bills = new ArrayList<>(EnumSet.allOf(Bill.class).stream()
            .sorted(Comparator.comparing(Bill::getBillValue).reversed()).collect(Collectors.toList()));

    private static List<Coin> coins = new ArrayList<>(EnumSet.allOf(Coin.class).stream()
            .sorted(Comparator.comparing(Coin::getCoinValue).reversed()).collect(Collectors.toList()));

    @Autowired
    public ChangeCalculatorService(CoinInventory coinInventory) {
        this.coinInventory = coinInventory;
    }

    public ChangeResponseDTO calculate(BigDecimal amount) throws CoinNotAvailableException {
        LOGGER.info("=== AMOUNT:" + amount);

      /*  Map<Bill, Integer> billDenomination = new HashMap<>();

        while (balance.doubleValue() > 1) {
            final BigDecimal bal = balance;
            Optional<Bill> billAvailable = bills.stream().filter(bill -> isLessThanOrEqual(bal, bill.getBillValue())).findFirst();

            if (billAvailable.isPresent()) {
                Bill bill = billAvailable.get();
                int count = (int) balance.divide(bill.getBillValue()).intValue();
                balance = balance.remainder(bill.getBillValue());
                billDenomination.put(bill, billDenomination.getOrDefault(bill, 0) + count);
            }

        }
        LOGGER.info(billDenomination.toString());
        LOGGER.info("=== COIN BALANCE:" + balance);*/

        // Coins
        Map<Coin, Integer> coinDenominations = calculateCoinDenominations(amount);

        ChangeResponseDTO changeResponseDTO = new ChangeResponseDTO();
        //changeResponseDTO.setBillsDenominations(billDenomination);
        changeResponseDTO.setCoinDenominations(coinDenominations);


        return changeResponseDTO;

    }

    private Map<Coin, Integer> calculateCoinDenominations(BigDecimal balance) throws CoinNotAvailableException {

        BigDecimal coinAmount = balance;
        coinAmount.setScale(2, RoundingMode.FLOOR);

        Map<Coin, Integer> coinDenominations = new HashMap<>();

        // Read and reduce coin count has to be atomic.
        var lock = new ReentrantLock();

        try {
            lock.lock();
            while (coinAmount.doubleValue() > 0) {
                Optional<Coin> isCoinAvailable = coinInventory.getAvailableCoin(coinAmount);
                if (isCoinAvailable.isPresent()) {
                    Coin coin = isCoinAvailable.get();
                    int numberOfCoins = getAvailableCoinCount(coinAmount, coin);
                    coinAmount = calculateRemainingBalance(coinAmount, coin, numberOfCoins);
                    coinInventory.debitCoinCount(coin, numberOfCoins);
                    LOGGER.info(coin + "-" + "Count:" + numberOfCoins + "- Remaining Balance:" + coinAmount);
                    coinDenominations.put(coin, coinDenominations.getOrDefault(coin, 0) + numberOfCoins);
                } else {
                    coinInventory.creditCoinCount(coinDenominations);
                    throw new CoinNotAvailableException("Coins are not available for the remaining balance amount:" + coinAmount + " , Coin balance:" + coinInventory.getCoinBalance());
                }
            }

        } finally {
            lock.unlock();
        }
        LOGGER.info(coinDenominations.toString());
        return coinDenominations;
    }

    private int getAvailableCoinCount(BigDecimal coinAmount, Coin coinType) {
        return Math.min(coinAmount.divide(coinType.getCoinValue(), RoundingMode.FLOOR).intValue(), coinInventory.getCount(coinType));
    }

    private static BigDecimal calculateRemainingBalance(BigDecimal coinAmount, Coin coin, int numOfCoins) {
        return coinAmount.subtract(coin.getCoinValue().multiply(BigDecimal.valueOf(numOfCoins)));
    }


    public CoinBalanceResponseDTO getCoinBalance() {
        return new CoinBalanceResponseDTO(coinInventory.getCoinBalance());
    }
}