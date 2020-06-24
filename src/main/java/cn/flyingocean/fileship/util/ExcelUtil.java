package cn.flyingocean.fileship.util;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;


public class ExcelUtil {
    public static class ParseResult{
        // 列数
        public int colNum;
        // 行数
        public int rowNum;
        // 完整结果矩阵
        public String[][] rMatrix;
        // 表头
        public String[] header;

        public ParseResult(int colNum,int rowNum,String[][] rMatrix){
            this.colNum = colNum;
            this.rowNum = rowNum;
            this.rMatrix = rMatrix;
        }
        public ParseResult(int colNum,String[] header){
            this.colNum = colNum;
            this.header = header;
        }
    }

    /**
     * 取表头
     * @param fp
     * @return
     * @throws IOException
     */
    public static ParseResult fetchHeader(String fp) throws IOException{
        // 获得excel工作簿
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(fp));

        // 获得第一张工作页
        XSSFSheet sheet = wb.getSheetAt(0);
        // 获取列数
        XSSFRow endRow = sheet.getRow(0);// the last row
        final int colNum = endRow.getPhysicalNumberOfCells();
        // 构造二维数组即返回的结果矩阵
        String[] rMatrix = new String[colNum];

        // 取当前工作页(sheet)的第一行，默认它就是表头
        XSSFRow firstRow = sheet.getRow(0);
        XSSFCell currentCell = null;
        for (int i=0;i<colNum;i++){
            currentCell = firstRow.getCell(i);
            currentCell.setCellType(CellType.STRING);
            rMatrix[i] = currentCell.getStringCellValue();
        }

        wb.close();
        return new ParseResult(colNum,rMatrix);
    }

    /**
     * 取完整结果
     * @param fp
     * @return
     * @throws IOException
     */
    public static ParseResult parse(String fp) throws IOException {

        // 获得excel工作簿
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(fp));

        // 获得第一张工作页
        XSSFSheet sheet = wb.getSheetAt(0);
        // 获取行数
        final int rowNum = sheet.getLastRowNum()+1;
        // 获取列数
        XSSFRow endRow = sheet.getRow(0);// the last row
        final int colNum = endRow.getPhysicalNumberOfCells();
        // 构造二维数组即返回的结果矩阵
        String[][] rMatrix = new String[rowNum][colNum];

        // 遍历当前工作页(sheet)
        XSSFCell currentCell = null;
        XSSFRow currentRow = null;
        for (int i=0;i<rowNum;i++){

            currentRow = sheet.getRow(i);

            for (int j=0;j<colNum;j++){
                currentCell = currentRow.getCell(j);
                currentCell.setCellType(CellType.STRING);
                rMatrix[i][j] = currentCell.getStringCellValue();
            }
        }

        wb.close();
        return new ParseResult(colNum,rowNum,rMatrix);
    }

}
