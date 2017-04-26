package ren.xiayi.dianping.shop.web.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ren.xiayi.dianping.shop.entity.JsonResponseMsg;
import ren.xiayi.dianping.shop.entity.Netbar;
import ren.xiayi.dianping.shop.service.NetbarService;

@Controller
@RequestMapping("/reload")
public class NetbarImgController extends BaseController {

	@Autowired
	private NetbarService netbarService;

	private static Logger logger = LoggerFactory.getLogger(NetbarImgController.class);

	@RequestMapping(value = "netbarImgs")
	@ResponseBody
	public JsonResponseMsg netbarImgs(int s) {
		JsonResponseMsg res = new JsonResponseMsg();
		long count = netbarService.countNotInImg();
		long maxPage = count / 50;
		for (int i = s; i < maxPage; i++) {
			logger.info("netbarImgs current page is :" + i);
			int start = i * 50;
			int end = 50;
			List<Map<String, Object>> netbars = netbarService.queryLimitNotInImg(start, end);
			for (Map<String, Object> bar : netbars) {
				long id = NumberUtils.toLong(bar.get("id").toString());
				Netbar netbar = netbarService.findById(id);
				netbarService.fetchNetbarImgs(netbar, true);
				logger.info(">|||||||||||||||||||||||||||||||||>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>fetch netbar ["
						+ id + "] imgs finished!");
				try {
					Thread.sleep(RandomUtils.nextInt(500) + 200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			logger.info("finished img fetch ---------------------------------- page [" + i + "]");
		}
		res.fill(0, "success");
		logger.info(">>>>>>>>>>>>>>>>>all netbar imgs info finished---------------------------------]");
		return res;
	}

}
