package com.fakultas.fasilitas.view;

import com.fakultas.fasilitas.model.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private JPanel panelListLaporan, panelListJadwal, panelListUser, panelHistoryBulanan;
    private JTextField txtIdLaporanJadwal, txtTanggalPerbaikan;
    private JComboBox<String> cbTeknisiAnggota;
    private JTextField txtNewNim;
    private JPasswordField txtNewPassword;

    private String selectedIdJadwal = "";
    private final int adminId;
    private final String adminUsername;

    public AdminDashboardFrame() {
        this(1, "admin");
    }

    public AdminDashboardFrame(int adminId, String adminUsername) {
        this.adminId = adminId;
        this.adminUsername = adminUsername;

        setTitle("JagaRuang - Panel Admin (" + adminUsername + ")");
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
        btnHistory.addActionListener(e -> { cardLayout.show(cardPanel, "HISTORY"); muatGridHistoryBulanan(Calendar.getInstance()); });
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

        final Calendar cal = Calendar.getInstance();
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

        JPanel panelGridTanggal = new JPanel(new GridLayout(6, 7, 5, 5));
        panelGridTanggal.setBackground(Color.WHITE);
        panelGridTanggal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Runnable renderKalender = () -> {
            panelGridTanggal.removeAll();
            lblBulanTahun.setText(new java.text.SimpleDateFormat("MMMM yyyy").format(cal.getTime()));

            Calendar cTemp = (Calendar) cal.clone();
            cTemp.set(Calendar.DAY_OF_MONTH, 1);
            int hariPertama = cTemp.get(Calendar.DAY_OF_WEEK) - 1;
            int maxHari = cTemp.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int i = 0; i < hariPertama; i++) panelGridTanggal.add(new JLabel(""));

            int tahunIni = cal.get(Calendar.YEAR);
            int bulanIni = cal.get(Calendar.MONTH);

            for (int i = 1; i <= maxHari; i++) {
                final int day = i;
                JButton btnDay = new JButton(String.valueOf(day));
                btnDay.setFocusPainted(false);
                btnDay.setBackground(new Color(241, 245, 249));
                btnDay.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                btnDay.addActionListener(e -> {
                    txtTanggalPerbaikan.setText(String.format("%d-%02d-%02d", tahunIni, (bulanIni + 1), day));
                    dialogKalender.dispose();
                });
                panelGridTanggal.add(btnDay);
            }

            for (int i = hariPertama + maxHari; i < 42; i++) panelGridTanggal.add(new JLabel(""));
            panelGridTanggal.revalidate();
            panelGridTanggal.repaint();
        };

        btnPrev.addActionListener(e -> { cal.add(Calendar.MONTH, -1); renderKalender.run(); });
        btnNext.addActionListener(e -> { cal.add(Calendar.MONTH, 1); renderKalender.run(); });

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

        muatGridHistoryBulanan(Calendar.getInstance());
        panel.add(panelHistoryBulanan, BorderLayout.CENTER);
        return panel;
    }

    private void tampilkanPopUpDetailTanggal(String tglKey) {
        JDialog dialogDetail = new JDialog(this, "Rincian Tanggal: " + tglKey, true);
        dialogDetail.setSize(380, 360);
        dialogDetail.setLocationRelativeTo(this);
        dialogDetail.setLayout(new BorderLayout(10, 10));

        JPanel panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
        panelContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelContent.setBackground(Color.WHITE);

        List<String> listMasuk = new ArrayList<>();
        List<String> listSelesai = new ArrayList<>();
        List<String> listDitolak = new ArrayList<>();

        String sql = "SELECT id, status FROM laporan WHERE tanggal_lapor = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tglKey);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String status = rs.getString("status");
                    listMasuk.add(id);
                    if ("Selesai".equalsIgnoreCase(status)) {
                        listSelesai.add(id);
                    } else if ("Ditolak".equalsIgnoreCase(status)) {
                        listDitolak.add(id);
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

        JLabel lblInfoDitolak = new JLabel("<html><b>Laporan Ditolak (" + listDitolak.size() + "):</b><br>" +
                (listDitolak.isEmpty() ? "-" : String.join(", ", listDitolak)) + "</html>");
        lblInfoDitolak.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfoDitolak.setForeground(new Color(220, 38, 38));

        panelContent.add(lblInfoMasuk);
        panelContent.add(Box.createRigidArea(new Dimension(0, 15)));
        panelContent.add(lblInfoSelesai);
        panelContent.add(Box.createRigidArea(new Dimension(0, 15)));
        panelContent.add(lblInfoDitolak);

        dialogDetail.add(new JScrollPane(panelContent), BorderLayout.CENTER);

        JButton btnTutup = new JButton("Tutup");
        btnTutup.setBackground(new Color(16, 185, 129));
        btnTutup.setForeground(Color.WHITE);
        btnTutup.setFocusPainted(false); btnTutup.setOpaque(true); btnTutup.setBorderPainted(false);
        btnTutup.addActionListener(e -> dialogDetail.dispose());

        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBottom.add(btnTutup);
        dialogDetail.add(panelBottom, BorderLayout.SOUTH);
        dialogDetail.setVisible(true);
    }

    private void muatGridHistoryBulanan(Calendar cal) {
        panelHistoryBulanan.removeAll();

        Map<String, Integer> mapJmlMasuk = new HashMap<>();
        Map<String, Integer> mapJmlSelesai = new HashMap<>();
        Map<String, Integer> mapJmlDitolak = new HashMap<>();

        String sql = "SELECT tanggal_lapor, COUNT(*) AS jml_masuk, " +
                "SUM(status = 'Selesai') AS jml_selesai, " +
                "SUM(status = 'Ditolak') AS jml_ditolak " +
                "FROM laporan GROUP BY tanggal_lapor";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String tgl = rs.getString("tanggal_lapor");
                mapJmlMasuk.put(tgl, rs.getInt("jml_masuk"));
                mapJmlSelesai.put(tgl, rs.getInt("jml_selesai"));
                mapJmlDitolak.put(tgl, rs.getInt("jml_ditolak"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        panelHeader.setBackground(new Color(6, 78, 59));

        JLabel lblBulanTahun = new JLabel(new java.text.SimpleDateFormat("MMMM yyyy").format(cal.getTime()));
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
        for (String h : new String[]{"Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"}) {
            JLabel lblH = new JLabel(h, SwingConstants.CENTER);
            lblH.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblH.setForeground(new Color(15, 118, 110));
            panelGridHari.add(lblH);
        }
        panelCenterContainer.add(panelGridHari, BorderLayout.NORTH);

        JPanel panelGrid = new JPanel(new GridLayout(6, 7, 4, 4));
        panelGrid.setBackground(new Color(241, 245, 249));
        panelGrid.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

        Calendar cTemp = (Calendar) cal.clone();
        cTemp.set(Calendar.DAY_OF_MONTH, 1);
        int hariPertama = cTemp.get(Calendar.DAY_OF_WEEK) - 1;
        int maxHari = cTemp.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < hariPertama; i++) {
            JPanel pKosong = new JPanel();
            pKosong.setBackground(Color.WHITE);
            panelGrid.add(pKosong);
        }

        int tahunIni = cal.get(Calendar.YEAR);
        int bulanIni = cal.get(Calendar.MONTH);

        for (int i = 1; i <= maxHari; i++) {
            final int day = i;
            String tglKey = String.format("%d-%02d-%02d", tahunIni, (bulanIni + 1), day);
            int jmlMasuk = mapJmlMasuk.getOrDefault(tglKey, 0);
            int jmlSelesai = mapJmlSelesai.getOrDefault(tglKey, 0);
            int jmlDitolak = mapJmlDitolak.getOrDefault(tglKey, 0);

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

            if (jmlMasuk > 0 || jmlSelesai > 0 || jmlDitolak > 0) {
                JLabel lblInfo = new JLabel(String.format(
                        "<html><div style='font-size:9px; padding:2px 3px; color:#065f46;'>Masuk: <b>%d</b><br>Selesai: <b>%d</b><br><font color='#dc2626'>Ditolak: <b>%d</b></font></div></html>",
                        jmlMasuk, jmlSelesai, jmlDitolak
                ));
                btnDay.add(lblInfo, BorderLayout.CENTER);
                btnDay.setBackground(new Color(209, 250, 229));
            }

            btnDay.addActionListener(e -> tampilkanPopUpDetailTanggal(tglKey));
            panelGrid.add(btnDay);
        }

        for (int i = hariPertama + maxHari; i < 42; i++) {
            JPanel pKosong = new JPanel();
            pKosong.setBackground(Color.WHITE);
            panelGrid.add(pKosong);
        }

        btnPrev.addActionListener(e -> { cal.add(Calendar.MONTH, -1); muatGridHistoryBulanan(cal); });
        btnNext.addActionListener(e -> { cal.add(Calendar.MONTH, 1); muatGridHistoryBulanan(cal); });

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

        JLabel lblNimNew = new JLabel("NIM:");
        lblNimNew.setBounds(15, 25, 80, 20);
        panelFormAdd.add(lblNimNew);

        txtNewNim = new JTextField();
        txtNewNim.setBounds(100, 25, 190, 28);
        panelFormAdd.add(txtNewNim);

        JLabel lblPassNew = new JLabel("Password:");
        lblPassNew.setBounds(15, 65, 80, 20);
        panelFormAdd.add(lblPassNew);

        txtNewPassword = new JPasswordField();
        txtNewPassword.setBounds(100, 65, 190, 28);
        panelFormAdd.add(txtNewPassword);

        JButton btnSimpanAkun = new JButton("Simpan Akun");
        btnSimpanAkun.setBackground(new Color(16, 185, 129));
        btnSimpanAkun.setForeground(Color.WHITE);
        btnSimpanAkun.setFocusPainted(false); btnSimpanAkun.setOpaque(true); btnSimpanAkun.setBorderPainted(false);
        btnSimpanAkun.setBounds(100, 110, 190, 35);
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

    private String formatNamaPelapor(String namaLengkap, String nim) {
        if (namaLengkap != null && !namaLengkap.isBlank() && !namaLengkap.trim().equals(nim)) {
            return namaLengkap.trim();
        }
        return "Mahasiswa";
    }

    private void muatDataAdminCard() {
        panelListLaporan.removeAll();
        panelListJadwal.removeAll();

        String sql = "SELECT l.id, f.nama_fasilitas AS fasilitas, gr.nama_lokasi AS lokasi, " +
                "l.tanggal_lapor AS tanggal, l.deskripsi, l.status, " +
                "u.nim, u.nama_lengkap, pl.jadwal_perbaikan, pl.nama_teknisi " +
                "FROM laporan l " +
                "JOIN gedung_ruangan gr ON l.ruangan_id = gr.id " +
                "JOIN fasilitas f ON l.fasilitas_id = f.id " +
                "JOIN users u ON l.pelapor_id = u.id " +
                "LEFT JOIN penanganan_laporan pl ON pl.laporan_id = l.id " +
                "ORDER BY l.id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String fasilitas = rs.getString("fasilitas");
                String lokasi = rs.getString("lokasi");
                String tanggal = rs.getString("tanggal");
                String deskripsi = rs.getString("deskripsi");
                String status = rs.getString("status");
                String namaPelapor = formatNamaPelapor(rs.getString("nama_lengkap"), rs.getString("nim"));
                String jadwal = rs.getString("jadwal_perbaikan");
                String teknisi = rs.getString("nama_teknisi");

                if ("Menunggu".equalsIgnoreCase(status)) {
                    JPanel cardLaporan = createAdminReportCardWithButtons(id, namaPelapor, fasilitas, lokasi, tanggal, deskripsi, status);
                    cardLaporan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
                    panelListLaporan.add(cardLaporan);
                    panelListLaporan.add(Box.createRigidArea(new Dimension(0, 10)));
                }

                if (!"Ditolak".equalsIgnoreCase(status)) {
                    JPanel cardJadwal = createAdminScheduleCard(id, namaPelapor, fasilitas, lokasi, status, jadwal, teknisi);
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        panelListLaporan.revalidate(); panelListLaporan.repaint();
        panelListJadwal.revalidate(); panelListJadwal.repaint();
    }

    private JPanel createAdminReportCardWithButtons(String id, String namaPelapor, String fasilitas, String lokasi,
                                                    String tanggal, String deskripsi, String status) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblHeader = new JLabel("<html><b>ID:</b> " + id + "   |   <b>Pelapor:</b> " + namaPelapor +
                "   |   <font color='#64748B'>Tanggal: " + tanggal + "  |  Status: <b>" + status + "</b></font></html>");
        lblHeader.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblBody = new JLabel("<html><b>Fasilitas:</b> " + fasilitas + "   |   <b>Lokasi:</b> " + lokasi +
                "<br><b>Keluhan:</b> " + deskripsi + "</html>");
        lblBody.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel panelBtnCard = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBtnCard.setOpaque(false);

        JButton btnTerimaCard = new JButton("Terima");
        btnTerimaCard.setBackground(new Color(16, 185, 129)); btnTerimaCard.setForeground(Color.WHITE);
        btnTerimaCard.setFocusPainted(false); btnTerimaCard.setOpaque(true); btnTerimaCard.setBorderPainted(false);
        btnTerimaCard.addActionListener(e -> terimaLaporan(id));

        JButton btnTolakCard = new JButton("Tolak");
        btnTolakCard.setBackground(new Color(239, 68, 68)); btnTolakCard.setForeground(Color.WHITE);
        btnTolakCard.setFocusPainted(false); btnTolakCard.setOpaque(true); btnTolakCard.setBorderPainted(false);
        btnTolakCard.addActionListener(e -> tolakLaporanDenganFeedback(id));

        panelBtnCard.add(btnTerimaCard);
        panelBtnCard.add(btnTolakCard);

        card.add(lblHeader, BorderLayout.NORTH);
        card.add(lblBody, BorderLayout.CENTER);
        card.add(panelBtnCard, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createAdminScheduleCard(String id, String namaPelapor, String fasilitas, String lokasi,
                                           String status, String jadwal, String teknisi) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblHeader = new JLabel("<html><b>ID:</b> " + id + "   |   <b>Pelapor:</b> " + namaPelapor +
                "   |   <font color='#64748B'>Status: <b>" + status + "</b></font></html>");
        lblHeader.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblBody = new JLabel("<html><b>Fasilitas:</b> " + fasilitas + "   |   <b>Lokasi:</b> " + lokasi +
                "<br><font color='#059669'><b>Jadwal:</b> " + (jadwal != null && !jadwal.isBlank() ? jadwal : "-") +
                "  |  <b>Teknisi:</b> " + (teknisi != null && !teknisi.isBlank() ? teknisi : "-") + "</font></html>");
        lblBody.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        card.add(lblHeader, BorderLayout.NORTH);
        card.add(lblBody, BorderLayout.CENTER);
        return card;
    }

    private void muatDataUserCard() {
        panelListUser.removeAll();
        String sql = "SELECT nim, nama_lengkap, password_hash, status_akun " +
                "FROM users WHERE role = 'pelapor' ORDER BY id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String nim = rs.getString("nim");
                String nama = rs.getString("nama_lengkap");
                String namaTampil = (nama != null && !nama.isBlank()) ? nama : "-";

                JPanel cardUser = new JPanel(new BorderLayout(10, 5));
                cardUser.setBackground(Color.WHITE);
                cardUser.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                cardUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

                JLabel lblBody = new JLabel("<html><b>NIM:</b> " + (nim != null ? nim : "-") +
                        "  |  <b>Nama:</b> " + namaTampil +
                        "  |  <b>Password:</b> " + rs.getString("password_hash") +
                        "  |  <b>Status:</b> " + rs.getString("status_akun") + "</html>");
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

    private void terimaLaporan(String idLaporan) {
        String sql = "UPDATE laporan SET status = 'Diproses' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idLaporan);
            ps.executeUpdate();
            muatDataAdminCard();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void tolakLaporanDenganFeedback(String idLaporan) {
        JTextArea txtFeedback = new JTextArea(4, 30);
        txtFeedback.setLineWrap(true);
        txtFeedback.setWrapStyleWord(true);

        JPanel dialogPanel = new JPanel(new BorderLayout(6, 6));
        dialogPanel.add(new JLabel("Masukkan feedback / alasan penolakan untuk " + idLaporan + ":"), BorderLayout.NORTH);
        dialogPanel.add(new JScrollPane(txtFeedback), BorderLayout.CENTER);

        int res = JOptionPane.showConfirmDialog(this, dialogPanel, "Feedback Penolakan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String alasan = txtFeedback.getText().trim();
        if (alasan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Feedback / alasan penolakan wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement psUpd = conn.prepareStatement("UPDATE laporan SET status = 'Ditolak' WHERE id = ?")) {
                psUpd.setString(1, idLaporan);
                psUpd.executeUpdate();
            }
            String sqlTolak = "INSERT INTO riwayat_penolakan (laporan_id, admin_id, alasan_penolakan) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE admin_id = VALUES(admin_id), alasan_penolakan = VALUES(alasan_penolakan)";
            try (PreparedStatement psTolak = conn.prepareStatement(sqlTolak)) {
                psTolak.setString(1, idLaporan);
                psTolak.setInt(2, adminId);
                psTolak.setString(3, alasan);
                psTolak.executeUpdate();
            }
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

        String sql = "INSERT INTO penanganan_laporan (laporan_id, admin_id, jadwal_perbaikan, nama_teknisi) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE admin_id = VALUES(admin_id), jadwal_perbaikan = VALUES(jadwal_perbaikan), nama_teknisi = VALUES(nama_teknisi)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idLaporan);
            ps.setInt(2, adminId);
            ps.setString(3, jadwal);
            ps.setString(4, teknisi);
            ps.executeUpdate();

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

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE laporan SET status = 'Selesai' WHERE id = ?")) {
                ps.setString(1, selectedIdJadwal);
                ps.executeUpdate();
            }
            try (PreparedStatement psPen = conn.prepareStatement("UPDATE penanganan_laporan SET tanggal_selesai = ? WHERE laporan_id = ?")) {
                psPen.setString(1, LocalDate.now().toString());
                psPen.setString(2, selectedIdJadwal);
                psPen.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Laporan ditandai Selesai!");
            selectedIdJadwal = "";
            muatDataAdminCard();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void tambahAkunPelapor() {
        String nim = txtNewNim.getText().trim();
        String password = new String(txtNewPassword.getPassword()).trim();
        if (nim.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIM & Password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO users (nim, username, nama_lengkap, password_hash, role, status_akun) " +
                "VALUES (?, ?, ?, ?, 'pelapor', 'aktif')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nim);
            ps.setString(2, nim);
            ps.setString(3, nim);
            ps.setString(4, password);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Akun pelapor berhasil ditambahkan!");
            txtNewNim.setText("");
            txtNewPassword.setText("");
            muatDataUserCard();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menambah akun: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}