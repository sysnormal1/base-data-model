package com.sysnormal.libs.db.entities.base_entities;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseCommonEntityWithParent<T extends BaseCommonEntityWithParent<T>> extends BaseCommonEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private T parent;

}
