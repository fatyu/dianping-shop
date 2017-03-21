package ren.xiayi.dianping.shop.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ren.xiayi.dianping.shop.dao.NetbarDao;
import ren.xiayi.dianping.shop.dao.QueryDao;
import ren.xiayi.dianping.shop.entity.Netbar;

/**
 *
 * 网吧网咖商户数据操作Service
 * @author fatyu
 */
@Component
public class NetbarService {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(NetbarService.class);
	@Autowired
	private NetbarDao netbarDao;

	@Autowired
	private QueryDao queryDao;

	public void save(Netbar netbar) {
		netbarDao.save(netbar);
	}

	/**
	 * @param region 地区编号
	 * @param category 数据类别
	 * @param type=rank
	 * @param city 城市id
	 * @param p 获取数据页数
	 */
	public void fetchNetbarInfos(long region, long category, String type, long city, int p) {
		try {
			Thread.sleep(RandomUtils.nextInt(1000) + 100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			logger.error("input is [" + city + "," + region + "," + type + "," + p + "]");
		}
		int timeout = 1000;
		String baseInfoUrl = "http://dpindex.dianping.com/dpindex?region=" + region + "&category=" + category + "&type="
				+ type + "&city=" + city + "&p=" + p;
		Document doc;
		try {
			doc = Jsoup.connect(baseInfoUrl).timeout(timeout).get();//超时时间1s
			Element outerDiv = doc.getElementsByAttributeValue("class", "idxmain-rank").get(1);
			if (outerDiv != null) {
				Elements lis = outerDiv.getElementsByTag("li");
				if (lis.size() > 0) {
					for (Element li : lis) {
						Elements a = li.getElementsByTag("a");
						Element element = a.get(0);
						String href = element.attr("href");
						String shopId = StringUtils.substringAfterLast(href, "/");
						Elements fieldName = element.getElementsByClass("field-name");
						Element netbarName = fieldName.get(0);
						String netbarTitle = netbarName.attr("title");
						String name = StringUtils.substringBefore(netbarTitle, "(");
						String subName = StringUtils.substringBetween(netbarTitle, "(", ")");
						Elements netbarStreets = element.getElementsByClass("field-addr");
						Element netbarStreet = netbarStreets.get(0);
						String street = netbarStreet.attr("title");
						logger.info("netbar-info is :{\"id\":\"" + shopId + "\",\"name\":\"" + name
								+ "\",\"subname\":\"" + subName + "\",\"street\":\"" + street + "\"}");
						Netbar netbar = new Netbar();
						netbar.setId(NumberUtils.toLong(shopId));
						netbar.setName(name);
						netbar.setSubName(subName);
						netbar.setDpUrl(href);
						netbar.setStreetName(street);
						netbarDao.save(netbar);
					}
					p++;
					fetchNetbarInfos(region, category, type, city, p);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadAllNetbarInfo() {
		List<Map<String, Object>> areas = queryDao.queryMap("select cid,id from area");
		for (Map<String, Object> area : areas) {
			long cid = NumberUtils.toLong(area.get("cid").toString());
			long id = NumberUtils.toLong(area.get("id").toString());
			fetchNetbarInfos(id, 20042, "rank", cid, 1);
			logger.info(" 完成了[cid:" + cid + ",id:" + id + "]的数据抓取");
		}
	}

}
