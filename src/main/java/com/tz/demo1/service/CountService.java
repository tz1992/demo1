package com.tz.demo1.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tz.demo1.entity.Debt;
import com.tz.demo1.utils.Util;

@Service
public class CountService {
  private static final String SUFFIX_2003 = ".xls";
  private static final String SUFFIX_2007 = ".xlsx";

  public void deal(MultipartFile file, String sex2, String census, double low, double high,
      HttpServletResponse response, String age, String days) throws IOException {
    ArrayList<Debt> list = new ArrayList<>();
    Workbook workbook = null;
    String originalFilename = file.getOriginalFilename();

    try {
      if (originalFilename.endsWith(SUFFIX_2003)) {
        workbook = new HSSFWorkbook(file.getInputStream());
      } else if (originalFilename.endsWith(SUFFIX_2007)) {
        workbook = new XSSFWorkbook(file.getInputStream());
      }
    } catch (Exception e) {

      e.printStackTrace();

    }

    if (workbook != null) {

      Sheet sheet = workbook.getSheetAt(0);
      double total = 0;
      for (int i = 1; i <= sheet.getLastRowNum(); i++) {

        Row row = sheet.getRow(i);
        // Excel表中的性别，户籍，进入案池时间，余额，证件信息
        String sex = row.getCell(6).getStringCellValue();
        String hometown = row.getCell(7).getStringCellValue();
        String days1 = row.getCell(3).getStringCellValue();
        double principle = row.getCell(14).getNumericCellValue();
        String ID = row.getCell(8).getStringCellValue();
        /*
         * 判断这些信息是否和理
         */

        if (Util.judgeCensus(census, hometown) && (sex.equals(sex2))
            && (principle >= low && principle <= high) && Util.judgeId(ID, age)
            && Util.judgeTime(days, days1)) {
          total += row.getCell(5).getNumericCellValue();
          Debt debt = new Debt();
          debt.setBalance(row.getCell(5).getNumericCellValue());
          debt.setClientCode(row.getCell(17).getStringCellValue());
          debt.setCode("Q72");
          debt.setDays(row.getCell(3).getStringCellValue());
          debt.setOrg("深创联");
          list.add(debt);
        } else {
          continue;
        }
      }
      if (total >= 15000000) {
        for (int i = 0; i < list.size(); i++) {
          Debt debt = list.get(i);
          total -= debt.balance;
          list.remove(i);
          if (total < 15000000) {
            break;
          }
        }
      }



    } else {
      System.out.println("格式错误");
    }

//    Debt[] newList = Util.sort(list);
    HSSFWorkbook wb = new HSSFWorkbook();


    HSSFSheet hsheet = wb.createSheet("邮件汇总");
    HSSFRow titlerRow = hsheet.createRow(0);
    titlerRow.createCell(0).setCellValue("主持卡人代码");
    titlerRow.createCell(1).setCellValue("余额（人民币）");
    titlerRow.createCell(2).setCellValue("机构简称");
    titlerRow.createCell(3).setCellValue("抢案代码");

    for (Debt debts : list) {
      int lastRowNum = hsheet.getLastRowNum();
      HSSFRow dataRow = hsheet.createRow(lastRowNum + 1);
      dataRow.createCell(0).setCellValue(debts.getClientCode());
      dataRow.createCell(1).setCellValue(debts.getBalance());
      dataRow.createCell(2).setCellValue(debts.getOrg());
      dataRow.createCell(3).setCellValue(debts.getCode());

    }

   Calendar calendar=Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH)+1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    String fileName = year + "年" + month + "月" + day + "日Q72抢案邮件";
    fileName = URLEncoder.encode(fileName, "UTF-8");
    response.reset();
    response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xls");
    OutputStream os = response.getOutputStream();
    wb.write(os);
    os.flush();
    os.close();

  }

}
