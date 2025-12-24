package org.example.dockerdemo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.dockerdemo.entity.TestUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TestUserMapper {

    TestUser findByUsernameAndAge(@Param("username") String username, @Param("age") Integer age);

    List<TestUser> findPage(@Param("offset") int offset, @Param("limit") int limit);

    int insert(TestUser user);

    int update(TestUser user);

    int logicalDelete(@Param("id") String id);
}
