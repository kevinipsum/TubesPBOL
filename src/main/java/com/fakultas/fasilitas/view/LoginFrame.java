package com.fakultas.fasilitas.view;

import com.fakultas.fasilitas.model.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;

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
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 32));
        leftPanel.add(lblBrand, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 0, 20);
        JLabel lblSub = new JLabel("<html><div style='text-align: center;'>Sistem Pelaporan & Perbaikan<br>Fasilitas Kampus</div></html>", SwingConstants.CENTER);
        lblSub.setForeground(new Color(16, 185, 129));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        leftPanel.add(lblSub, gbc);

        add(leftPanel, BorderLayout.WEST);

        // Panel Kanan (Form Input)
        JPanel rightPanel = new JPanel(null);
        rightPanel.setBackground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Selamat Datang");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
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
        rightPanel.add(txtUsername);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
        }
    }
}