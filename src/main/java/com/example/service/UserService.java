package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.User;
import com.example.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class UserService extends AbstractService<User, Integer> {

    @Inject
    private UserRepository userRepository;
    @Override
    protected CrudRepository<User, Integer> getRepository() {
        return userRepository;
    }


    @Override
    @Transactional
    public User create(User user) {
        userRepository.create(user);
        return user;
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
    @Transactional
    public User update(User user) {
        return userRepository.update(user);
    }

    @Override
    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }


    public boolean login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) return false;
        return user.getPasswordHash().equals(password);
    }
}
