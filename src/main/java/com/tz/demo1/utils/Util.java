package com.tz.demo1.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.tz.demo1.entity.Debt;

public class Util {

	public static Debt[] sort (ArrayList<Debt> list){
		Debt[]debts=new Debt[list.size()];
		for (int i = 0; i < debts.length; i++) {
			debts[i]=list.get(i);
		}
		Arrays.sort(debts, new Comparator<Debt>() {

		

			@Override
			public int compare(Debt o1, Debt o2) {
				String date1=o1.getDays();
				String date2=o2.getDays();
				String[]arr1=date1.split("/");
				String[]arr2=date1.split("/");
				for (int i = 0; i < arr1.length; i++) {
					int a1=Integer.parseInt(arr1[i]);
					int a2=Integer.parseInt(arr2[i]);
					if(a1>a2){
						return 1;
						
					}else if(a1<a2){
						return -1;
						
					}else{
						continue;
					}
				}
				return 0;
			}
		});
		return debts;
		
	}
}
