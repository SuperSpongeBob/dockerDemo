package org.example.dockerdemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.ibatis.type.Alias;
import org.example.dockerdemo.entity.base.BaseEntity;
import org.example.dockerdemo.enums.UserStatus;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "tb_test_user")
@Alias("TestUser")
public class TestUser extends BaseEntity {

    private String username;

    @JsonIgnore
    private String password;

    private String email;

    private Integer age;

    @Enumerated(EnumType.ORDINAL)
    private UserStatus status = UserStatus.ACTIVE;
}
