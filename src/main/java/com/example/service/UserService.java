package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.User;
import com.example.repository.UserRepository;
import jakarta.inject.Inject;

import java.util.List;

public class UserService extends AbstractService<User, Integer> {

    @Inject
    private UserRepository userRepository;

    @Override
    protected CrudRepository<User, Integer> getRepository() {
        return userRepository;
    }

    @Override
    public User create(User user) {
        return userRepository.create(user);
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(User user) {
        return userRepository.update(user);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    public boolean login(String email, String password) {
        User user = userRepository.findByEmail(email);
        return user != null && user.getPasswordHash().equals(password);
    }
}
