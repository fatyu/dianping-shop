package ren.xiayi.dianping.shop.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import ren.xiayi.dianping.shop.entity.Img;

public interface ImgDao extends PagingAndSortingRepository<Img, Long>, JpaSpecificationExecutor<Img> {

}