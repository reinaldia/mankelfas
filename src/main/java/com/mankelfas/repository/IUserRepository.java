package com.mankelfas.repository;

import com.mankelfas.model.user.User;
import java.util.List;

public interface IUserRepository {
    User login(String email, String password);
    List<User> getAllUsers();
    boolean addUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int idUser);
}
