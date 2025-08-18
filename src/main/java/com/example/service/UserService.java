package com.example.service;

import com.example.entity.User;
import com.example.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Transactional
    public void create(User user) {
        userRepository.create(user);
    }

    public User findById(Integer id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User update(User user) {
        return userRepository.update(user);
    }

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
