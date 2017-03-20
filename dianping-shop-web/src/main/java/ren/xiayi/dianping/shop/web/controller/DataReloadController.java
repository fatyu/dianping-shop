package ren.xiayi.dianping.shop.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ren.xiayi.dianping.shop.entity.JsonResponseMsg;
import ren.xiayi.dianping.shop.service.AreaService;
import ren.xiayi.dianping.shop.service.CategoryService;
import ren.xiayi.dianping.shop.service.CityService;

@Controller
@RequestMapping("/reload")
public class DataReloadController extends BaseController {
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CityService cityService;

	@Autowired
	private AreaService areaService;

	@RequestMapping(value = "city")
	@ResponseBody
	public JsonResponseMsg city() {
		JsonResponseMsg res = new JsonResponseMsg();
		cityService.reloadCityInfos();
		res.fill(0, "success");
		return res;

	}

	@RequestMapping(value = "category")
	@ResponseBody
	public JsonResponseMsg category() {
		JsonResponseMsg res = new JsonResponseMsg();
		categoryService.reloadCategories();
		res.fill(0, "success");
		return res;
	}

	@RequestMapping(value = "area")
	@ResponseBody
	public JsonResponseMsg area() {
		JsonResponseMsg res = new JsonResponseMsg();
		areaService.reloadAllArea();
		res.fill(0, "success");
		return res;
	}

}
