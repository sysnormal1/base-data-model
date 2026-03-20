package com.sysnormal.data.base_data_model.repositories;

import com.sysnormal.data.base_data_model.entities.BaseCommonEntity;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface BaseCommonRepository<E extends BaseCommonEntity, ID> extends BaseRepository<E, ID> {

    Optional<E> findByIdAtOrigin(String idAtOrigin);

}
