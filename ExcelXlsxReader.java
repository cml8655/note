package com.cml.excel.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelXlsxReader {

	private static final Integer SHEET_FIRST = 0;
	private XSSFWorkbook excel;
	private XSSFSheet currentSheet;// 当前读取的sheet

	public ExcelXlsxReader(File file) throws FileNotFoundException, IOException {
		excel = new XSSFWorkbook(new FileInputStream(file));
		currentSheet = excel.getSheetAt(SHEET_FIRST);
	}

	public void readRecord(OnReadRecordListener recordListener, int skipLine)
			throws IOException {

		if (null == recordListener) {
			throw new IllegalArgumentException(
					"recordListener can not be null!");
		}

		if (null == currentSheet) {
			return;
		}

		try {
			int rowNum = currentSheet.getLastRowNum();
			
			// 读取每行数据
			for (int i = skipLine; i < rowNum; i++) {
				XSSFRow row = currentSheet.getRow(i);

				List<String> cellData = new ArrayList<String>(
						row.getLastCellNum());

				Iterator<Cell> it = row.cellIterator();
				// 读取一行数据
				while (it.hasNext()) {
					String v = this.loadCellValue(it.next());
					cellData.add(v);
				}

				boolean result = recordListener.readRecord(cellData);

				// 停止读取
				if (!result) {
					return;
				}
			}
		} finally {
			excel.close();
		}

	}

	/**
	 * 将单元格数据转换成string类型
	 * 
	 * @param cellType
	 * @param cell
	 * @return
	 */
	private String loadCellValue(Cell cell) {

		String value = null;
		switch (cell.getCellType()) {

		case Cell.CELL_TYPE_BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			value = String.valueOf(cell.getNumericCellValue());
			break;
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		}

		return value;
	}

	public static interface OnReadRecordListener {
		/**
		 * 
		 * @param value
		 * @return true 继续读取，false 退出读取
		 */
		public boolean readRecord(List<String> rowValues);
	}
}
