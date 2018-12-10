package com.tz.demo1.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tz.demo1.entity.Debt;
import com.tz.demo1.utils.Util;

@Service
public class CountService {
  private static final String SUFFIX_2003 = ".xls";
  private static final String SUFFIX_2007 = ".xlsx";
  @Value("${mail.fromMail.sender}")
  private String sender;

  @Value("${mail.fromMail.receiver}")
  private String receiver;

  @Autowired
  private JavaMailSender javaMailSender;



  public void deal(MultipartFile file, String sex2, String census, double low, double high,
      HttpServletResponse response, String age, String days, String sum, String overTime) throws IOException {
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
        String time=row.getCell(0).getStringCellValue();
        String hometown = row.getCell(7).getStringCellValue();
        String days1 = row.getCell(3).getStringCellValue();
        //余额
        double principle = row.getCell(5).getNumericCellValue();
        String ID = row.getCell(8).getStringCellValue();
        /*
         * 判断这些信息是否和理
         */

        if (Util.judge(census, hometown) && Util.judge(sex2, sex)&&Util.judge(overTime, time)
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
      if (total >= Integer.parseInt(sum)) {
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
    hsheet.setColumnWidth(0, 230*20);
    hsheet.setColumnWidth(1, 180*20);
    hsheet.setColumnWidth(2, 180*20);
    hsheet.setColumnWidth(3, 180*20);
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
    /*
     * 发带附件的邮件
     */
    MimeMessage message=javaMailSender.createMimeMessage();
    try {
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(sender);
        helper.setTo(receiver);
        helper.setSubject("附件邮件");
        helper.setText("这是一封带附件的邮件", true);
        helper.addAttachment(fileName, file);
       

        javaMailSender.send(message);
       
    } catch (MessagingException e) {
        
    }


  }

}
