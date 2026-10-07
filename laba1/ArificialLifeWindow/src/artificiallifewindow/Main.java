package artificiallifewindow;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Main extends JFrame {

    private final SimulationPanel simulationPanel;
    private final JLabel statsLabel;

    private final JButton startButton;
    private final JButton newGameButton;

    public Main() {

        setTitle("Artificial Life");

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE);

        setLayout(
                new BorderLayout());

        // Заголовок.
        JLabel title =
                new JLabel(
                        "Artificial Life",
                        JLabel.CENTER);

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24));

        title.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10));

        add(
                title,
                BorderLayout.NORTH);

        // Игровое поле.
        simulationPanel =
                new SimulationPanel();

        add(
                simulationPanel,
                BorderLayout.CENTER);

        // Нижняя панель.
        JPanel bottomPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                15,
                                10));

        statsLabel =
                new JLabel();

        statsLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        15));

        startButton =
                new JButton("Старт");

        newGameButton =
                new JButton("Новая игра");

        bottomPanel.add(
                statsLabel);

        bottomPanel.add(
                startButton);

        bottomPanel.add(
                newGameButton);

        add(
                bottomPanel,
                BorderLayout.SOUTH);

        // Старт.
        startButton.addActionListener(e -> {

            simulationPanel.startSimulation();

            startButton.setEnabled(false);
        });

        // Новая игра.
        newGameButton.addActionListener(e -> {

            simulationPanel.newGame();

            startButton.setEnabled(true);

            updateStats();
        });

        // Обновление статистики.
        Timer statsTimer =
                new Timer(
                        100,
                        e -> updateStats());

        statsTimer.start();

        updateStats();

        setSize(
                1200,
                800);

        setMinimumSize(
                new Dimension(
                        800,
                        650));

        setLocationRelativeTo(null);
    }

    private void updateStats() {

        Environment environment =
                simulationPanel.getEnvironment();

        statsLabel.setText(
                "Шаг: "
                        + simulationPanel.getSteps()
                        + "   |   "
                        + "Растений "
                        + environment.countPlants()
                        + "   |   "
                        + "Травоядных "
                        + environment.countHerbivores()
                        + "   |   "
                        + "Хищников "
                        + environment.countPredators());
    }

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(() -> {

            Main window =
                    new Main();

            window.setVisible(true);
        });
    }
}