package ren.xiayi.dianping.shop.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
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
		String json = getCategoryJson(HttpConnectionUtil.getDirectHttpClient());
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
	private String getCategoryJson(CloseableHttpClient client) {
		HttpGet get = new HttpGet("http://dpindex.dianping.com/ajax/categorylist?cityid=1&shopids=");
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

}
