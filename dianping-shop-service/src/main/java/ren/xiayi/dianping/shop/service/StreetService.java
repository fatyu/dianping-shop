package ren.xiayi.dianping.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ren.xiayi.dianping.shop.dao.StreetDao;
import ren.xiayi.dianping.shop.entity.Street;

/**
 *
 * 街区数据操作Service
 * @author fatyu
 */
@Component
public class StreetService {
	@Autowired
	private StreetDao streetDao;

	public void save(Street street) {
		streetDao.save(street);
	}

}
