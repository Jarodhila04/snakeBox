SnakeBox – README (Deutsch)

SnakeBox ist ein modernisiertes Snake-Spiel, das in Java mit Swing
entwickelt wurde.
Es enthält ein Levelsystem, dynamische Geschwindigkeitsanpassung, WASD-
und Pfeiltastensteuerung, einen Restart-Button und ein klares,
rasterbasiertes Spielfeld-Design.

------------------------------------------------------------------------

✅ Hauptfunktionen

-   Flüssige Schlangenbewegung (25px Kacheln)
-   Level-System: alle 5 Punkte steigt das Level
-   Automatische Geschwindigkeitssteigerung: pro Level wird der Timer
    schneller
-   Steuerung über WASD + Pfeiltasten
-   Game-Over-Overlay
-   Restart-Button
-   Übersichtliches Grid, farbige Snake und Food-Elemente

------------------------------------------------------------------------

🕹️ Startanleitung

1.  Java installieren (empfohlen: Java 17 oder aktueller)

2.  Projekt kompilieren:

        javac App.java SnakeGame.java

3.  Programm ausführen:

        java App

Das Spielfenster öffnet sich automatisch.

------------------------------------------------------------------------

📁 Projektstruktur

    SnakeBox/
    │
    ├─ App.java          # Erstellt das Fenster & lädt SnakeGame
    └─ SnakeGame.java    # Enthält die gesamte Spiellogik & Darstellung

------------------------------------------------------------------------

🎮 Steuerung

  Aktion   Taste
  -------- ----------
  Hoch     W oder ↑
  Runter   S oder ↓
  Links    A oder ←
  Rechts   D oder →

Die Schlange kann sich nicht direkt umdrehen (z. B. rechts → links).

------------------------------------------------------------------------

🚀 Level- & Geschwindigkeitssystem

-   Alle 5 Punkte: Level +1
-   Pro Level verringert sich das Timer-Intervall
-   Ab ca. Level 8 wird es deutlicher schneller

------------------------------------------------------------------------

💥 Game Over

Das Spiel endet bei: - Kollision mit dem Spielfeldrand - Kollision mit
dem eigenen Körper

Danach erscheint ein Restart-Button, um sofort neu zu beginnen.

------------------------------------------------------------------------

🔧 Bekannte Einschränkungen

-   Keine Pausefunktion
-   Bei sehr hohen Levels wird die Steuerung schwieriger

------------------------------------------------------------------------

