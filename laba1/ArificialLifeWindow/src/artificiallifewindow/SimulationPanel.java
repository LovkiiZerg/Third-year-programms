package artificiallifewindow;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SimulationPanel extends JPanel {

    private Environment environment;
    private Timer timer;

    private int steps;
    private boolean running;

    public SimulationPanel() {

        setPreferredSize(
                new Dimension(1000, 650));

        setBackground(Color.WHITE);

        // Параметры НЕЛЬЗЯ менять.
        environment =
                new Environment(40, 40);

        environment.initialize(
                375,
                40,
                12);

        steps = 0;
        running = false;

        // Автоматическая симуляция.
        timer = new Timer(120, e -> {

            if (!running) {
                return;
            }

            environment.step();

            steps++;

            repaint();

            checkWinner();
        });
    }

    public void startSimulation() {

        if (!running) {

            running = true;

            timer.start();
        }
    }

    public void newGame() {

        timer.stop();

        environment =
                new Environment(40, 40);

        environment.initialize(
                375,
                40,
                12);

        steps = 0;

        running = false;

        repaint();
    }

    private void checkWinner() {

        int herbivores =
                environment.countHerbivores();

        int predators =
                environment.countPredators();

        if (herbivores == 0
                && predators == 0) {

            finishGame(
                    "Ничья!\nОба вида вымерли.");
        }

        else if (herbivores == 0) {

            finishGame(
                    "Победили хищники!");
        }

        else if (predators == 0) {

            finishGame(
                    "Победили травоядные!");
        }
    }

    private void finishGame(
            String message) {

        if (!running) {
            return;
        }

        running = false;

        timer.stop();

        JOptionPane.showMessageDialog(
                this,
                message
                        + "\nШагов: "
                        + steps,
                "Симуляция завершена",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public int getSteps() {
        return steps;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    protected void paintComponent(
            Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 =
                (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int columns =
                environment.getWidth();

        int rows =
                environment.getHeight();

        int cellWidth =
                getWidth() / columns;

        int cellHeight =
                getHeight() / rows;

        int cellSize =
                Math.min(
                        cellWidth,
                        cellHeight);

        int fieldWidth =
                columns * cellSize;

        int fieldHeight =
                rows * cellSize;

        int offsetX =
                (getWidth() - fieldWidth) / 2;

        int offsetY =
                (getHeight() - fieldHeight) / 2;

        // Сетка.
        g2.setColor(
                new Color(225, 225, 225));

        for (int x = 0;
                x <= columns;
                x++) {

            int px =
                    offsetX
                    + x * cellSize;

            g2.drawLine(
                    px,
                    offsetY,
                    px,
                    offsetY + fieldHeight);
        }

        for (int y = 0;
                y <= rows;
                y++) {

            int py =
                    offsetY
                    + y * cellSize;

            g2.drawLine(
                    offsetX,
                    py,
                    offsetX + fieldWidth,
                    py);
        }

        int fontSize =
                Math.max(
                        10,
                        (int) (cellSize * 0.75));

        g2.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.PLAIN,
                        fontSize));

        g2.setColor(Color.BLACK);

        for (Agent agent :
                environment.getAgents()) {

            int x = agent.getX();
            int y = agent.getY();

            int px =
                    offsetX
                    + x * cellSize;

            int py =
                    offsetY
                    + y * cellSize;

            String symbol =
                    agent.getSymbol();

            int textWidth =
                    g2.getFontMetrics()
                       .stringWidth(symbol);

            int textHeight =
                    g2.getFontMetrics()
                       .getAscent();

            int textX =
                    px
                    + (cellSize - textWidth) / 2;

            int textY =
                    py
                    + (cellSize + textHeight) / 2
                    - 2;

            g2.drawString(
                    symbol,
                    textX,
                    textY);
        }

        g2.dispose();
    }
}