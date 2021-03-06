package br.com.impex.converter;

import br.com.impex.domain.Address;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelToImpexConveter {
    private static final String SAMPLE_EXCEL_FILE = "/home/themarkiron/Desktop/impexTestConverter.xlsx";
    private static final String INSERT_UPDATE = "INSERT_UPDATE ";

    public static void main(String[] args) throws IOException, InvalidFormatException {


        Workbook workbook = WorkbookFactory.create(new File(SAMPLE_EXCEL_FILE));

        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();
        List<String> impexConverter = new ArrayList<>();
        int fields = Address.class.getDeclaredFields().length;

        StringBuilder impexInsert = new StringBuilder();
        impexInsert.append(insertUpdate(Address.class));


        Sheet createSheet = workbook.createSheet("Address");

        Row rows = createSheet.createRow(0);
        rows.createCell(0).setCellValue(insertUpdate(Address.class).toString());
        for (int i = 0; i < fields; i++) {
            String field = Address.class.getDeclaredFields()[i].getName();
            if (field.equals("code")) {
                String replace = field + "[unique=true]";
                impexInsert.append(replace);
                generateCell(rows, replace, i);
            } else if (field.equals("owner")) {
                String replace = field + "(B2BUnit.uid)";
                impexInsert.append(replace);
                generateCell(rows, replace, i);
            } else {
                impexInsert.append(field).append(";");
                generateCell(rows, field, i);
            }

        }


        StringBuilder linesImpex = new StringBuilder();

        var ref = new Object() {
            Row linha = null;
        };

        var ref2 = new Object() {
            int count = 1;
        };

        var ref3 = new Object() {
            int count2 = 1;
        };

        sheet.forEach(row -> {
            ref.linha = createSheet.createRow(ref3.count2++);

            row.forEach(cell -> {
                String cellValue = dataFormatter.formatCellValue(cell);
                linesImpex.append(cellValue);

                if (cellValue.equalsIgnoreCase(";")) {
                    ref.linha.createCell(ref2.count++).setCellValue(cellValue);
                } else {

                    ref.linha.createCell(ref2.count++).setCellValue(cellValue + ";");
                }
            });

            impexConverter.add(linesImpex.toString());
            linesImpex.delete(0, linesImpex.length());
            ref2.count = 1;

        });

        for (String string : impexConverter) {
            impexInsert.append("\n").append(string);
        }
        System.out.println(impexInsert);

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("/home/themarkiron/Desktop/poi-generated-file2.xls");
        workbook.write(fileOut);
        fileOut.close();


    }

    private static void generateCell(Row rows, String replace, int i) {
        rows.createCell(i + 1).setCellValue(replace + ";");
    }

    public static StringBuilder insertUpdate(Class cls) {
        return new StringBuilder().append(INSERT_UPDATE + cls.getSimpleName()).append(";");
    }

}
