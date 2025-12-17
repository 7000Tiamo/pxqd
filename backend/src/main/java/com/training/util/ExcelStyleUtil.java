package com.training.util;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.*;

/**
 * Excel样式工具类
 * 用于设置表头和数据单元格的样式、列宽
 */
public class ExcelStyleUtil implements SheetWriteHandler {

    // 定义各列的宽度（字符数）
    private static final int[] COLUMN_WIDTHS = {15, 12, 15, 20, 15, 10, 10, 20};

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        
        // 设置列宽
        for (int i = 0; i < COLUMN_WIDTHS.length; i++) {
            sheet.setColumnWidth(i, COLUMN_WIDTHS[i] * 256); // 256是Excel的单位转换
        }
    }
}

