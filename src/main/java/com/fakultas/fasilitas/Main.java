package com.fakultas.fasilitas;

import com.fakultas.fasilitas.view.LoginFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}