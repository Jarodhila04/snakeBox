import javax.swing.*;

public final class App {

    // Hilfsklasse – soll nicht instanziert werden
    private App() {}

    public static void main(String[] args) {
        // Die grafische Oberfläche wird über den Event Dispatch Thread gestartet
        SwingUtilities.invokeLater(() -> {
            installSystemLookAndFeel();  // Optik an das Betriebssystem anpassen

            final int widthPx  = 600;
            final int heightPx = widthPx; // quadratisches Spielfeld

            // Hauptfenster des Spiels
            JFrame window = new JFrame("Snake ★");
            window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            window.setResizable(false);  // Fenstergröße soll fix bleiben

            // Das SnakeGame-Panel legt seine Wunschgröße selbst fest
            SnakeGame game = new SnakeGame(widthPx, heightPx);
            window.setContentPane(game);
            window.pack();                     // Fenstergröße exakt anpassen
            window.setLocationRelativeTo(null);// mittig am Bildschirm platzieren
            game.requestFocusInWindow();       // Fokus für Tastatureingaben setzen

            window.setVisible(true);           // Fenster zuletzt sichtbar machen
        });
    }

    private static void installSystemLookAndFeel() {
        try {
            // Versucht das Look-and-Feel des Betriebssystems zu nutzen,
            // damit Buttons & Menüs etwas „natürlicher” aussehen
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Falls das nicht klappt, bleibt einfach der Standard übrig
        }
    }
}
