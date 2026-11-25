package com.fakultas.fasilitas.view;

import com.fakultas.fasilitas.model.DatabaseConnection; // Sesuaikan jika package-nya berbeda
import com.fakultas.fasilitas.model.ModelLaporan;
import com.fakultas.fasilitas.util.PasswordUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardFrame extends JFrame {

    private final String namaAdmin;
    private final String roleAdmin;

    private JLabel lblStatMenunggu;
    private JLabel lblStatDiproses;
    private JLabel lblStatSelesai;

    private JPanel panelDaftarLaporan;
    private JPanel panelDaftarJadwal;
    private JPanel panelDaftarUser;
    private JPanel panelKalenderBulanan;
    private JPanel panelRincianBulanan;

    private JLabel lblSelectedJadwal;
    private JTextField txtTanggalPerbaikan;
    private JTextField txtNamaTeknisi;
    private JButton btnSimpanJadwal;
    private JButton btnSelesai;

    private JLabel lblSelectedUser;
    private JTextField txtEditNim;
    private JTextField txtEditNama;
    private JComboBox<String> cmbEditRole;
    private JTextField txtPasswordBaru;
    private JButton btnSimpanAkun;
    private JButton btnUpdatePass;

    private JComboBox<String> cmbBulanHistory;
    private JComboBox<String> cmbTahunHistory;
    private JLabel lblJudulBulanHistory;

    private JPanel selectedJadwalCard;
    private String selectedLaporanId;

    private JPanel selectedUserCard;
    private String selectedUserId;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AdminDashboardFrame(String namaAdmin, String roleAdmin) {
        this.namaAdmin = namaAdmin != null ? namaAdmin : "Admin";
        this.roleAdmin = roleAdmin != null ? roleAdmin : "Admin";

        setTitle("JagaRuang - Dashboard Admin");
        setSize(1280, 780);
        setMinimumSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
        muatSemuaData();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(new Color(241, 245, 249));
        root.setBorder(new EmptyBorder(14, 16, 14, 16));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel lblApp = new JLabel("JagaRuang - Panel Administrasi Fasilitas");
        lblApp.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblApp.setForeground(new Color(15, 23, 42));

        JLabel lblSub = new JLabel("Login sebagai: " + namaAdmin + " (" + roleAdmin + ")");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(new Color(100, 116, 139));

        titleBox.add(lblApp);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(lblSub);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false);

        JButton btnRefresh = new JButton("Refresh Data");
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                muatSemuaData();
            }
        });

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(220, 38, 38));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        rightHeader.add(btnRefresh);
        rightHeader.add(btnLogout);

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        statsPanel.setOpaque(false);

        lblStatMenunggu = new JLabel("0");
        lblStatDiproses = new JLabel("0");
        lblStatSelesai = new JLabel("0");

        statsPanel.add(createStatCard("Menunggu Tinjauan", lblStatMenunggu, new Color(217, 119, 6)));
        statsPanel.add(createStatCard("Sedang Diproses", lblStatDiproses, new Color(37, 99, 235)));
        statsPanel.add(createStatCard("Selesai", lblStatSelesai, new Color(22, 163, 74)));

        JPanel topContainer = new JPanel(new BorderLayout(0, 10));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(statsPanel, BorderLayout.SOUTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.addTab("Peninjauan & Penjadwalan", buildPeninjauanDanJadwalTab());
        tabbedPane.addTab("History Bulanan", buildHistoryBulananTab());
        tabbedPane.addTab("Kelola Akun User", buildKelolaAkunTab());

        root.add(topContainer, BorderLayout.NORTH);
        root.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblTitle.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        valueLabel.setForeground(accent);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildPeninjauanDanJadwalTab() {
        JPanel tab = new JPanel(new GridLayout(1, 2, 14, 0));
        tab.setBackground(new Color(241, 245, 249));
        tab.setBorder(new EmptyBorder(10, 4, 4, 4));

        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblLeftTitle = new JLabel("Laporan Masuk (Menunggu)");
        lblLeftTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblLeftTitle.setForeground(new Color(15, 23, 42));

        panelDaftarLaporan = new JPanel();
        panelDaftarLaporan.setLayout(new BoxLayout(panelDaftarLaporan, BoxLayout.Y_AXIS));
        panelDaftarLaporan.setBackground(Color.WHITE);

        JScrollPane scrollLaporan = new JScrollPane(panelDaftarLaporan);
        scrollLaporan.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollLaporan.getVerticalScrollBar().setUnitIncrement(16);

        leftPanel.add(lblLeftTitle, BorderLayout.NORTH);
        leftPanel.add(scrollLaporan, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblRightTitle = new JLabel("Jadwal & Penyelesaian Perbaikan");
        lblRightTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblRightTitle.setForeground(new Color(15, 23, 42));

        panelDaftarJadwal = new JPanel();
        panelDaftarJadwal.setLayout(new BoxLayout(panelDaftarJadwal, BoxLayout.Y_AXIS));
        panelDaftarJadwal.setBackground(Color.WHITE);

        JScrollPane scrollJadwal = new JScrollPane(panelDaftarJadwal);
        scrollJadwal.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollJadwal.setPreferredSize(new Dimension(0, 300));
        scrollJadwal.getVerticalScrollBar().setUnitIncrement(16);

        JPanel formJadwal = new JPanel(new GridBagLayout());
        formJadwal.setBackground(new Color(248, 250, 252));
        formJadwal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblSelectedJadwal = new JLabel("Terpilih: Belum ada laporan dipilih");
        lblSelectedJadwal.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblSelectedJadwal.setForeground(new Color(30, 41, 59));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        formJadwal.add(lblSelectedJadwal, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        formJadwal.add(new JLabel("Tanggal (YYYY-MM-DD):"), gbc);

        txtTanggalPerbaikan = new JTextField(12);
        txtTanggalPerbaikan.setEditable(false);
        txtTanggalPerbaikan.setBackground(Color.WHITE);
        txtTanggalPerbaikan.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        txtTanggalPerbaikan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bukaPilihTanggalDialog();
            }
        });
        gbc.gridx = 1;
        gbc.weightx = 1;
        formJadwal.add(txtTanggalPerbaikan, gbc);

        JButton btnPilihTanggal = new JButton("Pilih Tanggal");
        btnPilihTanggal.setFocusPainted(false);
        btnPilihTanggal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bukaPilihTanggalDialog();
            }
        });
        gbc.gridx = 2;
        gbc.weightx = 0;
        formJadwal.add(btnPilihTanggal, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        formJadwal.add(new JLabel("Nama Teknisi:"), gbc);

        txtNamaTeknisi = new JTextField();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        formJadwal.add(txtNamaTeknisi, gbc);

        JPanel btnJadwalRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnJadwalRow.setOpaque(false);

        btnSimpanJadwal = new JButton("Simpan Jadwal");
        btnSimpanJadwal.setBackground(new Color(37, 99, 235));
        btnSimpanJadwal.setForeground(Color.WHITE);
        btnSimpanJadwal.setFocusPainted(false);
        btnSimpanJadwal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simpanJadwalPerbaikan();
            }
        });

        btnSelesai = new JButton("Tandai Selesai");
        btnSelesai.setBackground(new Color(22, 163, 74));
        btnSelesai.setForeground(Color.WHITE);
        btnSelesai.setFocusPainted(false);
        btnSelesai.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tandaiSelesaiPerbaikan();
            }
        });

        btnJadwalRow.add(btnSimpanJadwal);
        btnJadwalRow.add(btnSelesai);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        formJadwal.add(btnJadwalRow, gbc);

        JPanel topRight = new JPanel(new BorderLayout(0, 6));
        topRight.setOpaque(false);
        topRight.add(lblRightTitle, BorderLayout.NORTH);
        topRight.add(scrollJadwal, BorderLayout.CENTER);

        rightPanel.add(topRight, BorderLayout.CENTER);
        rightPanel.add(formJadwal, BorderLayout.SOUTH);

        tab.add(leftPanel);
        tab.add(rightPanel);
        return tab;
    }

    private JPanel buildHistoryBulananTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 10));
        tab.setBackground(new Color(241, 245, 249));
        tab.setBorder(new EmptyBorder(10, 4, 4, 4));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filterBar.setBackground(Color.WHITE);
        filterBar.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        String[] bulanList = {
                "01 - Januari", "02 - Februari", "03 - Maret", "04 - April",
                "05 - Mei", "06 - Juni", "07 - Juli", "08 - Agustus",
                "09 - September", "10 - Oktober", "11 - November", "12 - Desember"
        };
        cmbBulanHistory = new JComboBox<>(bulanList);
        cmbBulanHistory.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        int tahunSekarang = LocalDate.now().getYear();
        List<String> tahunItems = new ArrayList<>();
        for (int y = tahunSekarang + 1; y >= tahunSekarang - 4; y--) {
            tahunItems.add(String.valueOf(y));
        }
        cmbTahunHistory = new JComboBox<>(tahunItems.toArray(new String[0]));
        cmbTahunHistory.setSelectedItem(String.valueOf(tahunSekarang));

        JButton btnTampilBulan = new JButton("Tampilkan");
        btnTampilBulan.setFocusPainted(false);
        btnTampilBulan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                muatHistoryBulanan();
            }
        });

        filterBar.add(new JLabel("Bulan:"));
        filterBar.add(cmbBulanHistory);
        filterBar.add(new JLabel("Tahun:"));
        filterBar.add(cmbTahunHistory);
        filterBar.add(btnTampilBulan);

        JPanel contentSplit = new JPanel(new GridLayout(1, 2, 14, 0));
        contentSplit.setOpaque(false);

        JPanel calCard = new JPanel(new BorderLayout(0, 8));
        calCard.setBackground(Color.WHITE);
        calCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        lblJudulBulanHistory = new JLabel("Kalender Laporan");
        lblJudulBulanHistory.setFont(new Font("SansSerif", Font.BOLD, 14));

        panelKalenderBulanan = new JPanel(new GridLayout(0, 7, 6, 6));
        panelKalenderBulanan.setBackground(Color.WHITE);

        calCard.add(lblJudulBulanHistory, BorderLayout.NORTH);
        calCard.add(panelKalenderBulanan, BorderLayout.CENTER);

        JPanel detailCard = new JPanel(new BorderLayout(0, 8));
        detailCard.setBackground(Color.WHITE);
        detailCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblDetailTitle = new JLabel("Rincian Laporan Bulan Terpilih");
        lblDetailTitle.setFont(new Font("SansSerif", Font.BOLD, 14));

        panelRincianBulanan = new JPanel();
        panelRincianBulanan.setLayout(new BoxLayout(panelRincianBulanan, BoxLayout.Y_AXIS));
        panelRincianBulanan.setBackground(Color.WHITE);

        JScrollPane scrollRincian = new JScrollPane(panelRincianBulanan);
        scrollRincian.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollRincian.getVerticalScrollBar().setUnitIncrement(16);

        detailCard.add(lblDetailTitle, BorderLayout.NORTH);
        detailCard.add(scrollRincian, BorderLayout.CENTER);

        contentSplit.add(calCard);
        contentSplit.add(detailCard);

        tab.add(filterBar, BorderLayout.NORTH);
        tab.add(contentSplit, BorderLayout.CENTER);
        return tab;
    }

    private JPanel buildKelolaAkunTab() {
        JPanel tab = new JPanel(new GridLayout(1, 2, 14, 0));
        tab.setBackground(new Color(241, 245, 249));
        tab.setBorder(new EmptyBorder(10, 4, 4, 4));

        JPanel listCard = new JPanel(new BorderLayout(0, 8));
        listCard.setBackground(Color.WHITE);
        listCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblListUser = new JLabel("Daftar Akun Pengguna");
        lblListUser.setFont(new Font("SansSerif", Font.BOLD, 14));

        panelDaftarUser = new JPanel();
        panelDaftarUser.setLayout(new BoxLayout(panelDaftarUser, BoxLayout.Y_AXIS));
        panelDaftarUser.setBackground(Color.WHITE);

        JScrollPane scrollUser = new JScrollPane(panelDaftarUser);
        scrollUser.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        scrollUser.getVerticalScrollBar().setUnitIncrement(16);

        listCard.add(lblListUser, BorderLayout.NORTH);
        listCard.add(scrollUser, BorderLayout.CENTER);

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblSelectedUser = new JLabel("Pilih akun di sebelah kiri untuk mengedit");
        lblSelectedUser.setFont(new Font("SansSerif", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formCard.add(lblSelectedUser, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        formCard.add(new JLabel("NIM / Username:"), gbc);

        txtEditNim = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1;
        formCard.add(txtEditNim, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        formCard.add(new JLabel("Nama Lengkap:"), gbc);

        txtEditNama = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1;
        formCard.add(txtEditNama, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0;
        formCard.add(new JLabel("Role:"), gbc);

        cmbEditRole = new JComboBox<>(new String[]{"Pelapor", "Admin"});
        gbc.gridx = 1;
        gbc.weightx = 1;
        formCard.add(cmbEditRole, gbc);

        btnSimpanAkun = new JButton("Simpan Profil / Role");
        btnSimpanAkun.setBackground(new Color(37, 99, 235));
        btnSimpanAkun.setForeground(Color.WHITE);
        btnSimpanAkun.setFocusPainted(false);
        btnSimpanAkun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simpanPerubahanAkun();
            }
        });

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        formCard.add(btnSimpanAkun, gbc);

        JSeparator sep = new JSeparator();
        gbc.gridy = 5;
        formCard.add(sep, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.weightx = 0;
        formCard.add(new JLabel("Password Baru:"), gbc);

        txtPasswordBaru = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1;
        formCard.add(txtPasswordBaru, gbc);

        btnUpdatePass = new JButton("Reset Password User");
        btnUpdatePass.setBackground(new Color(15, 23, 42));
        btnUpdatePass.setForeground(Color.WHITE);
        btnUpdatePass.setFocusPainted(false);
        btnUpdatePass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetPasswordUser();
            }
        });

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        formCard.add(btnUpdatePass, gbc);

        tab.add(listCard);
        tab.add(formCard);
        return tab;
    }

    private void muatSemuaData() {
        muatDataLaporanDanJadwal();
        muatHistoryBulanan();
        muatDaftarUser();
    }

    private void muatDataLaporanDanJadwal() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT l.id, l.tanggal_lapor, l.deskripsi, l.status, "
                    + "gr.nama_lokasi AS lokasi, f.nama_fasilitas AS fasilitas, "
                    + "u.nim, u.nama_lengkap, "
                    + "pl.jadwal_perbaikan, pl.nama_teknisi, "
                    + "rp.alasan_penolakan "
                    + "FROM laporan l "
                    + "JOIN gedung_ruangan gr ON l.ruangan_id = gr.id "
                    + "JOIN fasilitas f ON l.fasilitas_id = f.id "
                    + "JOIN users u ON l.pelapor_id = u.id "
                    + "LEFT JOIN penanganan_laporan pl ON pl.laporan_id = l.id "
                    + "LEFT JOIN riwayat_penolakan rp ON rp.laporan_id = l.id "
                    + "ORDER BY l.tanggal_lapor DESC, l.id DESC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            panelDaftarLaporan.removeAll();
            panelDaftarJadwal.removeAll();
            selectedJadwalCard = null;
            selectedLaporanId = null;

            int totalMenunggu = 0;
            int totalDiproses = 0;
            int totalSelesai = 0;

            while (rs.next()) {
                ModelLaporan item = new ModelLaporan();
                item.setId(rs.getString("id"));
                item.setTanggalLapor(rs.getString("tanggal_lapor"));
                item.setDeskripsi(rs.getString("deskripsi"));
                item.setStatus(rs.getString("status"));
                item.setLokasi(rs.getString("lokasi"));
                item.setFasilitas(rs.getString("fasilitas"));
                item.setPelapor(formatNamaPelapor(rs.getString("nama_lengkap"), rs.getString("nim")));
                item.setJadwalPerbaikan(rs.getString("jadwal_perbaikan"));
                item.setNamaTeknisi(rs.getString("nama_teknisi"));
                item.setAlasanPenolakan(rs.getString("alasan_penolakan"));

                String st = item.getStatus() != null ? item.getStatus() : "Menunggu";
                if ("Menunggu".equalsIgnoreCase(st)) {
                    totalMenunggu++;
                } else if ("Diproses".equalsIgnoreCase(st)) {
                    totalDiproses++;
                } else if ("Selesai".equalsIgnoreCase(st)) {
                    totalSelesai++;
                }

                if ("Menunggu".equalsIgnoreCase(st)) {
                    JPanel cardLap = createLaporanCard(item);
                    panelDaftarLaporan.add(cardLap);
                    panelDaftarLaporan.add(Box.createVerticalStrut(12));
                }

                if ("Diproses".equalsIgnoreCase(st) || "Selesai".equalsIgnoreCase(st)) {
                    JPanel cardJad = createJadwalCard(item);
                    panelDaftarJadwal.add(cardJad);
                    panelDaftarJadwal.add(Box.createVerticalStrut(10));
                }
            }

            if (lblStatMenunggu != null) lblStatMenunggu.setText(String.valueOf(totalMenunggu));
            if (lblStatDiproses != null) lblStatDiproses.setText(String.valueOf(totalDiproses));
            if (lblStatSelesai != null) lblStatSelesai.setText(String.valueOf(totalSelesai));

            if (panelDaftarLaporan.getComponentCount() == 0) {
                JLabel kosong = new JLabel("Tidak ada laporan yang menunggu tinjauan.");
                kosong.setFont(new Font("SansSerif", Font.PLAIN, 13));
                kosong.setForeground(new Color(100, 116, 139));
                panelDaftarLaporan.add(kosong);
            }

            if (panelDaftarJadwal.getComponentCount() == 0) {
                JLabel kosongJadwal = new JLabel("Belum ada laporan yang dijadwalkan.");
                kosongJadwal.setFont(new Font("SansSerif", Font.PLAIN, 13));
                kosongJadwal.setForeground(new Color(100, 116, 139));
                panelDaftarJadwal.add(kosongJadwal);
            }

            panelDaftarLaporan.revalidate();
            panelDaftarLaporan.repaint();
            panelDaftarJadwal.revalidate();
            panelDaftarJadwal.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data laporan: " + e.getMessage());
        } finally {
            tutupResource(rs, ps, conn);
        }
    }

    private JPanel createLaporanCard(final ModelLaporan item) {
        final String id = item.getId();
        String fasilitas = item.getFasilitas() != null ? item.getFasilitas() : "-";
        String lokasi = item.getLokasi() != null ? item.getLokasi() : "-";
        String tanggal = item.getTanggalLapor() != null ? item.getTanggalLapor() : "-";
        String deskripsi = item.getDeskripsi() != null ? item.getDeskripsi() : "-";
        String status = item.getStatus() != null ? item.getStatus() : "Menunggu";
        String pelapor = item.getPelapor() != null ? item.getPelapor() : "-";

        JPanel card = new JPanel(new BorderLayout(12, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblJudul = new JLabel("#" + id + " - " + fasilitas + " (" + lokasi + ")");
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblJudul.setForeground(new Color(15, 23, 42));

        JLabel lblMeta = new JLabel("Pelapor: " + pelapor + "  |  Tanggal: " + tanggal + "  |  Status: " + status);
        lblMeta.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblMeta.setForeground(new Color(71, 85, 105));

        JTextArea txtDesk = new JTextArea(deskripsi);
        txtDesk.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtDesk.setForeground(new Color(30, 41, 59));
        txtDesk.setLineWrap(true);
        txtDesk.setWrapStyleWord(true);
        txtDesk.setEditable(false);
        txtDesk.setOpaque(false);
        txtDesk.setBorder(null);

        infoPanel.add(lblJudul);
        infoPanel.add(Box.createVerticalStrut(4));
        infoPanel.add(lblMeta);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(txtDesk);

        JPanel aksiPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        aksiPanel.setOpaque(false);

        JButton btnTerima = new JButton("Terima");
        btnTerima.setBackground(new Color(22, 163, 74));
        btnTerima.setForeground(Color.WHITE);
        btnTerima.setFocusPainted(false);

        JButton btnTolak = new JButton("Tolak");
        btnTolak.setBackground(new Color(220, 38, 38));
        btnTolak.setForeground(Color.WHITE);
        btnTolak.setFocusPainted(false);

        btnTerima.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prosesTerimaLaporan(id);
            }
        });

        btnTolak.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prosesTolakLaporan(id);
            }
        });

        aksiPanel.add(btnTerima);
        aksiPanel.add(btnTolak);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(aksiPanel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createJadwalCard(final ModelLaporan item) {
        final String id = item.getId();
        String fasilitas = item.getFasilitas() != null ? item.getFasilitas() : "-";
        String lokasi = item.getLokasi() != null ? item.getLokasi() : "-";
        String status = item.getStatus() != null ? item.getStatus() : "-";
        String jadwal = (item.getJadwalPerbaikan() != null && !item.getJadwalPerbaikan().trim().isEmpty())
                ? item.getJadwalPerbaikan() : "Belum dijadwalkan";
        String teknisi = (item.getNamaTeknisi() != null && !item.getNamaTeknisi().trim().isEmpty())
                ? item.getNamaTeknisi() : "Belum ditugaskan";

        final JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        JLabel lblId = new JLabel("#" + id + " - " + fasilitas + " (" + status + ")");
        lblId.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblId.setForeground(new Color(15, 23, 42));

        JLabel lblLok = new JLabel("Lokasi: " + lokasi);
        lblLok.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblLok.setForeground(new Color(71, 85, 105));

        JLabel lblJad = new JLabel("Jadwal: " + jadwal + " | Teknisi: " + teknisi);
        lblJad.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblJad.setForeground(new Color(51, 65, 85));

        card.add(lblId);
        card.add(Box.createVerticalStrut(3));
        card.add(lblLok);
        card.add(Box.createVerticalStrut(3));
        card.add(lblJad);

        attachJadwalClick(card, item);
        return card;
    }

    private void attachJadwalClick(Component comp, final ModelLaporan item) {
        comp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        comp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel targetCard = findParentJadwalCard(e.getComponent());
                if (targetCard != null) {
                    selectedJadwalCard = targetCard;
                    highlightCard(panelDaftarJadwal, targetCard);
                }
                selectedLaporanId = item.getId();
                if (lblSelectedJadwal != null) {
                    lblSelectedJadwal.setText("Terpilih: #" + item.getId() + " (" + item.getFasilitas() + ")");
                }
                if (txtTanggalPerbaikan != null && item.getJadwalPerbaikan() != null) {
                    txtTanggalPerbaikan.setText(item.getJadwalPerbaikan());
                }
                if (txtNamaTeknisi != null && item.getNamaTeknisi() != null) {
                    txtNamaTeknisi.setText(item.getNamaTeknisi());
                }
            }
        });

        if (comp instanceof Container) {
            Container c = (Container) comp;
            for (int i = 0; i < c.getComponentCount(); i++) {
                attachJadwalClick(c.getComponent(i), item);
            }
        }
    }

    private JPanel findParentJadwalCard(Component comp) {
        Component curr = comp;
        while (curr != null && curr.getParent() != panelDaftarJadwal) {
            curr = curr.getParent();
        }
        return (curr instanceof JPanel) ? (JPanel) curr : null;
    }

    private void highlightCard(JPanel container, JPanel selectedCard) {
        if (container == null) return;
        for (Component c : container.getComponents()) {
            if (c instanceof JPanel) {
                JPanel p = (JPanel) c;
                boolean isSelected = (p == selectedCard);
                p.setBackground(isSelected ? new Color(239, 246, 255) : Color.WHITE);
                p.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                isSelected ? new Color(37, 99, 235) : new Color(203, 213, 225),
                                isSelected ? 2 : 1
                        ),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }
        }
        container.repaint();
    }

    private void prosesTerimaLaporan(String laporanId) {
        Connection conn = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psInsert = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            psUpdate = conn.prepareStatement("UPDATE laporan SET status = 'Diproses' WHERE id = ?");
            psUpdate.setString(1, laporanId);
            psUpdate.executeUpdate();

            psInsert = conn.prepareStatement(
                    "INSERT INTO penanganan_laporan (laporan_id, status_penanganan) VALUES (?, 'Diproses') "
                            + "ON DUPLICATE KEY UPDATE status_penanganan = VALUES(status_penanganan)"
            );
            psInsert.setString(1, laporanId);
            psInsert.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Laporan #" + laporanId + " diterima dan berstatus Diproses.");
            muatDataLaporanDanJadwal();
            muatHistoryBulanan();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            JOptionPane.showMessageDialog(this, "Gagal menerima laporan: " + e.getMessage());
        } finally {
            tutupResource(null, psUpdate, null);
            tutupResource(null, psInsert, conn);
        }
    }

    private void prosesTolakLaporan(String laporanId) {
        String feedback = JOptionPane.showInputDialog(
                this,
                "Alasan penolakan / feedback (opsional):",
                "Tolak Laporan #" + laporanId,
                JOptionPane.PLAIN_MESSAGE
        );
        if (feedback == null) {
            return;
        }

        Connection conn = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psInsert = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            psUpdate = conn.prepareStatement("UPDATE laporan SET status = 'Ditolak' WHERE id = ?");
            psUpdate.setString(1, laporanId);
            psUpdate.executeUpdate();

            String trimmed = feedback.trim();
            psInsert = conn.prepareStatement(
                    "INSERT INTO riwayat_penolakan (laporan_id, alasan_penolakan) VALUES (?, ?) "
                            + "ON DUPLICATE KEY UPDATE alasan_penolakan = VALUES(alasan_penolakan)"
            );
            psInsert.setString(1, laporanId);
            if (trimmed.isEmpty()) {
                psInsert.setNull(2, java.sql.Types.VARCHAR);
            } else {
                psInsert.setString(2, trimmed);
            }
            psInsert.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Laporan #" + laporanId + " telah ditolak.");
            muatDataLaporanDanJadwal();
            muatHistoryBulanan();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            JOptionPane.showMessageDialog(this, "Gagal menolak laporan: " + e.getMessage());
        } finally {
            tutupResource(null, psUpdate, null);
            tutupResource(null, psInsert, conn);
        }
    }

    private void simpanJadwalPerbaikan() {
        if (selectedLaporanId == null || selectedLaporanId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih laporan terlebih dahulu pada daftar jadwal.");
            return;
        }
        String tanggal = txtTanggalPerbaikan.getText().trim();
        String teknisi = txtNamaTeknisi.getText().trim();
        if (tanggal.isEmpty() || teknisi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tanggal perbaikan dan nama teknisi wajib diisi.");
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO penanganan_laporan (laporan_id, jadwal_perbaikan, nama_teknisi, status_penanganan) "
                    + "VALUES (?, ?, ?, 'Diproses') "
                    + "ON DUPLICATE KEY UPDATE jadwal_perbaikan = VALUES(jadwal_perbaikan), "
                    + "nama_teknisi = VALUES(nama_teknisi), status_penanganan = 'Diproses'";
            ps = conn.prepareStatement(sql);
            ps.setString(1, selectedLaporanId);
            ps.setString(2, tanggal);
            ps.setString(3, teknisi);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Jadwal perbaikan berhasil disimpan.");
            muatDataLaporanDanJadwal();
            muatHistoryBulanan();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan jadwal: " + e.getMessage());
        } finally {
            tutupResource(null, ps, conn);
        }
    }

    private void tandaiSelesaiPerbaikan() {
        if (selectedLaporanId == null || selectedLaporanId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih laporan terlebih dahulu pada daftar jadwal.");
            return;
        }

        Connection conn = null;
        PreparedStatement psLap = null;
        PreparedStatement psPen = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            psLap = conn.prepareStatement("UPDATE laporan SET status = 'Selesai' WHERE id = ?");
            psLap.setString(1, selectedLaporanId);
            psLap.executeUpdate();

            psPen = conn.prepareStatement(
                    "UPDATE penanganan_laporan SET status_penanganan = 'Selesai', tanggal_selesai = CURDATE() WHERE laporan_id = ?"
            );
            psPen.setString(1, selectedLaporanId);
            psPen.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Laporan #" + selectedLaporanId + " ditandai Selesai.");
            muatDataLaporanDanJadwal();
            muatHistoryBulanan();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            JOptionPane.showMessageDialog(this, "Gagal menandai selesai: " + e.getMessage());
        } finally {
            tutupResource(null, psLap, null);
            tutupResource(null, psPen, conn);
        }
    }

    private void muatHistoryBulanan() {
        if (cmbBulanHistory == null || cmbTahunHistory == null || panelKalenderBulanan == null || panelRincianBulanan == null) {
            return;
        }

        int bulan = cmbBulanHistory.getSelectedIndex() + 1;
        int tahun = Integer.parseInt((String) cmbTahunHistory.getSelectedItem());
        YearMonth ym = YearMonth.of(tahun, bulan);

        if (lblJudulBulanHistory != null) {
            lblJudulBulanHistory.setText("Kalender Laporan - " + ym.getMonth() + " " + tahun);
        }

        Map<Integer, List<ModelLaporan>> byDay = new HashMap<>();
        List<ModelLaporan> semuaBulanIni = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT l.id, l.tanggal_lapor, l.deskripsi, l.status, "
                    + "gr.nama_lokasi AS lokasi, f.nama_fasilitas AS fasilitas, "
                    + "u.nim, u.nama_lengkap, pl.jadwal_perbaikan, pl.nama_teknisi "
                    + "FROM laporan l "
                    + "JOIN gedung_ruangan gr ON l.ruangan_id = gr.id "
                    + "JOIN fasilitas f ON l.fasilitas_id = f.id "
                    + "JOIN users u ON l.pelapor_id = u.id "
                    + "LEFT JOIN penanganan_laporan pl ON pl.laporan_id = l.id "
                    + "WHERE MONTH(l.tanggal_lapor) = ? AND YEAR(l.tanggal_lapor) = ? "
                    + "ORDER BY l.tanggal_lapor ASC, l.id ASC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, bulan);
            ps.setInt(2, tahun);
            rs = ps.executeQuery();

            while (rs.next()) {
                ModelLaporan item = new ModelLaporan();
                item.setId(rs.getString("id"));
                item.setTanggalLapor(rs.getString("tanggal_lapor"));
                item.setDeskripsi(rs.getString("deskripsi"));
                item.setStatus(rs.getString("status"));
                item.setLokasi(rs.getString("lokasi"));
                item.setFasilitas(rs.getString("fasilitas"));
                item.setPelapor(formatNamaPelapor(rs.getString("nama_lengkap"), rs.getString("nim")));
                item.setJadwalPerbaikan(rs.getString("jadwal_perbaikan"));
                item.setNamaTeknisi(rs.getString("nama_teknisi"));

                semuaBulanIni.add(item);
                try {
                    LocalDate d = LocalDate.parse(item.getTanggalLapor().substring(0, 10), DATE_FMT);
                    byDay.computeIfAbsent(d.getDayOfMonth(), k -> new ArrayList<>()).add(item);
                } catch (Exception ignored) {
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat history bulanan: " + e.getMessage());
        } finally {
            tutupResource(rs, ps, conn);
        }

        renderKalenderBulan(ym, byDay);
        renderRincianBulanan(semuaBulanIni, null);
    }

    private void renderKalenderBulan(YearMonth ym, final Map<Integer, List<ModelLaporan>> byDay) {
        panelKalenderBulanan.removeAll();
        String[] hari = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};
        for (String h : hari) {
            JLabel lblHari = new JLabel(h, SwingConstants.CENTER);
            lblHari.setFont(new Font("SansSerif", Font.BOLD, 11));
            lblHari.setForeground(new Color(71, 85, 105));
            panelKalenderBulanan.add(lblHari);
        }

        int firstDayOfWeek = ym.atDay(1).getDayOfWeek().getValue();
        for (int i = 1; i < firstDayOfWeek; i++) {
            panelKalenderBulanan.add(new JLabel(""));
        }

        int lengthOfMonth = ym.lengthOfMonth();
        for (int day = 1; day <= lengthOfMonth; day++) {
            final int tgl = day;
            final List<ModelLaporan> itemsHari = byDay.getOrDefault(day, new ArrayList<>());
            JButton btnDay = new JButton("<html><center>" + day + (itemsHari.isEmpty() ? "" : "<br><small>(" + itemsHari.size() + ")</small>") + "</center></html>");
            btnDay.setFont(new Font("SansSerif", Font.PLAIN, 11));
            btnDay.setFocusPainted(false);
            btnDay.setBackground(itemsHari.isEmpty() ? Color.WHITE : new Color(219, 234, 254));
            btnDay.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    renderRincianBulanan(itemsHari, ym.atDay(tgl).format(DATE_FMT));
                }
            });
            panelKalenderBulanan.add(btnDay);
        }

        panelKalenderBulanan.revalidate();
        panelKalenderBulanan.repaint();
    }

    private void renderRincianBulanan(List<ModelLaporan> list, String tanggalFilter) {
        panelRincianBulanan.removeAll();
        if (list == null || list.isEmpty()) {
            String msg = (tanggalFilter != null)
                    ? "Tidak ada laporan pada tanggal " + tanggalFilter + "."
                    : "Tidak ada laporan pada bulan ini.";
            JLabel empty = new JLabel(msg);
            empty.setFont(new Font("SansSerif", Font.PLAIN, 12));
            empty.setForeground(new Color(100, 116, 139));
            panelRincianBulanan.add(empty);
        } else {
            for (ModelLaporan item : list) {
                JPanel row = new JPanel();
                row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
                row.setBackground(new Color(248, 250, 252));
                row.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                        new EmptyBorder(8, 10, 8, 10)
                ));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

                JLabel l1 = new JLabel("#" + item.getId() + " | " + item.getTanggalLapor() + " | " + item.getStatus());
                l1.setFont(new Font("SansSerif", Font.BOLD, 12));

                JLabel l2 = new JLabel(item.getFasilitas() + " - " + item.getLokasi() + " (" + item.getPelapor() + ")");
                l2.setFont(new Font("SansSerif", Font.PLAIN, 11));

                JLabel l3 = new JLabel("Deskripsi: " + item.getDeskripsi());
                l3.setFont(new Font("SansSerif", Font.PLAIN, 11));

                row.add(l1);
                row.add(l2);
                row.add(l3);

                panelRincianBulanan.add(row);
                panelRincianBulanan.add(Box.createVerticalStrut(8));
            }
        }
        panelRincianBulanan.revalidate();
        panelRincianBulanan.repaint();
    }

    private void muatDaftarUser() {
        if (panelDaftarUser == null) return;
        panelDaftarUser.removeAll();
        selectedUserCard = null;
        selectedUserId = null;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement("SELECT id, nim, nama_lengkap, role FROM users ORDER BY id ASC");
            rs = ps.executeQuery();

            while (rs.next()) {
                final String uid = rs.getString("id");
                final String nim = rs.getString("nim");
                final String nama = rs.getString("nama_lengkap");
                final String role = rs.getString("role");

                final JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                        new EmptyBorder(10, 12, 10, 12)
                ));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
                card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                JLabel lblNama = new JLabel(nama + " (" + nim + ")");
                lblNama.setFont(new Font("SansSerif", Font.BOLD, 12));

                JLabel lblRole = new JLabel("Role: " + role);
                lblRole.setFont(new Font("SansSerif", Font.PLAIN, 11));
                lblRole.setForeground(new Color(71, 85, 105));

                card.add(lblNama);
                card.add(Box.createVerticalStrut(2));
                card.add(lblRole);

                card.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selectedUserId = uid;
                        selectedUserCard = card;
                        highlightCard(panelDaftarUser, card);
                        lblSelectedUser.setText("Mengedit User ID #" + uid);
                        txtEditNim.setText(nim != null ? nim : "");
                        txtEditNama.setText(nama != null ? nama : "");
                        cmbEditRole.setSelectedItem(role != null ? role : "Pelapor");
                        txtPasswordBaru.setText("");
                    }
                });

                panelDaftarUser.add(card);
                panelDaftarUser.add(Box.createVerticalStrut(8));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat daftar user: " + e.getMessage());
        } finally {
            tutupResource(rs, ps, conn);
        }

        panelDaftarUser.revalidate();
        panelDaftarUser.repaint();
    }

    private void simpanPerubahanAkun() {
        if (selectedUserId == null || selectedUserId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih akun pengguna terlebih dahulu.");
            return;
        }
        String nim = txtEditNim.getText().trim();
        String nama = txtEditNama.getText().trim();
        String role = (String) cmbEditRole.getSelectedItem();

        if (nim.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIM dan Nama Lengkap wajib diisi.");
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement("UPDATE users SET nim = ?, nama_lengkap = ?, role = ? WHERE id = ?");
            ps.setString(1, nim);
            ps.setString(2, nama);
            ps.setString(3, role);
            ps.setString(4, selectedUserId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data akun berhasil diperbarui.");
            muatDaftarUser();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui akun: " + e.getMessage());
        } finally {
            tutupResource(null, ps, conn);
        }
    }

    private void resetPasswordUser() {
        if (selectedUserId == null || selectedUserId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih akun pengguna terlebih dahulu.");
            return;
        }
        String passBaru = txtPasswordBaru.getText();
        if (passBaru == null || passBaru.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan password baru terlebih dahulu.");
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String hashed = PasswordUtil.hashPassword(passBaru.trim());
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement("UPDATE users SET password_hash = ? WHERE id = ?");
            ps.setString(1, hashed);
            ps.setString(2, selectedUserId);
            ps.executeUpdate();

            txtPasswordBaru.setText("");
            JOptionPane.showMessageDialog(this, "Password pengguna berhasil direset.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mereset password: " + e.getMessage());
        } finally {
            tutupResource(null, ps, conn);
        }
    }

    private void bukaPilihTanggalDialog() {
        final JDialog dialog = new JDialog(this, "Pilih Tanggal Perbaikan", true);
        dialog.setSize(340, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(8, 8));

        LocalDate baseDate = LocalDate.now();
        try {
            if (!txtTanggalPerbaikan.getText().trim().isEmpty()) {
                baseDate = LocalDate.parse(txtTanggalPerbaikan.getText().trim(), DATE_FMT);
            }
        } catch (Exception ignored) {
        }

        final YearMonth[] currentYm = {YearMonth.from(baseDate)};
        final JLabel lblMonth = new JLabel("", SwingConstants.CENTER);
        lblMonth.setFont(new Font("SansSerif", Font.BOLD, 13));

        final JPanel grid = new JPanel(new GridLayout(0, 7, 4, 4));
        grid.setBorder(new EmptyBorder(6, 8, 8, 8));

        final Runnable renderCal = new Runnable() {
            @Override
            public void run() {
                grid.removeAll();
                lblMonth.setText(currentYm[0].getMonth() + " " + currentYm[0].getYear());
                String[] h = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};
                for (String s : h) {
                    grid.add(new JLabel(s, SwingConstants.CENTER));
                }
                int start = currentYm[0].atDay(1).getDayOfWeek().getValue();
                for (int i = 1; i < start; i++) {
                    grid.add(new JLabel(""));
                }
                for (int d = 1; d <= currentYm[0].lengthOfMonth(); d++) {
                    final int dayNum = d;
                    JButton b = new JButton(String.valueOf(d));
                    b.setMargin(new Insets(2, 2, 2, 2));
                    b.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            LocalDate chosen = currentYm[0].atDay(dayNum);
                            txtTanggalPerbaikan.setText(chosen.format(DATE_FMT));
                            dialog.dispose();
                        }
                    });
                    grid.add(b);
                }
                grid.revalidate();
                grid.repaint();
            }
        };

        JPanel topNav = new JPanel(new BorderLayout());
        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        btnPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentYm[0] = currentYm[0].minusMonths(1);
                renderCal.run();
            }
        });
        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentYm[0] = currentYm[0].plusMonths(1);
                renderCal.run();
            }
        });

        topNav.add(btnPrev, BorderLayout.WEST);
        topNav.add(lblMonth, BorderLayout.CENTER);
        topNav.add(btnNext, BorderLayout.EAST);

        renderCal.run();
        dialog.add(topNav, BorderLayout.NORTH);
        dialog.add(grid, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private String formatNamaPelapor(String nama, String nim) {
        String n = nama != null ? nama.trim() : "";
        String m = nim != null ? nim.trim() : "";
        if (!n.isEmpty() && !m.isEmpty()) {
            return n + " (" + m + ")";
        }
        return !n.isEmpty() ? n : (!m.isEmpty() ? m : "-");
    }

    private void tutupResource(ResultSet rs, PreparedStatement ps, Connection conn) {
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