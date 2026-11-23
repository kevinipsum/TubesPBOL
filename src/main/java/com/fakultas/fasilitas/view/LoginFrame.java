package com.fakultas.fasilitas.view;

import com.fakultas.fasilitas.model.DatabaseConnection;
import com.fakultas.fasilitas.util.PasswordUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final JTextField txtNim;
    private final JPasswordField txtPassword;

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
        btnMasuk.setBounds(60, 320, 152, 42);
        btnMasuk.setBackground(new Color(16, 185, 129));
        btnMasuk.setForeground(Color.WHITE);
        btnMasuk.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnMasuk.setFocusPainted(false);
        btnMasuk.setOpaque(true);
        btnMasuk.setBorderPainted(false);
        rightPanel.add(btnMasuk);

        JButton btnGantiPass = new JButton("Ganti Password");
        btnGantiPass.setBounds(228, 320, 152, 42);
        btnGantiPass.setBackground(new Color(226, 232, 240));
        btnGantiPass.setForeground(new Color(15, 23, 42));
        btnGantiPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGantiPass.setFocusPainted(false);
        btnGantiPass.setOpaque(true);
        btnGantiPass.setBorderPainted(false);
        rightPanel.add(btnGantiPass);

        btnMasuk.addActionListener(e -> prosesLogin());
        btnGantiPass.addActionListener(e -> bukaDialogGantiPassword());
        txtPassword.addActionListener(e -> prosesLogin());

        add(leftPanel);
        add(rightPanel);
    }

    private void prosesLogin() {
        String nimInput = txtNim.getText().trim();
        String passInput = new String(txtPassword.getPassword());

        if (nimInput.isEmpty() || passInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIM dan password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT id, nim, username, password_hash, role, status_akun " +
                "FROM users WHERE nim = ? OR username = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nimInput);
            ps.setString(2, nimInput);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (!PasswordUtil.verifyPassword(passInput, storedHash)) {
                        JOptionPane.showMessageDialog(this, "NIM atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String statusAkun = rs.getString("status_akun");
                    if (statusAkun != null && !"aktif".equalsIgnoreCase(statusAkun)) {
                        JOptionPane.showMessageDialog(this, "Akun Anda sedang tidak aktif!", "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int userId = rs.getInt("id");
                    String role = rs.getString("role");
                    String nimDb = rs.getString("nim");
                    String usernameDb = rs.getString("username");

                    if (!PasswordUtil.isSha256Hex(storedHash)) {
                        try (PreparedStatement psUpgrade = conn.prepareStatement("UPDATE users SET password_hash = ? WHERE id = ?")) {
                            psUpgrade.setString(1, PasswordUtil.hashPassword(passInput));
                            psUpgrade.setInt(2, userId);
                            psUpgrade.executeUpdate();
                        }
                    }

                    catatLogAktivitas(conn, userId, "LOGIN", "Berhasil login ke aplikasi JagaRuang");

                    dispose();
                    if ("admin".equalsIgnoreCase(role)) {
                        String adminLabel = (usernameDb != null && !usernameDb.isBlank()) ? usernameDb : nimInput;
                        new AdminDashboardFrame(userId, adminLabel).setVisible(true);
                    } else {
                        String nimFinal = (nimDb != null && !nimDb.isBlank()) ? nimDb : nimInput;
                        new PelaporDashboardFrame(userId, nimFinal).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "NIM atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal terhubung ke database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bukaDialogGantiPassword() {
        JDialog dialog = new JDialog(this, "Ganti Password", true);
        dialog.setSize(400, 310);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);

        JLabel lblNim = new JLabel("NIM");
        lblNim.setBounds(25, 20, 150, 20);
        dialog.add(lblNim);

        JTextField tfNim = new JTextField(txtNim.getText().trim());
        tfNim.setBounds(25, 42, 335, 32);
        dialog.add(tfNim);

        JLabel lblPassLama = new JLabel("Password Lama");
        lblPassLama.setBounds(25, 80, 150, 20);
        dialog.add(lblPassLama);

        JPasswordField pfPassLama = new JPasswordField();
        pfPassLama.setBounds(25, 102, 335, 32);
        dialog.add(pfPassLama);

        JLabel lblPassBaru = new JLabel("Password Baru");
        lblPassBaru.setBounds(25, 140, 150, 20);
        dialog.add(lblPassBaru);

        JPasswordField pfPassBaru = new JPasswordField();
        pfPassBaru.setBounds(25, 162, 335, 32);
        dialog.add(pfPassBaru);

        JButton btnSimpan = new JButton("Simpan Password");
        btnSimpan.setBounds(200, 210, 160, 36);
        btnSimpan.setBackground(new Color(16, 185, 129));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setOpaque(true);
        btnSimpan.setBorderPainted(false);
        dialog.add(btnSimpan);

        btnSimpan.addActionListener(e -> {
            String nim = tfNim.getText().trim();
            String passLama = new String(pfPassLama.getPassword());
            String passBaru = new String(pfPassBaru.getPassword());

            if (nim.isEmpty() || passLama.isEmpty() || passBaru.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Semua field wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (passBaru.length() < 4) {
                JOptionPane.showMessageDialog(dialog, "Password baru minimal 4 karakter!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sqlCek = "SELECT id, password_hash FROM users WHERE nim = ? OR username = ? LIMIT 1";
            String sqlUpd = "UPDATE users SET password_hash = ? WHERE id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement psCek = conn.prepareStatement(sqlCek)) {
                psCek.setString(1, nim);
                psCek.setString(2, nim);

                try (ResultSet rs = psCek.executeQuery()) {
                    if (!rs.next() || !PasswordUtil.verifyPassword(passLama, rs.getString("password_hash"))) {
                        JOptionPane.showMessageDialog(dialog, "NIM atau password lama tidak sesuai!", "Gagal", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int userId = rs.getInt("id");
                    try (PreparedStatement psUpd = conn.prepareStatement(sqlUpd)) {
                        psUpd.setString(1, PasswordUtil.hashPassword(passBaru));
                        psUpd.setInt(2, userId);
                        psUpd.executeUpdate();
                    }
                    catatLogAktivitas(conn, userId, "GANTI_PASSWORD", "Password diganti melalui halaman login");
                    JOptionPane.showMessageDialog(dialog, "Password berhasil diperbarui!");
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Gagal mengganti password: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void catatLogAktivitas(Connection conn, int userId, String jenisAktivitas, String keterangan) {
        String sql = "INSERT INTO log_aktivitas_akun (user_id, jenis_aktivitas, keterangan) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, jenisAktivitas);
            ps.setString(3, keterangan);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}