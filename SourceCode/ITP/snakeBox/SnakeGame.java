import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    // ---- Typen & Konstanten ---------------------------------------------------
    // Größe jeder Spielfeldkachel (25 Pixel)
    private static final int TILE_SIZE   = 25;

    // Grundgeschwindigkeit (ms zwischen Bewegungen)
    private static final int BASE_DELAY  = 140;

    // Minimale Geschwindigkeit – Spiel wird nicht schneller als das
    private static final int MIN_DELAY   = 70;

    // Nach wie vielen Punkten ein Levelaufstieg passiert (5, 10, 15,…)
    private static final int LVL_STEP    = 5;

    // Schriften & Farben, damit später alles einheitlich dargestellt wird
    private static final Font HUD_FONT   = new Font("Arial", Font.BOLD, 16);
    private static final Color GRID_COL  = new Color(40, 40, 40);
    private static final Color FOOD_COL  = new Color(220, 70, 70);
    private static final Color HEAD_COL  = new Color(0, 220, 100);
    private static final Color BODY_COL  = new Color(0, 180, 80);

    // Für Richtungssteuerung – einfacher lesbar als nur Zahlen
    private enum Direction { UP, DOWN, LEFT, RIGHT }

    // Kleine Hilfsklasse für Positionen auf dem Spielfeld
    private static class Tile {
        int x, y;
        Tile(int x, int y) { this.x = x; this.y = y; }
    }

    // ---- Spielfeldgröße -------------------------------------------------------
    private final int boardWidth;
    private final int boardHeight;

    // ---- Schlange & Futter ----------------------------------------------------
    private Tile snakeHead;
    private ArrayList<Tile> segments;
    private Tile food;
    private final Random random = new Random();

    // ---- Spielstatus ----------------------------------------------------------
    private int dx = 1, dy = 0;             // Bewegungsrichtung der Schlange (in Tiles)
    private Direction dir = Direction.RIGHT;// Ausgewählte Richtung merken
    private boolean dead = false;           // Ob das Spiel vorbei ist

    private int level = 1;                  // Startlevel
    private Timer ticker;                   // steuert die Spielaktualisierung

    // UI-Elemente
    private JButton restartButton;

    // ---- Konstruktor ----------------------------------------------------------
    public SnakeGame(int width, int height) {
        this.boardWidth = width;
        this.boardHeight = height;

        // Grundlegende Panel-Einstellungen
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        setLayout(null);         // Für absolute Positionierung des Restart-Buttons
        setFocusable(true);
        addKeyListener(this);

        createRestartButton();   // Restart-Knopf erstellen
        resetWorld();            // Anfangszustand herstellen

        ticker = new Timer(BASE_DELAY, this);
        ticker.start();
    }

    // ---- Setup / Neustart -----------------------------------------------------
    private void createRestartButton() {
        restartButton = new JButton("Restart");
        restartButton.setFocusable(false);
        restartButton.setVisible(false);
        // Button mittig platzieren
        restartButton.setBounds(boardWidth / 2 - 60, boardHeight / 2, 120, 40);

        // Aktion für den Neustart
        restartButton.addActionListener(e -> {
            resetWorld();
            restartButton.setVisible(false);
            ticker.start();
            requestFocusInWindow();  // wichtig für Tastatureingaben
            repaint();
        });

        add(restartButton);
    }

    private void resetWorld() {
        // Schlange zurücksetzen
        snakeHead = new Tile(4, 4);
        segments  = new ArrayList<>();

        // Neues Futter
        food      = new Tile(0, 0);
        placeFood();

        // Standardrichtung
        dx = 1; dy = 0;
        dir = Direction.RIGHT;

        dead = false;
        level = 1;
        applyLevelSpeed(); // Geschwindigkeit anpassen
    }

    // ---- Zeichnen -------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderScene((Graphics2D) g.create());
    }

    private void renderScene(Graphics2D g2) {
        // Schönere Linien & Text
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Hintergrundgitter zeichnen
        g2.setColor(GRID_COL);
        for (int i = 0; i <= boardWidth / TILE_SIZE; i++) {
            int x = i * TILE_SIZE;
            g2.drawLine(x, 0, x, boardHeight);
        }
        for (int j = 0; j <= boardHeight / TILE_SIZE; j++) {
            int y = j * TILE_SIZE;
            g2.drawLine(0, y, boardWidth, y);
        }

        // Futter zeichnen
        int fx = food.x * TILE_SIZE, fy = food.y * TILE_SIZE;
        g2.setColor(FOOD_COL);
        g2.fillOval(fx + 4, fy + 4, TILE_SIZE - 8, TILE_SIZE - 8);

        // Schlangenkopf
        int hx = snakeHead.x * TILE_SIZE, hy = snakeHead.y * TILE_SIZE;
        g2.setColor(HEAD_COL);
        g2.fillRoundRect(hx + 2, hy + 2, TILE_SIZE - 4, TILE_SIZE - 4, 8, 8);

        // Schlangenkörper
        g2.setColor(BODY_COL);
        for (Tile t : segments) {
            int bx = t.x * TILE_SIZE, by = t.y * TILE_SIZE;
            g2.fillRoundRect(bx + 3, by + 3, TILE_SIZE - 6, TILE_SIZE - 6, 6, 6);
        }

        // HUD-Text (Score & Level)
        g2.setFont(HUD_FONT);
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + score(), 10, 20);
        g2.drawString("Level: " + level, 10, 40);

        // Game-Over-Overlay
        if (dead) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, boardWidth, boardHeight);

            g2.setColor(Color.WHITE);
            g2.setFont(HUD_FONT.deriveFont(Font.BOLD, 36f));
            String msg = "GAME OVER!";
            int w = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (boardWidth - w) / 2, boardHeight / 2 - 40);

            restartButton.setVisible(true);
        }

        g2.dispose();
    }

    // ---- Spielzyklus ----------------------------------------------------------
    @Override
    public void actionPerformed(ActionEvent e) {
        step();
        repaint();
    }

    private void step() {
        if (dead) return;

        // Prüfen, ob Futter gegessen wird
        if (touching(snakeHead, food)) {
            segments.add(new Tile(food.x, food.y));
            placeFood();
            checkLevelUp();
        }

        // Körper bewegen (jedes Segment übernimmt die Position des vorherigen)
        for (int i = segments.size() - 1; i >= 0; i--) {
            if (i == 0) {
                segments.get(0).x = snakeHead.x;
                segments.get(0).y = snakeHead.y;
            } else {
                segments.get(i).x = segments.get(i - 1).x;
                segments.get(i).y = segments.get(i - 1).y;
            }
        }

        // Kopf bewegen
        snakeHead.x += dx;
        snakeHead.y += dy;

        // Kollidiert die Schlange mit sich selbst?
        for (Tile t : segments) {
            if (touching(snakeHead, t)) {
                dead = true;
                break;
            }
        }

        // Kollision mit Spielfeldrändern prüfen
        if (!dead) {
            if (snakeHead.x < 0 || snakeHead.x >= boardWidth / TILE_SIZE ||
                    snakeHead.y < 0 || snakeHead.y >= boardHeight / TILE_SIZE) {
                dead = true;
            }
        }

        // Spiel anhalten, wenn Game Over
        if (dead) {
            ticker.stop();
        }
    }

    // ---- Hilfsmethoden --------------------------------------------------------
    private void placeFood() {
        // Futter an zufälliger Position platzieren
        food.x = random.nextInt(boardWidth / TILE_SIZE);
        food.y = random.nextInt(boardHeight / TILE_SIZE);
    }

    private boolean touching(Tile a, Tile b) {
        return a.x == b.x && a.y == b.y;
    }

    private int score() {
        return segments.size();
    }

    private void checkLevelUp() {
        // Levelerhöhung nach 5, 10, 15, …
        int expected = (score() / LVL_STEP) + 1;
        if (expected > level) {
            level = expected;
            applyLevelSpeed();
        }
    }

    private void applyLevelSpeed() {
        // Geschwindigkeit basierend auf Level anpassen
        int newDelay = Math.max(MIN_DELAY, BASE_DELAY - (level - 1) * 10);
        if (ticker != null) {
            ticker.setDelay(newDelay);
        }
    }

    // ---- Tasteneingaben -------------------------------------------------------
    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();

        // Richtungswechsel, aber nicht in die entgegengesetzte Richtung
        if ((k == KeyEvent.VK_UP || k == KeyEvent.VK_W) && dir != Direction.DOWN) {
            dx = 0; dy = -1; dir = Direction.UP;
        } else if ((k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) && dir != Direction.UP) {
            dx = 0; dy = 1;  dir = Direction.DOWN;
        } else if ((k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) && dir != Direction.RIGHT) {
            dx = -1; dy = 0; dir = Direction.LEFT;
        } else if ((k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) && dir != Direction.LEFT) {
            dx = 1;  dy = 0; dir = Direction.RIGHT;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
