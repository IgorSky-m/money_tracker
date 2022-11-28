package com.bot.calculation.fileProcessor;

import com.bot.calculation.entity.amount.Amount;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.List;


public class CsvFileProcessor {

    private static final String[] amountHeaders = new String[]{
            Amount.UUID_FIELD,
            Amount.DT_CREATE_FIELD,
            Amount.USER_ID_FIELD,
            Amount.AMOUNT_FIELD,
            Amount.DESCRIPTION_FIELD,
            Amount.CURRENCY_FIELD
    };

    private final CSVFormat csvFormat;

    public CsvFileProcessor(){
        csvFormat = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setHeader(amountHeaders)
                .setSkipHeaderRecord(false)
                .build();
    }

    public InputStream loadCSVFile(List<Amount> amounts) throws IOException {
        File tmp = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".csv");
        FileWriter writer = new FileWriter(tmp);
        try (CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
            for (Amount amount : amounts) {

                printer.print(amount.getUuid());
                printer.print(amount.getDtCreate());
                printer.print(amount.getUserId());
                printer.print(amount.getAmount());
                printer.print(amount.getDescription());
                printer.print(amount.getCurrency());
                printer.println();
            }
            printer.flush();
        }
        return new FileInputStream(tmp);
    }
}
