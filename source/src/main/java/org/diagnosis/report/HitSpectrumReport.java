package org.diagnosis.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.diagnosis.algorithms.entities.Component;
import org.diagnosis.algorithms.entities.HitSpectrum;
import org.diagnosis.algorithms.entities.TestCase;
import org.diagnosis.algorithms.sfl.ssfl.SimilarityCoefficients;
import org.diagnosis.evaluation.ground_truth.GroundTruth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HitSpectrumReport implements Report {
    private final HitSpectrum hitSpectrum;
    private final GroundTruth groundTruth;
    private final Map<Component, Double> components;
    private final Workbook workbook;
    private final Sheet sheet;
    private final XSSFColor lightRed;
    private final XSSFColor veryLightGray;
    private final XSSFColor lightOrange;
    private final File fileName;
    private final boolean randomWalks;

    public HitSpectrumReport(HitSpectrum hitSpectrum, GroundTruth groundTruth, SimilarityCoefficients similarityCoefficients, File fileName) {
        this(hitSpectrum, groundTruth, similarityCoefficients, fileName, false);
    }

    public HitSpectrumReport(HitSpectrum hitSpectrum, GroundTruth groundTruth, SimilarityCoefficients similarityCoefficients, File fileName, boolean randomWalks) {
        this.hitSpectrum = hitSpectrum;
        this.groundTruth = groundTruth;
        this.components = initializeComponents(similarityCoefficients);
        this.fileName = fileName;
        this.workbook = new XSSFWorkbook();
        this.sheet = workbook.createSheet("Example");
        this.lightRed = createColor((byte) 255, (byte) 102, (byte) 102);
        this.veryLightGray = createColor((byte) 0, (byte) 136, (byte) 143);
        this.lightOrange = createColor((byte) 247, (byte) 209, (byte) 205);
        this.randomWalks = randomWalks;
    }

    private Map<Component, Double> initializeComponents(SimilarityCoefficients similarityCoefficients) {
        return similarityCoefficients.getCoefficients().entrySet().stream()
                .filter(entry -> entry.getValue() >= 0.0)
                .sorted(Map.Entry.<Component, Double>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    private XSSFColor createColor(byte red, byte green, byte blue) {
        return new XSSFColor(new byte[]{red, green, blue}, null);
    }

    @Override
    public void report() {
        createHeaderRow();
        createCoefficientRow();
        populateTestCases();
        autoSizeColumns();
        writeToFile();
    }

    private void createHeaderRow() {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Tests");
        headerRow.createCell(components.size() + 1).setCellValue("Failed");
    }

    private void createCoefficientRow() {
        Row coefficientRow = sheet.createRow(1);
        CellStyle coefficientStyle = createCellStyle(veryLightGray, "0.00");

        coefficientRow.createCell(0).setCellValue("Coefficient");
        int componentCounter = 1;
        for (Component component : components.keySet()) {
            Cell headerCell = sheet.getRow(0).createCell(componentCounter);
            setHeaderCellStyle(headerCell, component.toString());

            Cell coefficientCell = coefficientRow.createCell(componentCounter);
            coefficientCell.setCellValue(components.get(component));
            coefficientCell.setCellStyle(coefficientStyle);
            componentCounter++;
        }
    }

    private void populateTestCases() {
        int testCounter = 2;
        for (TestCase testCase : hitSpectrum.getHitSpectrum().keySet()) {
            populateTestCaseRow(testCounter++, testCase);
        }
    }

    private void populateTestCaseRow(int rowIndex, TestCase testCase) {
        Row testRow = sheet.createRow(rowIndex);
        Cell testCell = testRow.createCell(0);
        testCell.setCellValue(testCase.toString());

        Cell outputCell = testRow.createCell(components.size() + 1);
        outputCell.setCellValue(testCase.hasFailed());

        if (testCase.hasFailed()) {
            CellStyle failedStyle = createCellStyle(lightRed, null);
            applyStyleToTestCaseRow(testRow, failedStyle, testCell, outputCell);
        }

        populateComponentCells(testRow, testCase, rowIndex);
    }

    private void applyStyleToTestCaseRow(Row row, CellStyle style, Cell testCell, Cell outputCell) {
        testCell.setCellStyle(style);
        outputCell.setCellStyle(style);
        for (int i = 1; i <= components.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
        }
    }

    private void populateComponentCells(Row row, TestCase testCase, int rowIndex) {
        CellStyle hitStyle = createCellStyle(lightOrange, null);
        int componentCounter = 1;
        for (Component component : components.keySet()) {
            Cell cell = row.createCell(componentCounter);
            //cell.setCellValue(numberOfHits > 0 ? 1 : 0);
            if (this.randomWalks) {
                cell.setCellValue(hitSpectrum.getHitSpectrum().get(testCase).getAverageComponentsInRandomWalks(component));
            } else {
                float numberOfHits = hitSpectrum.getHitSpectrum().get(testCase).getHits(component);
                cell.setCellValue(numberOfHits);
            }

            if (groundTruth.getComponents().contains(component)) {
                cell.setCellStyle(hitStyle);
            }
            componentCounter++;
        }
    }

    private CellStyle createCellStyle(XSSFColor color, String dataFormat) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(color);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        if (dataFormat != null) {
            DataFormat format = workbook.createDataFormat();
            style.setDataFormat(format.getFormat(dataFormat));
        }
        return style;
    }

    private void setHeaderCellStyle(Cell cell, String text) {
        CellStyle style = workbook.createCellStyle();
        style.setRotation((short) 90);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        cell.setCellValue(text);
        cell.setCellStyle(style);
        sheet.setColumnWidth(cell.getColumnIndex(), (18 / 7) * 256);
    }

    private void autoSizeColumns() {
        for (int i = 0; i <= components.size() + 1; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void writeToFile() {
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
