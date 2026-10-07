package artificiallife;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Environment environment = new Environment(40, 40);

        environment.initialize(375, 40, 12);

        Scanner scanner = new Scanner(System.in);

        int step = 0;

        System.out.println("=== ARTIFICIAL LIFE ===");
        System.out.println();
        System.out.println("Нажмите ENTER для перехода к следующему шагу.");
        System.out.println();

        while (true) {

            System.out.println("========================================");
            System.out.println("Шаг: " + step);
            System.out.println("========================================");

            System.out.println(
                    "Растения: " + environment.countPlants()
                    + " | Травоядные: " + environment.countHerbivores()
                    + " | Хищники: " + environment.countPredators()
            );

            System.out.println(
                    "Общая энергия: " + environment.getTotalEnergy()
            );

            System.out.println();

            printEnvironment(environment);

            // Если все травоядные умерли
            if (environment.countHerbivores() == 0) {

                System.out.println();
                System.out.println("========================================");
                System.out.println("        ХИЩНИКИ ПОБЕДИЛИ!");
                System.out.println("========================================");
                System.out.println("Травоядные полностью вымерли.");

                break;
            }

            // Если все хищники умерли
            if (environment.countPredators() == 0) {

                System.out.println();
                System.out.println("========================================");
                System.out.println("       ТРАВОЯДНЫЕ ПОБЕДИЛИ!");
                System.out.println("========================================");
                System.out.println("Хищники полностью вымерли.");

                break;
            }

            System.out.println();
            System.out.println("Нажмите ENTER для следующего шага...");

            scanner.nextLine();

            // Ровно один шаг симуляции
            environment.step();

            step++;
        }

        scanner.close();
    }

    private static void printEnvironment(Environment environment) {

        for (int y = 0; y < environment.getHeight(); y++) {

            for (int x = 0; x < environment.getWidth(); x++) {

                Agent agent = environment.getAgent(x, y);

                if (agent == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(agent.getSymbol() + " ");
                }
            }

            System.out.println();
        }
    }
}