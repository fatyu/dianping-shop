package ren.xiayi.dianping.shop.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ren.xiayi.dianping.shop.entity.JsonResponseMsg;
import ren.xiayi.dianping.shop.service.CategoryService;

@Controller
@RequestMapping("/reload")
public class CategoryController extends BaseController {
	@Autowired
	private CategoryService categoryService;

	@RequestMapping(value = "category")
	@ResponseBody
	public JsonResponseMsg category() {
		categoryService.reloadCategories();
		return new JsonResponseMsg().fill(0, "success");
	}

}
