package ren.xiayi.dianping.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ren.xiayi.dianping.shop.dao.ImgDao;
import ren.xiayi.dianping.shop.entity.Img;

/**
 *
 * 网吧图片数据操作Service
 * @author fatyu
 */
@Component
public class ImgService {
	@Autowired
	private ImgDao imgDao;

	public void save(Img img) {
		imgDao.save(img);
	}

}
