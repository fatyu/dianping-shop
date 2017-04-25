package ren.xiayi.dianping.shop.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

import ren.xiayi.dianping.shop.dao.CityDao;
import ren.xiayi.dianping.shop.entity.City;
import ren.xiayi.dianping.shop.utils.HttpConnectionUtil;

/**
 *
 * 城市数据操作Service
 * @author fatyu
 */
@Component
public class CityService {

	private static final Logger logger = LoggerFactory.getLogger(CityService.class);
	@Autowired
	private CityDao cityDao;

	public void save(City city) {
		cityDao.save(city);
	}

	public Iterable<City> findAll() {
		return cityDao.findAll();
	}

	/**
	 * 1.使用httpclient获取返回数据xml
	 * 2.使用进行xml字符串->Object转换
	 * 3.保存数据库
	 */
	@SuppressWarnings("unchecked")
	public void reloadCityInfos() {
		String xml = getXml(HttpConnectionUtil.getDirectHttpClient());
		XStream xStream = XStreamFactory.getXStream();
		xStream.alias("citys", List.class);
		xStream.alias("city", City.class);
		List<City> citys = (List<City>) xStream.fromXML(xml);
		for (City city : citys) {
			this.save(city);
			logger.info(city.getName() + "|" + city.getEnname() + "|" + city.getId());
		}
	}

	/**
	 * 获取点评的分类json数据
	 * @param httpclient
	 * @return json字符串
	 */
	private String getXml(CloseableHttpClient client) {
		HttpGet get = new HttpGet("http://api.t.dianping.com/n/base/cities.xml");
		CloseableHttpResponse execute = null;
		try {
			execute = client.execute(get);
			HttpEntity entity = execute.getEntity();
			String string = HttpConnectionUtil.dataConvertToString(entity, Charset.defaultCharset());
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

	@SuppressWarnings("rawtypes")
	public static XStream setAliasType(XStream xStream, String type, Class clazz) {
		xStream.aliasType(type, clazz);
		return xStream;
	}

	static class XStreamFactory {
		public static XStream getXStream() {
			return new XStream(new DomDriver("UTF8", new XmlFriendlyNameCoder("-_", "_")));
		}
	}

	public static void main(String[] args) {
		CityService s = new CityService();
		s.reloadCityInfos();
	}
}
