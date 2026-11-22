package com.fakultas.fasilitas.model;

import java.sql.Date;
import java.sql.Timestamp;

public class ModelLaporan {
    private String id;
    private int pelaporId;
    private String nimPelapor;
    private String namaPelapor;
    private int ruanganId;
    private String lokasi;
    private String kategoriArea;
    private int fasilitasId;
    private String jenisFasilitas;
    private Date tanggalLapor;
    private String deskripsi;
    private String status;
    private String jadwalPerbaikan;
    private String namaTeknisi;
    private String catatanPenanganan;
    private Date tanggalSelesai;
    private String alasanPenolakan;
    private Timestamp waktuDitolak;

    public ModelLaporan() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPelaporId() {
        return pelaporId;
    }

    public void setPelaporId(int pelaporId) {
        this.pelaporId = pelaporId;
    }

    public String getNimPelapor() {
        return nimPelapor;
    }

    public void setNimPelapor(String nimPelapor) {
        this.nimPelapor = nimPelapor;
    }

    public String getNamaPelapor() {
        return namaPelapor;
    }

    public void setNamaPelapor(String namaPelapor) {
        this.namaPelapor = namaPelapor;
    }

    public int getRuanganId() {
        return ruanganId;
    }

    public void setRuanganId(int ruanganId) {
        this.ruanganId = ruanganId;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getKategoriArea() {
        return kategoriArea;
    }

    public void setKategoriArea(String kategoriArea) {
        this.kategoriArea = kategoriArea;
    }

    public int getFasilitasId() {
        return fasilitasId;
    }

    public void setFasilitasId(int fasilitasId) {
        this.fasilitasId = fasilitasId;
    }

    public String getJenisFasilitas() {
        return jenisFasilitas;
    }

    public void setJenisFasilitas(String jenisFasilitas) {
        this.jenisFasilitas = jenisFasilitas;
    }

    public Date getTanggalLapor() {
        return tanggalLapor;
    }

    public void setTanggalLapor(Date tanggalLapor) {
        this.tanggalLapor = tanggalLapor;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJadwalPerbaikan() {
        return jadwalPerbaikan;
    }

    public void setJadwalPerbaikan(String jadwalPerbaikan) {
        this.jadwalPerbaikan = jadwalPerbaikan;
    }

    public String getNamaTeknisi() {
        return namaTeknisi;
    }

    public void setNamaTeknisi(String namaTeknisi) {
        this.namaTeknisi = namaTeknisi;
    }

    public String getCatatanPenanganan() {
        return catatanPenanganan;
    }

    public void setCatatanPenanganan(String catatanPenanganan) {
        this.catatanPenanganan = catatanPenanganan;
    }

    public Date getTanggalSelesai() {
        return tanggalSelesai;
    }

    public void setTanggalSelesai(Date tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }

    public String getAlasanPenolakan() {
        return alasanPenolakan;
    }

    public void setAlasanPenolakan(String alasanPenolakan) {
        this.alasanPenolakan = alasanPenolakan;
    }

    public Timestamp getWaktuDitolak() {
        return waktuDitolak;
    }

    public void setWaktuDitolak(Timestamp waktuDitolak) {
        this.waktuDitolak = waktuDitolak;
    }

    public boolean isDitolak() {
        return "Ditolak".equalsIgnoreCase(status);
    }

    public boolean isDiproses() {
        return "Diproses".equalsIgnoreCase(status);
    }

    public boolean isSelesai() {
        return "Selesai".equalsIgnoreCase(status);
    }

    public boolean isMenunggu() {
        return "Menunggu".equalsIgnoreCase(status);
    }
}