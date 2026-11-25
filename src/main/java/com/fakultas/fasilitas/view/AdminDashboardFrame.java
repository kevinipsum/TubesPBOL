package com.fakultas.fasilitas.view;

import com.fakultas.fasilitas.model.DatabaseConnection;
import com.fakultas.fasilitas.util.PasswordUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AdminDashboardFrame extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private JPanel panelListLaporan;
    private JPanel panelListJadwal;
    private JPanel panelListUser;
    private JPanel panelHistoryBulanan;

    private JTextField txtIdLaporanJadwal;
    private JTextField txtTanggalPerbaikan;
    private JComboBox<String> cbTeknisiAnggota;

    private JTextField txtNewNim;
    private JPasswordField txtNewPassword;

    private JTextField txtTargetNimPassword;
    private JPasswordField txtGantiPasswordBaru;

    private String selectedIdJadwal = "";
    private String selectedNimAkun = "";
    private int adminId;

    public AdminDashboardFrame(int adminId, String adminUsername) {
        this.adminId = adminId;

        setTitle("JagaRuang - Panel Admin (" + adminUsername + ")");
        setSize(1200, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(initLaporanPanel(), "LAPORAN");
        cardPanel.add(initJadwalPanel(), "JADWAL");
        cardPanel.add(initHistoryPanel(), "HISTORY");
        cardPanel.add(initAkunPanel(), "AKUN");

        add(cardPanel, BorderLayout.CENTER);

        muatDataLaporanDanJadwal();
        muatDataAkun();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(6, 78, 59));
        sidebar.setPreferredSize(new Dimension(280, 720));
        sidebar.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 4, 20);

        JLabel lblBrand = new JLabel("JagaRuang");
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        sidebar.add(lblBrand, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 25, 20);
        JLabel lblSub = new JLabel("Panel Administrator");
        lblSub.setForeground(new Color(16, 185, 129));
        sidebar.add(lblSub, gbc);

        JButton btnLaporan = createNavButton("Laporan Masuk");
        JButton btnJadwal = createNavButton("Jadwal & Perbaikan");
        JButton btnHistory = createNavButton("History Bulanan");
        JButton btnAkun = createNavButton("Manajemen Akun");

        gbc.gridy = 2;
        gbc.insets = new Insets(6, 20, 6, 20);
        sidebar.add(btnLaporan, gbc);

        gbc.gridy = 3;
        sidebar.add(btnJadwal, gbc);

        gbc.gridy = 4;
        sidebar.add(btnHistory, gbc);

        gbc.gridy = 5;
        sidebar.add(btnAkun, gbc);

        gbc.gridy = 6;
        gbc.weighty = 1.0;
        sidebar.add(new JLabel(), gbc);

        gbc.gridy = 7;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 20, 20, 20);
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(239, 68, 68));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setOpaque(true);
        btnLogout.setBorderPainted(false);
        btnLogout.setPreferredSize(new Dimension(240, 38));
        sidebar.add(btnLogout, gbc);

        btnLaporan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "LAPORAN");
                muatDataLaporanDanJadwal();
            }
        });

        btnJadwal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "JADWAL");
                resetPilihanJadwal();
                muatDataLaporanDanJadwal();
            }
        });

        btnHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "HISTORY");
                muatGridHistoryBulanan(Calendar.getInstance());
            }
        });

        btnAkun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "AKUN");
                resetPilihanAkun();
                muatDataAkun();
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        return sidebar;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(15, 118, 110));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(240, 40));
        return btn;
    }

    private void resetPilihanJadwal() {
        selectedIdJadwal = "";
        if (txtIdLaporanJadwal != null) {
            txtIdLaporanJadwal.setText("");
        }
    }

    private void resetPilihanAkun() {
        selectedNimAkun = "";
        if (txtTargetNimPassword != null) {
            txtTargetNimPassword.setText("");
        }
        if (txtGantiPasswordBaru != null) {
            txtGantiPasswordBaru.setText("");
        }
    }

    private JPanel initLaporanPanel() {
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

    private JPanel initJadwalPanel() {
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

        JPanel panelBottom = new JPanel(new BorderLayout(10, 10));
        panelBottom.setOpaque(false);

        JPanel panelForm = new JPanel(null);
        panelForm.setBackground(Color.WHITE);
        panelForm.setPreferredSize(new Dimension(0, 135));
        panelForm.setBorder(BorderFactory.createTitledBorder("Atur Jadwal & Teknisi (Klik Card berstatus Diproses di atas)"));

        JLabel lblId = new JLabel("ID Laporan:");
        lblId.setBounds(20, 25, 90, 22);
        panelForm.add(lblId);

        txtIdLaporanJadwal = new JTextField();
        txtIdLaporanJadwal.setBounds(115, 25, 175, 28);
        txtIdLaporanJadwal.setEditable(false);
        panelForm.add(txtIdLaporanJadwal);

        JLabel lblTgl = new JLabel("Tgl Perbaikan:");
        lblTgl.setBounds(320, 25, 95, 22);
        panelForm.add(lblTgl);

        txtTanggalPerbaikan = new JTextField(LocalDate.now().toString());
        txtTanggalPerbaikan.setBounds(420, 25, 175, 28);
        txtTanggalPerbaikan.setEditable(false);
        txtTanggalPerbaikan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        txtTanggalPerbaikan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                bukaPilihTanggalDialog();
            }
        });
        panelForm.add(txtTanggalPerbaikan);

        JLabel lblTeknisi = new JLabel("Nama Teknisi:");
        lblTeknisi.setBounds(20, 70, 95, 22);
        panelForm.add(lblTeknisi);

        cbTeknisiAnggota = new JComboBox<String>(new String[]{
                "Qeizha Aleesha Zyva",
                "Syamil Mali Uzair",
                "Syalikhafaradhiya Agira",
                "Kevin Rodrigues Pandiangan"
        });
        cbTeknisiAnggota.setBounds(115, 70, 290, 28);
        panelForm.add(cbTeknisiAnggota);

        JButton btnSimpanJadwal = new JButton("Simpan Jadwal");
        btnSimpanJadwal.setBackground(new Color(16, 185, 129));
        btnSimpanJadwal.setForeground(Color.WHITE);
        btnSimpanJadwal.setFocusPainted(false);
        btnSimpanJadwal.setOpaque(true);
        btnSimpanJadwal.setBorderPainted(false);
        btnSimpanJadwal.setBounds(420, 70, 175, 28);
        btnSimpanJadwal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simpanJadwalPerbaikan();
            }
        });
        panelForm.add(btnSimpanJadwal);

        panelBottom.add(panelForm, BorderLayout.CENTER);

        JPanel panelBtnSelesai = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBtnSelesai.setOpaque(false);
        JButton btnSelesai = new JButton("Tandai Selesai Perbaikan");
        btnSelesai.setBackground(new Color(15, 118, 110));
        btnSelesai.setForeground(Color.WHITE);
        btnSelesai.setFocusPainted(false);
        btnSelesai.setOpaque(true);
        btnSelesai.setBorderPainted(false);
        btnSelesai.setPreferredSize(new Dimension(210, 38));
        btnSelesai.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tandaiSelesaiPerbaikan();
            }
        });
        panelBtnSelesai.add(btnSelesai);

        panelBottom.add(panelBtnSelesai, BorderLayout.SOUTH);
        panel.add(panelBottom, BorderLayout.SOUTH);
        return panel;
    }

    private void bukaPilihTanggalDialog() {
        final JDialog dialog = new JDialog(this, "Pilih Tanggal Perbaikan", true);
        dialog.setSize(380, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelHeader.setBackground(new Color(6, 78, 59));

        final Calendar cal = Calendar.getInstance();
        final JLabel lblBulan = new JLabel();
        lblBulan.setForeground(Color.WHITE);
        lblBulan.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        btnPrev.setFocusPainted(false);
        btnNext.setFocusPainted(false);

        panelHeader.add(btnPrev);
        panelHeader.add(lblBulan);
        panelHeader.add(btnNext);
        dialog.add(panelHeader, BorderLayout.NORTH);

        final JPanel panelGrid = new JPanel(new GridLayout(6, 7, 5, 5));
        panelGrid.setBackground(Color.WHITE);
        panelGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final Runnable renderKalender = new Runnable() {
            @Override
            public void run() {
                panelGrid.removeAll();
                lblBulan.setText(new SimpleDateFormat("MMMM yyyy").format(cal.getTime()));

                Calendar cTemp = (Calendar) cal.clone();
                cTemp.set(Calendar.DAY_OF_MONTH, 1);
                int hariPertama = cTemp.get(Calendar.DAY_OF_WEEK) - 1;
                int maxHari = cTemp.getActualMaximum(Calendar.DAY_OF_MONTH);

                for (int i = 0; i < hariPertama; i++) {
                    panelGrid.add(new JLabel(""));
                }

                final int tahun = cal.get(Calendar.YEAR);
                final int bulan = cal.get(Calendar.MONTH);

                for (int i = 1; i <= maxHari; i++) {
                    final JButton btnDay = new JButton(String.valueOf(i));
                    btnDay.setFocusPainted(false);
                    btnDay.setBackground(new Color(241, 245, 249));
                    btnDay.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            txtTanggalPerbaikan.setText(String.format("%d-%02d-%02d", tahun, bulan + 1, Integer.parseInt(btnDay.getText())));
                            dialog.dispose();
                        }
                    });
                    panelGrid.add(btnDay);
                }

                for (int i = hariPertama + maxHari; i < 42; i++) {
                    panelGrid.add(new JLabel(""));
                }

                panelGrid.revalidate();
                panelGrid.repaint();
            }
        };

        btnPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cal.add(Calendar.MONTH, -1);
                renderKalender.run();
            }
        });

        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cal.add(Calendar.MONTH, 1);
                renderKalender.run();
            }
        });

        renderKalender.run();
        dialog.add(panelGrid, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel initHistoryPanel() {
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

    private void muatGridHistoryBulanan(final Calendar cal) {
        panelHistoryBulanan.removeAll();

        HashMap<String, Integer> mapMasuk = new HashMap<String, Integer>();
        HashMap<String, Integer> mapSelesai = new HashMap<String, Integer>();
        HashMap<String, Integer> mapDitolak = new HashMap<String, Integer>();

        String sql = "SELECT tanggal_lapor, COUNT(*) AS jml_masuk, " +
                "SUM(status = 'Selesai') AS jml_selesai, " +
                "SUM(status = 'Ditolak') AS jml_ditolak " +
                "FROM laporan GROUP BY tanggal_lapor";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String tgl = rs.getString("tanggal_lapor");
                mapMasuk.put(tgl, rs.getInt("jml_masuk"));
                mapSelesai.put(tgl, rs.getInt("jml_selesai"));
                mapDitolak.put(tgl, rs.getInt("jml_ditolak"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            tutupResource(rs, stmt, conn);
        }

        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        panelHeader.setBackground(new Color(6, 78, 59));

        JLabel lblBulanTahun = new JLabel(new SimpleDateFormat("MMMM yyyy").format(cal.getTime()));
        lblBulanTahun.setForeground(Color.WHITE);
        lblBulanTahun.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnPrev = new JButton("< Bulan Sebelumnya");
        JButton btnNext = new JButton("Bulan Berikutnya >");
        btnPrev.setFocusPainted(false);
        btnNext.setFocusPainted(false);

        panelHeader.add(btnPrev);
        panelHeader.add(lblBulanTahun);
        panelHeader.add(btnNext);
        panelHistoryBulanan.add(panelHeader, BorderLayout.NORTH);

        JPanel panelCenter = new JPanel(new BorderLayout());

        JPanel panelHari = new JPanel(new GridLayout(1, 7, 4, 4));
        panelHari.setBackground(new Color(241, 245, 249));
        panelHari.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        String[] namaHari = {"Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
        for (int i = 0; i < namaHari.length; i++) {
            JLabel lblH = new JLabel(namaHari[i], SwingConstants.CENTER);
            lblH.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblH.setForeground(new Color(15, 118, 110));
            panelHari.add(lblH);
        }
        panelCenter.add(panelHari, BorderLayout.NORTH);

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
            final String tglKey = String.format("%d-%02d-%02d", tahunIni, bulanIni + 1, i);
            int jmlMasuk = mapMasuk.containsKey(tglKey) ? mapMasuk.get(tglKey) : 0;
            int jmlSelesai = mapSelesai.containsKey(tglKey) ? mapSelesai.get(tglKey) : 0;
            int jmlDitolak = mapDitolak.containsKey(tglKey) ? mapDitolak.get(tglKey) : 0;

            JButton btnDay = new JButton();
            btnDay.setLayout(new BorderLayout());
            btnDay.setFocusPainted(false);
            btnDay.setBackground(Color.WHITE);
            btnDay.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
            btnDay.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel lblNum = new JLabel(" " + i);
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

            btnDay.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tampilkanPopUpDetailTanggal(tglKey);
                }
            });
            panelGrid.add(btnDay);
        }

        for (int i = hariPertama + maxHari; i < 42; i++) {
            JPanel pKosong = new JPanel();
            pKosong.setBackground(Color.WHITE);
            panelGrid.add(pKosong);
        }

        btnPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cal.add(Calendar.MONTH, -1);
                muatGridHistoryBulanan(cal);
            }
        });

        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cal.add(Calendar.MONTH, 1);
                muatGridHistoryBulanan(cal);
            }
        });

        panelCenter.add(panelGrid, BorderLayout.CENTER);
        panelHistoryBulanan.add(panelCenter, BorderLayout.CENTER);
        panelHistoryBulanan.revalidate();
        panelHistoryBulanan.repaint();
    }

    private void tampilkanPopUpDetailTanggal(String tglKey) {
        final JDialog dialog = new JDialog(this, "Rincian Tanggal: " + tglKey, true);
        dialog.setSize(380, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.Y_AXIS));
        panelContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelContent.setBackground(Color.WHITE);

        ArrayList<String> listMasuk = new ArrayList<String>();
        ArrayList<String> listSelesai = new ArrayList<String>();
        ArrayList<String> listDitolak = new ArrayList<String>();

        String sql = "SELECT id, status FROM laporan WHERE tanggal_lapor = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, tglKey);
            rs = ps.executeQuery();
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
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            tutupResource(rs, ps, conn);
        }

        JLabel lblMasuk = new JLabel("<html><b>Laporan Masuk (" + listMasuk.size() + "):</b><br>" +
                (listMasuk.isEmpty() ? "-" : String.join(", ", listMasuk)) + "</html>");
        JLabel lblSelesai = new JLabel("<html><b>Laporan Selesai (" + listSelesai.size() + "):</b><br>" +
                (listSelesai.isEmpty() ? "-" : String.join(", ", listSelesai)) + "</html>");
        lblSelesai.setForeground(new Color(5, 150, 105));
        JLabel lblDitolak = new JLabel("<html><b>Laporan Ditolak (" + listDitolak.size() + "):</b><br>" +
                (listDitolak.isEmpty() ? "-" : String.join(", ", listDitolak)) + "</html>");
        lblDitolak.setForeground(new Color(220, 38, 38));

        panelContent.add(lblMasuk);
        panelContent.add(Box.createRigidArea(new Dimension(0, 12)));
        panelContent.add(lblSelesai);
        panelContent.add(Box.createRigidArea(new Dimension(0, 12)));
        panelContent.add(lblDitolak);

        dialog.add(new JScrollPane(panelContent), BorderLayout.CENTER);

        JButton btnTutup = new JButton("Tutup");
        btnTutup.setBackground(new Color(16, 185, 129));
        btnTutup.setForeground(Color.WHITE);
        btnTutup.setFocusPainted(false);
        btnTutup.setOpaque(true);
        btnTutup.setBorderPainted(false);
        btnTutup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBottom.add(btnTutup);
        dialog.add(panelBottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel initAkunPanel() {
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

        JPanel panelBottom = new JPanel(new GridLayout(1, 2, 15, 0));
        panelBottom.setOpaque(false);
        panelBottom.setPreferredSize(new Dimension(0, 180));

        JPanel panelTambah = new JPanel(null);
        panelTambah.setBackground(Color.WHITE);
        panelTambah.setBorder(BorderFactory.createTitledBorder("Tambah Akun Pelapor Baru"));

        JLabel lblNimNew = new JLabel("NIM:");
        lblNimNew.setBounds(15, 28, 85, 22);
        panelTambah.add(lblNimNew);

        txtNewNim = new JTextField();
        txtNewNim.setBounds(105, 25, 215, 28);
        panelTambah.add(txtNewNim);

        JLabel lblPassNew = new JLabel("Password:");
        lblPassNew.setBounds(15, 68, 85, 22);
        panelTambah.add(lblPassNew);

        txtNewPassword = new JPasswordField();
        txtNewPassword.setBounds(105, 65, 215, 28);
        panelTambah.add(txtNewPassword);

        JButton btnSimpanAkun = new JButton("Simpan Akun");
        btnSimpanAkun.setBackground(new Color(16, 185, 129));
        btnSimpanAkun.setForeground(Color.WHITE);
        btnSimpanAkun.setFocusPainted(false);
        btnSimpanAkun.setOpaque(true);
        btnSimpanAkun.setBorderPainted(false);
        btnSimpanAkun.setBounds(105, 110, 215, 34);
        btnSimpanAkun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tambahAkunPelapor();
            }
        });
        panelTambah.add(btnSimpanAkun);

        JPanel panelGantiPass = new JPanel(null);
        panelGantiPass.setBackground(Color.WHITE);
        panelGantiPass.setBorder(BorderFactory.createTitledBorder("Ganti Password Akun (Klik Akun di atas)"));

        JLabel lblTargetNim = new JLabel("NIM Akun:");
        lblTargetNim.setBounds(15, 28, 95, 22);
        panelGantiPass.add(lblTargetNim);

        txtTargetNimPassword = new JTextField();
        txtTargetNimPassword.setBounds(115, 25, 205, 28);
        txtTargetNimPassword.setEditable(false);
        panelGantiPass.add(txtTargetNimPassword);

        JLabel lblPassBaru = new JLabel("Password Baru:");
        lblPassBaru.setBounds(15, 68, 95, 22);
        panelGantiPass.add(lblPassBaru);

        txtGantiPasswordBaru = new JPasswordField();
        txtGantiPasswordBaru.setBounds(115, 65, 205, 28);
        panelGantiPass.add(txtGantiPasswordBaru);

        JButton btnUpdatePass = new JButton("Simpan Password");
        btnUpdatePass.setBackground(new Color(15, 118, 110));
        btnUpdatePass.setForeground(Color.WHITE);
        btnUpdatePass.setFocusPainted(false);
        btnUpdatePass.setOpaque(true);
        btnUpdatePass.setBorderPainted(false);
        btnUpdatePass.setBounds(115, 110, 205, 34);
        btnUpdatePass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gantiPasswordAkun();
            }
        });
        panelGantiPass.add(btnUpdatePass);

        panelBottom.add(panelTambah);
        panelBottom.add(panelGantiPass);
        panel.add(panelBottom, BorderLayout.SOUTH);
        return panel;
    }

    private String formatNamaPelapor(String namaLengkap, String nim) {
        if (namaLengkap != null && !namaLengkap.trim().isEmpty() && !namaLengkap.trim().equals(nim)) {
            return namaLengkap.trim();
        }
        return "Mahasiswa";
    }

    private void muatDataLaporanDanJadwal() {
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

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

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
                    JPanel cardLaporan = createLaporanCard(id, namaPelapor, fasilitas, lokasi, tanggal, deskripsi, status);
                    cardLaporan.setPreferredSize(new Dimension(780, 110));
                    cardLaporan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
                    panelListLaporan.add(cardLaporan);
                    panelListLaporan.add(Box.createRigidArea(new Dimension(0, 10)));
                }

                boolean isDiproses = "Diproses".equalsIgnoreCase(status);
                boolean isSelesai = "Selesai".equalsIgnoreCase(status);
                if (isDiproses || isSelesai) {
                    JPanel cardJadwal = createJadwalCard(id, namaPelapor, fasilitas, lokasi, status, jadwal, teknisi, isDiproses);
                    cardJadwal.setPreferredSize(new Dimension(780, 95));
                    cardJadwal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

                    if (isDiproses) {
                        attachJadwalClick(cardJadwal, id);
                        if (id.equals(selectedIdJadwal)) {
                            highlightCard(panelListJadwal, cardJadwal);
                        }
                    }

                    panelListJadwal.add(cardJadwal);
                    panelListJadwal.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            tutupResource(rs, stmt, conn);
        }

        panelListLaporan.revalidate();
        panelListLaporan.repaint();
        panelListJadwal.revalidate();
        panelListJadwal.repaint();
    }

    private void attachJadwalClick(Component comp, final String id) {
        comp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedIdJadwal = id;
                txtIdLaporanJadwal.setText(id);
                JPanel parentCard = findParentJadwalCard(comp);
                if (parentCard != null) {
                    highlightCard(panelListJadwal, parentCard);
                }
            }
        });
        if (comp instanceof Container) {
            Container c = (Container) comp;
            for (int i = 0; i < c.getComponentCount(); i++) {
                attachJadwalClick(c.getComponent(i), id);
            }
        }
    }

    private JPanel findParentJadwalCard(Component comp) {
        Component curr = comp;
        while (curr != null && curr.getParent() != panelListJadwal) {
            curr = curr.getParent();
        }
        return (curr instanceof JPanel) ? (JPanel) curr : null;
    }

    private JPanel createLaporanCard(final String id, String namaPelapor, String fasilitas, String lokasi,
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

        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBtn.setOpaque(false);

        JButton btnTerima = new JButton("Terima");
        btnTerima.setBackground(new Color(16, 185, 129));
        btnTerima.setForeground(Color.WHITE);
        btnTerima.setFocusPainted(false);
        btnTerima.setOpaque(true);
        btnTerima.setBorderPainted(false);
        btnTerima.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                terimaLaporan(id);
            }
        });

        JButton btnTolak = new JButton("Tolak");
        btnTolak.setBackground(new Color(239, 68, 68));
        btnTolak.setForeground(Color.WHITE);
        btnTolak.setFocusPainted(false);
        btnTolak.setOpaque(true);
        btnTolak.setBorderPainted(false);
        btnTolak.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tolakLaporan(id);
            }
        });

        panelBtn.add(btnTerima);
        panelBtn.add(btnTolak);

        card.add(lblHeader, BorderLayout.NORTH);
        card.add(lblBody, BorderLayout.CENTER);
        card.add(panelBtn, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createJadwalCard(String id, String namaPelapor, String fasilitas, String lokasi,
                                    String status, String jadwal, String teknisi, boolean isDiproses) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(isDiproses ? Color.WHITE : new Color(248, 250, 252));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isDiproses ? new Color(148, 163, 184) : new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        String hint = isDiproses ? "  <font color='#0D9488'><i>(Klik untuk pilih)</i></font>" : "";

        JLabel lblHeader = new JLabel("<html><b>ID:</b> " + id + "   |   <b>Pelapor:</b> " + namaPelapor +
                "   |   <font color='#64748B'>Status: <b>" + status + "</b></font>" + hint + "</html>");
        lblHeader.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblBody = new JLabel("<html><b>Fasilitas:</b> " + fasilitas + "   |   <b>Lokasi:</b> " + lokasi +
                "<br><font color='#059669'><b>Jadwal:</b> " + (jadwal != null && !jadwal.isEmpty() ? jadwal : "-") +
                "  |  <b>Teknisi:</b> " + (teknisi != null && !teknisi.isEmpty() ? teknisi : "-") + "</font></html>");
        lblBody.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        card.add(lblHeader, BorderLayout.NORTH);
        card.add(lblBody, BorderLayout.CENTER);
        return card;
    }

    private void muatDataAkun() {
        panelListUser.removeAll();
        String sql = "SELECT nim, nama_lengkap, status_akun FROM users WHERE role = 'pelapor' ORDER BY id ASC";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                final String nim = rs.getString("nim");
                String nama = rs.getString("nama_lengkap");
                String namaTampil = (nama != null && !nama.trim().isEmpty()) ? nama : "-";

                final JPanel cardUser = new JPanel(new BorderLayout(10, 5));
                cardUser.setBackground(Color.WHITE);
                cardUser.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(226, 232, 240)),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                cardUser.setPreferredSize(new Dimension(780, 55));
                cardUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
                cardUser.setCursor(new Cursor(Cursor.HAND_CURSOR));

                JLabel lblBody = new JLabel("<html><b>NIM:</b> " + (nim != null ? nim : "-") +
                        "  |  <b>Nama:</b> " + namaTampil +
                        "  |  <b>Status:</b> " + rs.getString("status_akun") +
                        "  <font color='#0D9488'><i>(Klik untuk ganti password)</i></font></html>");
                lblBody.setFont(new Font("Segoe UI", Font.PLAIN, 13));

                cardUser.add(lblBody, BorderLayout.CENTER);

                cardUser.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selectedNimAkun = nim != null ? nim : "";
                        txtTargetNimPassword.setText(selectedNimAkun);
                        highlightCard(panelListUser, cardUser);
                    }
                });

                if (nim != null && nim.equals(selectedNimAkun)) {
                    highlightCard(panelListUser, cardUser);
                }

                panelListUser.add(cardUser);
                panelListUser.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            tutupResource(rs, stmt, conn);
        }

        panelListUser.revalidate();
        panelListUser.repaint();
    }

    private void highlightCard(JPanel container, JPanel selectedCard) {
        Component[] comps = container.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof JPanel) {
                JPanel p = (JPanel) comps[i];
                p.setBackground(Color.WHITE);
                p.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(148, 163, 184)),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        }
        selectedCard.setBackground(new Color(240, 253, 250));
        selectedCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(16, 185, 129), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }

    private void terimaLaporan(String idLaporan) {
        String sql = "UPDATE laporan SET status = 'Diproses' WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, idLaporan);
            ps.executeUpdate();
            muatDataLaporanDanJadwal();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memproses laporan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            tutupResource(null, ps, conn);
        }
    }

    private void tolakLaporan(String idLaporan) {
        JTextArea txtFeedback = new JTextArea(4, 30);
        txtFeedback.setLineWrap(true);
        txtFeedback.setWrapStyleWord(true);

        JPanel dialogPanel = new JPanel(new BorderLayout(6, 6));
        dialogPanel.add(new JLabel("Feedback / alasan penolakan untuk " + idLaporan + " (opsional):"), BorderLayout.NORTH);
        dialogPanel.add(new JScrollPane(txtFeedback), BorderLayout.CENTER);

        int res = JOptionPane.showConfirmDialog(this, dialogPanel, "Feedback Penolakan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) {
            return;
        }

        String alasan = txtFeedback.getText().trim();
        String alasanSimpan = alasan.isEmpty() ? "-" : alasan;

        Connection conn = null;
        PreparedStatement psUpd = null;
        PreparedStatement psTolak = null;
        try {
            conn = DatabaseConnection.getConnection();
            psUpd = conn.prepareStatement("UPDATE laporan SET status = 'Ditolak' WHERE id = ?");
            psUpd.setString(1, idLaporan);
            psUpd.executeUpdate();

            String sqlTolak = "INSERT INTO riwayat_penolakan (laporan_id, admin_id, alasan_penolakan) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE admin_id = VALUES(admin_id), alasan_penolakan = VALUES(alasan_penolakan)";
            psTolak = conn.prepareStatement(sqlTolak);
            psTolak.setString(1, idLaporan);
            psTolak.setInt(2, adminId);
            psTolak.setString(3, alasanSimpan);
            psTolak.executeUpdate();

            muatDataLaporanDanJadwal();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menolak laporan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            tutupResource(null, psUpd, null);
            tutupResource(null, psTolak, conn);
        }
    }

    private void simpanJadwalPerbaikan() {
        String idLaporan = txtIdLaporanJadwal.getText().trim();
        if (idLaporan.isEmpty()) {
            idLaporan = selectedIdJadwal;
        }
        String jadwal = txtTanggalPerbaikan.getText().trim();
        String teknisi = (String) cbTeknisiAnggota.getSelectedItem();

        if (idLaporan.isEmpty() || jadwal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih card laporan berstatus Diproses terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO penanganan_laporan (laporan_id, admin_id, jadwal_perbaikan, nama_teknisi) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE admin_id = VALUES(admin_id), jadwal_perbaikan = VALUES(jadwal_perbaikan), nama_teknisi = VALUES(nama_teknisi)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, idLaporan);
            ps.setInt(2, adminId);
            ps.setString(3, jadwal);
            ps.setString(4, teknisi);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Jadwal dan teknisi berhasil disimpan!");
            resetPilihanJadwal();
            muatDataLaporanDanJadwal();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menyimpan jadwal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            tutupResource(null, ps, conn);
        }
    }

    private void tandaiSelesaiPerbaikan() {
        String idTarget = selectedIdJadwal.isEmpty() ? txtIdLaporanJadwal.getText().trim() : selectedIdJadwal;
        if (idTarget.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih Card pada daftar Jadwal di atas terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement psPen = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement("UPDATE laporan SET status = 'Selesai' WHERE id = ?");
            ps.setString(1, idTarget);
            ps.executeUpdate();

            psPen = conn.prepareStatement("UPDATE penanganan_laporan SET tanggal_selesai = ? WHERE laporan_id = ?");
            psPen.setString(1, LocalDate.now().toString());
            psPen.setString(2, idTarget);
            psPen.executeUpdate();

            JOptionPane.showMessageDialog(this, "Laporan ditandai Selesai!");
            resetPilihanJadwal();
            muatDataLaporanDanJadwal();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menandai selesai: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            tutupResource(null, ps, null);
            tutupResource(null, psPen, conn);
        }
    }

    private void tambahAkunPelapor() {
        String nim = txtNewNim.getText().trim();
        String password = new String(txtNewPassword.getPassword()).trim();
        if (nim.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIM & Password wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String passHash = PasswordUtil.hashPassword(password);
        String sql = "INSERT INTO users (nim, username, nama_lengkap, password_hash, role, status_akun) " +
                "VALUES (?, ?, NULL, ?, 'pelapor', 'aktif')";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, nim);
            ps.setString(2, nim);
            ps.setString(3, passHash);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Akun pelapor berhasil ditambahkan!");
            txtNewNim.setText("");
            txtNewPassword.setText("");
            muatDataAkun();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal menambah akun: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            tutupResource(null, ps, conn);
        }
    }

    private void gantiPasswordAkun() {
        String targetNim = txtTargetNimPassword.getText().trim();
        String passBaru = new String(txtGantiPasswordBaru.getPassword()).trim();

        if (targetNim.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih akun pelapor pada daftar di atas terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (passBaru.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password baru minimal 4 karakter!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String passHashBaru = PasswordUtil.hashPassword(passBaru);
        String sql = "UPDATE users SET password_hash = ? WHERE nim = ? AND role = 'pelapor'";

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, passHashBaru);
            ps.setString(2, targetNim);
            int affected = ps.executeUpdate();

            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Password akun " + targetNim + " berhasil diperbarui!");
                txtGantiPasswordBaru.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Akun tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memperbarui password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            tutupResource(null, ps, conn);
        }
    }

    private void tutupResource(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
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