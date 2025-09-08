package com.example.service;

import com.example.common.AbstractService;
import com.example.common.CrudRepository;
import com.example.domain.User;
import com.example.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

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

        if (user.getPasswordHash() != null && !looksHashed(user.getPasswordHash())) {
            user.setPasswordHash(BCrypt.hashpw(user.getPasswordHash(), BCrypt.gensalt()));
        }
        return userRepository.create(user);
    }

    @Transactional
    @Override
    public User update(User incoming) {
        if (incoming.getId() == null) {
            throw new IllegalArgumentException("User id required");
        }


        User managed = userRepository.findById(incoming.getId());
        if (managed == null) {
            throw new IllegalArgumentException("User not found: " + incoming.getId());
        }


        if (incoming.getName() != null)  managed.setName(incoming.getName().trim());
        if (incoming.getEmail() != null) managed.setEmail(incoming.getEmail().trim());
        if (incoming.getPhone() != null) managed.setPhone(incoming.getPhone().trim());


        if (incoming.getPasswordHash() != null && !looksHashed(incoming.getPasswordHash())) {
            managed.setPasswordHash(BCrypt.hashpw(incoming.getPasswordHash(), BCrypt.gensalt()));
        }

        return managed;
    }

    @Override public User findById(Integer id) { return userRepository.findById(id); }
    @Override public List<User> findAll() { return userRepository.findAll(); }
    @Override public void delete(User user) { userRepository.delete(user); }


    public boolean login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) return false;
        String stored = user.getPasswordHash();
        if (stored == null) return false;

        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            return BCrypt.checkpw(rawPassword, stored);
        } else {
            boolean ok = rawPassword.equals(stored);
            if (ok) {
                user.setPasswordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
                userRepository.update(user);
            }
            return ok;
        }
    }


    private boolean looksHashed(String s) {

        return s.startsWith("$2a$") || s.startsWith("$2b$") || s.startsWith("$2y$");
    }
}
