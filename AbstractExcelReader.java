package com.cml.excel.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * excel文件读取抽象类，完成excel多个sheet迭代读取
 * 
 * @author 陈孟琳
 *
 *         2015年6月11日
 */
public abstract class AbstractExcelReader {

	private static final Integer SHEET_FIRST = 0;

	private Workbook excelBook;
	private Sheet currentSheet;

	public AbstractExcelReader(Workbook excelBook)
			throws FileNotFoundException, IOException {
		this.excelBook = excelBook;
		int sheets = excelBook.getNumberOfSheets();
		// 没有内容
		if (sheets == 0) {
			throw new IllegalArgumentException("sheet size can not be 0");
		}
		currentSheet = excelBook.getSheetAt(SHEET_FIRST);
	}

	/**
	 * 
	 * @param recordListener
	 * @param skipLine
	 *            每个sheet中跳过的行数
	 * @throws IOException
	 */
	public void readRecord(OnReadRecordListener recordListener, int skipLine)
			throws IOException {

		try {

			int sheets = excelBook.getNumberOfSheets();

			for (int index = 0; index < sheets; index++) {

				// 获取当前sheet
				currentSheet = getSheetAt(index);

				// 读取sheet数据
				readSheetData(recordListener, index, skipLine);

			}

		} finally {
			excelBook.close();
		}

	}

	/**
	 * 读取sheet数据
	 * 
	 * @param recordListener
	 * @param sheetIndex
	 */
	private void readSheetData(OnReadRecordListener recordListener,
			int sheetIndex, int skipLine) {

		// sheet行数
		int rowNum = currentSheet.getLastRowNum();

		// 读取每行数据
		for (int i = skipLine; i < rowNum; i++) {

			Row row = currentSheet.getRow(i);

			List<String> cellData = new ArrayList<String>(row.getLastCellNum());

			Iterator<Cell> it = row.cellIterator();

			// 读取一行数据
			while (it.hasNext()) {
				String v = this.loadCellValue(it.next());
				cellData.add(v);
			}

			boolean result = recordListener.readRecord(cellData, sheetIndex, i);

			// 停止读取
			if (!result) {
				break;
			}
		}
	}

	private Sheet getSheetAt(int i) {
		return excelBook.getSheetAt(i);
	}

	/**
	 * 将单元格数据转换成string类型
	 * 
	 * @param cellType
	 * @param cell
	 * @return
	 */
	protected String loadCellValue(Cell cell) {

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
		 * 文件读取每一行回调信息，抛出任何异常表示文件读取失败，文件停止读取，注意捕获异常信息
		 * 
		 * @param value
		 * @param sheetIndex
		 *            当前sheet
		 * @return true 继续读取，false 退出当前sheet读取，继续下一个sheet读取
		 */
		public boolean readRecord(List<String> rowValues, int sheetIndex,
				int rowNum);
	}
}
