package artificiallifewindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Herbivore extends Agent {

    public static final int INITIAL_ENERGY = 160;
    public static final int FOOD_ENERGY = 10;

    private static final int ENERGY_COST = 1;
    private static final int REPRODUCTION_THRESHOLD = 460;
    private static final int CHILD_ENERGY = 100;

    public Herbivore(int x, int y, int energy) {
        super(x, y, energy);
    }

    @Override
    public void act(Environment environment) {

        // Каждый шаг тратится ровно 1 единица энергии.
        removeEnergy(ENERGY_COST);

        if (!isAlive()) {
            return;
        }

        /*
         * Одно животное за один шаг выполняет только ОДНО действие:
         *
         * 1. убегает от хищника;
         * 2. ест растение, если оно уже рядом;
         * 3. идёт к растению;
         * 4. случайно перемещается.
         */

        Agent predator = environment.findNearestVisible(
                this,
                Predator.class
        );

        if (predator != null) {

            // Одно действие: только движение.
            flee(environment, predator);

        } else {

            Agent plant = environment.findNearestVisible(
                    this,
                    Plant.class
            );

            if (plant != null) {

                // Если растение уже рядом — действие = еда.
                if (distance(
                        x,
                        y,
                        plant.getX(),
                        plant.getY()) == 1) {

                    environment.eatPlant(this);

                } else {

                    // Иначе действие = один шаг к растению.
                    moveTowards(environment, plant);
                }

            } else {

                // Одно действие = один случайный шаг.
                wander(environment);
            }
        }

        /*
         * Размножение выполняется только если у животного
         * достаточно энергии и есть свободная клетка.
         */
        if (energy >= REPRODUCTION_THRESHOLD) {
            reproduce(environment);
        }
    }

    /**
     * Убегание от хищника.
     */
    private void flee(
            Environment environment,
            Agent predator) {

        List<int[]> moves = getFreeMoves(environment);

        if (moves.isEmpty()) {
            return;
        }

        int bestX = x;
        int bestY = y;

        int bestDistance = distance(
                x,
                y,
                predator.getX(),
                predator.getY()
        );

        for (int[] move : moves) {

            int currentDistance = distance(
                    move[0],
                    move[1],
                    predator.getX(),
                    predator.getY()
            );

            if (currentDistance > bestDistance) {

                bestDistance = currentDistance;

                bestX = move[0];
                bestY = move[1];
            }
        }

        environment.moveAgent(
                this,
                bestX,
                bestY
        );
    }

    /**
     * Один шаг в сторону растения.
     * Диагональное движение невозможно.
     */
    private void moveTowards(
            Environment environment,
            Agent target) {

        List<int[]> moves = getFreeMoves(environment);

        if (moves.isEmpty()) {
            return;
        }

        int bestX = x;
        int bestY = y;

        int bestDistance = Integer.MAX_VALUE;

        for (int[] move : moves) {

            int currentDistance = distance(
                    move[0],
                    move[1],
                    target.getX(),
                    target.getY()
            );

            if (currentDistance < bestDistance) {

                bestDistance = currentDistance;

                bestX = move[0];
                bestY = move[1];
            }
        }

        environment.moveAgent(
                this,
                bestX,
                bestY
        );
    }

    /**
     * Случайный ход.
     */
    private void wander(Environment environment) {

        List<int[]> moves = getFreeMoves(environment);

        if (moves.isEmpty()) {
            return;
        }

        Collections.shuffle(
                moves,
                environment.getRandom()
        );

        int[] move = moves.get(0);

        environment.moveAgent(
                this,
                move[0],
                move[1]
        );
    }

    /**
     * Только четыре соседние клетки.
     */
    private List<int[]> getFreeMoves(
            Environment environment) {

        List<int[]> moves = new ArrayList<>();

        int[][] directions = {
                {0, -1},
                {0, 1},
                {-1, 0},
                {1, 0}
        };

        for (int[] direction : directions) {

            int nx = x + direction[0];
            int ny = y + direction[1];

            if (environment.isEmpty(nx, ny)) {
                moves.add(new int[]{nx, ny});
            }
        }

        return moves;
    }

    private int distance(
            int x1,
            int y1,
            int x2,
            int y2) {

        return Math.abs(x1 - x2)
                + Math.abs(y1 - y2);
    }

    /**
     * Размножение.
     */
    private void reproduce(Environment environment) {

        List<int[]> freeCells = getFreeMoves(environment);

        if (freeCells.isEmpty()) {
            return;
        }

        Collections.shuffle(
                freeCells,
                environment.getRandom()
        );

        int[] cell = freeCells.get(0);

        Herbivore child = new Herbivore(
                cell[0],
                cell[1],
                CHILD_ENERGY
        );

        if (environment.addAgent(
                child,
                cell[0],
                cell[1])) {

            removeEnergy(CHILD_ENERGY);
        }
    }

    @Override
    public String getType() {
        return "Herbivore";
    }

    @Override
    public String getSymbol() {
        return "🐇";
    }
}