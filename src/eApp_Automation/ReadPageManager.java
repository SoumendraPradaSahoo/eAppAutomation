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

public class ReadPageManager {
	public static String[] getLocators(String fileName, String sheetname, String rowName) throws IOException 
	{
	String[] returnString = new String[5];
	String Field_Type = "";	
	String Identifier = "";
	String Locator = "";
	String Client_Side_Message_Identifier = "";
	String Client_Side_Message_Locator = "";
	try {
	
	FileInputStream excelFile = new FileInputStream(new File(fileName));
	XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
	XSSFSheet TestCase_Sheet = workbook.getSheet(sheetname);
	int rowCount = 0;
	Row currentRow;
	Cell currentCell;
	Iterator<Row> iterator = TestCase_Sheet.iterator();
	while (iterator.hasNext()) 
	{
		currentRow = TestCase_Sheet.getRow(rowCount);
		currentCell = currentRow.getCell(0);
		if (currentCell.getStringCellValue().equalsIgnoreCase(rowName))
		{
			//System.out.println(currentCell.getStringCellValue());
			Field_Type = currentRow.getCell(1).getStringCellValue();
			Identifier = currentRow.getCell(2).getStringCellValue();
			Locator = currentRow.getCell(3).getStringCellValue();
			
			try{
			Client_Side_Message_Identifier = currentRow.getCell(4).getStringCellValue();}
			catch (Exception e){
				Log.error("Error in getting Client_Side_Message_Identifier in getLocators in ReadPageManager Class");
				Log.error(e.toString());
			}
			
			try{
			Client_Side_Message_Locator = currentRow.getCell(5).getStringCellValue();}
			catch (Exception e){
				Log.error("Error in getting Client_Side_Message_Locator in getLocators in ReadPageManager Class");
				Log.error(e.toString());
			}
			returnString[0] = Field_Type;
			returnString[1] = Identifier;
			returnString[2] = Locator;
			returnString[3] = Client_Side_Message_Identifier;
			returnString[4] = Client_Side_Message_Locator;
			workbook.close();
			excelFile.close();
			Log.info("Locator for " + rowName + " in page " + sheetname);
			Log.info(returnString);
			return returnString;
		}
		rowCount++;
		iterator.next();
	}
	workbook.close();
	excelFile.close();
	return returnString;
	}
catch (Exception e){
	Log.error("Error in ReadPageManager Class");
	e.printStackTrace();
	Log.error(e.toString());
	return null;	
}
}
}