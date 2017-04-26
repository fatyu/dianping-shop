package ren.xiayi.dianping.shop.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
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
		String url = "http://dpindex.dianping.com/ajax/categorylist?cityid=1&shopids=";
		return HttpConnectionUtil.get(client, url);
	}

}
