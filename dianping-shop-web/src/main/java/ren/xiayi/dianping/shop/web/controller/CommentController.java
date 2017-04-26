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
public class CommentController extends BaseController {

	@Autowired
	private NetbarService netbarService;

	private static Logger logger = LoggerFactory.getLogger(CommentController.class);

	@RequestMapping(value = "netbarComments")
	@ResponseBody
	public JsonResponseMsg netbarComment(int s) {
		long count = netbarService.count();
		long maxPage = count / 50;
		for (int i = s; i < maxPage; i++) {
			logger.info("netbarComments current page is :" + i);
			int start = i * 50;
			int end = 50;
			List<Map<String, Object>> netbars = netbarService.queryLimit(start, end);
			for (Map<String, Object> area : netbars) {
				long id = NumberUtils.toLong(area.get("id").toString());
				Netbar netbar = netbarService.findById(id);
				netbarService.fetchNetbarComments(netbar, 1);
				logger.info("fetch netbar [" + id + "] comment finished!");
				try {
					Thread.sleep(RandomUtils.nextInt(500) + 200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			logger.info("finished comment fetch ---------------------------------- page [" + i + "]");
		}
		logger.info(">>>>>>>>>>>>>>>>>all netbar comment info finished---------------------------------]");
		return new JsonResponseMsg().fill(0, "success");
	}

	@RequestMapping(value = "netbarCommentsFix")
	@ResponseBody
	public JsonResponseMsg netbarCommentFix() {
		long count = netbarService.countHasCommentNetbar();
		long maxPage = count / 50;
		for (int i = 0; i < maxPage; i++) {
			logger.info("netbarComments current page is :" + i);
			int start = i * 50;
			int end = 50;
			List<Map<String, Object>> netbars = netbarService.queryHasCommentLimit(start, end);
			for (Map<String, Object> area : netbars) {
				long id = NumberUtils.toLong(area.get("id").toString());
				Netbar netbar = netbarService.findById(id);
				netbarService.fetchNetbarComments(netbar, 1);
				logger.info("fetch netbar [" + id + "] comment finished!");
				try {
					Thread.sleep(RandomUtils.nextInt(500) + 200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			logger.info("finished comment fetch ---------------------------------- page [" + i + "]");
		}
		logger.info(">>>>>>>>>>>>>>>>>all netbar comment info finished---------------------------------]");
		return new JsonResponseMsg().fill(0, "success");
	}

}
