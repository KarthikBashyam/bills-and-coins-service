package com.bills.api.service;

import com.bills.api.domain.Bill;
import com.bills.api.domain.Coin;
import com.bills.api.dto.ChangeRequestDTO;
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

    private Logger LOGGER = LoggerFactory.getLogger(ChangeCalculatorService.class);

    private CoinInventory coinInventory;

    private static List<Bill> bills = new ArrayList<>(EnumSet.allOf(Bill.class).stream()
            .sorted(Comparator.comparing(Bill::getBillValue).reversed()).collect(Collectors.toList()));

    @Autowired
    public ChangeCalculatorService(CoinInventory coinInventory) {
        this.coinInventory = coinInventory;
    }

    public ChangeResponseDTO calculate(ChangeRequestDTO changeRequestDTO) throws CoinNotAvailableException {
        LOGGER.info("=== AMOUNT:" + changeRequestDTO.getAmount());

        BigDecimal balance = BigDecimal.valueOf(changeRequestDTO.getAmount());

        BigDecimal remainingBalance = null;
        Map<Coin, Integer> coinDenominationsMap = null;
        HashMap<Bill, Integer> billDenominationMap = null;

        if(changeRequestDTO.isRequestForMoreCoins()) {
            coinDenominationsMap = calculateCoinDenominations(balance, changeRequestDTO.isRequestForMoreCoins());
            remainingBalance = BigDecimal.valueOf(changeRequestDTO.getAmount()).subtract(getTotalCoinDenominationAmount(coinDenominationsMap));
            LOGGER.info("=== BILL BALANCE:" + remainingBalance);
            billDenominationMap = calculateBillDenominations(remainingBalance);

        } else {
            billDenominationMap = calculateBillDenominations(balance);
            remainingBalance = BigDecimal.valueOf(changeRequestDTO.getAmount()).subtract(getTotalBillDenominationAmount(billDenominationMap));
            LOGGER.info("=== COIN BALANCE:" + remainingBalance);
            coinDenominationsMap = calculateCoinDenominations(remainingBalance, changeRequestDTO.isRequestForMoreCoins());
        }

        ChangeResponseDTO changeResponseDTO = new ChangeResponseDTO();
        changeResponseDTO.setBillsDenominations(billDenominationMap);
        changeResponseDTO.setCoinDenominations(coinDenominationsMap);

        return changeResponseDTO;

    }

    private BigDecimal getTotalCoinDenominationAmount(Map<Coin, Integer> billDenomination) {

        return billDenomination
                .entrySet()
                .stream()
                .map(entry -> entry.getKey().getCoinValue().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    private BigDecimal getTotalBillDenominationAmount(Map<Bill, Integer> billDenomination) {

        return billDenomination
                .entrySet()
                .stream()
                .map(entry -> entry.getKey().getBillValue().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    public HashMap<Bill, Integer> calculateBillDenominations(BigDecimal balance) {

        var billDenomination = new HashMap<Bill, Integer>();

        while (balance.doubleValue() > 1) {
            final BigDecimal bal = balance;
            Optional<Bill> isBillAvailable = bills.stream().filter(bill -> isBillOrCoinLessThanOrEqualToBalance(bal, bill.getBillValue())).findFirst();

            if (isBillAvailable.isPresent()) {
                Bill bill = isBillAvailable.get();
                int count = (int) balance.divide(bill.getBillValue()).intValue();
                balance = balance.remainder(bill.getBillValue());
                billDenomination.put(bill, billDenomination.getOrDefault(bill, 0) + count);
            }

        }
        return billDenomination;
    }

    private Map<Coin, Integer> calculateCoinDenominations(BigDecimal balance, boolean requestForMoreCoins) throws CoinNotAvailableException {

        List<Coin> coins = new ArrayList<>(EnumSet.allOf(Coin.class).stream()
                .sorted(getCoinComparator(requestForMoreCoins)).collect(Collectors.toList()));

        BigDecimal coinAmount = balance;
        coinAmount.setScale(2, RoundingMode.FLOOR);

        Map<Coin, Integer> coinDenominations = new HashMap<>();

        while (coinAmount.doubleValue() > 0) {

            final var bal = coinAmount;

            Optional<Coin> isCoinAvailable = coins.stream()
                    .filter(coinInventory::isCoinAvailable)
                    .filter(coin -> isBillOrCoinLessThanOrEqualToBalance(bal, coin.getCoinValue())).findFirst();

            if (isCoinAvailable.isPresent()) {
                Coin coin = isCoinAvailable.get();
                int count = Math.min(bal.divide(coin.getCoinValue(), 0).intValue(), coinInventory.getCount(coin));
                coinAmount = coinAmount.subtract(coin.getCoinValue().multiply(BigDecimal.valueOf(count)));
                coinInventory.reduceCoinCount(coin, count);
                LOGGER.info(coin + "-" + "Count:" + count + "- Remaining Balance:" + coinAmount);
                coinDenominations.put(coin, coinDenominations.getOrDefault(coin, 0) + count);
            } else {
                throw new CoinNotAvailableException("Coins are not available for the remaining balance amount:" + coinAmount + " , Coin balance:" + coinInventory.getCoinBalance());
            }

        }
        LOGGER.info(coinDenominations.toString());
        return coinDenominations;
    }

    private Comparator<Coin> getCoinComparator(boolean requestForMoreCoins) {
        Comparator<Coin> comparator = requestForMoreCoins == true ? Comparator.comparing(Coin::getCoinValue) : Comparator.comparing(Coin::getCoinValue) .reversed();
        return comparator;
    }

    private boolean isBillOrCoinLessThanOrEqualToBalance(BigDecimal bal, BigDecimal billValue) {
        return billValue.compareTo(bal) == -1 || billValue.compareTo(bal) == 0;
    }

    public CoinBalanceResponseDTO getCoinBalance() {
        return new CoinBalanceResponseDTO(coinInventory.getCoinBalance());
    }
}