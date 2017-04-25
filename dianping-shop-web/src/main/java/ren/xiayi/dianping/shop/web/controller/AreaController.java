package ren.xiayi.dianping.shop.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ren.xiayi.dianping.shop.entity.JsonResponseMsg;
import ren.xiayi.dianping.shop.service.AreaService;

@Controller
@RequestMapping("/reload")
public class AreaController extends BaseController {

	@Autowired
	private AreaService areaService;

	@RequestMapping(value = "area")
	@ResponseBody
	public JsonResponseMsg area() {
		JsonResponseMsg res = new JsonResponseMsg();
		areaService.reloadAllArea();//重新加载所有区域数据并保存数据库
		res.fill(0, "success");
		return res;
	}

}
