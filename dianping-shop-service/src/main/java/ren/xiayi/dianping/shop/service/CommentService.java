package ren.xiayi.dianping.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ren.xiayi.dianping.shop.dao.CommentDao;
import ren.xiayi.dianping.shop.entity.Comment;

/**
 *
 * 评论数据操作Service
 * @author fatyu
 */
@Component
public class CommentService {
	@Autowired
	private CommentDao commentDao;

	public void save(Comment comment) {
		commentDao.save(comment);
	}

}
