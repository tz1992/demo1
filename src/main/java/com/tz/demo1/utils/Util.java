package com.tz.demo1.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.regex.Pattern;

import com.tz.demo1.entity.Debt;

public class Util {

	public static Debt[] sort(ArrayList<Debt> list) {
		Debt[] debts = new Debt[list.size()];
		for (int i = 0; i < debts.length; i++) {
			debts[i] = list.get(i);
		}
		Arrays.sort(debts, new Comparator<Debt>() {

			@Override
			public int compare(Debt o1, Debt o2) {
				String date1 = o1.getDays();
				String date2 = o2.getDays();
				String[] arr1 = date1.split("/");
				String[] arr2 = date2.split("/");
				for (int i = 0; i < arr1.length; i++) {
					int a1 = Integer.parseInt(arr1[i]);
					int a2 = Integer.parseInt(arr2[i]);
					if (a1 > a2) {
						return 1;

					} else if (a1 < a2) {
						return -1;

					} else {
						continue;
					}
				}
				return 0;
			}
		});
		return debts;

	}

	// 判断年龄段
	public static boolean judgeId(String ID, String age) {
		int len = ID.length();
		boolean flagAge = false;
		String[] arr = age.split(",");
		if (len == 18) {

			int arrAge = Integer.parseInt(ID.substring(6, 10));
			Calendar calendar = Calendar.getInstance();
			int ages = calendar.get(Calendar.YEAR) - arrAge;
			for (String s : arr) {
				int first = Integer.parseInt(s.split("-")[0]);
				int second = Integer.parseInt(s.split("-")[1]);
				if (ages <= second && ages >= first) {
					flagAge = true;
					break;

				}
			}
		}

		return flagAge;
	}

	public static boolean judgeTime(String days, String days1) {
		/*
		 * 时间段判断
		 */
		
	 
	  String reg="([0-9]{4})/([0-9]{1,2})/([0-9]{1,2})";
	   
	    if(Pattern.compile(reg).matcher(days1).matches()){
	   // 表中时间
	      String[] arr = days1.split("/");
	        int year = Integer.parseInt(arr[0]);
	        int month = Integer.parseInt(arr[1]);
	        // 选定的时间
	        int year1 = Integer.parseInt(days.split("/")[0]);
	        int month1 = Integer.parseInt(days.split("/")[1]);
	        
	        if (year > year1 || (year == year1 && month >= month1)) {
	            return true;
	        }else {
              return false;
            }
	    }else{
	      return false;
	    }
		
	}

	// 字符串处理
	public static String dealStr(String str) {
		if (str.startsWith("全部")) {
			return str.substring(3);
		} else {
			return str;
		}
	}
     //判断性别，户籍，逾期时间
	public static boolean judge(String arrs, String string) {
		String[] arr = arrs.split(",");
		boolean flag = false;
		for (String s : arr) {
			if (s.equals(string)) {
				flag = true;
				break;
			}
		}
		return flag;
	}
}
