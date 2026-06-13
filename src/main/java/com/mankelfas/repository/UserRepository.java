package com.mankelfas.repository;

import com.mankelfas.config.DatabaseConnection;
import com.mankelfas.model.user.Admin;
import com.mankelfas.model.user.Mahasiswa;
import com.mankelfas.model.user.Teknisi;
import com.mankelfas.model.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements IUserRepository {

    /**
     * Mengecek kecocokan email dan password di database.
     * Mengembalikan objek User jika berhasil login, atau null jika gagal.
     */
    @Override
    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal login database: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Mengambil seluruh data akun dari tabel users.
     * Sangat berguna untuk ditampilkan di halaman Kelola Akun oleh Admin.
     */
    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal mengambil data user: " + e.getMessage(), e);
        }
        return users;
    }

    /**
     * Menambahkan akun baru ke database dan mengambil ID yang baru saja digenerate oleh MySQL.
     * Menyimpan data spesifik sesuai role (misalnya NIM untuk Mahasiswa).
     */
    @Override
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (nama, email, password, role, nim, level, keahlian) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getNama());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            
            if (user instanceof Mahasiswa) {
                stmt.setString(5, ((Mahasiswa) user).getNim());
                stmt.setNull(6, java.sql.Types.VARCHAR);
                stmt.setNull(7, java.sql.Types.VARCHAR);
            } else if (user instanceof Admin) {
                stmt.setNull(5, java.sql.Types.VARCHAR);
                stmt.setString(6, ((Admin) user).getLevel());
                stmt.setNull(7, java.sql.Types.VARCHAR);
            } else if (user instanceof Teknisi) {
                stmt.setNull(5, java.sql.Types.VARCHAR);
                stmt.setNull(6, java.sql.Types.VARCHAR);
                stmt.setString(7, ((Teknisi) user).getKeahlian());
            } else {
                stmt.setNull(5, java.sql.Types.VARCHAR);
                stmt.setNull(6, java.sql.Types.VARCHAR);
                stmt.setNull(7, java.sql.Types.VARCHAR);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setIdUser(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menambah data user: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET nama=?, email=?, password=?, role=?, nim=?, level=?, keahlian=? WHERE id_user=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getNama());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            
            if (user instanceof Mahasiswa) {
                stmt.setString(5, ((Mahasiswa) user).getNim());
                stmt.setNull(6, java.sql.Types.VARCHAR);
                stmt.setNull(7, java.sql.Types.VARCHAR);
            } else if (user instanceof Admin) {
                stmt.setNull(5, java.sql.Types.VARCHAR);
                stmt.setString(6, ((Admin) user).getLevel());
                stmt.setNull(7, java.sql.Types.VARCHAR);
            } else if (user instanceof Teknisi) {
                stmt.setNull(5, java.sql.Types.VARCHAR);
                stmt.setNull(6, java.sql.Types.VARCHAR);
                stmt.setString(7, ((Teknisi) user).getKeahlian());
            }

            stmt.setInt(8, user.getIdUser());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Gagal update data user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteUser(int idUser) {
        String sql = "DELETE FROM users WHERE id_user=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUser);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Gagal menghapus user: " + e.getMessage(), e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        int id = rs.getInt("id_user");
        String nama = rs.getString("nama");
        String email = rs.getString("email");
        String password = rs.getString("password");

        if (role == null) return null;

        switch (role) {
            case "Admin":
                return new Admin(id, nama, email, password, rs.getString("level"));
            case "Mahasiswa":
                return new Mahasiswa(id, nama, email, password, rs.getString("nim"));
            case "Teknisi":
                return new Teknisi(id, nama, email, password, rs.getString("keahlian"));
            default:
                return null;
        }
    }
}
