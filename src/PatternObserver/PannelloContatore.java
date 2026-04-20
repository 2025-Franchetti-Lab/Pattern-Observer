/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PatternObserver;

import javax.swing.*;
import java.awt.*;

/**
 * ConcreteObserver: finestra Swing che visualizza il valore del contatore.
 * Implementa Observer → riceve le notifiche e aggiorna la JLabel.
 */
public class PannelloContatore extends JFrame implements Observer {

    private final JLabel lblValore;
    private final JLabel lblStatus;
    private final ContatoreThread contatore;

    public PannelloContatore(ContatoreThread contatore) {
        this.contatore = contatore;

        // ── Impostazioni finestra ──
        setTitle("Pattern Observer – Contatore in tempo reale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(247, 245, 240));

        // ── Pannello centrale con il valore del contatore ──
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);

        lblValore = new JLabel("0");
        lblValore.setFont(new Font("SansSerif", Font.BOLD, 72));
        lblValore.setForeground(new Color(26, 79, 196));
        centro.add(lblValore);

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
            contatore.ferma();
            contatore.removeObserver(this);
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
    public void update(int valore) {
        /*
         * IMPORTANTE: il metodo update() viene chiamato dal thread in background.
         * Le modifiche ai componenti Swing devono avvenire sull'EDT.
         * SwingUtilities.invokeLater() schedula l'aggiornamento correttamente.
         */
        SwingUtilities.invokeLater(() ->
            lblValore.setText(String.valueOf(valore))
        );
    }
}