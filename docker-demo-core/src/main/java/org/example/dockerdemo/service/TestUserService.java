package org.example.dockerdemo.service;

import org.example.dockerdemo.entity.TestUser;
import org.example.dockerdemo.enums.DeleteFlag;
import org.example.dockerdemo.mapper.TestUserMapper;
import org.example.dockerdemo.repository.TestUserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestUserService {

    private final TestUserRepository testUserRepository;
    private final TestUserMapper testUserMapper;

    public TestUserService(TestUserRepository testUserRepository, TestUserMapper testUserMapper) {
        this.testUserRepository = testUserRepository;
        this.testUserMapper = testUserMapper;
    }

    public TestUser getById(String id) {
        return testUserRepository.findByIdAndDeleteFlag(id, DeleteFlag.undeleted).orElse(null);
    }

    public TestUser getByUsername(String username) {
        return testUserRepository.findByUsernameAndDeleteFlag(username, DeleteFlag.undeleted).orElse(null);
    }

    public List<TestUser> list(int page, int size) {
        return testUserRepository.findByDeleteFlag(DeleteFlag.undeleted, PageRequest.of(Math.max(page - 1, 0), size))
                .getContent();
    }

    @Transactional
    public TestUser save(TestUser user) {
        return testUserRepository.save(user);
    }

    @Transactional
    public boolean update(TestUser user) {
        if (user.getId() == null || !testUserRepository.existsById(user.getId())) return false;
        testUserRepository.save(user);
        return true;
    }

    @Transactional
    public boolean removeById(String id) {
        return testUserRepository.findById(id)
                .map(user -> {
                    user.setDeleteFlag(DeleteFlag.deleted);
                    testUserRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    public TestUser getByUsernameAndAge(String username, Integer age) {
        return testUserMapper.findByUsernameAndAge(username, age);
    }
}
