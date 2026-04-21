DESIGN PATTERNS · JAVA · CLASSE 4 INFORMATICA

# Il Pattern Observer

Dalle basi teoriche all'implementazione con Swing e Thread

## 📋 Indice del Tutorial

1. [Introduzione ai Design Pattern](#s1)
2. [Cos'è il Pattern Observer](#s2)
3. [Struttura e ruoli principali](#s3)
4. [Perché usare Observer](#s4)
5. [Observer in Java: le interfacce](#s5)
6. [Esempio pratico: contatore con Swing](#s6)
7. [Analisi del codice](#s7)
8. [Esecuzione e output atteso](#s8)
9. [Varianti e approfondimenti](#s9)
10. [Riepilogo e domande di verifica](#s10)

## 1 Introduzione ai Design Pattern

Un **Design Pattern** (o *pattern di progettazione*) è una soluzione
riutilizzabile a un problema ricorrente nel campo della progettazione del software.
Non si tratta di codice pronto da incollare, ma di un *modello concettuale*
che descrive come strutturare classi e oggetti per risolvere una certa categoria di
problemi.

I pattern furono sistematizzati nel celebre libro
*Design Patterns: Elements of Reusable Object-Oriented Software* (1994)
dalla cosiddetta **Gang of Four (GoF)**: Gamma, Helm, Johnson e Vlissides.
Il libro classifica 23 pattern in tre famiglie:

#### Creazionali

Riguardano la *creazione* di oggetti (es. Singleton, Factory, Builder).

#### Strutturali

Riguardano la *composizione* di classi (es. Adapter, Decorator, Facade).

#### Comportamentali

Riguardano la *comunicazione* tra oggetti (es. Observer, Strategy, Command).

Il Pattern **Observer** appartiene alla categoria *comportamentale*.

## 2 Cos'è il Pattern Observer

Il Pattern Observer definisce una relazione **uno-a-molti** tra oggetti:
quando lo stato di un oggetto cambia, tutti gli oggetti che dipendono da esso
vengono *notificati e aggiornati automaticamente*.

**Definizione GoF:** «Define a one-to-many dependency between objects so that
when one object changes state, all its dependents are notified and updated automatically.»

Un esempio quotidiano: immaginate di iscrivervi a una newsletter.
Il sito web (l'*osservato*) mantiene una lista di iscritti (gli *osservatori*).
Ogni volta che viene pubblicato un articolo, tutti gli iscritti ricevono una notifica.
Se vi cancellate, smettete di ricevere le email. Il sito non sa (né gli importa)
quanti siete: chiama semplicemente "notifica" su ciascuno.

### Analogo nella programmazione

In un'applicazione con interfaccia grafica, un **thread in background**
può calcolare valori (temperatura, conteggio, progresso) mentre la GUI deve
aggiornarsi ad ogni cambiamento. Senza un meccanismo di notifica, il thread
dovrebbe conoscere tutti i componenti grafici da aggiornare → forte accoppiamento.
Con Observer, il thread notifica semplicemente i suoi "ascoltatori" e non sa
nulla della loro implementazione concreta.

## 3 Struttura e ruoli principali

«interface»
Subject
+ addObserver(o)
+ removeObserver(o)
+ notifyObservers()

ConcreteSubject
- observers: List
- state: int
+ getState(): int

«interface»
Observer
+ update(valore: int)

ConcreteObserver
- label: JLabel
+ update(valore)
 → aggiorna GUI

usa

notifica

ContatoreThread
implements Runnable
+ run(): loop
+ setState()

#### Subject (Osservabile)

Mantiene la lista degli osservatori, permette di aggiungerli/rimuoverli e li notifica.

#### Observer (Osservatore)

Interfaccia con il metodo `update()` che viene chiamato quando lo stato cambia.

#### ConcreteSubject

Implementa Subject. Contiene lo stato reale e chiama `notifyObservers()` ad ogni modifica.

## 4 Perché usare il Pattern Observer

| Vantaggio | Spiegazione |
| --- | --- |
| **Basso accoppiamento** | Il Subject conosce solo l'interfaccia Observer, non le classi concrete. |
| **Apertura all'estensione** | Si aggiungono nuovi osservatori senza toccare il Subject (principio Open/Closed). |
| **Notifica automatica** | Gli osservatori non devono interrogare continuamente il Subject (no polling). |
| **Riuso del codice** | Lo stesso Subject può notificare osservatori di tipo completamente diverso. |
| **Thread-safety** | Con opportune accortezze, permette comunicazione sicura tra thread e GUI. |

**Attenzione:** un uso eccessivo di Observer può rendere difficile il debug,
perché le catene di notifiche possono essere lunghe e non immediatamente visibili nel codice.
Usatelo dove la dipendenza uno-a-molti è davvero il problema da risolvere.

## 5 Observer in Java: le interfacce

Java ha incluso fino a Java 8 le classi `java.util.Observable` e
`java.util.Observer`. Queste sono state **deprecate in Java 9**
per alcune limitazioni di design (Observable è una classe, non un'interfaccia).
Per questo motivo, nel nostro esempio definiremo le nostre interfacce personalizzate,
approccio consigliato nelle applicazioni moderne.

**Best practice:** definire interfacce proprie ti dà pieno controllo
sulla firma del metodo `update()` e rende il codice più leggibile
e testabile.

### Interfaccia Observer

Observer.java

```
package observer.example;

/**
 * Interfaccia Observer.
 * Ogni classe che vuole ricevere notifiche deve implementarla.
 */
public interface Observer {
    /**
     * Chiamato dal Subject quando il suo stato cambia.
     * @param valore il nuovo valore dello stato
     */
    void update(int valore);
}
```

### Interfaccia Subject

Subject.java

```
package observer.example;

/**
 * Interfaccia Subject (Osservabile).
 * Gestisce la lista degli Observer e le notifiche.
 */
public interface Subject {
    void addObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}
```

## 6 Esempio pratico: Contatore con Thread e Swing

Realizziamo un'applicazione Swing in cui un **thread in background**
incrementa un contatore ogni secondo. La finestra principale mostra il valore
corrente in una `JLabel`, aggiornata in tempo reale grazie al pattern Observer.

**Nota su Swing e Thread:** Swing non è thread-safe. Le modifiche ai
componenti grafici devono sempre avvenire sull'*Event Dispatch Thread (EDT)*.
Per questo useremo `SwingUtilities.invokeLater()` all'interno del metodo
`update()`.

### Struttura del progetto

Struttura cartelle

```
observer-example/
 └── src/
      └── observer/
           └── example/
                ├── Observer.java          // interfaccia Observer
                ├── Subject.java           // interfaccia Subject
                ├── ContatoreThread.java    // Runnable + Subject
                ├── PannelloContatore.java // JFrame + Observer
                └── Main.java              // punto di ingresso
```

### ContatoreThread.java — il Subject che implementa Runnable

ContatoreThread.java

```
package observer.example;

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
```

### PannelloContatore.java — il ConcreteObserver (JFrame)

PannelloContatore.java

```
package observer.example;

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
        JButton btnStop = new JButton("⏹ Ferma");
        btnStop.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnStop.setBackground(new Color(217, 79, 0));
        btnStop.setForeground(Color.WHITE);
        btnStop.setFocusPainted(false);
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
```

### Main.java — punto di ingresso

Main.java

```
package observer.example;

import javax.swing.SwingUtilities;

public class Main {

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
```

## 7 Analisi approfondita del codice

### Flusso di esecuzione

* `Main.main()` crea un'istanza di `ContatoreThread` (il Subject).
* Sull'EDT viene creato `PannelloContatore` (l'Observer), che viene registrato nel Subject tramite `addObserver(finestra)`.
* Il thread viene avviato: il metodo `run()` entra nel ciclo e aspetta 1 secondo con `Thread.sleep(1000)`.
* Dopo ogni attesa, chiama `setValore(valore + 1)`, che internamente invoca `notifyObservers()`.
* `notifyObservers()` itera la lista e chiama `update(valore)` su ogni Observer registrato.
* `PannelloContatore.update()` usa `SwingUtilities.invokeLater()` per aggiornare la `JLabel` in modo thread-safe sull'EDT.

### Parole chiave da ricordare

| Costrutto | Perché è stato usato |
| --- | --- |
| `volatile int valore` | Garantisce la visibilità della variabile tra thread diversi senza necessità di sincronizzazione completa. |
| `synchronized` su addObserver / notify | Protegge la lista degli observer da accessi concorrenti provenienti da thread diversi. |
| Copia della lista in `notifyObservers()` | Evita `ConcurrentModificationException` nel caso in cui un observer si rimuova durante la notifica. |
| `SwingUtilities.invokeLater()` | Schedula la modifica della GUI sull'Event Dispatch Thread (EDT), unico thread sicuro per Swing. |
| `thread.setDaemon(true)` | Il thread si chiude automaticamente quando si chiude la finestra principale, senza bloccare la JVM. |

**Tip per il debug:** aggiungete una `System.out.println()`
all'interno di `update()` e di `notifyObservers()` per visualizzare
in console il nome del thread corrente con `Thread.currentThread().getName()`.
Osserverete che la notifica parte dal thread *"Thread-Contatore"*, mentre la modifica
alla GUI avviene sul thread *"AWT-EventQueue-0"*.

## 8 Esecuzione e output atteso

Compilate ed eseguite il progetto con il vostro IDE (IntelliJ IDEA, Eclipse o NetBeans).
Vedrete una finestra simile a questa:

Pattern Observer – Contatore in tempo reale

 Contatore (aggiornato ogni secondo)

42

⏹ Ferma
● Thread in esecuzione

Ogni secondo il numero aumenta di 1. Premendo **Ferma**, il thread si arresta
e l'Observer viene rimosso dalla lista: la label smette di aggiornarsi.

## 9 Varianti e approfondimenti

### 9.1 — Observer con più componenti

Basta creare un secondo Observer, ad esempio un `JProgressBar`, e registrarlo
con `contatore.addObserver(progressBar)`. Il Subject non cambia: notificherà
entrambi automaticamente.

Esempio: secondo Observer

```
// In Main.java, dopo aver creato finestra:
JProgressBar bar = new JProgressBar(0, 100);

Observer obsBar = valore -> SwingUtilities.invokeLater(() ->
    bar.setValue(valore % 101)   // cicla da 0 a 100
);

contatore.addObserver(finestra);   // Observer 1 → JLabel
contatore.addObserver(obsBar);    // Observer 2 → JProgressBar
```

### 9.2 — Pattern Observer e PropertyChangeListener

Il JDK include `java.beans.PropertyChangeSupport` e
`PropertyChangeListener`, un'implementazione pronta del pattern Observer
già integrata con Swing (usata da molti componenti standard). Vale la pena studiarla
dopo aver capito il pattern di base.

### 9.3 — Observer nella programmazione reattiva

Il pattern Observer è il fondamento della **Reactive Programming**:
librerie come *RxJava* e *Project Reactor* estendono questo concetto
con stream di dati asincroni, operatori di trasformazione e gestione avanzata degli errori.

**Approfondimento:** studiare il pattern Observer è il primo passo per
comprendere l'architettura *Model-View-Controller (MVC)*, dove il Model è il
Subject e la View è l'Observer. MVC è alla base di framework come Spring MVC e JavaFX.

## 10 Riepilogo e domande di verifica

### Concetti chiave

* Il Pattern Observer definisce una relazione **uno-a-molti** tra oggetti.
* Il **Subject** mantiene la lista degli osservatori e li notifica ad ogni cambio di stato.
* L'**Observer** implementa il metodo `update()` per ricevere le notifiche.
* Il pattern riduce l'**accoppiamento**: Subject e Observer si conoscono solo tramite interfaccia.
* In Swing, le notifiche da thread secondari devono passare per `SwingUtilities.invokeLater()`.
* La parola chiave `volatile` garantisce la visibilità delle variabili tra thread.

### Domande di verifica

1. Qual è la differenza tra *accoppiamento forte* e *accoppiamento debole*? Come il pattern Observer favorisce il secondo?
2. Perché è necessario `SwingUtilities.invokeLater()` nel metodo `update()`? Cosa potrebbe succedere senza?
3. Cosa accade se si rimuove la `volatile` dalla variabile `valore`? Quali problemi potrebbero verificarsi?
4. Implementate un terzo Observer che, ogni volta che il contatore raggiunge un multiplo di 5, mostri un `JOptionPane` con un messaggio di notifica.
5. Spiegate perché in `notifyObservers()` viene creata una copia della lista degli observer prima di iterarla.
6. Come modifichereste il progetto per permettere a un Observer di ricevere sia il valore precedente che quello nuovo?

Tutorial realizzato per la **Classe 4 Informatica** — Pattern Observer in Java & Swing

Design Patterns · GoF · Thread-safe Observer · SwingUtilities
