package com.tz.demo1.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.tz.demo1.WebSecurityConfig;
import com.tz.demo1.entity.User;
@Component
public class LoginInterceptor implements HandlerInterceptor{

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception {
		HttpSession httpSession=request.getSession();
	     User user=(User) httpSession.getAttribute(WebSecurityConfig.SESSION_KEY);
	     if(user!=null){
	    	 return true;
	     }else {
	    	 response.sendRedirect("/login");
	    	 return false;
		}
		
	}

}
