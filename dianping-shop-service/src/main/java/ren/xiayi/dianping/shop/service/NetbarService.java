package ren.xiayi.dianping.shop.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import ren.xiayi.dianping.shop.dao.NetbarDao;
import ren.xiayi.dianping.shop.dao.QueryDao;
import ren.xiayi.dianping.shop.entity.Comment;
import ren.xiayi.dianping.shop.entity.Img;
import ren.xiayi.dianping.shop.entity.Netbar;
import ren.xiayi.dianping.shop.utils.HttpConnectionUtil;
import ren.xiayi.dianping.shop.utils.JsonUtils;
import ren.xiayi.dianping.shop.utils.JsoupUtils;

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
	private CommentService commentService;
	@Autowired
	private ImgService imgService;

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
		String baseInfoUrl = "http://dpindex.dianping.com/dpindex?region=" + region + "&category=" + category + "&type="
				+ type + "&city=" + city + "&p=" + p;
		try {
			Document doc = JsoupUtils.getDirectConnection(baseInfoUrl, 3000, "http://www.dianping.com").get();
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

	/**
	 * 获取网吧的详细信息
	 * http://www.dianping.com/shop/77312616 店铺基本信息
	 * http://www.dianping.com/ajax/json/shoppic/find?type=all_new&shopId=77312616&firstPos=0&count=100  抓取商铺图片
	 *  店铺所有评论
	 * @param shopId 网吧id
	 */
	public boolean fetchNetbarDetailInfos(Netbar netbar) {
		String baseInfoUrl = "http://www.dianping.com/shop/" + netbar.getId();
		try {
			Document doc = JsoupUtils.getProxyConnection(baseInfoUrl, 3000, "http://www.dianping.com").get();
			Element basicInfo = doc.getElementById("basic-info");//获取基本信息
			if (basicInfo != null) {
				//logger.error("netbar detail info html is :" + basicInfo.html());
				Elements score = basicInfo.getElementsByClass("mid-rank-stars");//评分
				String avgScore = score.get(0).attr("title");//分数
				double avgScoreVal = 0;
				//该商户暂无星级
				if (StringUtils.contains(avgScore, "暂无")) {
					avgScoreVal = 0;
				} else if (StringUtils.contains(avgScore, "准四")) {
					avgScoreVal = 3.5;
				} else if (StringUtils.contains(avgScore, "准三")) {
					avgScoreVal = 2.5;
				} else if (StringUtils.contains(avgScore, "准二")) {
					avgScoreVal = 1.5;
				} else if (StringUtils.contains(avgScore, "准一")) {
					avgScoreVal = 0.5;
				} else if (StringUtils.contains(avgScore, "准五")) {
					avgScoreVal = 4.5;
				} else if (StringUtils.contains(avgScore, "五")) {
					avgScoreVal = 5;
				} else if (StringUtils.contains(avgScore, "四")) {
					avgScoreVal = 4;
				} else if (StringUtils.contains(avgScore, "三")) {
					avgScoreVal = 3;
				} else if (StringUtils.contains(avgScore, "二")) {
					avgScoreVal = 2;
				} else if (StringUtils.contains(avgScore, "一")) {
					avgScoreVal = 1;
				}
				Element elementById = basicInfo.getElementById("reviewCount");
				if (null != elementById) {
					String commentCount = elementById.text();//回复评论数量
					netbar.setCommentCount(NumberUtils.toInt(StringUtils.substringBefore(commentCount, "条")));
				}
				Element priceTitle = basicInfo.getElementById("avgPriceTitle");
				if (null != priceTitle) {
					String avgCost = priceTitle.text();//人均价格
					netbar.setAvgCost(NumberUtils.toInt(StringUtils.substringBetween(avgCost, "：", "元")));
				}
				String address = basicInfo.getElementsByClass("expand-info address").get(0).getElementsByClass("item")
						.get(0).attr("title");
				netbar.setAddress(address);
				String telephone = "";
				Elements tels = basicInfo.getElementsByClass("expand-info tel").get(0).getElementsByClass("item");
				for (Element element : tels) {
					telephone = telephone + " " + element.text();
				}
				netbar.setPhone(telephone);
				netbar.setScore(avgScoreVal);
			}
			String html = doc.html();
			String lat = StringUtils.substringBetween(html, "shopGlat: \"", "\",");
			String lon = StringUtils.substringBetween(html, "shopGlng:\"", "\",");

			netbar.setQqLat(NumberUtils.toDouble(lat));
			netbar.setQqLon(NumberUtils.toDouble(lon));

			double qqLon = netbar.getQqLon();
			double qqLat = netbar.getQqLat();
			if (qqLon > 0 && qqLat > 0) {
				String coords = qqLon + "," + qqLat;
				try {
					double[] lonLat = convert(coords);
					netbar.setLat(lonLat[0]);
					netbar.setLon(lonLat[1]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			this.save(netbar);//保存网吧数据
			logger.error(">>>>>>>>>>>>>>>>>>>>抓取网吧{}信息完成", netbar.getId());
			return true;
		} catch (Exception e) {
			logger.error("|||||||||||||||||||||||||||抓取网吧{}信息异常:{}", netbar.getId(), e.getMessage());
			e.printStackTrace();
		} finally {
		}
		return false;
	}

	/**
	 * 经纬度转换(将腾讯地图的经纬度转换为百度经纬度)
	 * @param coords
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private double[] convert(String coords) {
		//进行编码转换
		//http://api.map.baidu.com/geoconv/v1/?coords=114.21892734521,29.57542977892&ak=cofuT2iu779FwXsa61jUpxEq4xGufR4s&output=json&from=3
		String llJson = getLLJson(HttpConnectionUtil.getDirectHttpClient(), coords);
		Map<String, Object> map = JsonUtils.stringToObject(llJson, Map.class);
		List<Map<String, Object>> lls = (List<Map<String, Object>>) map.get("result");
		double[] ll = new double[2];
		for (Map<String, Object> i : lls) {
			String x = i.get("x").toString();
			String y = i.get("y").toString();
			ll[0] = NumberUtils.toDouble(y);
			ll[1] = NumberUtils.toDouble(x);
			break;
		}
		return ll;
	}

	/**
	 * 获取经纬度转换json数据
	 * @param httpclient
	 * @return json字符串
	 */
	private String getLLJson(CloseableHttpClient client, String coords) {
		String url = "http://api.map.baidu.com/geoconv/v1/?coords=" + coords
				+ "&ak=cofuT2iu779FwXsa61jUpxEq4xGufR4s&output=json&from=3";
		return HttpConnectionUtil.get(client, url);
	}

	/**
	 * 1.使用httpclient获取返回数据json
	 * 2.使用jackson进行string->map转换
	 * 3.保存数据库
	 */
	@SuppressWarnings("unchecked")
	public void fetchNetbarImgs(Netbar netbar, boolean useProxy) {
		CloseableHttpClient client;
		if (useProxy) {
			client = HttpConnectionUtil.getProxyHttpClient();
		} else {
			client = HttpConnectionUtil.getDirectHttpClient();
		}
		Long shopId = netbar.getId();
		String json = getNetbarImgJson(client, shopId);
		logger.error("netbar detail info imgs is :" + json);
		if (StringUtils.isNotBlank(json)) {
			try {
				Map<String, Object> map = JsonUtils.stringToObject(json, Map.class);
				Map<String, Object> msg = (Map<String, Object>) map.get("msg");
				if (MapUtils.isNotEmpty(msg)) {
					List<Map<String, Object>> imgs = (List<Map<String, Object>>) msg.get("img");
					for (Map<String, Object> i : imgs) {
						String imgUrl = i.get("full").toString();
						Long imgId = NumberUtils.toLong(StringUtils.substringAfterLast(i.get("href").toString(), "/"));
						logger.info("fetch img>" + imgId + "-------->" + imgUrl);
						Img img = new Img();
						img.setId(imgId);
						img.setNid(shopId);
						img.setUrl(imgUrl);
						imgService.save(img);
					}
				}
			} catch (Exception e) {
				logger.error("fetch imgs data error>>>>>>" + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取网吧图片信息
	 * @param httpclient
	 * @return json字符串
	 */
	private String getNetbarImgJson(CloseableHttpClient client, long shopId) {
		String url = "http://www.dianping.com/ajax/json/shoppic/find?type=all_new&shopId=" + shopId
				+ "&firstPos=0&count=100";
		return HttpConnectionUtil.get(client, url);
	}

	/**
	 * 20 一页,判断获取的数据量是不是等于20,如果是的,加载下一页
	 * @param netbar
	 */
	public void fetchNetbarComments(Netbar netbar, int page) {
		String baseInfoUrl = "http://www.dianping.com/shop/" + netbar.getId() + "/review_more?pageno=" + page;
		try {
			Document doc = JsoupUtils.getProxyConnection(baseInfoUrl, 3000, "http://www.dianping.com").get();
			logger.info("load comment data>>>>>>>>>>>>>>>>>>>>" + doc.html());
			Elements lists = doc.getElementsByClass("comment-list");
			if (CollectionUtils.isNotEmpty(lists)) {

				Element commentContainer = lists.get(0);
				Elements comments = commentContainer.getElementsByTag("li");
				if (CollectionUtils.isNotEmpty(comments)) {
					for (Element e : comments) {
						Comment comment = new Comment();
						long eId = NumberUtils.toLong(e.attr("data-id"));
						comment.setId(eId);
						Elements userInfo = e.getElementsByClass("user-info");
						if (CollectionUtils.isNotEmpty(userInfo)) {
							String[] score = StringUtils.substringsBetween(userInfo.get(0).html(),
									"item-rank-rst irr-star", "\"");
							if (ArrayUtils.isNotEmpty(score)) {
								double commentScore = NumberUtils.toInt(score[0]) / 10;
								comment.setScore(commentScore);
							}
						}
						Elements contentContainer = e.getElementsByClass("J_brief-cont");
						if (CollectionUtils.isNotEmpty(contentContainer)) {
							Element content = contentContainer.get(0);
							String userContent = content.html();
							comment.setComment(userContent);
						}
						comment.setNid(netbar.getId());
						commentService.save(comment);
					}
					if (comments.size() == 20) {
						fetchNetbarComments(netbar, page++);
					}
				}
			}
		} catch (Exception e) {
			logger.error("fetch comments error >>>>>>>>>>>>>>>>>>>>>>:" + e.getMessage());
		}
	}

	@Autowired
	private QueryDao queryDao;

	public long count() {
		return netbarDao.count();
	}

	public long count(boolean unUpdate) {
		if (unUpdate) {
			Number count = queryDao.query("select count(1) from netbar where address is null");
			return count.longValue();
		}
		return netbarDao.count();
	}

	public List<Map<String, Object>> queryLimit(int start, int end) {
		return queryDao.queryMap("select id from netbar order by id  limit " + start + "," + end);
	}

	public Netbar findById(long id) {
		return netbarDao.findOne(id);
	}

	public List<Map<String, Object>> queryLimitByAddressIsNull(int start, int end) {
		return queryDao.queryMap("select id from netbar where address is null order by id  limit " + start + "," + end);
	}

	public List<Map<String, Object>> queryLimitByGeo(int start, int end) {
		return queryDao.queryMap(
				"select id from netbar where qq_lat is null or qq_lat<=0 order by id  limit " + start + "," + end);
	}

	public long countNotInImg() {
		Number count = queryDao.query("select count(1) from netbar where id not in (select distinct nid from img)");
		return count.longValue();
	}

	public List<Map<String, Object>> queryLimitNotInImg(int start, int end) {
		return queryDao
				.queryMap("select id from netbar where  id not in (select distinct nid from img) order by id  limit "
						+ start + "," + end);
	}

	public long countHasCommentNetbar() {
		Number count = queryDao.query(
				"select count(1) from netbar where id not in (select distinct nid from comment) and comment_count>0");
		return count.longValue();
	}

	public List<Map<String, Object>> queryHasCommentLimit(int start, int end) {
		return queryDao.queryMap(
				"select id from netbar where id not in (select distinct nid from comment) and comment_count>0 order by id  limit "
						+ start + "," + end);
	}
}
