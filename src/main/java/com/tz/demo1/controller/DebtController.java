package com.tz.demo1.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.tz.demo1.WebSecurityConfig;
import com.tz.demo1.entity.User;
import com.tz.demo1.service.CountService;
import com.tz.demo1.utils.Util;

@Controller
public class DebtController {
	@Autowired
	private CountService service;

	@GetMapping("/")
	public String get() {
		
		return "view";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@PostMapping("/loginPost")
	public @ResponseBody Map<String, Object> loginPost(String account, String password, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		if (!(("123456".equals(password))&&("admin".equals(account)))) {
			map.put("success", false);
			map.put("message", "密码错误");
			return map;
		
		}
        User user=new User();
        user.setAccout(account);
        user.setPassword(password);
		// 设置session
		session.setAttribute(WebSecurityConfig.SESSION_KEY, user);

		map.put("success", true);
		map.put("message", "登录成功");
		return map;
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		// 移除session
		session.removeAttribute(WebSecurityConfig.SESSION_KEY);
		return "redirect:/login";
		
	}

	@PostMapping("/")
	public synchronized void upload(@RequestParam("file") MultipartFile file, @RequestParam("sex") String sex,
			@RequestParam("census") String census, @RequestParam("low") String low, @RequestParam("high") String high,
			HttpServletResponse response, @RequestParam("age") String age, @RequestParam("days") String days,
			@RequestParam("sum") String sum, @RequestParam("overTime") String overTime)
			throws IOException, MessagingException {

		double l = Double.parseDouble(low);
		double h = Double.parseDouble(high);

		service.deal(file, Util.dealStr(sex), Util.dealStr(census), l, h, response, Util.dealStr(age), days, sum,
				Util.dealStr(overTime));

	}

	/*
	 * 暂时不用
	 * 
	 * @PostMapping("/sendMail") public String sendMail(@RequestParam("file")
	 * MultipartFile file) throws MessagingException, IOException { String
	 * fileName = file.getOriginalFilename(); System.out.println(fileName);
	 * service.sendMail(file, fileName);
	 * 
	 * return "mail"; }
	 */
}
