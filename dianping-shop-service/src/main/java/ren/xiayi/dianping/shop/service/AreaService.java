package ren.xiayi.dianping.shop.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.impl.client.CloseableHttpClient;
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
		reloadAreaAndStreetByCity(cityId, cityName);
	}

	/**
	 * 根据城市获取城市下属区域数据
	 */
	public void reloadAllArea() {
		Iterable<City> cities = cityService.findAll();
		for (City city : cities) {
			reloadAreaAndStreetByCity(city.getId(), city.getName());
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
	private void reloadAreaAndStreetByCity(long cid, String cname) {
		String json = loadAreaAndStreetInfos(HttpConnectionUtil.getDirectHttpClient(), cid);
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
	 * 获取指定城市下属地区(市和街道或者商区信息)json数据
	 * @param httpclient
	 * @return json字符串
	 * dpindex没有做IP访问频次限制,故使用直连方式
	 */
	private String loadAreaAndStreetInfos(CloseableHttpClient client, long cid) {
		String uri = "http://dpindex.dianping.com/ajax/regionlist?cityid=" + cid + "&shopids=";
		return HttpConnectionUtil.get(client, uri);
	}

	/**
	 * 获取所有的区域信息
	 * @return
	 */
	public List<Map<String, Object>> queryAllAreaMap() {
		return queryDao.queryMap("select cid,id from area order by cid");
	}
}
