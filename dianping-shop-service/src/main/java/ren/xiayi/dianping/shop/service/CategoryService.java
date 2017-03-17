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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ren.xiayi.dianping.shop.dao.CategoryDao;
import ren.xiayi.dianping.shop.entity.Category;
import ren.xiayi.dianping.shop.utils.HttpConnectionUtil;
import ren.xiayi.dianping.shop.utils.JsonUtils;

/**
 *
 * 类别数据操作Service
 * @author fatyu
 */
@Component
public class CategoryService {
	@Autowired
	private CategoryDao categoryDao;

	public void save(Category category) {
		categoryDao.save(category);
	}

	/**
	 * 1.使用httpclient获取返回数据json
	 * 2.使用jackson进行string->map转换
	 * 3.保存数据库
	 */
	@SuppressWarnings("unchecked")
	public void reloadCategories() {
		CloseableHttpClient client = HttpConnectionUtil.getHttpClient();
		String json = getJson(client);
		Map<String, Object> map = JsonUtils.stringToObject(json, Map.class);
		Map<String, Object> msg = (Map<String, Object>) map.get("msg");
		List<Map<String, Object>> cates = (List<Map<String, Object>>) msg.get("categoryids");
		for (Map<String, Object> cate : cates) {
			String name = cate.get("name").toString();
			Long id = NumberUtils.toLong(cate.get("key").toString());
			Category category = new Category(id, name);
			this.save(category);
			List<Map<String, Object>> subCates = (List<Map<String, Object>>) cate.get("children");
			for (Map<String, Object> subCate : subCates) {
				long subId = NumberUtils.toLong(subCate.get("key").toString());
				Category subCat = new Category(subId, subCate.get("name").toString(), id);
				this.save(subCat);
			}
		}
	}

	/**
	 * 获取点评的分类json数据
	 * @param httpclient
	 * @return json字符串
	 */
	private String getJson(CloseableHttpClient client) {
		HttpGet get = new HttpGet("http://dpindex.dianping.com/ajax/categorylist?cityid=1&shopids=");
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

}
