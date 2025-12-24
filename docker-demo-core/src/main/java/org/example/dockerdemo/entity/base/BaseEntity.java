package org.example.dockerdemo.entity.base;

import lombok.Data;
import org.example.dockerdemo.enums.DeleteFlag;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@MappedSuperclass
public class BaseEntity {
    @Id
    @Column(length = 36)
    private String id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "delete_flag")
    private DeleteFlag deleteFlag = DeleteFlag.undeleted;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        LocalDateTime now = LocalDateTime.now();
        if (this.createTime == null) this.createTime = now;
        if (this.updateTime == null) this.updateTime = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    public void preInsert() {
        prePersist();
    }
}
