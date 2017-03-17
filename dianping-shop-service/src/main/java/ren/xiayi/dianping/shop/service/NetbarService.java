package ren.xiayi.dianping.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ren.xiayi.dianping.shop.dao.NetbarDao;
import ren.xiayi.dianping.shop.entity.Netbar;

/**
 *
 * 网吧网咖商户数据操作Service
 * @author fatyu
 */
@Component
public class NetbarService {
	@Autowired
	private NetbarDao netbarDao;

	public void save(Netbar netbar) {
		netbarDao.save(netbar);
	}
}
