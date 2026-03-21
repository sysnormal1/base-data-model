package com.sysnormal.data.base_data_model.repositories;

import com.sysnormal.data.base_data_model.entities.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E extends BaseEntity, ID> extends JpaRepository<E, ID>, JpaSpecificationExecutor<E> {

}