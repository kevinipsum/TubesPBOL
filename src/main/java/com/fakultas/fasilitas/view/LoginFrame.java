package com.fakultas.fasilitas.view;

import com.fakultas.fasilitas.model.DatabaseConnection;
import com.fakultas.fasilitas.util.PasswordUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField txtNim;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("JagaRuang - Sistem Pelaporan Kerusakan Fasilitas");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(new Color(6, 78, 59));
        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.anchor = GridBagConstraints.WEST;

        JLabel lblBrand = new JLabel("JagaRuang");
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 32));
        leftPanel.add(lblBrand, gbcLeft);

        gbcLeft.gridy = 1;
        gbcLeft.insets = new Insets(10, 0, 0, 0);
        JLabel lblDesc = new JLabel("<html>Sistem Pelaporan Kerusakan Fasilitas<br>Ruang & Gedung Fakultas</html>");
        lblDesc.setForeground(new Color(167, 243, 208));
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        leftPanel.add(lblDesc, gbcLeft);

        JPanel rightPanel = new JPanel(null);
        rightPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Masuk ke Akun");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setBounds(60, 80, 300, 35);
        rightPanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Silakan masukkan NIM dan password Anda.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        lblSubtitle.setBounds(60, 118, 320, 20);
        rightPanel.add(lblSubtitle);

        JLabel lblNim = new JLabel("NIM");
        lblNim.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNim.setBounds(60, 160, 150, 20);
        rightPanel.add(lblNim);

        txtNim = new JTextField();
        txtNim.setBounds(60, 185, 320, 38);
        rightPanel.add(txtNim);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setBounds(60, 235, 150, 20);
        rightPanel.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(60, 260, 320, 38);
        rightPanel.add(txtPassword);

        JButton btnMasuk = new JButton("Masuk");
        btnMasuk.setBounds(60, 320, 320, 42);
        btnMasuk.setBackground(new Color(16, 185, 129));
        btnMasuk.setForeground(Color.WHITE);
        btnMasuk.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMasuk.setFocusPainted(false);
        btnMasuk.setOpaque(true);
        btnMasuk.setBorderPainted(false);
        rightPanel.add(btnMasuk);

        btnMasuk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prosesLogin();
            }
        });

        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prosesLogin();
            }
        });

        add(leftPanel);
        add(rightPanel);
    }

    private void prosesLogin() {
        String nimInput = txtNim.getText().trim();
        String passInput = new String(txtPassword.getPassword()).trim();

        if (nimInput.isEmpty() || passInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIM dan password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String passHashInput = PasswordUtil.hashPassword(passInput);
        String sql = "SELECT id, nim, username, password_hash, role, status_akun FROM users WHERE (nim = ? OR username = ?) AND password_hash = ? LIMIT 1";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, nimInput);
            ps.setString(2, nimInput);
            ps.setString(3, passHashInput);

            rs = ps.executeQuery();
            if (rs.next()) {
                String statusAkun = rs.getString("status_akun");
                if (statusAkun != null && !statusAkun.equalsIgnoreCase("aktif")) {
                    JOptionPane.showMessageDialog(this, "Akun Anda sedang tidak aktif!", "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int userId = rs.getInt("id");
                String role = rs.getString("role");
                String nimDb = rs.getString("nim");
                String usernameDb = rs.getString("username");

                dispose();
                if ("admin".equalsIgnoreCase(role)) {
                    String adminLabel = (nimDb != null && !nimDb.isEmpty()) ? nimDb :
                            ((usernameDb != null && !usernameDb.isEmpty()) ? usernameDb : nimInput);
                    AdminDashboardFrame adminFrame = new AdminDashboardFrame(userId, adminLabel);
                    adminFrame.setVisible(true);
                } else {
                    String nimFinal = (nimDb != null && !nimDb.isEmpty()) ? nimDb :
                            ((usernameDb != null && !usernameDb.isEmpty()) ? usernameDb : nimInput);
                    PelaporDashboardFrame pelaporFrame = new PelaporDashboardFrame(userId, nimFinal);
                    pelaporFrame.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "NIM atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal terhubung ke database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ignored) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }
}