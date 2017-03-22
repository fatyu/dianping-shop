package ren.xiayi.dianping.shop.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import ren.xiayi.dianping.shop.dao.NetbarDao;
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

	public void save(Netbar netbar) {
		netbarDao.save(netbar);
	}

	public void save(List<Netbar> netbar) {
		netbarDao.save(netbar);
	}

	/**
	 * @param region 地区编号
	 * @param category 数据类别
	 * @param type=rank
	 * @param city 城市id
	 * @param p 获取数据页数
	 */
	public boolean fetchNetbarInfos(long region, long category, String type, long city, int p) {

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
					List<Netbar> netbars = Lists.newArrayList();
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
						netbar.setCid(city);
						netbar.setAid(region);
						netbars.add(netbar);
					}
					if (netbars.size() > 0) {
						save(netbars);
						return true;
					}
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	//http://www.dianping.com/shop/77312616 店铺基本信息
	//http://www.dianping.com/ajax/json/shoppic/find?type=all_new&typeId=116&shopId=77312616&shopType=30&full=0&href=1&firstPos=1&count=100  抓取商铺图片
	//http://www.dianping.com/shop/77312616/review_more 店铺所有评论
}
