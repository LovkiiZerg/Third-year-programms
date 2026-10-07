package artificiallifewindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Predator extends Agent {

    public static final int INITIAL_ENERGY = 280;
    public static final int FOOD_ENERGY = 120;

    private static final int ENERGY_COST = 1;
    private static final int REPRODUCTION_THRESHOLD = 900;
    private static final int CHILD_ENERGY = 180;

    public Predator(int x, int y, int energy) {
        super(x, y, energy);
    }

    @Override
    public void act(Environment environment) {

        // Каждый шаг тратится ровно 1 энергия.
        removeEnergy(ENERGY_COST);

        if (!isAlive()) {
            return;
        }

        /*
         * Одно действие за шаг:
         *
         * если травоядное рядом — съесть его;
         * иначе сделать один шаг к травоядному;
         * если травоядного нет — один случайный шаг.
         */

        Agent herbivore = environment.findNearestVisible(
                this,
                Herbivore.class
        );

        if (herbivore != null) {

            // Если жертва непосредственно рядом —
            // действие = атака.
            if (distance(
                    x,
                    y,
                    herbivore.getX(),
                    herbivore.getY()) == 1) {

                environment.eatHerbivore(this);

            } else {

                // Иначе действие = один шаг к жертве.
                moveTowards(environment, herbivore);
            }

        } else {

            // Одно действие = случайный шаг.
            wander(environment);
        }

        /*
         * Размножение после действия.
         */
        if (energy >= REPRODUCTION_THRESHOLD) {
            reproduce(environment);
        }
    }

    /**
     * Один шаг к травоядному.
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

        Predator child = new Predator(
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
        return "Predator";
    }

    @Override
    public String getSymbol() {
        return "🐺";
    }
}