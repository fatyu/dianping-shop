package ren.xiayi.dianping.shop.web.controller;

import org.springframework.web.servlet.ModelAndView;

public class BaseController {

	public void backIndex(ModelAndView mv, String msg) {
		mv.addObject("sitemesh", "N");
		mv.addObject("msg", msg);
		mv.setViewName("forward:/");
	}

	public void backError(ModelAndView mv, String msg, String url) {
		mv.addObject("sitemesh", "N");
		mv.addObject("backUrl", url);
		mv.addObject("msg", msg);
		mv.setViewName("error/error");
	}

	public ModelAndView backPage(ModelAndView mv, String msg, String url) {
		mv.addObject("msg", msg);
		mv.setViewName(url);
		return mv;
	}

}
