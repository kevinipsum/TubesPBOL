package com.fakultas.fasilitas.model;

public class ModelLaporan {
    private String id;
    private String fasilitas;
    private String lokasi;
    private String tanggalLapor;
    private String deskripsi;
    private String status;
    private String pelapor;
    private String jadwalPerbaikan;
    private String namaTeknisi;
    private String alasanPenolakan;

    public ModelLaporan() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFasilitas() {
        return fasilitas;
    }

    public void setFasilitas(String fasilitas) {
        this.fasilitas = fasilitas;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getTanggalLapor() {
        return tanggalLapor;
    }

    public void setTanggalLapor(String tanggalLapor) {
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

    public String getPelapor() {
        return pelapor;
    }

    public void setPelapor(String pelapor) {
        this.pelapor = pelapor;
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

    public String getAlasanPenolakan() {
        return alasanPenolakan;
    }

    public void setAlasanPenolakan(String alasanPenolakan) {
        this.alasanPenolakan = alasanPenolakan;
    }
}