package ren.xiayi.dianping.shop.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;

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
	@Autowired
	private CityDao cityDao;

	public void save(City city) {
		cityDao.save(city);
	}

	/**
	 * 1.使用httpclient获取返回数据xml
	 * 2.使用进行xml字符串->Object转换
	 * 3.保存数据库
	 */
	@SuppressWarnings("unchecked")
	public void reloadCityInfos() {
		CloseableHttpClient client = HttpConnectionUtil.getHttpClient();
		String xml = getXml(client);
		XStream xStream = XStreamFactory.getXStream();
		xStream.alias("citys", List.class);
		xStream.alias("city", City.class);
		List<City> citys = (List<City>) xStream.fromXML(xml);
		for (City city : citys) {
			this.save(city);
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
