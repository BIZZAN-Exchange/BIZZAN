package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * @author Hevin  E-mail:bizzanhevin@gmail.com
 * @date 2020年02月10日
 */
public interface CountryDao extends JpaRepository<Country,String>,JpaSpecificationExecutor<Country>,QueryDslPredicateExecutor<Country> {
    @Query("select a from Country a where a.delFlag=0 order by a.sort")
    List<Country> findAllOrderBySort();

    Country findByZhName(String zhname);
}
