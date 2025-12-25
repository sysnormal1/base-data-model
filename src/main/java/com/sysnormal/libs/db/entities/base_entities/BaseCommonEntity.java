package com.sysnormal.libs.db.entities.base_entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;


@MappedSuperclass
@Getter
@Setter
public abstract class BaseCommonEntity<T extends BaseCommonEntity<T>>  extends BaseEntity implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "updated_at")
    @ColumnDefault("null")
    private LocalDateTime updatedAt;

    @Column(name = "id_at_origin", length = 127)
    private String idAtOrigin;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_sys_rec", nullable = false, length = 1)
    @ColumnDefault("0")
    @Check(constraints = "is_sys_rec in (0,1)")
    private byte isSysRec = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private T parent;

    @Transient
    private boolean isNew = true;

    public void setAsNew() {
        this.isNew = true;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    public void markNotNew() {
        this.isNew = false;
    }

}
