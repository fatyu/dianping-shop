package ren.xiayi.dianping.shop.web.controller;

import java.util.List;
import java.util.Map;

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
import ren.xiayi.dianping.shop.service.NetbarService;

@Controller
@RequestMapping("/reload")
public class NetbarInfoController extends BaseController {

	@Autowired
	private AreaService areaService;

	@Autowired
	private NetbarService netbarService;

	private static Logger logger = LoggerFactory.getLogger(NetbarInfoController.class);

	/**
	 * 获取所有网吧列表数据,保存基础的id 名称 等信息
	 * @return
	 */
	@RequestMapping(value = "netbarList")
	@ResponseBody
	public JsonResponseMsg netbarList() {
		JsonResponseMsg res = new JsonResponseMsg();
		List<Map<String, Object>> areas = areaService.queryAllAreaMap();
		for (Map<String, Object> area : areas) {
			long cid = NumberUtils.toLong(area.get("cid").toString());
			long id = NumberUtils.toLong(area.get("id").toString());
			logger.info("start------------------------------------- is [" + cid + "," + id + "," + "rank" + "]");
			for (int p = 1; p < 50; p++) {
				boolean continue_ = netbarService.fetchNetbarInfos(id, 20042, "rank", cid, p);
				if (continue_) {
					continue;
				} else {
					break;
				}
			}
			logger.info("end------------------------------------- is [" + cid + "," + id + "," + "rank" + "]");
		}
		res.fill(0, "success");
		return res;
	}

	/**
	 * 获取网吧的详情信息
	 * @param s 所有网吧数据分页的开始页
	 * @return
	 */
	@RequestMapping(value = "netbarDetail")
	@ResponseBody
	public JsonResponseMsg netbarDetail(int s) {
		JsonResponseMsg res = new JsonResponseMsg();
		long count = netbarService.count();
		long maxPage = count / 50;
		for (int i = s; i < maxPage; i++) {
			logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>current page is :" + i);
			int start = i * 50;
			int end = 50;
			List<Map<String, Object>> netbars = netbarService.queryLimit(start, end);
			for (Map<String, Object> area : netbars) {
				long id = NumberUtils.toLong(area.get("id").toString());
				Netbar netbar = netbarService.findById(id);
				if (StringUtils.isBlank(netbar.getAddress())) {
					netbarService.fetchNetbarDetailInfos(netbar);
					logger.info("update---------------------------------- is [" + id + "]");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			logger.info("finished---------------------------------- page [" + i + "]");
		}
		res.fill(0, "success");
		logger.info(">>>>>>>>>>>>>>>>>all netbar update detail info finished---------------------------------]");
		return res;
	}

	/**
	 * 根据地址信息为空,重新获取网吧信息
	 * @return
	 */
	@RequestMapping(value = "netbarDetailRefreshByAddressIsNull")
	@ResponseBody
	public JsonResponseMsg netbarDetailRefreshByAddressIsNull() {
		JsonResponseMsg res = new JsonResponseMsg();
		long left = 0;
		long count = netbarService.count(true);
		left = count;
		long maxPage = count / 50;
		for (int i = 0; i <= maxPage; i++) {
			logger.info(">netbarDetailRefreshByAddressIsNull>>>>>>>>>>>>>>>>>>>>>>>>>>>current page is :" + i);
			int start = 0;
			int end = 50;
			List<Map<String, Object>> netbars = netbarService.queryLimitByAddressIsNull(start, end);
			for (Map<String, Object> area : netbars) {
				long id = NumberUtils.toLong(area.get("id").toString());
				Netbar netbar = netbarService.findById(id);
				if (StringUtils.isBlank(netbar.getAddress())) {
					boolean result = netbarService.fetchNetbarDetailInfos(netbar);
					if (result) {
						left = left - 1;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				logger.info("total num is " + count
						+ ">>>>>>>>>>>>>>>>>left data to update num is ::::::::::::::::::::>" + left);
			}
			logger.info(
					"finished-netbarDetailRefreshByAddressIsNull--------------------------------- page [" + i + "]");
		}
		res.fill(0, "success");
		logger.info(
				">netbarDetailRefreshByAddressIsNull>>>>>>>>>>>>>>>>all netbar update detail info finished---------------------------------]");
		return res;
	}

	/**
	 * 根据无效geo信息,重新获取网吧信息
	 * @return
	 */
	@RequestMapping(value = "netbarDetailRefreshByGeo")
	@ResponseBody
	public JsonResponseMsg netbarDetailRefreshByGeo() {
		JsonResponseMsg res = new JsonResponseMsg();
		long count = netbarService.count(true);
		long maxPage = count / 50;
		for (int i = 0; i < maxPage; i++) {
			logger.info(">netbarDetailRefreshByAddressIsNull>>>>>>>>>>>>>>>>>>>>>>>>>>>current page is :" + i);
			int start = i * 50;
			int end = 50;
			List<Map<String, Object>> netbars = netbarService.queryLimitByGeo(start, end);
			for (Map<String, Object> area : netbars) {
				long id = NumberUtils.toLong(area.get("id").toString());
				Netbar netbar = netbarService.findById(id);
				if (StringUtils.isBlank(netbar.getAddress())) {
					netbarService.fetchNetbarDetailInfos(netbar);
					logger.info("update---------------------------------- is [" + id + "]");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			logger.info(
					"finished-netbarDetailRefreshByAddressIsNull--------------------------------- page [" + i + "]");
		}
		res.fill(0, "success");
		logger.info(
				">netbarDetailRefreshByAddressIsNull>>>>>>>>>>>>>>>>all netbar update detail info finished---------------------------------]");
		return res;
	}

}
