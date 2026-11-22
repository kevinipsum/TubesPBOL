package com.fakultas.fasilitas.view;

import com.fakultas.fasilitas.model.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.sql.*;
import java.time.LocalDate;

public class PelaporDashboardFrame extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JComboBox<String> cbLokasi, cbFasilitas;
    private JTextArea txtDeskripsi;
    private JLabel lblSubSidebar;

    private JPanel panelDiproses, panelSelesai, panelDitolak;
    private JButton btnTabDiproses, btnTabSelesai, btnTabDitolak;

    private JTextField txtAkunNim, txtAkunNama, txtAkunStatus;
    private JPasswordField txtPassBaru, txtPassKonfirmasi;

    private final int pelaporId;
    private final String currentNim;

    public PelaporDashboardFrame(String nim) {
        this(0, nim);
    }

    public PelaporDashboardFrame(int pelaporId, String nim) {
        this.pelaporId = pelaporId;
        this.currentNim = nim;

        setTitle("JagaRuang - Dashboard Pelapor (" + currentNim + ")");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar Menu
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(6, 78, 59));
        sidebar.setPreferredSize(new Dimension(300, 700));
        sidebar.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(10, 20, 10, 20);

        JLabel lblBrand = new JLabel("JagaRuang");
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        sidebar.add(lblBrand, gbc);

        gbc.gridy = 1;
        lblSubSidebar = new JLabel("Panel Pelapor / Mahasiswa");
        lblSubSidebar.setForeground(new Color(16, 185, 129));
        sidebar.add(lblSubSidebar, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(30, 20, 10, 20);
        JButton btnMenuForm = createNavButton("Form Pengajuan Laporan");
        sidebar.add(btnMenuForm, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(5, 20, 10, 20);
        JButton btnMenuRiwayat = createNavButton("Status Laporan");
        sidebar.add(btnMenuRiwayat, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(5, 20, 10, 20);
        JButton btnMenuAkun = createNavButton("Kelola Akun");
        sidebar.add(btnMenuAkun, gbc);

        gbc.gridy = 5; gbc.weighty = 1.0;
        sidebar.add(new JLabel(), gbc);

        gbc.gridy = 6; gbc.weighty = 0; gbc.insets = new Insets(10, 20, 20, 20);
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(239, 68, 68));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false); btnLogout.setOpaque(true); btnLogout.setBorderPainted(false);
        btnLogout.setPreferredSize(new Dimension(260, 38));
        sidebar.add(btnLogout, gbc);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(initFormPanel(), "FORM");
        cardPanel.add(initRiwayatCardPanel(), "RIWAYAT");
        cardPanel.add(initKelolaAkunPanel(), "AKUN");

        btnMenuForm.addActionListener(e -> {
            cardLayout.show(cardPanel, "FORM");
            muatLokasiDariDb();
        });
        btnMenuRiwayat.addActionListener(e -> {
            cardLayout.show(cardPanel, "RIWAYAT");
            muatDataRiwayatCard();
        });
        btnMenuAkun.addActionListener(e -> {
            cardLayout.show(cardPanel, "AKUN");
            muatProfilAkun();
        });

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        muatProfilAkun();
        muatLokasiDariDb();
    }

    private void perbaruiLabelSidebar(String namaLengkap) {
        if (namaLengkap != null && !namaLengkap.isBlank() && !namaLengkap.trim().equals(currentNim)) {
            lblSubSidebar.setText(namaLengkap.trim());
        } else {
            lblSubSidebar.setText("Panel Pelapor / Mahasiswa");
        }
        lblSubSidebar.revalidate();
        lblSubSidebar.repaint();
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(15, 118, 110));
        btn.setFocusPainted(false); btn.setOpaque(true); btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(260, 42));
        return btn;
    }

    private JPanel initFormPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(248, 250, 252));

        JLabel lblTitle = new JLabel("Form Pengajuan Kerusakan Fasilitas");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBounds(30, 30, 400, 25);
        panel.add(lblTitle);

        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBounds(30, 75, 700, 520);
        card.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        JLabel lblLokasi = new JLabel("Lokasi Ruangan / Fasilitas");
        lblLokasi.setBounds(30, 20, 200, 20);
        card.add(lblLokasi);

        cbLokasi = new JComboBox<>();
        cbLokasi.addItem("-- Pilih Lokasi / Ruangan Dulu --");
        cbLokasi.setBounds(30, 45, 630, 35);
        card.add(cbLokasi);

        JLabel lblFasilitas = new JLabel("Jenis Fasilitas");
        lblFasilitas.setBounds(30, 95, 200, 20);
        card.add(lblFasilitas);

        cbFasilitas = new JComboBox<>();
        cbFasilitas.addItem("-- Pilih Lokasi Terlebih Dahulu --");
        cbFasilitas.setEnabled(false);
        cbFasilitas.setBounds(30, 120, 630, 35);
        card.add(cbFasilitas);

        cbLokasi.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                muatFasilitasDariDb((String) cbLokasi.getSelectedItem());
            }
        });

        JLabel lblDesk = new JLabel("Deskripsi Kerusakan");
        lblDesk.setBounds(30, 170, 200, 20);
        card.add(lblDesk);

        txtDeskripsi = new JTextArea();
        JScrollPane scrollDesk = new JScrollPane(txtDeskripsi);
        scrollDesk.setBounds(30, 195, 630, 120);
        card.add(scrollDesk);

        JButton btnKirim = new JButton("Kirim Laporan");
        btnKirim.setBackground(new Color(16, 185, 129)); btnKirim.setForeground(Color.WHITE);
        btnKirim.setFocusPainted(false); btnKirim.setOpaque(true); btnKirim.setBorderPainted(false);
        btnKirim.setBounds(520, 340, 140, 40);
        card.add(btnKirim);

        JButton btnReset = new JButton("Reset");
        btnReset.setBackground(new Color(203, 213, 225)); btnReset.setForeground(new Color(15, 23, 42));
        btnReset.setFocusPainted(false); btnReset.setOpaque(true); btnReset.setBorderPainted(false);
        btnReset.setBounds(410, 340, 100, 40);
        card.add(btnReset);

        btnKirim.addActionListener(e -> kirimLaporan());
        btnReset.addActionListener(e -> {
            cbLokasi.setSelectedIndex(0);
            txtDeskripsi.setText("");
        });

        panel.add(card);
        return panel;
    }

    private void muatLokasiDariDb() {
        String prev = (String) cbLokasi.getSelectedItem();
        cbLokasi.removeAllItems();
        cbLokasi.addItem("-- Pilih Lokasi / Ruangan Dulu --");

        String sql = "SELECT nama_lokasi FROM gedung_ruangan ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cbLokasi.addItem(rs.getString("nama_lokasi"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (prev != null) cbLokasi.setSelectedItem(prev);
    }

    private void muatFasilitasDariDb(String namaLokasi) {
        cbFasilitas.removeAllItems();
        if (namaLokasi == null || namaLokasi.startsWith("--")) {
            cbFasilitas.addItem("-- Pilih Lokasi Terlebih Dahulu --");
            cbFasilitas.setEnabled(false);
            return;
        }

        cbFasilitas.setEnabled(true);
        String sql = "SELECT f.nama_fasilitas " +
                "FROM ruangan_fasilitas rf " +
                "JOIN gedung_ruangan gr ON rf.ruangan_id = gr.id " +
                "JOIN fasilitas f ON rf.fasilitas_id = f.id " +
                "WHERE gr.nama_lokasi = ? ORDER BY f.id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, namaLokasi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cbFasilitas.addItem(rs.getString("nama_fasilitas"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (cbFasilitas.getItemCount() == 0) {
            cbFasilitas.addItem("Fasilitas Umum / Eksternal");
        }
    }

    private JPanel initRiwayatCardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(new Color(248, 250, 252));

        JLabel lblTitle = new JLabel("Status Laporan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel tabHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tabHeaderPanel.setOpaque(false);
        tabHeaderPanel.setPreferredSize(new Dimension(0, 45));

        btnTabDiproses = createModernTabButton("Diproses");
        btnTabSelesai = createModernTabButton("Selesai");
        btnTabDitolak = createModernTabButton("Ditolak");

        tabHeaderPanel.add(btnTabDiproses);
        tabHeaderPanel.add(btnTabSelesai);
        tabHeaderPanel.add(btnTabDitolak);

        JPanel contentCardPanel = new JPanel(new CardLayout());
        contentCardPanel.setOpaque(false);

        panelDiproses = new JPanel();
        panelDiproses.setLayout(new BoxLayout(panelDiproses, BoxLayout.Y_AXIS));
        panelDiproses.setBackground(new Color(248, 250, 252));
        panelDiproses.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelSelesai = new JPanel();
        panelSelesai.setLayout(new BoxLayout(panelSelesai, BoxLayout.Y_AXIS));
        panelSelesai.setBackground(new Color(248, 250, 252));
        panelSelesai.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelDitolak = new JPanel();
        panelDitolak.setLayout(new BoxLayout(panelDitolak, BoxLayout.Y_AXIS));
        panelDitolak.setBackground(new Color(248, 250, 252));
        panelDitolak.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollDiproses = new JScrollPane(panelDiproses);
        JScrollPane scrollSelesai = new JScrollPane(panelSelesai);
        JScrollPane scrollDitolak = new JScrollPane(panelDitolak);

        Border cleanBorder = BorderFactory.createLineBorder(new Color(226, 232, 240));
        scrollDiproses.setBorder(cleanBorder);
        scrollSelesai.setBorder(cleanBorder);
        scrollDitolak.setBorder(cleanBorder);

        contentCardPanel.add(scrollDiproses, "DIPROSES");
        contentCardPanel.add(scrollSelesai, "SELESAI");
        contentCardPanel.add(scrollDitolak, "DITOLAK");

        CardLayout cl = (CardLayout) contentCardPanel.getLayout();

        btnTabDiproses.addActionListener(e -> { cl.show(contentCardPanel, "DIPROSES"); updateModernTabStyles("DIPROSES"); });
        btnTabSelesai.addActionListener(e -> { cl.show(contentCardPanel, "SELESAI"); updateModernTabStyles("SELESAI"); });
        btnTabDitolak.addActionListener(e -> { cl.show(contentCardPanel, "DITOLAK"); updateModernTabStyles("DITOLAK"); });

        updateModernTabStyles("DIPROSES");

        panel.add(tabHeaderPanel, BorderLayout.NORTH);
        panel.add(contentCardPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel initKelolaAkunPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(248, 250, 252));

        JLabel lblTitle = new JLabel("Kelola Akun Pelapor");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBounds(30, 30, 400, 25);
        panel.add(lblTitle);

        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBounds(30, 75, 700, 520);
        card.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        JLabel lblNim = new JLabel("NIM");
        lblNim.setBounds(30, 25, 200, 20);
        card.add(lblNim);

        txtAkunNim = new JTextField(currentNim);
        txtAkunNim.setEditable(false);
        txtAkunNim.setBackground(new Color(241, 245, 249));
        txtAkunNim.setBounds(30, 48, 300, 35);
        card.add(txtAkunNim);

        JLabel lblStatus = new JLabel("Status Akun");
        lblStatus.setBounds(360, 25, 200, 20);
        card.add(lblStatus);

        txtAkunStatus = new JTextField("aktif");
        txtAkunStatus.setEditable(false);
        txtAkunStatus.setBackground(new Color(241, 245, 249));
        txtAkunStatus.setBounds(360, 48, 300, 35);
        card.add(txtAkunStatus);

        JLabel lblNama = new JLabel("Nama Lengkap");
        lblNama.setBounds(30, 98, 200, 20);
        card.add(lblNama);

        txtAkunNama = new JTextField();
        txtAkunNama.setBounds(30, 121, 630, 35);
        card.add(txtAkunNama);

        JLabel lblPassBaru = new JLabel("Password Baru");
        lblPassBaru.setBounds(30, 173, 250, 20);
        card.add(lblPassBaru);

        txtPassBaru = new JPasswordField();
        txtPassBaru.setBounds(30, 196, 630, 35);
        card.add(txtPassBaru);

        JLabel lblPassKonf = new JLabel("Konfirmasi Password Baru");
        lblPassKonf.setBounds(30, 246, 250, 20);
        card.add(lblPassKonf);

        txtPassKonfirmasi = new JPasswordField();
        txtPassKonfirmasi.setBounds(30, 269, 630, 35);
        card.add(txtPassKonfirmasi);

        JButton btnSimpan = new JButton("Simpan Perubahan");
        btnSimpan.setBackground(new Color(16, 185, 129));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.setFocusPainted(false);
        btnSimpan.setOpaque(true);
        btnSimpan.setBorderPainted(false);
        btnSimpan.setBounds(480, 330, 180, 40);
        btnSimpan.addActionListener(e -> simpanPerubahanAkun());
        card.add(btnSimpan);

        panel.add(card);
        return panel;
    }

    private void muatProfilAkun() {
        String sql = "SELECT nim, nama_lengkap, status_akun FROM users WHERE nim = ? OR username = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentNim);
            ps.setString(2, currentNim);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nimDb = rs.getString("nim");
                    String namaDb = rs.getString("nama_lengkap");
                    String statusDb = rs.getString("status_akun");

                    txtAkunNim.setText(nimDb != null ? nimDb : currentNim);
                    txtAkunNama.setText(namaDb != null ? namaDb : "");
                    txtAkunStatus.setText(statusDb != null ? statusDb : "aktif");
                    perbaruiLabelSidebar(namaDb);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void simpanPerubahanAkun() {
        String namaBaru = txtAkunNama.getText().trim();
        String passBaru = new String(txtPassBaru.getPassword());
        String passKonf = new String(txtPassKonfirmasi.getPassword());

        if (!passBaru.isEmpty() || !passKonf.isEmpty()) {
            if (!passBaru.equals(passKonf)) {
                JOptionPane.showMessageDialog(this, "Konfirmasi password baru tidak cocok!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (passBaru.length() < 4) {
                JOptionPane.showMessageDialog(this, "Password baru minimal 4 karakter!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            int uid = resolvePelaporId(conn);
            if (uid <= 0) {
                JOptionPane.showMessageDialog(this, "Akun tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String namaSimpan = namaBaru.isEmpty() ? currentNim : namaBaru;

            if (!passBaru.isEmpty()) {
                String sql = "UPDATE users SET nama_lengkap = ?, password_hash = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, namaSimpan);
                    ps.setString(2, passBaru);
                    ps.setInt(3, uid);
                    ps.executeUpdate();
                }
                catatLog(conn, uid, "GANTI_PASSWORD", "Pelapor memperbarui profil & password");
            } else {
                String sql = "UPDATE users SET nama_lengkap = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, namaSimpan);
                    ps.setInt(2, uid);
                    ps.executeUpdate();
                }
                catatLog(conn, uid, "UPDATE_PROFIL", "Pelapor memperbarui nama lengkap");
            }

            txtPassBaru.setText("");
            txtPassKonfirmasi.setText("");
            muatProfilAkun();
            JOptionPane.showMessageDialog(this, "Perubahan akun berhasil disimpan!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan perubahan akun: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private JButton createModernTabButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(140, 38));
        return btn;
    }

    private void updateModernTabStyles(String activeTabName) {
        btnTabDiproses.setBackground(new Color(204, 251, 241));
        btnTabDiproses.setForeground(new Color(15, 118, 110));
        btnTabSelesai.setBackground(new Color(219, 234, 254));
        btnTabSelesai.setForeground(new Color(29, 78, 216));
        btnTabDitolak.setBackground(new Color(254, 226, 226));
        btnTabDitolak.setForeground(new Color(185, 28, 28));

        if ("DIPROSES".equals(activeTabName)) {
            btnTabDiproses.setBackground(new Color(13, 148, 136));
            btnTabDiproses.setForeground(Color.WHITE);
        } else if ("SELESAI".equals(activeTabName)) {
            btnTabSelesai.setBackground(new Color(37, 99, 235));
            btnTabSelesai.setForeground(Color.WHITE);
        } else if ("DITOLAK".equals(activeTabName)) {
            btnTabDitolak.setBackground(new Color(220, 38, 38));
            btnTabDitolak.setForeground(Color.WHITE);
        }
    }

    private String generateIdLaporan(Connection conn) throws SQLException {
        String query = "SELECT id FROM laporan ORDER BY id DESC LIMIT 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                String lastId = rs.getString("id");
                if (lastId != null && lastId.startsWith("LAP-")) {
                    int num = Integer.parseInt(lastId.substring(4));
                    return String.format("LAP-%04d", num + 1);
                }
            }
        }
        return "LAP-0001";
    }

    private int resolvePelaporId(Connection conn) throws SQLException {
        if (pelaporId > 0) return pelaporId;
        String sql = "SELECT id FROM users WHERE nim = ? OR username = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentNim);
            ps.setString(2, currentNim);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id") : 0;
            }
        }
    }

    private void kirimLaporan() {
        if (cbLokasi.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih lokasi ruangan terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String lokasi = (String) cbLokasi.getSelectedItem();
        String fasilitas = (String) cbFasilitas.getSelectedItem();
        String deskripsi = txtDeskripsi.getText().trim();

        if (deskripsi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Deskripsi kerusakan wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sqlInsert = "INSERT INTO laporan (id, pelapor_id, ruangan_id, fasilitas_id, tanggal_lapor, deskripsi, status) " +
                "SELECT ?, ?, gr.id, f.id, ?, ?, 'Menunggu' " +
                "FROM gedung_ruangan gr, fasilitas f " +
                "WHERE gr.nama_lokasi = ? AND f.nama_fasilitas = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection()) {
            int uid = resolvePelaporId(conn);
            if (uid <= 0) {
                JOptionPane.showMessageDialog(this, "Akun pelapor tidak ditemukan di database!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String idLaporan = generateIdLaporan(conn);
            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setString(1, idLaporan);
                ps.setInt(2, uid);
                ps.setString(3, LocalDate.now().toString());
                ps.setString(4, deskripsi);
                ps.setString(5, lokasi);
                ps.setString(6, fasilitas);
                if (ps.executeUpdate() == 0) {
                    JOptionPane.showMessageDialog(this, "Lokasi atau fasilitas tidak valid di database!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Laporan berhasil dikirim dengan ID: " + idLaporan);
            cbLokasi.setSelectedIndex(0);
            txtDeskripsi.setText("");
            cardLayout.show(cardPanel, "RIWAYAT");
            muatDataRiwayatCard();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengirim laporan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void muatDataRiwayatCard() {
        panelDiproses.removeAll();
        panelSelesai.removeAll();
        panelDitolak.removeAll();

        String sql = "SELECT l.id, f.nama_fasilitas AS fasilitas, gr.nama_lokasi AS lokasi, " +
                "l.tanggal_lapor AS tanggal, l.deskripsi, l.status, " +
                "pl.jadwal_perbaikan, pl.nama_teknisi, rp.alasan_penolakan " +
                "FROM laporan l " +
                "JOIN gedung_ruangan gr ON l.ruangan_id = gr.id " +
                "JOIN fasilitas f ON l.fasilitas_id = f.id " +
                "JOIN users u ON l.pelapor_id = u.id " +
                "LEFT JOIN penanganan_laporan pl ON pl.laporan_id = l.id " +
                "LEFT JOIN riwayat_penolakan rp ON rp.laporan_id = l.id " +
                "WHERE (u.nim = ? OR u.username = ?) " +
                "ORDER BY l.id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentNim);
            ps.setString(2, currentNim);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    JPanel cardItem = createReportCard(
                            rs.getString("id"),
                            rs.getString("fasilitas"),
                            rs.getString("lokasi"),
                            rs.getString("tanggal"),
                            rs.getString("deskripsi"),
                            status,
                            rs.getString("jadwal_perbaikan"),
                            rs.getString("nama_teknisi"),
                            rs.getString("alasan_penolakan")
                    );
                    cardItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

                    if ("Diproses".equalsIgnoreCase(status) || "Menunggu".equalsIgnoreCase(status)) {
                        panelDiproses.add(cardItem);
                        panelDiproses.add(Box.createRigidArea(new Dimension(0, 10)));
                    } else if ("Selesai".equalsIgnoreCase(status)) {
                        panelSelesai.add(cardItem);
                        panelSelesai.add(Box.createRigidArea(new Dimension(0, 10)));
                    } else if ("Ditolak".equalsIgnoreCase(status)) {
                        panelDitolak.add(cardItem);
                        panelDitolak.add(Box.createRigidArea(new Dimension(0, 10)));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        panelDiproses.revalidate(); panelDiproses.repaint();
        panelSelesai.revalidate(); panelSelesai.repaint();
        panelDitolak.revalidate(); panelDitolak.repaint();
    }

    private JPanel createReportCard(String id, String fasilitas, String lokasi, String tanggal, String deskripsi,
                                    String status, String jadwal, String teknisi, String alasanTolak) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        JLabel lblHeader = new JLabel("<html><b>ID:</b> " + id + "   |   <font color='#64748B'>Tanggal: " + tanggal + "  |  Status: <b>" + status + "</b></font></html>");
        lblHeader.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        String infoTambahan = "";
        if ("Menunggu".equalsIgnoreCase(status)) {
            infoTambahan = "<br><font color='#D97706'><b>Status:</b> Menunggu verifikasi admin</font>";
        } else if ("Diproses".equalsIgnoreCase(status)) {
            infoTambahan = "<br><font color='#059669'><b>Jadwal:</b> " + (jadwal != null && !jadwal.isBlank() ? jadwal : "-") +
                    "  |  <b>Teknisi:</b> " + (teknisi != null && !teknisi.isBlank() ? teknisi : "-") + "</font>";
        } else if ("Selesai".equalsIgnoreCase(status)) {
            infoTambahan = "<br><font color='#2563EB'><b>Status:</b> Selesai diperbaiki</font>";
        } else if ("Ditolak".equalsIgnoreCase(status)) {
            infoTambahan = "<br><font color='#DC2626'><b>Feedback / Alasan:</b> " + (alasanTolak != null && !alasanTolak.isBlank() ? alasanTolak : "-") + "</font>";
        }

        JLabel lblBody = new JLabel("<html><b>Lokasi:</b> " + lokasi + "   |   <b>Fasilitas:</b> " + fasilitas +
                "<br><b>Keluhan:</b> " + deskripsi + infoTambahan + "</html>");
        lblBody.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        card.add(lblHeader, BorderLayout.NORTH);
        card.add(lblBody, BorderLayout.CENTER);
        return card;
    }
}