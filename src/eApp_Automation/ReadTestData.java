package eApp_Automation;

import utility.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadTestData {
	
	//Method to Get the Total No of Cases to Execute
		public static int getTestCases(String fileName) throws IOException 
		{
			FileInputStream excelFile = new FileInputStream(new File(fileName));
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet TestCase_Sheet = workbook.getSheet("TestCase");
			Iterator<Row> iterator = TestCase_Sheet.iterator();
			int rowCount = 0;
			while (iterator.hasNext()) 
			{
				rowCount++;
				iterator.next();
			}
			workbook.close();
			excelFile.close();
			Log.info("Total no of case: " + (rowCount-1));
			return rowCount-1;
		}

		//Method to Get if Particular test case needs to be executed
		public static String[] getExecutable(String fileName, int row_num)
		{
			FileInputStream excelFile;
			try {
			excelFile = new FileInputStream(new File(fileName));
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet TestCase_Sheet = workbook.getSheet("TestCase");
			String[] returnString = new String[2];
			workbook.close();
			excelFile.close();
			Row currentRow = TestCase_Sheet.getRow(row_num);
			Cell testCaseNo = currentRow.getCell(1);
			Cell currentCell = currentRow.getCell(2);
			if (currentCell.getStringCellValue().equalsIgnoreCase("Yes")) 
			{
				returnString[0]=testCaseNo.getStringCellValue();
				returnString[1]=currentCell.getStringCellValue();
				Log.info(returnString);
				return returnString;
			}
			else
			{
				returnString[0]=testCaseNo.getStringCellValue();
				returnString[1]="No";
				Log.info(returnString);
				return returnString;
			}
			} catch (Exception e) {
				Log.error("Error in getExecutable");
				e.printStackTrace();
				Log.error(e);
				return null;
			}
		}
	
	//Method to get the Coloumn no of the test case to be executed
	public static int getTestCaseColumnNo(String fileName, String testcaseno) throws IOException 
	{
		try{
		FileInputStream excelFile = new FileInputStream(new File(fileName));
		XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
		XSSFSheet TestCase_Sheet = workbook.getSheet("TestData (2)");
		workbook.close();
		excelFile.close();
		int columnNo=0;
		Cell currentCell;
		Iterator<Cell> cellIter = TestCase_Sheet.getRow(1).cellIterator();
		while (cellIter.hasNext()) 
		{
			currentCell = (Cell) cellIter.next();
			if (currentCell.getStringCellValue().equalsIgnoreCase(testcaseno))
			{
				return columnNo;
			}
			columnNo++;
		}
		return 0;
		}
		catch(Exception e)
		{
			Log.error("Error in getTestCaseColumnNo");
			e.printStackTrace();
			Log.error(e);
			return 0;
		}
	}
	
	//Method to get the total no of rows in the test data sheet
		public static int getTotalNoOfRows(String fileName) throws IOException 
		{
			FileInputStream excelFile = new FileInputStream(new File(fileName));
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			XSSFSheet TestCase_Sheet = workbook.getSheet("TestData (2)");
			workbook.close();
			excelFile.close();
			Iterator<Row> iterator = TestCase_Sheet.iterator();
			int rowCount = 0;
			while (iterator.hasNext()) 
			{
				rowCount++;
				iterator.next();
			}
			return rowCount-1;
		}
	
	//Method to get the test data of the test case to be executed
	public static String[] getTestCaseData(String fileName, int testcaseno, int RowNo) throws IOException 
	{
		try{
		String[] returnString = new String[4];
		FileInputStream excelFile = new FileInputStream(new File(fileName));
		XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
		XSSFSheet TestCase_Sheet = workbook.getSheet("TestData (2)");
		workbook.close();
		excelFile.close();
		Row currentRow = TestCase_Sheet.getRow(RowNo);
		returnString[0] = currentRow.getCell(0).getStringCellValue();
		returnString[1] = currentRow.getCell(1).getStringCellValue();
		returnString[2] = currentRow.getCell(2).getStringCellValue();
		returnString[3] = "";
		try{
		if (currentRow.getCell(testcaseno) != null){
		returnString[3] = currentRow.getCell(testcaseno).getStringCellValue();
		}}
		catch (Exception e) {
			Log.error("Error in getting test data. Null value is returned for test data");
			e.printStackTrace();
			Log.error(e.toString());
		}
		Log.info("Test Case Data");
		Log.info(returnString);
		return returnString;
		}
		catch (Exception e){
			Log.error("Error in getTestCaseData");
			e.printStackTrace();
			Log.error(e.toString());
			return null;
		}
	}
}
