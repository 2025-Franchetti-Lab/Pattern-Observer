/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PatternObserver;


import java.util.ArrayList;
import java.util.List;

/**
 * ConcreteSubject che implementa anche Runnable.
 * Incrementa un contatore ogni secondo e notifica gli Observer.
 */
public class ContatoreThread implements Subject, Runnable {

    // ── Lista degli osservatori (sincronizzata per accesso multi-thread) ──
    private final List<Observer> observers = new ArrayList<>();

    // ── Stato del Subject: il valore del contatore ──
    private volatile int valore = 0;

    // ── Flag per fermare il thread ──
    private volatile boolean attivo = true;

    // ════════════ Implementazione di Subject ════════════

    @Override
    public synchronized void addObserver(Observer o) {
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public synchronized void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public synchronized void notifyObservers() {
        // Notifica una copia della lista per evitare ConcurrentModificationException
        List<Observer> copia = new ArrayList<>(observers);
        for (Observer o : copia) {
            o.update(valore);
        }
    }

    // ════════════ Metodi del Subject concreto ════════════

    public int getValore() {
        return valore;
    }

    private void setValore(int v) {
        this.valore = v;
        notifyObservers();   // ogni modifica scatena la notifica
    }

    /** Ferma il loop del thread in modo pulito */
    public void ferma() {
        attivo = false;
    }

    // ════════════ Implementazione di Runnable ════════════

    @Override
    public void run() {
        while (attivo) {
            try {
                Thread.sleep(1000);   // aspetta 1 secondo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            setValore(valore + 1);  // incrementa e notifica
        }
    }
}