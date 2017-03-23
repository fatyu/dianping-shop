package ren.xiayi.dianping.shop.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
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
import ren.xiayi.dianping.shop.utils.HttpConnectionUtil;
import ren.xiayi.dianping.shop.utils.JsonUtils;

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

	/**
	 * 获取网吧的详细信息
	 * http://www.dianping.com/shop/77312616 店铺基本信息
	 * http://www.dianping.com/ajax/json/shoppic/find?type=all_new&shopId=77312616&firstPos=0&count=100  抓取商铺图片
	 *  店铺所有评论
	 * @param shopId 网吧id
	 */
	public void fetchNetbarDetailInfos(Netbar netbar) {
		int timeout = 1000;
		String baseInfoUrl = "http://www.dianping.com/shop/" + netbar.getId();
		Document doc;
		try {
			doc = Jsoup.connect(baseInfoUrl).timeout(timeout).get();//超时时间1s
			Element basicInfo = doc.getElementById("basic-info");//获取基本信息
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
			String commentCount = basicInfo.getElementById("reviewCount").text();//回复评论数量
			String avgCost = basicInfo.getElementById("avgPriceTitle").text();//人均价格
			String address = basicInfo.getElementsByClass("expand-info address").get(0).getElementsByClass("item")
					.get(0).attr("title");
			String telephone = "";
			Elements tels = basicInfo.getElementsByClass("expand-info tel").get(0).getElementsByClass("item");
			for (Element element : tels) {
				telephone = telephone + " " + element.text();
			}
			netbar.setAddress(address);
			netbar.setPhone(telephone);
			netbar.setScore(avgScoreVal);
			netbar.setCommentCount(NumberUtils.toInt(StringUtils.substringBefore(commentCount, "条")));
			netbar.setAvgCost(NumberUtils.toInt(StringUtils.substringBetween(avgCost, "：", "元")));
			Element map = doc.getElementById("map");//获取经纬度;
			Elements imgs = map.getElementsByTag("img");
			if (imgs.size() > 0) {
				String href = imgs.get(0).attr("src");
				String ll = StringUtils.substringAfterLast(href, "|");
				String[] llArr = StringUtils.split(ll, ",");

				netbar.setQqLat(NumberUtils.toDouble(llArr[0]));
				netbar.setQqLon(NumberUtils.toDouble(llArr[1]));

				String coords = netbar.getLon() + "," + netbar.getLat();
				try {
					double[] lonLat = convert(coords);
					netbar.setLat(lonLat[0]);
					netbar.setLon(lonLat[1]);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			this.save(netbar);//保存网吧数据
			fetchNetbarImgs(netbar);
			fetchNetbarComments(netbar);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	private double[] convert(String coords) {
		//进行编码转换
		//http://api.map.baidu.com/geoconv/v1/?coords=114.21892734521,29.57542977892&ak=cofuT2iu779FwXsa61jUpxEq4xGufR4s&output=json&from=3
		CloseableHttpClient client = HttpConnectionUtil.getHttpClient();
		String llJson = getLLJson(client, coords);
		Map<String, Object> map = JsonUtils.stringToObject(llJson, Map.class);
		Map<String, Object> msg = (Map<String, Object>) map.get("result");
		List<Map<String, Object>> imgs = (List<Map<String, Object>>) map.get("result");
		double[] ll = new double[2];
		for (Map<String, Object> i : imgs) {
			String x = i.get("x").toString();
			String y = i.get("y").toString();
			Long imgId = NumberUtils.toLong(StringUtils.substringAfterLast(i.get("href").toString(), "/"));
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
		HttpGet get = new HttpGet(url);
		CloseableHttpResponse execute = null;
		try {
			execute = client.execute(get);
			HttpEntity entity = execute.getEntity();
			String string = toString(entity, Charset.defaultCharset());
			if (null == entity) {
				return null;
			} else if (execute.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				return null;
			} else {
				return string;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (execute != null) {
					execute.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 1.使用httpclient获取返回数据json
	 * 2.使用jackson进行string->map转换
	 * 3.保存数据库
	 */
	@SuppressWarnings("unchecked")
	public void fetchNetbarImgs(Netbar netbar) {
		CloseableHttpClient client = HttpConnectionUtil.getHttpClient();
		Long shopId = netbar.getId();
		String json = getCommentJson(client, shopId);
		Map<String, Object> map = JsonUtils.stringToObject(json, Map.class);
		Map<String, Object> msg = (Map<String, Object>) map.get("msg");
		List<Map<String, Object>> imgs = (List<Map<String, Object>>) msg.get("img");
		for (Map<String, Object> i : imgs) {
			String imgUrl = i.get("full").toString();
			Long imgId = NumberUtils.toLong(StringUtils.substringAfterLast(i.get("href").toString(), "/"));
			System.out.println(imgId + "-------->" + imgUrl);
			//			Img img = new Img();
			//			img.setId(imgId);
			//			img.setNid(shopId);
			//			img.setUrl(imgUrl);
			//			imgService.save(img);
		}
	}

	/**
	 * 获取评论json数据
	 * @param httpclient
	 * @return json字符串
	 */
	private String getCommentJson(CloseableHttpClient client, long shopId) {
		HttpGet get = new HttpGet("http://www.dianping.com/ajax/json/shoppic/find?type=all_new&shopId=" + shopId
				+ "&firstPos=0&count=100");
		CloseableHttpResponse execute = null;
		try {
			execute = client.execute(get);
			HttpEntity entity = execute.getEntity();
			String string = toString(entity, Charset.defaultCharset());
			if (null == entity) {
				return null;
			} else if (execute.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				return null;
			} else {
				return string;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (execute != null) {
					execute.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取http请求响应的内容
	 */
	private String toString(final HttpEntity entity, final Charset defaultCharset) throws IOException, ParseException {
		final InputStream instream = entity.getContent();
		if (instream == null) {
			return null;
		}
		try {
			Args.check(entity.getContentLength() <= Integer.MAX_VALUE,
					"HTTP entity too large to be buffered in memory");
			int i = (int) entity.getContentLength();
			if (i < 0) {
				i = 4096;
			}
			Charset charset = null;
			try {
				final ContentType contentType = ContentType.get(entity);
				if (contentType != null) {
					charset = contentType.getCharset();
				}
			} catch (final UnsupportedCharsetException ex) {
				throw new UnsupportedEncodingException(ex.getMessage());
			}
			if (charset == null) {
				charset = defaultCharset;
			}
			if (charset == null) {
				charset = HTTP.DEF_CONTENT_CHARSET;
			}
			final Reader reader = new InputStreamReader(instream, charset);
			final CharArrayBuffer buffer = new CharArrayBuffer(i);
			final char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
			return buffer.toString();
		} finally {
			instream.close();
		}
	}

	/**
	 * 20 一页,判断获取的数据量是不是等于20,如果是的,加载下一页
	 * @param netbar
	 */
	private void fetchNetbarComments(Netbar netbar) {
		int timeout = 1000;
		String baseInfoUrl = "http://www.dianping.com/shop/" + netbar.getId() + "/review_more?pageno=1";
		Document doc;
		try {
			doc = Jsoup.connect(baseInfoUrl).timeout(timeout).get();//超时时间1s
			Element basicInfo = doc.getElementById("basic-info");//获取基本信息
			Element map = doc.getElementById("map");//获取经纬度;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		NetbarService netbarService = new NetbarService();
		Netbar netbar = new Netbar();
		netbar.setId(69155418L);
		netbarService.fetchNetbarDetailInfos(netbar);
	}
}
