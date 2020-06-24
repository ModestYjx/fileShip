package cn.flyingocean.fileship.util;

import org.junit.Test;



public class ExcelUtilTest {

    @Test
    public void parse() throws Exception {
        ExcelUtil.ParseResult parseResult = ExcelUtil.parse("C:\\Users\\胡伟\\Desktop\\DIR\\软工1611班级信息统计表 - 副本.xlsx");
        System.out.println(parseResult.colNum+"-"+parseResult.rowNum);
        for (int i=0;i<parseResult.rowNum;i++){
            for (int j=0;j<parseResult.colNum;j++){
                System.out.print(parseResult.rMatrix[i][j]);
                System.out.print("-");
            }
            System.out.println();
        }
    }

    @Test
    public void fetchHeader() throws Exception {
        ExcelUtil.ParseResult parseResult = ExcelUtil.fetchHeader("C:\\Users\\胡伟\\Desktop\\DIR\\软工1611班级信息统计表 - 副本.xlsx");
        System.out.println(parseResult.colNum+"-"+parseResult.rowNum);
        for (int i=0;i<parseResult.colNum;i++){
            System.out.print(parseResult.header[i]+"-");
        }
    }
}