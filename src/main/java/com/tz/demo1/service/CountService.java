package com.tz.demo1.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
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

	

	public void deal(MultipartFile file, String level2, String census, double low, double high,
			HttpServletResponse response, String age, String days, String sum, String overTime)
			throws IOException, MessagingException {
	  //存放结果一
		ArrayList<Debt> list = new ArrayList<>();
		//存放结果二
		ArrayList<Debt> result=new ArrayList<>();
		ArrayList<Debt> result1=new ArrayList<>();
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
			
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {

				Row row = sheet.getRow(i);
				// Excel表中的性别，户籍，进入案池时间，余额，证件信息
				String level = row.getCell(2).getStringCellValue();
				//逾期
				String time = row.getCell(0).getStringCellValue();
				String hometown = row.getCell(9).getStringCellValue();
				//进入案池时间
				String days1 = row.getCell(3).getStringCellValue();
				// 余额
				double balance = row.getCell(5).getNumericCellValue();
				String ID = row.getCell(7).getStringCellValue();
				/*
				 * 判断这些信息是否和理
				 */

				if (Util.judge(census, hometown) && Util.judge(level2, level)  && Util.judgeId(ID, age)
						&& Util.judgeTime(days, days1)) {
					
					Debt debt = new Debt();
					debt.setBalance(balance);
					debt.setOverTime(time);
					debt.setClientCode(row.getCell(11).getStringCellValue());
					debt.setCode("Q72");
					debt.setDays(row.getCell(3).getStringCellValue());
					debt.setOrg("深创联");
					list.add(debt);
				} else {
					continue;
				}
			}
			
	

			
			HashMap<String, ArrayList<Debt>> map=new HashMap<>();
			
			for(Debt debt:list){
				String clientCode=debt.getClientCode();
				if(map.containsKey(clientCode)){
					ArrayList<Debt> temp=map.get(clientCode);
					temp.add(debt);
					map.put(clientCode, temp);
				}else{
					ArrayList<Debt> temp=new ArrayList<>();
					temp.add(debt);
					map.put(clientCode, temp);
				}
			}
			     
			for(Map.Entry<String, ArrayList<Debt>> entry:map.entrySet()){
				if(entry.getValue().size()==1){
					String time=entry.getValue().get(0).getOverTime();
					double balance1=entry.getValue().get(0).getBalance();
					boolean flag=(balance1>=low&&balance1<=high&&Util.judge(overTime, time));
					if(flag){
						result.add(entry.getValue().get(0));
						
					}
				}else {
					ArrayList<Debt> temp=entry.getValue();
					boolean flag=false;
					for(Debt debt:temp){
						String time=debt.getOverTime();
						double balance1=debt.getBalance();
						if(balance1>=low&&balance1<=high&&Util.judge(overTime, time)){
							flag=true;
							break;
						}
					}
					if(flag){
						for(Debt debt:temp){
							result.add(debt);
							
						}
					}
				}
			}
			int total=0;
		   
		   for(Debt debt:result){
		     double balance=debt.getBalance();
		     if(total+balance<Integer.parseInt(sum)){
		       total+=balance;
		       result1.add(debt);
		     }else {
              break;
            }
		     
		   }
			

		} else {
			System.out.println("格式错误");
		}

		// Debt[] newList = Util.sort(list);
		HSSFWorkbook wb = new HSSFWorkbook();

		HSSFSheet hsheet = wb.createSheet("邮件汇总");
		hsheet.setColumnWidth(0, 230 * 20);
		hsheet.setColumnWidth(1, 180 * 20);
		hsheet.setColumnWidth(2, 180 * 20);
		hsheet.setColumnWidth(3, 180 * 20);
		HSSFRow titlerRow = hsheet.createRow(0);
		titlerRow.createCell(0).setCellValue("主持卡人代码");
		titlerRow.createCell(1).setCellValue("余额（人民币）");
		titlerRow.createCell(2).setCellValue("机构简称");
		titlerRow.createCell(3).setCellValue("抢案代码");

		for (Debt debts :result1  ) {
			int lastRowNum = hsheet.getLastRowNum();
			HSSFRow dataRow = hsheet.createRow(lastRowNum + 1);
			dataRow.createCell(0).setCellValue(debts.getClientCode());
			dataRow.createCell(1).setCellValue(debts.getBalance());
			dataRow.createCell(2).setCellValue(debts.getOrg());
			dataRow.createCell(3).setCellValue(debts.getCode());

		}
		/*
		 * 生成的Excel文件名
		 */
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
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
	/*
	 * 发带附件的邮件
	 

	public void sendMail(MultipartFile file, String fileName) throws MessagingException, IOException {
		System.setProperty("mail.mime.splitlongparameters", "false");
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(sender);
		helper.setTo(receiver);
		helper.setSubject(fileName);
		helper.setText("这是一封带附件的邮件", true);
		helper.addAttachment(fileName, file);
        
		javaMailSender.send(message);

	}
	*/

}
