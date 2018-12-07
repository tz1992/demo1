package com.tz.demo1.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tz.demo1.service.CountService;
@Controller
public class DebtController {
	@Autowired
	private CountService service;
	
@GetMapping("/")
	public String get(){
		return "view";
	}

@PostMapping("/")
public void upload(@RequestParam("file")MultipartFile file,
		RedirectAttributes redirectAttributes,@RequestParam("sex") String sex,@RequestParam("census") String census,
		@RequestParam("low") String low,@RequestParam("high") String high,HttpServletResponse response) throws IOException{

double l =Double.parseDouble(low);
double h=Double.parseDouble(high);
 
	service.deal(file,sex,census,l,h,response);
	
}
}
