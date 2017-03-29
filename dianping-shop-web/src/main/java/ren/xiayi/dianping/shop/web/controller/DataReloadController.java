package ren.xiayi.dianping.shop.web.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ren.xiayi.dianping.shop.entity.JsonResponseMsg;
import ren.xiayi.dianping.shop.entity.Netbar;
import ren.xiayi.dianping.shop.service.AreaService;
import ren.xiayi.dianping.shop.service.CategoryService;
import ren.xiayi.dianping.shop.service.CityService;
import ren.xiayi.dianping.shop.service.NetbarService;

@Controller
@RequestMapping("/reload")
public class DataReloadController extends BaseController {
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CityService cityService;

	@Autowired
	private AreaService areaService;

	@Autowired
	private NetbarService netbarService;

	private static Logger logger = LoggerFactory.getLogger(DataReloadController.class);

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

	@RequestMapping(value = "netbarList")
	@ResponseBody
	public JsonResponseMsg netbarList() {
		JsonResponseMsg res = new JsonResponseMsg();
		List<Map<String, Object>> areas = areaService.queryAllAreaMap();
		for (Map<String, Object> area : areas) {
			long cid = NumberUtils.toLong(area.get("cid").toString());
			long id = NumberUtils.toLong(area.get("id").toString());
			logger.error("start------------------------------------- is [" + cid + "," + id + "," + "rank" + "]");
			for (int p = 1; p < 50; p++) {
				boolean continue_ = netbarService.fetchNetbarInfos(id, 20042, "rank", cid, p);
				if (continue_) {
					continue;
				} else {
					break;
				}
			}
			logger.error("end------------------------------------- is [" + cid + "," + id + "," + "rank" + "]");
		}
		res.fill(0, "success");
		return res;
	}

	@RequestMapping(value = "netbarDetail")
	@ResponseBody
	public JsonResponseMsg netbarDetail(int s) {
		JsonResponseMsg res = new JsonResponseMsg();
		long count = netbarService.count();
		long maxPage = count / 50;
		for (int i = s; i < maxPage; i++) {
			System.out.println(
					"|||||||||||||||||||||||||||||||||||||||||||||||||||||>>>>>>>>>>>>>>>>>>>>>>>>>>>>current page is :"
							+ i);
			int start = i * 50;
			int end = 50;
			List<Map<String, Object>> netbars = netbarService.queryLimit(start, end);
			for (Map<String, Object> area : netbars) {
				long id = NumberUtils.toLong(area.get("id").toString());
				Netbar netbar = netbarService.findById(id);
				if (StringUtils.isBlank(netbar.getAddress())) {
					netbarService.fetchNetbarDetailInfos(netbar);
					logger.error("update---------------------------------- is [" + id + "]");
					try {
						Thread.sleep(RandomUtils.nextInt(500) + 200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			logger.error("finished---------------------------------- page [" + i + "]");
		}
		res.fill(0, "success");
		logger.error(">>>>>>>>>>>>>>>>>all netbar update detail info finished---------------------------------]");
		return res;
	}

	@RequestMapping(value = "netbarComments")
	@ResponseBody
	public JsonResponseMsg netbarComment(int s) {
		JsonResponseMsg res = new JsonResponseMsg();
		long count = netbarService.count();
		long maxPage = count / 50;
		for (int i = s; i < maxPage; i++) {
			System.out.println("current page is :" + i);
			int start = i * 50;
			int end = 50;
			List<Map<String, Object>> netbars = netbarService.queryLimit(start, end);
			for (Map<String, Object> area : netbars) {
				long id = NumberUtils.toLong(area.get("id").toString());
				Netbar netbar = netbarService.findById(id);
				if (StringUtils.isBlank(netbar.getAddress())) {
					netbarService.fetchNetbarComments(netbar, 1);
					logger.error("fetch netbar [" + id + "] comment finished!");
					try {
						Thread.sleep(RandomUtils.nextInt(500) + 200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			logger.error("finished comment fetch ---------------------------------- page [" + i + "]");
		}
		res.fill(0, "success");
		logger.error(">>>>>>>>>>>>>>>>>all netbar comment info finished---------------------------------]");
		return res;
	}

}
