package com.mankelfas.service;

import com.mankelfas.model.user.User;
import java.util.List;

public interface IUserService {
    User authenticate(String email, String password);
    List<User> getAllUsers();
    boolean addUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int idUser);
}
