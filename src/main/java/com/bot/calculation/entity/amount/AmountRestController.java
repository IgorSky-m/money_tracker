package com.bot.calculation.entity.amount;

import com.bot.calculation.entity.amount.api.IAmountService;
import com.bot.calculation.entity.security.annotations.Security;
import com.bot.calculation.fileProcessor.CsvFileProcessor;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/amounts")
@CrossOrigin(origins = "*")
public class AmountRestController {

    private final IAmountService amountService;

    private final CsvFileProcessor processor = new CsvFileProcessor();

    public AmountRestController(IAmountService amountService){
        this.amountService = amountService;
    }

    @Security
    @PostMapping
    public ResponseEntity<?> create(@RequestBody AmountCreateRequest amount) {

        final UUID uuid = this.amountService.create(amount);
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{uuid}")
                        .buildAndExpand(uuid)
                        .toUri()
                )
                .build();
    }

    @Security
    @DeleteMapping("{uuid}")
    public ResponseEntity<?> delete(@PathVariable UUID uuid){
        this.amountService.delete(uuid);
        return ResponseEntity.ok().build();
    }

    /**
     * Получить все записи
     */
    @GetMapping
    public List<Amount> getAll(
            @RequestParam(required = false, name = Amount.UUID_FIELD) UUID uuid,
            @RequestParam(required = false, name = Amount.DT_CREATE_FIELD) Long dtCreate,
            @RequestParam(required = false, name = Amount.USER_ID_FIELD) Long userId,
            @RequestParam(required = false, name = Amount.AMOUNT_FIELD) Double amount,
            @RequestParam(required = false, name = Amount.DESCRIPTION_FIELD) String description
    ){
        Date date = dtCreate == null ? null : new Date(dtCreate);
        return amountService.getAllWithFilterProps(uuid, date, userId, amount, description);
    }

    /**
     * Получить запись по идентификатору
     */
    @GetMapping("/{uuid}")
    public Amount getOne(@PathVariable UUID uuid){
        return amountService.getAmountById(uuid);
    }


    /**
     * Получить записи за период
     */
    @GetMapping("/by/period")
    public List<Amount> getByPeriod(@RequestParam(name = "dt_from") Long dtFrom, @RequestParam(name = "dt_to") Long dtTo){
        return amountService.getByPeriod(new Date(dtFrom), new Date(dtTo));
    }

    /**
     * Обновить описание записи по идентификатору
     */
    @Security
    @PatchMapping("/{uuid}")
    public ResponseEntity<?> updateDescription(@PathVariable UUID uuid, @RequestBody Map<String, String> description){
        amountService.updateDescription(uuid, description);
        return ResponseEntity.ok().build();
    }

    /**
     * Выгрузить все записи в csv файл
     */
    @GetMapping("/csv")
    public void loadCsv(HttpServletResponse response,
                        @RequestParam(name = Disposition.HEADER_PARAM_NAME, required = false, defaultValue = Disposition.ATTACHMENT) String disposition) throws IOException {

        try(InputStream inputStream = processor.loadCSVFile(amountService.getAll())) {
            response.setContentType("text/csv");
            response.setHeader(Disposition.HEADER_PARAM_NAME, String.format("%s; filename=\"%s\"", disposition, System.currentTimeMillis() + ".csv"));
            IOUtils.copy(inputStream, response.getOutputStream());
                        response.flushBuffer();
        }
    }


    private static class Disposition{
        public static final String HEADER_PARAM_NAME = "Content-Disposition";
        public static final String ATTACHMENT = "attachment";
        public static final String INLINE = "inline";

        public static boolean isValid(String disposition) {
            return ATTACHMENT.equals(disposition) || INLINE.equals(disposition);
        }

    }

}
