package com.fakultas.fasilitas.model;

public class ModelLaporan {
    private String id;
    private String jenisFasilitas;
    private String lokasi;
    private String tanggal;
    private String deskripsi;
    private String status;
    private String keterangan;
    private String catatan;

    public ModelLaporan(String id, String jenisFasilitas, String lokasi, String tanggal, String deskripsi, String status, String keterangan, String catatan) {
        this.id = id;
        this.jenisFasilitas = jenisFasilitas;
        this.lokasi = lokasi;
        this.tanggal = tanggal;
        this.deskripsi = deskripsi;
        this.status = status;
        this.keterangan = keterangan;
        this.catatan = catatan;
    }

    public String getId() { return id; }
    public String getJenisFasilitas() { return jenisFasilitas; }
    public String getLokasi() { return lokasi; }
    public String getTanggal() { return tanggal; }
    public String getDeskripsi() { return deskripsi; }
    public String getStatus() { return status; }
    public String getKeterangan() { return keterangan; }
    public String getCatatan() { return catatan; }
}