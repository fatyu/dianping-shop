package ren.xiayi.dianping.shop.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import ren.xiayi.dianping.shop.entity.Street;

public interface StreetDao extends PagingAndSortingRepository<Street, Long>, JpaSpecificationExecutor<Street> {

}