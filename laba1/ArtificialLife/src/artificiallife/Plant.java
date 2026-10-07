package artificiallife;

import artificiallife.Agent;
import artificiallife.Environment;
import artificiallife.Plant;

public class Plant extends Agent {

    public static final int INITIAL_ENERGY = 8;

    private static final int GROWTH = 1;

    private static final int REPRODUCTION_THRESHOLD = 20;

    private static final int CHILD_ENERGY = 6;

    public Plant(int x, int y, int energy) {
        super(x, y, energy);
    }

    @Override
    public void act(Environment environment) {

        /*
         * Растение за один шаг выполняет только ОДНО действие.
         *
         * Если энергии достаточно для размножения —
         * пытаемся создать одно новое растение.
         *
         * Если размножиться нельзя —
         * растение просто растёт на 1 энергию.
         */

        if (energy >= REPRODUCTION_THRESHOLD) {

            if (reproduce(environment)) {
                return;
            }
        }

        // Одно действие — рост.
        addEnergy(GROWTH);
    }

    /**
     * Попытка размножения.
     *
     * За один шаг создаётся максимум одно растение.
     * Используются только четыре соседние клетки.
     */
    private boolean reproduce(Environment environment) {

        int[][] directions = {
                {0, -1},
                {0, 1},
                {-1, 0},
                {1, 0}
        };

        for (int[] direction : directions) {

            int newX = x + direction[0];
            int newY = y + direction[1];

            if (environment.isInside(newX, newY)
                    && environment.isEmpty(newX, newY)) {

                Plant child = new Plant(
                        newX,
                        newY,
                        CHILD_ENERGY
                );

                if (environment.addAgent(
                        child,
                        newX,
                        newY)) {

                    removeEnergy(CHILD_ENERGY);

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String getType() {
        return "Plant";
    }

    @Override
    public String getSymbol() {
        return "Р";
    }
}