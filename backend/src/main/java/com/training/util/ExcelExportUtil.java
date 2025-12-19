package com.training.util;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ExcelExportUtil {

    /**
     * 将 List<Map<String, Object>> 导出为 Excel 并写入 HttpServletResponse
     *
     * @param data        数据列表，每个 Map 代表一行
     * @param sheetName   工作表名称（可为 null，默认 "Sheet1"）
     * @param fileName    下载的文件名（不含 .xlsx，例如 "培训统计"）
     * @param columnNames 可选：指定列顺序和中文列名，key=字段名, value=显示名
     *                    如果为 null，则使用 Map 的 key 作为列名，且顺序不确定（建议传 LinkedHashMap）
     * @param response    HttpServletResponse
     * @throws IOException
     */
    public static void exportToExcel(
            List<Map<String, Object>> data,
            String sheetName,
            String fileName,
            Map<String, String> columnNames,
            HttpServletResponse response) throws IOException {

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String encodedFileName = URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20"); // 兼容空格
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

        try (Workbook workbook = new XSSFWorkbook();
             OutputStream out = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet(sheetName != null ? sheetName : "Sheet1");

            if (data == null || data.isEmpty()) {
                workbook.write(out);
                return;
            }

            // 确定列顺序和标题
            List<String> columns;
            List<String> headers;
            if (columnNames != null && !columnNames.isEmpty()) {
                columns = new ArrayList<>(columnNames.keySet());
                headers = new ArrayList<>();
                for (String key : columns) {
                    headers.add(columnNames.getOrDefault(key, key));
                }
            } else {
                // 使用第一个 map 的 keySet（注意：HashMap 无序！建议业务层用 LinkedHashMap）
                columns = new ArrayList<>(data.get(0).keySet());
                headers = new ArrayList<>(columns);
            }

            // 创建表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                headerRow.createCell(i).setCellValue(headers.get(i));
            }

            // 填充数据
            for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
                Row row = sheet.createRow(rowIndex + 1);
                Map<String, Object> rowData = data.get(rowIndex);
                for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                    String key = columns.get(colIndex);
                    Object value = rowData.get(key);
                    Cell cell = row.createCell(colIndex);
                    setCellValue(cell, value);
                }
            }

            // 自动调整列宽（限制最大宽度防止异常）
            // 替换原来的 autoSizeColumn 部分
            int fixedWidth = 15 * 256; // Excel 列宽单位：1 字符 ≈ 256（实际是 1/256 字符宽度）
            for (int i = 0; i < columns.size(); i++) {
                sheet.setColumnWidth(i, fixedWidth);
            }

            workbook.write(out);
        }
    }

    /**
     * 根据值类型设置单元格内容
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }
}