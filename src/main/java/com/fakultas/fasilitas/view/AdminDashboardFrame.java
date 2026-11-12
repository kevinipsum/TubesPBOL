package com.fakultas.fasilitas.view;

import com.fakultas.fasilitas.model.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardFrame extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private JPanel panelListLaporan, panelListJadwal, panelListUser, panelHistoryBulanan;
    private JTable tblUser;
    private DefaultTableModel modelUser;

    private JTextField txtIdLaporanJadwal, txtTanggalPerbaikan;
    private JComboBox<String> cbTeknisiAnggota;

    private JTextField txtNewUsername;
    private JPasswordField txtNewPassword;

    private String selectedIdJadwal = "";

    public AdminDashboardFrame() {
        setTitle("JagaRuang - Panel Admin");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(6, 78, 59));
        sidebar.setPreferredSize(new Dimension(300, 720));
        sidebar.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(10, 20, 10, 20);

        JLabel lblBrand = new JLabel("JagaRuang");
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        sidebar.add(lblBrand, gbc);

        gbc.gridy = 1;
        JLabel lblSub = new JLabel("Panel Administrator");
        lblSub.setForeground(new Color(16, 185, 129));
        sidebar.add(lblSub, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(30, 20, 10, 20);
        JButton btnLaporan = createNavButton("Laporan Masuk");
        sidebar.add(btnLaporan, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(5, 20, 10, 20);
        JButton btnJadwal = createNavButton("Jadwal & Perbaikan");
        sidebar.add(btnJadwal, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(5, 20, 10, 20);
        JButton btnHistory = createNavButton("History Bulanan");
        sidebar.add(btnHistory, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(5, 20, 10, 20);
        JButton btnAkun = createNavButton("Manajemen Akun");
        sidebar.add(btnAkun, gbc);

        gbc.gridy = 6; gbc.weighty = 1.0;
        sidebar.add(new JLabel(), gbc);

        gbc.gridy = 7; gbc.weighty = 0; gbc.insets = new Insets(10, 20, 20, 20);
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(239, 68, 68));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false); btnLogout.setOpaque(true); btnLogout.setBorderPainted(false);
        btnLogout.setPreferredSize(new Dimension(260, 38));
        sidebar.add(btnLogout, gbc);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(initLaporanCardPanel(), "LAPORAN");
        cardPanel.add(initJadwalCardPanel(), "JADWAL");
        cardPanel.add(initHistoryBulananPanel(), "HISTORY");
        cardPanel.add(initAkunCardPanel(), "AKUN");

        btnLaporan.addActionListener(e -> { cardLayout.show(cardPanel, "LAPORAN"); muatDataAdminCard(); });
        btnJadwal.addActionListener(e -> { cardLayout.show(cardPanel, "JADWAL"); muatDataAdminCard(); });
        btnHistory.addActionListener(e -> { cardLayout.show(cardPanel, "HISTORY"); muatGridHistoryBulanan(java.util.Calendar.getInstance()); });
        btnAkun.addActionListener(e -> { cardLayout.show(cardPanel, "AKUN"); muatDataUserCard(); });

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        muatDataAdminCard();
        muatDataUserCard();
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

    private JPanel initLaporanCardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(new Color(248, 250, 252));

        JLabel lblTitle = new JLabel("Daftar Laporan Masuk (Menunggu Persetujuan)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        panelListLaporan = new JPanel();
        panelListLaporan.setLayout(new BoxLayout(panelListLaporan, BoxLayout.Y_AXIS));
        panelListLaporan.setBackground(new Color(241, 245, 249));
        panelListLaporan.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JScrollPane(panelListLaporan), BorderLayout.CENTER);
        return panel;
    }

    private JPanel initJadwalCardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(new Color(248, 250, 252));

        JLabel lblTitle = new JLabel("Jadwal & Penugasan Teknisi Perbaikan");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        panelListJadwal = new JPanel();
        panelListJadwal.setLayout(new BoxLayout(panelListJadwal, BoxLayout.Y_AXIS));
        panelListJadwal.setBackground(new Color(241, 245, 249));
        panelListJadwal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JScrollPane(panelListJadwal), BorderLayout.CENTER);

        JPanel panelBottomJadwal = new JPanel(new BorderLayout(10, 10));
        panelBottomJadwal.setOpaque(false);

        JPanel panelFormJadwal = new JPanel(null);
        panelFormJadwal.setBackground(Color.WHITE);
        panelFormJadwal.setPreferredSize(new Dimension(0, 140));
        panelFormJadwal.setBorder(BorderFactory.createTitledBorder("Atur Jadwal & Teknisi Anggota (Klik Laporan berstatus Diproses di atas)"));

        JLabel lblId = new JLabel("ID Laporan:");
        lblId.setBounds(20, 25, 90, 20);
        panelFormJadwal.add(lblId);

        txtIdLaporanJadwal = new JTextField();
        txtIdLaporanJadwal.setBounds(110, 25, 180, 28);
        txtIdLaporanJadwal.setEditable(false);
        panelFormJadwal.add(txtIdLaporanJadwal);

        JLabel lblTgl = new JLabel("Tgl Perbaikan:");
        lblTgl.setBounds(320, 25, 90, 20);
        panelFormJadwal.add(lblTgl);

        txtTanggalPerbaikan = new JTextField(LocalDate.now().toString());
        txtTanggalPerbaikan.setBounds(410, 25, 180, 28);
        txtTanggalPerbaikan.setEditable(false);
        txtTanggalPerbaikan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        txtTanggalPerbaikan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bukaPilihTanggalDialog();
            }
        });
        panelFormJadwal.add(txtTanggalPerbaikan);

        JLabel lblTeknisi = new JLabel("Nama Teknisi:");
        lblTeknisi.setBounds(20, 70, 90, 20);
        panelFormJadwal.add(lblTeknisi);

        cbTeknisiAnggota = new JComboBox<>(new String[]{
                "Qeizha Aleesha Zyva",
                "Syamil Mali Uzair",
                "Syalikhafaradhiya Agira",
                "Kevin Rodrigues Pandiangan"
        });
        cbTeknisiAnggota.setBounds(110, 70, 300, 28);
        panelFormJadwal.add(cbTeknisiAnggota);

        JButton btnSimpanJadwal = new JButton("Simpan Jadwal");
        btnSimpanJadwal.setBackground(new Color(16, 185, 129));
        btnSimpanJadwal.setForeground(Color.WHITE);
        btnSimpanJadwal.setFocusPainted(false); btnSimpanJadwal.setOpaque(true); btnSimpanJadwal.setBorderPainted(false);
        btnSimpanJadwal.setBounds(430, 70, 160, 28);
        btnSimpanJadwal.addActionListener(e -> simpanJadwalPerbaikanCard());
        panelFormJadwal.add(btnSimpanJadwal);

        panelBottomJadwal.add(panelFormJadwal, BorderLayout.CENTER);

        JPanel panelBtnSelesai = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtnSelesai.setOpaque(false);
        JButton btnSelesai = new JButton("Tandai Selesai Perbaikan");
        btnSelesai.setBackground(new Color(15, 118, 110));
        btnSelesai.setForeground(Color.WHITE);
        btnSelesai.setFocusPainted(false); btnSelesai.setOpaque(true); btnSelesai.setBorderPainted(false);
        btnSelesai.setPreferredSize(new Dimension(200, 38));
        btnSelesai.addActionListener(e -> tandaiSelesaiPerbaikan());
        panelBtnSelesai.add(btnSelesai);

        panelBottomJadwal.add(panelBtnSelesai, BorderLayout.SOUTH);
        panel.add(panelBottomJadwal, BorderLayout.SOUTH);

        return panel;
    }

    private void bukaPilihTanggalDialog() {
        JDialog dialogKalender = new JDialog(this, "Pilih Tanggal Perbaikan", true);
        dialogKalender.setSize(400, 450);
        dialogKalender.setLocationRelativeTo(this);
        dialogKalender.setLayout(new BorderLayout());

        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelHeader.setBackground(new Color(6, 78, 59));

        final java.util.Calendar cal = java.util.Calendar.getInstance();

        JLabel lblBulanTahun = new JLabel();
        lblBulanTahun.setForeground(Color.WHITE);
        lblBulanTahun.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        btnPrev.setFocusPainted(false); btnNext.setFocusPainted(false);

        panelHeader.add(btnPrev);
        panelHeader.add(lblBulanTahun);
        panelHeader.add(btnNext);
        dialogKalender.add(panelHeader, BorderLayout.NORTH);

        JPanel panelGridHari = new JPanel(new GridLayout(1, 7, 2, 2));
        panelGridHari.setBackground(new Color(226, 232, 240));
        String[] namaHari = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String h : namaHari) {
            JLabel lblH = new JLabel(h, SwingConstants.CENTER);
            lblH.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblH.setForeground(new Color(15, 118, 110));
            panelGridHari.add(lblH);
        }
        dialogKalender.add(panelGridHari, BorderLayout.NORTH);

        JPanel panelGridTanggal = new JPanel(new GridLayout(6, 7, 5, 5));
        panelGridTanggal.setBackground(Color.WHITE);
        panelGridTanggal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Runnable renderKalender = () -> {
            panelGridTanggal.removeAll();

            java.text.SimpleDateFormat sdfMonth = new java.text.SimpleDateFormat("MMMM yyyy");
            lblBulanTahun.setText(sdfMonth.format(cal.getTime()));

            java.util.Calendar cTemp = (java.util.Calendar) cal.clone();
            cTemp.set(java.util.Calendar.DAY_OF_MONTH, 1);
            int hariPertama = cTemp.get(java.util.Calendar.DAY_OF_WEEK) - 1;
            int maxHari = cTemp.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

            for (int i = 0; i < hariPertama; i++) {
                panelGridTanggal.add(new JLabel(""));
            }

            int tahunIni = cal.get(java.util.Calendar.YEAR);
            int bulanIni = cal.get(java.util.Calendar.MONTH);

            for (int i = 1; i <= maxHari; i++) {
                final int day = i;
                JButton btnDay = new JButton(String.valueOf(day));
                btnDay.setFocusPainted(false);
                btnDay.setBackground(new Color(241, 245, 249));
                btnDay.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                btnDay.addActionListener(e -> {
                    String tglPilih = String.format("%d-%02d-%02d", tahunIni, (bulanIni + 1), day);
                    txtTanggalPerbaikan.setText(tglPilih);
                    dialogKalender.dispose();
                });

                panelGridTanggal.add(btnDay);
            }

            int totalKotakTerisi = hariPertama + maxHari;
            int sisaKotak = 42 - totalKotakTerisi;
            for (int i = 0; i < sisaKotak; i++) {
                panelGridTanggal.add(new JLabel(""));
            }

            panelGridTanggal.revalidate();
            panelGridTanggal.repaint();
        };

        btnPrev.addActionListener(e -> {
            cal.add(java.util.Calendar.MONTH, -1);
            renderKalender.run();
        });

        btnNext.addActionListener(e -> {
            cal.add(java.util.Calendar.MONTH, 1);
            renderKalender.run();
        });

        renderKalender.run();
        dialogKalender.add(panelGridTanggal, BorderLayout.CENTER);
        dialogKalender.setVisible(true);
    }

    private JPanel initHistoryBulananPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(new Color(248, 250, 252));

        JLabel lblTitle = new JLabel("History Bulanan (Klik tanggal untuk melihat rincian ID Laporan)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        panelHistoryBulanan = new JPanel(new BorderLayout());
        panelHistoryBulanan.setBackground(Color.WHITE);
        panelHistoryBulanan.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        muatGridHistoryBulanan(java.util.Calendar.getInstance());

        panel.add(panelHistoryBulanan, BorderLayout.CENTER);
        return panel;
    }

    private void tampilkanPopUpDetailTanggal(String tglKey) {
        JDialog dialogDetail = new JDialog(this, "Rincian Tanggal: " + tglKey, true);
        dialogDetail.setSize(380, 320);
        dialogDetail.setLocationRelativeTo(this);
        dialogDetail.setLayout(new BorderLayout(10, 10));

        JPanel panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
        panelContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelContent.setBackground(Color.WHITE);

        List<String> listMasuk = new ArrayList<>();
        List<String> listSelesai = new ArrayList<>();

        String query = "SELECT id, status FROM laporan WHERE tanggal = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, tglKey);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String status = rs.getString("status");
                    listMasuk.add(id);
                    if ("Selesai".equalsIgnoreCase(status)) {
                        listSelesai.add(id);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JLabel lblInfoMasuk = new JLabel("<html><b>Laporan Masuk (" + listMasuk.size() + "):</b><br>" +
                (listMasuk.isEmpty() ? "-" : String.join(", ", listMasuk)) + "</html>");
        lblInfoMasuk.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblInfoSelesai = new JLabel("<html><b>Laporan Selesai (" + listSelesai.size() + "):</b><br>" +
                (listSelesai.isEmpty() ? "-" : String.join(", ", listSelesai)) + "</html>");
        lblInfoSelesai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfoSelesai.setForeground(new Color(5, 150, 105));

        panelContent.add(lblInfoMasuk);
        panelContent.add(Box.createRigidArea(new Dimension(0, 15)));
        panelContent.add(lblInfoSelesai);

        dialogDetail.add(new JScrollPane(panelContent), BorderLayout.CENTER);

        JButton btnTutup = new JButton("Tutup");
        btnTutup.setBackground(new Color(16, 185, 129)); // Hijau Emerald
        btnTutup.setForeground(Color.WHITE);            // Teks Putih
        btnTutup.setFocusPainted(false);
        btnTutup.setOpaque(true);
        btnTutup.setBorderPainted(false);
        btnTutup.addActionListener(e -> dialogDetail.dispose());

        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBottom.add(btnTutup);
        dialogDetail.add(panelBottom, BorderLayout.SOUTH);

        dialogDetail.setVisible(true);
    }

    private void muatGridHistoryBulanan(java.util.Calendar cal) {
        panelHistoryBulanan.removeAll();

        Map<String, Integer> mapJmlMasuk = new HashMap<>();
        Map<String, Integer> mapJmlSelesai = new HashMap<>();

        String query = "SELECT tanggal, COUNT(*) as jml_masuk, SUM(status='Selesai') as jml_selesai FROM laporan GROUP BY tanggal";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String tgl = rs.getString("tanggal");
                mapJmlMasuk.put(tgl, rs.getInt("jml_masuk"));
                mapJmlSelesai.put(tgl, rs.getInt("jml_selesai"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        panelHeader.setBackground(new Color(6, 78, 59));

        JLabel lblBulanTahun = new JLabel();
        lblBulanTahun.setForeground(Color.WHITE);
        lblBulanTahun.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnPrev = new JButton("< Bulan Sebelumnya");
        JButton btnNext = new JButton("Bulan Berikutnya >");
        btnPrev.setFocusPainted(false); btnNext.setFocusPainted(false);

        panelHeader.add(btnPrev);
        panelHeader.add(lblBulanTahun);
        panelHeader.add(btnNext);
        panelHistoryBulanan.add(panelHeader, BorderLayout.NORTH);

        JPanel panelCenterContainer = new JPanel(new BorderLayout());

        JPanel panelGridHari = new JPanel(new GridLayout(1, 7, 4, 4));
        panelGridHari.setBackground(new Color(241, 245, 249));
        panelGridHari.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        String[] namaHari = {"Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
        for (String h : namaHari) {
            JLabel lblH = new JLabel(h, SwingConstants.CENTER);
            lblH.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblH.setForeground(new Color(15, 118, 110));
            panelGridHari.add(lblH);
        }
        panelCenterContainer.add(panelGridHari, BorderLayout.NORTH);

        JPanel panelGrid = new JPanel(new GridLayout(6, 7, 4, 4));
        panelGrid.setBackground(new Color(241, 245, 249));
        panelGrid.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

        java.text.SimpleDateFormat sdfMonth = new java.text.SimpleDateFormat("MMMM yyyy");
        lblBulanTahun.setText(sdfMonth.format(cal.getTime()));

        java.util.Calendar cTemp = (java.util.Calendar) cal.clone();
        cTemp.set(java.util.Calendar.DAY_OF_MONTH, 1);
        int hariPertama = cTemp.get(java.util.Calendar.DAY_OF_WEEK) - 1;
        int maxHari = cTemp.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        for (int i = 0; i < hariPertama; i++) {
            JPanel pKosong = new JPanel();
            pKosong.setBackground(Color.WHITE);
            panelGrid.add(pKosong);
        }

        int tahunIni = cal.get(java.util.Calendar.YEAR);
        int bulanIni = cal.get(java.util.Calendar.MONTH);

        for (int i = 1; i <= maxHari; i++) {
            final int day = i;
            String tglKey = String.format("%d-%02d-%02d", tahunIni, (bulanIni + 1), day);

            int jmlMasuk = mapJmlMasuk.getOrDefault(tglKey, 0);
            int jmlSelesai = mapJmlSelesai.getOrDefault(tglKey, 0);

            JButton btnDay = new JButton();
            btnDay.setLayout(new BorderLayout());
            btnDay.setFocusPainted(false);
            btnDay.setBackground(Color.WHITE);
            btnDay.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
            btnDay.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel lblNum = new JLabel(" " + day);
            lblNum.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblNum.setForeground(new Color(15, 23, 42));
            btnDay.add(lblNum, BorderLayout.NORTH);

            if (jmlMasuk > 0 || jmlSelesai > 0) {
                JLabel lblInfo = new JLabel(String.format("<html><div style='font-size:9px; padding:3px; color:#065f46;'>Masuk: <b>%d</b><br>Selesai: <b>%d</b></div></html>", jmlMasuk, jmlSelesai));
                btnDay.add(lblInfo, BorderLayout.CENTER);
                btnDay.setBackground(new Color(209, 250, 229));
            }

            btnDay.addActionListener(e -> tampilkanPopUpDetailTanggal(tglKey));

            panelGrid.add(btnDay);
        }

        int totalKotakTerisi = hariPertama + maxHari;
        int sisaKotak = 42 - totalKotakTerisi;
        for (int i = 0; i < sisaKotak; i++) {
            JPanel pKosong = new JPanel();
            pKosong.setBackground(Color.WHITE);
            panelGrid.add(pKosong);
        }

        btnPrev.addActionListener(e -> {
            cal.add(java.util.Calendar.MONTH, -1);
            muatGridHistoryBulanan(cal);
        });

        btnNext.addActionListener(e -> {
            cal.add(java.util.Calendar.MONTH, 1);
            muatGridHistoryBulanan(cal);
        });

        panelCenterContainer.add(panelGrid, BorderLayout.CENTER);
        panelHistoryBulanan.add(panelCenterContainer, BorderLayout.CENTER);
        panelHistoryBulanan.revalidate();
        panelHistoryBulanan.repaint();
    }

    private JPanel initAkunCardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setBackground(new Color(248, 250, 252));

        JLabel lblTitle = new JLabel("Manajemen Akun Pelapor");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        panelListUser = new JPanel();
        panelListUser.setLayout(new BoxLayout(panelListUser, BoxLayout.Y_AXIS));
        panelListUser.setBackground(new Color(241, 245, 249));
        panelListUser.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JScrollPane(panelListUser), BorderLayout.CENTER);

        JPanel panelBottomContainer = new JPanel(new GridLayout(1, 2, 15, 0));
        panelBottomContainer.setOpaque(false);

        JPanel panelFormAdd = new JPanel(null);
        panelFormAdd.setBackground(Color.WHITE);
        panelFormAdd.setBorder(BorderFactory.createTitledBorder("Tambah Akun Pelapor Baru"));

        JLabel lblUserNew = new JLabel("Username:");
        lblUserNew.setBounds(15, 25, 80, 20);
        panelFormAdd.add(lblUserNew);

        txtNewUsername = new JTextField();
        txtNewUsername.setBounds(95, 25, 180, 28);
        panelFormAdd.add(txtNewUsername);

        JLabel lblPassNew = new JLabel("Password:");
        lblPassNew.setBounds(15, 65, 80, 20);
        panelFormAdd.add(lblPassNew);

        txtNewPassword = new JPasswordField();
        txtNewPassword.setBounds(95, 65, 180, 28);
        panelFormAdd.add(txtNewPassword);

        JButton btnSimpanAkun = new JButton("Simpan Akun");
        btnSimpanAkun.setBackground(new Color(16, 185, 129));
        btnSimpanAkun.setForeground(Color.WHITE);
        btnSimpanAkun.setFocusPainted(false); btnSimpanAkun.setOpaque(true); btnSimpanAkun.setBorderPainted(false);
        btnSimpanAkun.setBounds(95, 110, 180, 35);
        btnSimpanAkun.addActionListener(e -> tambahAkunPelapor());
        panelFormAdd.add(btnSimpanAkun);

        JPanel panelAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 50));
        panelAction.setOpaque(false);

        JButton btnHapusAkun = new JButton("Hapus Akun Terpilih");
        btnHapusAkun.setBackground(new Color(239, 68, 68));
        btnHapusAkun.setForeground(Color.WHITE);
        btnHapusAkun.setFocusPainted(false); btnHapusAkun.setOpaque(true); btnHapusAkun.setBorderPainted(false);
        btnHapusAkun.setPreferredSize(new Dimension(180, 40));
        panelAction.add(btnHapusAkun);

        panelBottomContainer.add(panelFormAdd);
        panelBottomContainer.add(panelAction);
        panelBottomContainer.setPreferredSize(new Dimension(0, 180));
        panel.add(panelBottomContainer, BorderLayout.SOUTH);

        return panel;
    }

    private void muatDataAdminCard() {
        panelListLaporan.removeAll();
        panelListJadwal.removeAll();

        String query = "SELECT * FROM laporan ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String fasilitas = rs.getString("jenis_fasilitas");
                String lokasi = rs.getString("lokasi");
                String tanggal = rs.getString("tanggal");
                String deskripsi = rs.getString("deskripsi");
                String status = rs.getString("status");
                String catatan = rs.getString("catatan");
                String keterangan = rs.getString("keterangan");

                if ("Menunggu".equalsIgnoreCase(status)) {
                    JPanel cardLaporan = createAdminReportCardWithButtons(id, fasilitas, lokasi, tanggal, deskripsi, status, keterangan);
                    cardLaporan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
                    panelListLaporan.add(cardLaporan);
                    panelListLaporan.add(Box.createRigidArea(new Dimension(0, 10)));
                }

                JPanel cardJadwal = createAdminScheduleCard(id, fasilitas, lokasi, status, catatan, keterangan);
                cardJadwal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

                if ("Diproses".equalsIgnoreCase(status)) {
                    cardJadwal.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            selectedIdJadwal = id;
                            txtIdLaporanJadwal.setText(id);
                            highlightSelectedCard(panelListJadwal, cardJadwal);
                        }
                    });
                } else {
                    cardJadwal.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                panelListJadwal.add(cardJadwal);
                panelListJadwal.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        panelListLaporan.revalidate();
        panelListLaporan.repaint();
        panelListJadwal.revalidate();
        panelListJadwal.repaint();
    }

    private JPanel createAdminReportCardWithButtons(String id, String fasilitas, String lokasi, String tanggal, String deskripsi, String status, String keterangan) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblHeader = new JLabel("<html><b>ID:</b> " + id + " &nbsp;&nbsp;|&nbsp;&nbsp; <font color='#64748B'>Tanggal: " + tanggal + " &nbsp;|&nbsp; Status: <b>" + status + "</b></font></html>");
        lblHeader.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblBody = new JLabel("<html><b>Fasilitas:</b> " + fasilitas + " &nbsp;&nbsp;|&nbsp;&nbsp; <b>Lokasi:</b> " + lokasi +
                "<br><b>Keluhan:</b> " + deskripsi + "</html>");
        lblBody.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel panelBtnCard = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBtnCard.setOpaque(false);

        JButton btnTerimaCard = new JButton("Terima");
        btnTerimaCard.setBackground(new Color(16, 185, 129)); btnTerimaCard.setForeground(Color.WHITE);
        btnTerimaCard.setFocusPainted(false); btnTerimaCard.setOpaque(true); btnTerimaCard.setBorderPainted(false);
        btnTerimaCard.addActionListener(e -> updateStatusDirect(id, "Diproses", "Laporan diterima admin."));

        JButton btnTolakCard = new JButton("Tolak");
        btnTolakCard.setBackground(new Color(239, 68, 68)); btnTolakCard.setForeground(Color.WHITE);
        btnTolakCard.setFocusPainted(false); btnTolakCard.setOpaque(true); btnTolakCard.setBorderPainted(false);
        btnTolakCard.addActionListener(e -> updateStatusDirect(id, "Ditolak", "Laporan ditolak oleh admin."));

        panelBtnCard.add(btnTerimaCard);
        panelBtnCard.add(btnTolakCard);

        card.add(lblHeader, BorderLayout.NORTH);
        card.add(lblBody, BorderLayout.CENTER);
        card.add(panelBtnCard, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createAdminScheduleCard(String id, String fasilitas, String lokasi, String status, String catatan, String keterangan) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblHeader = new JLabel("<html><b>ID:</b> " + id + " &nbsp;&nbsp;|&nbsp;&nbsp; <font color='#64748B'>Status: <b>" + status + "</b></font></html>");
        lblHeader.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblBody = new JLabel("<html><b>Fasilitas:</b> " + fasilitas + " &nbsp;&nbsp;|&nbsp;&nbsp; <b>Lokasi:</b> " + lokasi +
                "<br><font color='#059669'><b>Jadwal:</b> " + (catatan != null ? catatan : "-") + " &nbsp;|&nbsp; <b>Teknisi:</b> " + (keterangan != null ? keterangan : "-") + "</font></html>");
        lblBody.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        card.add(lblHeader, BorderLayout.NORTH);
        card.add(lblBody, BorderLayout.CENTER);
        return card;
    }

    private void muatDataUserCard() {
        panelListUser.removeAll();
        String query = "SELECT * FROM users WHERE role = 'pelapor' ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String status = rs.getString("status");

                JPanel cardUser = new JPanel(new BorderLayout(10, 5));
                cardUser.setBackground(Color.WHITE);
                cardUser.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                cardUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

                JLabel lblBody = new JLabel("<html><b>Username:</b> " + username + " &nbsp;|&nbsp; <b>Password:</b> " + password + " &nbsp;|&nbsp; <b>Status:</b> " + (status != null ? status : "Aktif") + "</html>");
                lblBody.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                cardUser.add(lblBody, BorderLayout.CENTER);
                panelListUser.add(cardUser);
                panelListUser.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        panelListUser.revalidate();
        panelListUser.repaint();
    }

    private void highlightSelectedCard(JPanel container, JPanel selectedCard) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                ((JPanel) comp).setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        }
        selectedCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(16, 185, 129), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    private void updateStatusDirect(String id, String statusBaru, String ketBaru) {
        String query = "UPDATE laporan SET status = ?, keterangan = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, statusBaru);
            pstmt.setString(2, ketBaru);
            pstmt.setString(3, id);
            pstmt.executeUpdate();
            muatDataAdminCard();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void simpanJadwalPerbaikanCard() {
        String idLaporan = txtIdLaporanJadwal.getText().trim();
        String jadwal = txtTanggalPerbaikan.getText().trim();
        String teknisi = (String) cbTeknisiAnggota.getSelectedItem();

        if (idLaporan.isEmpty() || jadwal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih card laporan berstatus Diproses terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String query = "UPDATE laporan SET catatan = ?, keterangan = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, jadwal);
            pstmt.setString(2, "Ditangani oleh: " + teknisi);
            pstmt.setString(3, idLaporan);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Jadwal dan teknisi berhasil disimpan!");
            txtIdLaporanJadwal.setText("");
            selectedIdJadwal = "";
            muatDataAdminCard();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void tandaiSelesaiPerbaikan() {
        if (selectedIdJadwal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih Card pada daftar Jadwal di atas terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String query = "UPDATE laporan SET status = ?, keterangan = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "Selesai");
            pstmt.setString(2, "Perbaikan telah selesai dikerjakan.");
            pstmt.setString(3, selectedIdJadwal);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Laporan ditandai Selesai!");
            selectedIdJadwal = "";
            muatDataAdminCard();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void tambahAkunPelapor() {
        String username = txtNewUsername.getText().trim();
        String password = new String(txtNewPassword.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username & Password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (username, password, role, status) VALUES (?, ?, 'pelapor', 'Aktif')";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Akun pelapor berhasil ditambahkan!");
            txtNewUsername.setText(""); txtNewPassword.setText("");
            muatDataUserCard();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}