package ren.xiayi.dianping.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ren.xiayi.dianping.shop.dao.AreaDao;
import ren.xiayi.dianping.shop.entity.Area;

/**
 *
 * 地区数据操作Service
 * @author fatyu
 */
@Component
public class AreaService {
	@Autowired
	private AreaDao areaDao;

	public void save(Area area) {
		areaDao.save(area);
	}

}
