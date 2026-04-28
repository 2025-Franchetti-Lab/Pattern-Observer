/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PatternObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ConcreteObserver: finestra Swing che visualizza il valore del contatore.
 * Implementa Observer → riceve le notifiche e aggiorna la JLabel.
 */
public class PannelloContatore extends JFrame implements Observer {

    private final JLabel lblValore;
    private final JLabel lblValore2;
    private final JLabel lblStatus;
    private final ContatoreThread contatore0;
    private final ContatoreThread contatore1;

    public PannelloContatore(ContatoreThread contatore0, ContatoreThread contatore1) {
        this.contatore0 = contatore0;
        this.contatore1 = contatore1;

        // ── Impostazioni finestra ──
        setTitle("Pattern Observer – Contatore in tempo reale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(247, 245, 240));

        // ── Pannello centrale con i valori dei due contatori affiancati e centrati ──
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 24, 0, 24);

        lblValore = new JLabel("0");
        lblValore.setFont(new Font("SansSerif", Font.BOLD, 72));
        lblValore.setForeground(new Color(26, 79, 196));
        gbc.gridx = 0;
        centro.add(lblValore, gbc);

        lblValore2 = new JLabel("0");
        lblValore2.setFont(new Font("SansSerif", Font.BOLD, 72));
        lblValore2.setForeground(new Color(196, 79, 26));
        gbc.gridx = 1;
        centro.add(lblValore2, gbc);

        // ── Barra superiore ──
        JLabel titolo = new JLabel("  Contatore (aggiornato ogni secondo)");
        titolo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titolo.setForeground(Color.DARK_GRAY);
        titolo.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

        // ── Barra di stato inferiore ──
        lblStatus = new JLabel("  ● Thread in esecuzione");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(14, 138, 74));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(0,0,8,0));

        // ── Pulsante Stop ──
        JButton btnStop = new JButton("⏹  Ferma");
        btnStop.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnStop.setBackground(new Color(217, 79, 0));
        btnStop.setForeground(Color.WHITE);
        btnStop.setOpaque(true);            // necessario su macOS/Nimbus per mostrare il colore
        btnStop.setBorderPainted(false);   // rimuove il bordo che copre il colore di sfondo
        btnStop.setFocusPainted(false);
        btnStop.setPreferredSize(new Dimension(160, 42));
        btnStop.addActionListener(e -> {
            contatore0.ferma();
            contatore0.removeObserver(this);
            contatore1.ferma();
            contatore1.removeObserver(this);
            lblStatus.setText("  ■ Thread fermato");
            lblStatus.setForeground(new Color(180, 50, 0));
            btnStop.setEnabled(false);
        });

        JPanel sud = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sud.setOpaque(false);
        sud.add(btnStop);
        sud.add(lblStatus);

        add(titolo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(sud,    BorderLayout.SOUTH);
    }

    // ════════════ Implementazione di Observer ════════════

    @Override
    public void update(int id, int valore) {
        /*
         * IMPORTANTE: il metodo update() viene chiamato dal thread in background.
         * Le modifiche ai componenti Swing devono avvenire sull'EDT.
         * SwingUtilities.invokeLater() schedula l'aggiornamento correttamente.
         */
        SwingUtilities.invokeLater(() -> {
            if (id == 0) {
                lblValore.setText(String.valueOf(valore));
            } else {
                lblValore2.setText(String.valueOf(valore));
            }
        });
    }
}