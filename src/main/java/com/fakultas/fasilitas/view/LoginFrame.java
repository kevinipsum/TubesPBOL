package com.fakultas.fasilitas.view;

import com.fakultas.fasilitas.model.DatabaseConnection;
import com.fakultas.fasilitas.model.ModelUser;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnMasuk, btnGantiPass;

    public LoginFrame() {
        setTitle("JagaRuang - Login");
        setSize(900, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        // Panel Kiri (Hijau Tua)
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(new Color(6, 78, 59));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 20, 8, 20);

        JLabel lblBrand = new JLabel("JagaRuang", SwingConstants.CENTER);
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 32));
        leftPanel.add(lblBrand, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 0, 20);
        JLabel lblTagline = new JLabel("<html><div style='text-align:center;'>Sistem Pelaporan & Perbaikan<br>Fasilitas Kampus</div></html>", SwingConstants.CENTER);
        lblTagline.setForeground(new Color(167, 243, 208));
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leftPanel.add(lblTagline, gbc);

        // Panel Kanan (Putih)
        JPanel rightPanel = new JPanel(null);
        rightPanel.setBackground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Selamat Datang");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWelcome.setForeground(new Color(15, 23, 42));
        lblWelcome.setBounds(65, 85, 320, 32);
        rightPanel.add(lblWelcome);

        JLabel lblDesc = new JLabel("Silakan masuk menggunakan akun Anda");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(100, 116, 139));
        lblDesc.setBounds(65, 118, 320, 22);
        rightPanel.add(lblDesc);

        JLabel lblUser = new JLabel("NIM");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(15, 23, 42));
        lblUser.setBounds(65, 165, 320, 20);
        rightPanel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setBounds(65, 190, 320, 38);
        rightPanel.add(txtUsername);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPass.setForeground(new Color(15, 23, 42));
        lblPass.setBounds(65, 242, 320, 20);
        rightPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBounds(65, 267, 320, 38);
        rightPanel.add(txtPassword);

        btnMasuk = new JButton("Masuk");
        btnMasuk.setBackground(new Color(16, 185, 129));
        btnMasuk.setForeground(Color.WHITE);
        btnMasuk.setFocusPainted(false);
        btnMasuk.setOpaque(true);
        btnMasuk.setBorderPainted(false);
        btnMasuk.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnMasuk.setBounds(65, 330, 320, 42);
        rightPanel.add(btnMasuk);

        btnGantiPass = new JButton("Ganti Password");
        btnGantiPass.setBackground(Color.WHITE);
        btnGantiPass.setForeground(new Color(15, 118, 110));
        btnGantiPass.setFocusPainted(false);
        btnGantiPass.setBorderPainted(false);
        btnGantiPass.setContentAreaFilled(false);
        btnGantiPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnGantiPass.setBounds(150, 380, 150, 28);
        rightPanel.add(btnGantiPass);

        btnMasuk.addActionListener(e -> prosesLogin());
        btnGantiPass.addActionListener(e -> bukaDialogGantiPassword());
        txtPassword.addActionListener(e -> prosesLogin());

        add(leftPanel);
        add(rightPanel);
    }

    private void prosesLogin() {
        String idInput = txtUsername.getText().trim();
        String passInput = new String(txtPassword.getPassword());

        if (idInput.isEmpty() || passInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIM dan Password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT id, nim, username, nama_lengkap, password_hash, role, status_akun " +
                "FROM users WHERE (nim = ? OR username = ?) AND password_hash = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idInput);
            pstmt.setString(2, idInput);
            pstmt.setString(3, passInput);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ModelUser user = new ModelUser();
                    user.setId(rs.getInt("id"));
                    user.setNim(rs.getString("nim"));
                    user.setUsername(rs.getString("username"));
                    user.setNamaLengkap(rs.getString("nama_lengkap"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setRole(rs.getString("role"));
                    user.setStatusAkun(rs.getString("status_akun"));

                    if ("nonaktif".equalsIgnoreCase(user.getStatusAkun())) {
                        JOptionPane.showMessageDialog(this, "Akun sedang nonaktif.", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    catatLog(conn, user.getId(), "LOGIN", "Login ke sistem JagaRuang");

                    dispose();
                    if (user.isAdmin()) {
                        new AdminDashboardFrame(user.getId(), user.getIdentitasTampil()).setVisible(true);
                    } else {
                        new PelaporDashboardFrame(user.getId(), user.getIdentitasTampil()).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "NIM atau Password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kesalahan koneksi database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bukaDialogGantiPassword() {
        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));

        JTextField fNim = new JTextField(txtUsername.getText().trim());
        JPasswordField fPassBaru = new JPasswordField();
        JPasswordField fPassKonf = new JPasswordField();

        form.add(new JLabel("NIM:"));
        form.add(fNim);
        form.add(new JLabel("Password Baru:"));
        form.add(fPassBaru);
        form.add(new JLabel("Konfirmasi Password Baru:"));
        form.add(fPassKonf);

        int hasil = JOptionPane.showConfirmDialog(
                this,
                form,
                "Ganti Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (hasil != JOptionPane.OK_OPTION) {
            return;
        }

        String nim = fNim.getText().trim();
        String passBaru = new String(fPassBaru.getPassword());
        String passKonf = new String(fPassKonf.getPassword());

        if (nim.isEmpty() || passBaru.isEmpty() || passKonf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!passBaru.equals(passKonf)) {
            JOptionPane.showMessageDialog(this, "Konfirmasi password baru tidak cocok!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (passBaru.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password baru minimal 4 karakter!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cekSql = "SELECT id FROM users WHERE nim = ? OR username = ? LIMIT 1";
        String updSql = "UPDATE users SET password_hash = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement cekStmt = conn.prepareStatement(cekSql)) {

            cekStmt.setString(1, nim);
            cekStmt.setString(2, nim);

            try (ResultSet rs = cekStmt.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Akun tidak ditemukan!", "Gagal", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int userId = rs.getInt("id");

                try (PreparedStatement updStmt = conn.prepareStatement(updSql)) {
                    updStmt.setString(1, passBaru);
                    updStmt.setInt(2, userId);
                    updStmt.executeUpdate();
                }

                catatLog(conn, userId, "GANTI_PASSWORD", "Ganti password tanpa password lama");
                JOptionPane.showMessageDialog(this, "Password berhasil diperbarui! Silakan masuk dengan password baru.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengganti password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void catatLog(Connection conn, int userId, String jenis, String ket) {
        String sql = "INSERT INTO log_aktivitas_akun (user_id, jenis_aktivitas, keterangan) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, jenis);
            ps.setString(3, ket);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}