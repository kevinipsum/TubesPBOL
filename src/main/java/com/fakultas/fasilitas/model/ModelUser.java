package com.fakultas.fasilitas.model;

<<<<<<< HEAD
import java.sql.Timestamp;

public class ModelUser {
    private int id;
    private String nim;
    private String username;
    private String namaLengkap;
    private String passwordHash;
    private String role;
    private String statusAkun;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public ModelUser() {
    }

    public ModelUser(int id, String nim, String username, String namaLengkap,
                     String passwordHash, String role, String statusAkun) {
        this.id = id;
        this.nim = nim;
        this.username = username;
        this.namaLengkap = namaLengkap;
        this.passwordHash = passwordHash;
        this.role = role;
        this.statusAkun = statusAkun;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatusAkun() {
        return statusAkun;
    }

    public void setStatusAkun(String statusAkun) {
        this.statusAkun = statusAkun;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getIdentitasTampil() {
        if (nim != null && !nim.isBlank()) {
            return nim;
        }
        return username;
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    public boolean isPelapor() {
        return "pelapor".equalsIgnoreCase(role);
    }
=======
public class ModelUser {
    private String id;
    private String username;
    private String role;
    private String status;

    public ModelUser(String id, String username, String role, String status) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.status = status;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
>>>>>>> d0438a77b2aacef5cdd1c847833573b6a69a68de
}