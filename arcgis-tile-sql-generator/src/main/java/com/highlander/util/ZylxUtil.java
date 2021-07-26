package com.highlander.util;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.github.stuxuhai.jpinyin.PinyinHelper;

/**
 * 作业类型表里的数据整合到字典表中
 * 
 * @author w
 *
 */
public class ZylxUtil {

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        try {
            String sql = "SELECT * FROM [dbo].[T_ZYLX]";
            ResultSet rs = DatabaseUtil.executeQuery(sql);
            StringBuilder dictSql = new StringBuilder("INSERT INTO [vts_base].[dbo].[SYS_DICT] ( [TYPE_CODE], [TYPE_NAME]) VALUES ");
            StringBuilder dictDataSql = new StringBuilder("INSERT INTO [vts_base].[dbo].[SYS_DICT_DATA] ([TYPE_CODE], [ITEM_CODE], [ITEM_NAME]) VALUES ");
            Map<String, String> codeMap = new HashMap<>();
            while (rs.next()) {
                String typeCode = rs.getString("type_code");
                String typeName = rs.getString("type_name");
                String itemCode = rs.getString("item_code");
                String itemName = rs.getString("item_name");

                if (!codeMap.containsKey(typeCode)) {
                    String shortName = PinyinHelper.getShortPinyin(typeName).toUpperCase();
                    String newTypeCode = String.format("JOB_TYPE_%s_%s", shortName, typeCode);
                    String dictStr = String.format("('%s', '%s'),\n", newTypeCode, typeName);
                    dictSql.append(dictStr);
                    codeMap.put(typeCode, newTypeCode);
                }

                if (itemName != null && itemName.length() > 0) {
                    String newTypeCode = codeMap.get(typeCode);
                    
//                    INSERT INTO [vts_base].[dbo].[SYS_DICT_DATA] ([ID], [TYPE_CODE], [ITEM_CODE], [ITEM_NAME]) VALUES ('1', 'REGION_TYPE', '1', '行政区域');
                    String dictStr = String.format(" ('%s', '%s', '%s'),\n", newTypeCode, itemCode, itemName);
                    dictDataSql.append(dictStr);
                }
            }

            String dict = dictSql.toString().replaceAll(",$", ";");
            String dictData = dictDataSql.toString().replaceAll(",$", ";");
            System.out.println(dict);
            System.out.println(dictData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
