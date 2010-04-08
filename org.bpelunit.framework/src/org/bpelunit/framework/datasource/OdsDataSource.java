package org.bpelunit.framework.datasource;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

public class OdsDataSource implements DataSource {

	private static final int DEFAULT_SHEET_INDEX = 0;
	private static final int MAX_CELL_SEARCH_DEPTH = 10;
	private OdfTable table;
	private int headingRowIndex;
	private List<String> headings;
	private int sheetIndex;
	private int firstCellIndex;
	private int currentRowIndex = headingRowIndex;
	private OdfTableRow currentRow;

	@ConfigurationOption(defaultValue = "" + DEFAULT_SHEET_INDEX, description = "The number of the sheet in which the test data reside. Counting starts with 1 for the first sheet.")
	public void setSheet(String index) {
		checkIfMayAlterConfiguration();
		try {
			int sheetIndex = Integer.parseInt(index) - 1;
			if (sheetIndex < 0) {
				throw new IllegalArgumentException(
						"Sheet Index must be a positive number");
			}
			this.sheetIndex = sheetIndex;
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Sheet Index must be a positive number");
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] getFieldNames() {
		return headings.toArray(new String[headings.size()]);
	}

	@Override
	public String[] getSupportedContentTypes() {
		return new String[] { "application/vnd.oasis.opendocument.spreadsheet",
				"application/x-vnd.oasis.opendocument.spreadsheet" };
	}

	@Override
	public String getValueFor(String fieldName) {
		int cellIndex = headings.indexOf(fieldName);
		cellIndex += firstCellIndex;
		return currentRow.getCellByIndex(cellIndex).getStringValue();
	}

	@Override
	public boolean next() {
		this.currentRow = this.table.getRowByIndex(currentRowIndex + 1);
		String firstCellValue = currentRow.getCellByIndex(this.firstCellIndex)
				.getStringValue();
		if (!StringUtils.isEmpty(firstCellValue)) {
			this.currentRowIndex++;
			return true;
		} else {
			return false;
		}

	}

	@Override
	public void setData(String data) throws InvalidDataSourceException {
		throw new InvalidDataSourceException(
				"Inline sources are not allowed for Open Document Spreadsheets (e.g. OpenOffice.org) files");
	}

	@Override
	public void setSource(String uri) throws InvalidDataSourceException {
		OdfDocument ods;
		try {
			ods = OdfSpreadsheetDocument.loadDocument(uri);
			table = ods.getTableList().get(sheetIndex);
			extractFieldNames();
		} catch (InvalidDataSourceException e) {
			throw e;
		} catch (Exception e) {
			throw new InvalidDataSourceException("Could not open " + uri, e);
		}
	}

	private void extractFieldNames() throws InvalidDataSourceException {
		List<String> headings = new ArrayList<String>();
		OdfTableRow headingRow = table.getRowByIndex(headingRowIndex);

		firstCellIndex = findFirstCellIndex(headingRow);

		if (firstCellIndex < 0) {
			throw new InvalidDataSourceException("No headings found at row "
					+ headingRowIndex);
		}

		int i = firstCellIndex;
		do {
			String heading = headingRow.getCellByIndex(i).getStringValue();
			headings.add(heading);
			i++;
		} while (!StringUtils.isEmpty(headingRow.getCellByIndex(i)
				.getStringValue()));

		this.headings = headings;
		this.currentRowIndex = this.headingRowIndex;
	}

	private int findFirstCellIndex(OdfTableRow headingRow) {
		int firstCellIndex = 0;
		while (StringUtils.isEmpty(headingRow.getCellByIndex(firstCellIndex)
				.getStringValue())) {
			if (firstCellIndex > MAX_CELL_SEARCH_DEPTH) {
				return -1;
			}

			firstCellIndex++;
		}
		return firstCellIndex;
	}

	private void checkIfMayAlterConfiguration() {
		if (table != null) {
			throw new IllegalStateException(
					"Data has already been loaded; must not alter settings anymore!");
		}
	}
}
