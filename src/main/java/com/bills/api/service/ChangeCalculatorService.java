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
import java.util.stream.Collectors;

@Service
public class ChangeCalculatorService {

    private Logger LOGGER  = LoggerFactory.getLogger(ChangeCalculatorService.class);

    private CoinInventory coinInventory;

    private static List<Bill> bills = new ArrayList<>(EnumSet.allOf(Bill.class).stream()
            .sorted(Comparator.comparing(Bill::getBillValue).reversed()).collect(Collectors.toList()));

    private static List<Coin> coins = new ArrayList<>(EnumSet.allOf(Coin.class).stream()
            .sorted(Comparator.comparing(Coin::getCoinValue).reversed()).collect(Collectors.toList()));

    @Autowired
    public ChangeCalculatorService(CoinInventory coinInventory) {
        this.coinInventory = coinInventory;
    }

    public ChangeResponseDTO calculate(double amount) throws CoinNotAvailableException {
        System.out.println("=== AMOUNT:" + amount);
        double balance = amount;

        Map<Bill, Integer> billDenomination = new HashMap<>();

        while (balance > 1 && balance > 0) {
            final double bal = balance;
            Optional<Bill> billAvailable = bills.stream().filter(bill -> bill.getBillValue() <= bal).findFirst();

            if (billAvailable.isPresent()) {
                Bill bill = billAvailable.get();
                int count = (int) balance / bill.getBillValue();
                balance = balance % bill.getBillValue();
                billDenomination.put(bill, billDenomination.getOrDefault(bill, 0) + count);
            }

        }
        LOGGER.info(billDenomination.toString());
        LOGGER.info("=== COIN BALANCE:" + balance);

        // Coins
        Map<Coin, Integer> coinDenominations = calculateCoinDenominations(balance);

        ChangeResponseDTO changeResponseDTO = new ChangeResponseDTO();
        changeResponseDTO.setBillsDenominations(billDenomination);
        changeResponseDTO.setCoinDenominations(coinDenominations);


        return changeResponseDTO;

    }

    private Map<Coin, Integer> calculateCoinDenominations(double balance) throws CoinNotAvailableException {

        BigDecimal coinAmount = BigDecimal.valueOf(balance);
        coinAmount.setScale(2, RoundingMode.FLOOR);

        Map<Coin, Integer> coinDenominations = new HashMap<>();

        while (coinAmount.doubleValue() > 0) {

            final var bal = coinAmount;

            Optional<Coin> coinAvailable = coins.stream().filter(coin -> coinInventory.isCoinAvailable(coin)).filter(coin -> isLessThanOrEqual(bal, coin)).findFirst();

            if (coinAvailable.isPresent()) {
                Coin coin = coinAvailable.get();
                int count = Math.min(bal.divide(coin.getCoinValue(), 0).intValue(), coinInventory.getCount(coin));
                coinAmount = coinAmount.subtract(coin.getCoinValue().multiply(BigDecimal.valueOf(count)));
                coinInventory.reduceCoinCount(coin, count);
                LOGGER.info(coin + "-" + "Count:"+count+"- Remaining Balance:"+coinAmount);
                coinDenominations.put(coin, coinDenominations.getOrDefault(coin,0) + count);
            } else {
                throw new CoinNotAvailableException("Coins not available for remaining balance:" + coinAmount+" , Coin balance:" + coinInventory.getCoinBalance());
            }

        }
        LOGGER.info(coinDenominations.toString());
        return coinDenominations;
    }

    private boolean isLessThanOrEqual(BigDecimal bal, Coin coin) {
        return coin.getCoinValue().compareTo(bal) == -1 || coin.getCoinValue().compareTo(bal) == 0;
    }

    public CoinBalanceResponseDTO getCoinBalance() {
        return new CoinBalanceResponseDTO(coinInventory.getCoinBalance());
    }
}