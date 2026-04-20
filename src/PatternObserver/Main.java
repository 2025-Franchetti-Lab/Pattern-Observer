/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package PatternObserver;
import javax.swing.SwingUtilities;

/**
 *
 * @author delfo
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // 1. Crea il Subject (che è anche Runnable)
        ContatoreThread contatore = new ContatoreThread();

        // 2. Crea la GUI sull'EDT e la registra come Observer
        SwingUtilities.invokeLater(() -> {
            PannelloContatore finestra = new PannelloContatore(contatore);
            contatore.addObserver(finestra);  // ← registrazione Observer
            finestra.setVisible(true);
        });

        // 3. Avvia il thread in background (è un thread demone: si chiude con la JVM)
        Thread t = new Thread(contatore);
        t.setDaemon(true);
        t.setName("Thread-Contatore");
        t.start();
    }
    
}
