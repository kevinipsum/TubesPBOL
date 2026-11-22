package com.fakultas.fasilitas.view;

import com.fakultas.fasilitas.model.DatabaseConnection;
<<<<<<< HEAD
import com.fakultas.fasilitas.model.ModelUser;

import javax.swing.*;
import java.awt.*;
=======

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
>>>>>>> d0438a77b2aacef5cdd1c847833573b6a69a68de
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
<<<<<<< HEAD
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
=======

    public LoginFrame() {
        setTitle("JagaRuang - Login");
        setSize(750, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel Samping Kiri (Hijau Khas JagaRuang)
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(new Color(6, 78, 59));
        leftPanel.setPreferredSize(new Dimension(320, 480));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblBrand = new JLabel("JagaRuang");
>>>>>>> d0438a77b2aacef5cdd1c847833573b6a69a68de
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 32));
        leftPanel.add(lblBrand, gbc);

        gbc.gridy = 1;
<<<<<<< HEAD
        gbc.insets = new Insets(0, 20, 0, 20);
        JLabel lblTagline = new JLabel("<html><div style='text-align:center;'>Sistem Pelaporan & Perbaikan<br>Fasilitas Kampus</div></html>", SwingConstants.CENTER);
        lblTagline.setForeground(new Color(167, 243, 208));
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leftPanel.add(lblTagline, gbc);

        // Panel Kanan (Putih)
=======
        gbc.insets = new Insets(10, 20, 0, 20);
        JLabel lblSub = new JLabel("<html><div style='text-align: center;'>Sistem Pelaporan & Perbaikan<br>Fasilitas Kampus</div></html>", SwingConstants.CENTER);
        lblSub.setForeground(new Color(16, 185, 129));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        leftPanel.add(lblSub, gbc);

        add(leftPanel, BorderLayout.WEST);

        // Panel Kanan (Form Input)
>>>>>>> d0438a77b2aacef5cdd1c847833573b6a69a68de
        JPanel rightPanel = new JPanel(null);
        rightPanel.setBackground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Selamat Datang");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
<<<<<<< HEAD
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
=======
        lblWelcome.setBounds(50, 45, 300, 30);
        rightPanel.add(lblWelcome);

        JLabel lblInstruction = new JLabel("Silakan masuk menggunakan akun Anda");
        lblInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInstruction.setForeground(new Color(100, 116, 139));
        lblInstruction.setBounds(50, 75, 300, 20);
        rightPanel.add(lblInstruction);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setBounds(50, 120, 100, 20);
        rightPanel.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(50, 145, 325, 35);
>>>>>>> d0438a77b2aacef5cdd1c847833573b6a69a68de
        rightPanel.add(txtUsername);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
<<<<<<< HEAD
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
=======
        lblPass.setBounds(50, 195, 100, 20);
        rightPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 220, 325, 35);
        rightPanel.add(txtPassword);

        JButton btnLogin = new JButton("Masuk");
        btnLogin.setBackground(new Color(16, 185, 129));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false); btnLogin.setOpaque(true); btnLogin.setBorderPainted(false);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBounds(50, 280, 325, 42);
        btnLogin.addActionListener(e -> prosesLogin());
        rightPanel.add(btnLogin);

        // Opsi Lupa Password berbentuk Link Teks di bawah tombol Masuk
        JLabel lblLupaPass = new JLabel("<html><a href='' style='color: #0d9488; text-decoration: none;'>Lupa Password?</a></html>");
        lblLupaPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLupaPass.setBounds(50, 335, 200, 25);
        lblLupaPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblLupaPass.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                prosesLupaPassword();
            }
        });
        rightPanel.add(lblLupaPass);

        add(rightPanel, BorderLayout.CENTER);
    }

    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                JOptionPane.showMessageDialog(this, "Login Berhasil!");
                dispose();

                if ("admin".equalsIgnoreCase(role)) {
                    new AdminDashboardFrame().setVisible(true);
                } else {
                    new PelaporDashboardFrame(username).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Koneksi database gagal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void prosesLupaPassword() {
        String username = JOptionPane.showInputDialog(this, "Masukkan Username akun Anda:");
        if (username != null && !username.trim().isEmpty()) {
            String query = "SELECT password FROM users WHERE username = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username.trim());
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String passLama = rs.getString("password");
                    JOptionPane.showMessageDialog(this, "Password untuk akun '" + username + "' adalah: " + passLama);
                } else {
                    JOptionPane.showMessageDialog(this, "Username tidak ditemukan.", "Informasi", JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
>>>>>>> d0438a77b2aacef5cdd1c847833573b6a69a68de
        }
    }
}