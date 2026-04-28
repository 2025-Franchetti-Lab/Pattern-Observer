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

        // 1. Crea i due Subject (uno per thread), ognuno con il proprio ID
        ContatoreThread contatore0 = new ContatoreThread(0, 1000);
        ContatoreThread contatore1 = new ContatoreThread(1, 500);

        // 2. Crea la GUI sull'EDT e la registra come Observer su entrambi i thread
        SwingUtilities.invokeLater(() -> {
            PannelloContatore finestra = new PannelloContatore(contatore0, contatore1);
            contatore0.addObserver(finestra);  // ← registrazione Observer thread 0
            contatore1.addObserver(finestra);  // ← registrazione Observer thread 1
            finestra.setVisible(true);
        });

        // 3. Avvia i thread in background (demoni: si chiudono con la JVM)
        Thread t0 = new Thread(contatore0);
        t0.setDaemon(true);
        t0.setName("Thread-Contatore-0");
        t0.start();

        Thread t1 = new Thread(contatore1);
        t1.setDaemon(true);
        t1.setName("Thread-Contatore-1");
        t1.start();
    }
    
}
