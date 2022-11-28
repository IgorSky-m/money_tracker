package com.bot.calculation.entity.currency;

import com.bot.calculation.entity.amount.api.IAmountRepository;
import com.bot.calculation.entity.currency.api.ECurrencyCode;
import com.bot.calculation.entity.currency.api.ICurrencyService;
import com.bot.calculation.exchanger.api.IExchanger;
import com.bot.calculation.util.annotations.Wip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exchange")
@CrossOrigin(origins = "*")
public class CurrencyRestController {

    private final ICurrencyService currencyService;
    private final IExchanger exchanger;

    @Autowired
    IAmountRepository amountRepository;

    public CurrencyRestController(
            IExchanger exchanger,
            ICurrencyService currencyService
    ){
        this.exchanger = exchanger;
        this.currencyService = currencyService;
    }

    /**
     * Получить текущие курсы
     */
    @GetMapping("/rates/current")
    public ExchangeCurrency getActual(){
        return this.currencyService.getLastCurrency();
    }

    /**
     * Получить все записанные курсы
     */
    @Wip
    @GetMapping("/rates/on/date")
    public ExchangeCurrency getByDate(@RequestParam Long date){
        Date dtCreate = date == null ? null : new Date(date);
        return this.currencyService.getCurrencyByDate(dtCreate);
    }

    /**
     * Получить все записанные курсы
     */
    @GetMapping("/rates")
    public List<ExchangeCurrency> getAll(){
        return this.currencyService.getAll();
    }


    /**
     * Конвертер валют
     */
    @GetMapping("/convert")
    public BigDecimal exchange(@RequestParam String from, @RequestParam String to, @RequestParam BigDecimal amount){
        return exchanger.exchange(ECurrencyCode.valueOf(from.toUpperCase()), ECurrencyCode.valueOf(to.toUpperCase()), amount);
    }

    @GetMapping("/currencies/available")
    public List<String> availableCurrencies(){
        return Arrays.stream(ECurrencyCode.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
