package com.mankelfas.service;

import com.mankelfas.model.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private final UserService userService = UserService.getInstance();

    @Test
    void testAuthenticate_withEmptyCredentials_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.authenticate("", "");
        });
        assertEquals("Email dan password tidak boleh kosong.", exception.getMessage());
    }

    @Test
    void testAuthenticate_withNullCredentials_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.authenticate(null, null);
        });
        assertEquals("Email dan password tidak boleh kosong.", exception.getMessage());
    }

    @Test
    void testAuthenticate_withInvalidCredentials_returnsNull() {
        // Asumsi email dan password ngawur tidak akan ada di database dan login gagal (null)
        User result = userService.authenticate("email_salah@domain.com", "password_salah_123");
        assertNull(result);
    }

    @Test
    void testAddUser_withNullUser_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.addUser(null);
        });
        assertEquals("Data pengguna tidak boleh kosong.", exception.getMessage());
    }

    @Test
    void testAddUser_withInvalidEmailFormat_throwsException() {
        User u = new User(1, "Budi", "emailtanpaat", "password123") {
            @Override public void tampilDashboard() {}
            @Override public String getRole() { return "Test"; }
        };
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.addUser(u);
        });
        assertEquals("Format email tidak valid.", exception.getMessage());
    }

    @Test
    void testAddUser_withShortPassword_throwsException() {
        User u = new User(2, "Budi", "budi@domain.com", "123") {
            @Override public void tampilDashboard() {}
            @Override public String getRole() { return "Test"; }
        };
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.addUser(u);
        });
        assertEquals("Password harus memiliki minimal 6 karakter.", exception.getMessage());
    }

    @Test
    void testUpdateUser_withInvalidId_throwsException() {
        User u = new User(0, "Budi", "budi@domain.com", "password123") {
            @Override public void tampilDashboard() {}
            @Override public String getRole() { return "Test"; }
        };
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(u);
        });
        assertEquals("Data pengguna tidak valid untuk diupdate.", exception.getMessage());
    }
    
    @Test
    void testDeleteUser_withInvalidId_throwsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(-1);
        });
        assertEquals("ID pengguna tidak valid.", exception.getMessage());
    }
}
