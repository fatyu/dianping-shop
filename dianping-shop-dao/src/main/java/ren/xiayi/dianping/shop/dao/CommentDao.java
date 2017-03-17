package ren.xiayi.dianping.shop.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import ren.xiayi.dianping.shop.entity.Comment;

public interface CommentDao extends PagingAndSortingRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

}