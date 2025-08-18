package com.example.repository;
import com.example.entity.User;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository extends AbstractRepository<User, Integer> {
    public UserRepository() {
        super(User.class);
    }
}
