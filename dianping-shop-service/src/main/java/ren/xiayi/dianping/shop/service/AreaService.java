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

import org.apache.commons.lang.math.NumberUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ren.xiayi.dianping.shop.dao.AreaDao;
import ren.xiayi.dianping.shop.dao.QueryDao;
import ren.xiayi.dianping.shop.entity.Area;
import ren.xiayi.dianping.shop.entity.City;
import ren.xiayi.dianping.shop.entity.Street;
import ren.xiayi.dianping.shop.utils.HttpConnectionUtil;
import ren.xiayi.dianping.shop.utils.JsonUtils;

/**
 *
 * 地区数据操作Service
 * @author fatyu
 */
@Component
public class AreaService {

	private static final Logger logger = LoggerFactory.getLogger(CityService.class);
	@Autowired
	private AreaDao areaDao;
	@Autowired
	private StreetService streetService;
	@Autowired
	private CityService cityService;

	@Autowired
	private QueryDao queryDao;

	public void save(Area area) {
		areaDao.save(area);
	}

	public void reloadByCity(long cityId, String cityName) {
		reloadAreaByCity(cityId, cityName);
	}

	public void reloadAllArea() {
		Iterable<City> cities = cityService.findAll();
		for (City city : cities) {
			reloadAreaByCity(city.getId(), city.getName());
		}
	}

	/**
	 * 1.使用httpclient获取返回数据json
	 * 2.使用jackson进行string->map转换
	 * 3.保存数据库
	 * @param cityId 城市id
	 * @param cityName 城市名称
	 */
	@SuppressWarnings("unchecked")
	private void reloadAreaByCity(long cid, String cname) {
		CloseableHttpClient client = HttpConnectionUtil.getHttpClient();
		String json = getJson(client, cid);
		Map<String, Object> map = JsonUtils.stringToObject(json, Map.class);
		Map<String, Object> msg = (Map<String, Object>) map.get("msg");
		List<Map<String, Object>> areas = (List<Map<String, Object>>) msg.get("regionids");
		int i = 0;
		for (Map<String, Object> area : areas) {
			logger.info("process:  " + cname + "总计" + areas.size() + "个区,准备更新第 " + i + "个区数据");
			String name = area.get("name").toString();
			Long id = NumberUtils.toLong(area.get("key").toString());
			Area a = new Area(id, name);
			a.setCid(cid);
			this.save(a);
			List<Map<String, Object>> streets = (List<Map<String, Object>>) area.get("children");
			for (Map<String, Object> street : streets) {
				long sId = NumberUtils.toLong(street.get("key").toString());
				Street s = new Street(sId, street.get("name").toString(), id);
				streetService.save(s);
			}
			logger.info("-------------------------------------------------------------------------------");
			i++;
		}
	}

	/**
	 * 获取点评的分类json数据
	 * @param httpclient
	 * @return json字符串
	 */
	private String getJson(CloseableHttpClient client, long cid) {
		HttpGet get = new HttpGet("http://dpindex.dianping.com/ajax/regionlist?cityid=" + cid + "&shopids=");
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

	public List<Map<String, Object>> queryAllAreaMap() {
		return queryDao.queryMap("select cid,id from area order by cid");
	}
}
