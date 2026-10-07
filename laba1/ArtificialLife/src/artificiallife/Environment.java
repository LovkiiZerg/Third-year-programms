package artificiallife;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Environment {

    private final int width;
    private final int height;

    private final Agent[][] grid;
    private final List<Agent> agents;

    private final Random random = new Random();

    public Environment(int width, int height) {
        this.width = width;
        this.height = height;

        grid = new Agent[height][width];
        agents = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Random getRandom() {
        return random;
    }

    public boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Agent getAgent(int x, int y) {
        if (!isInside(x, y)) {
            return null;
        }

        return grid[y][x];
    }

    public boolean isEmpty(int x, int y) {
        return isInside(x, y) && grid[y][x] == null;
    }

    public List<Agent> getAgents() {
        return new ArrayList<>(agents);
    }

    public boolean addAgent(Agent agent, int x, int y) {

        if (!isInside(x, y)) {
            return false;
        }

        if (!isEmpty(x, y)) {
            return false;
        }

        agent.setPosition(x, y);

        grid[y][x] = agent;
        agents.add(agent);

        return true;
    }

    public boolean addAgentRandomly(Agent agent) {

        for (int attempt = 0; attempt < 500; attempt++) {

            int x = random.nextInt(width);
            int y = random.nextInt(height);

            if (addAgent(agent, x, y)) {
                return true;
            }
        }

        return false;
    }

    public void removeAgent(Agent agent) {

        if (agent == null) {
            return;
        }

        int x = agent.getX();
        int y = agent.getY();

        if (isInside(x, y) && grid[y][x] == agent) {
            grid[y][x] = null;
        }

        agents.remove(agent);
    }

    /**
     * Перемещение только на одну соседнюю клетку.
     * Диагональное перемещение запрещено.
     */
    public boolean moveAgent(Agent agent, int newX, int newY) {

        if (agent == null) {
            return false;
        }

        if (!isInside(newX, newY)) {
            return false;
        }

        if (!isEmpty(newX, newY)) {
            return false;
        }

        int dx = Math.abs(newX - agent.getX());
        int dy = Math.abs(newY - agent.getY());

        // Ровно одна клетка: вверх, вниз, влево или вправо.
        if (dx + dy != 1) {
            return false;
        }

        grid[agent.getY()][agent.getX()] = null;

        agent.setPosition(newX, newY);

        grid[newY][newX] = agent;

        return true;
    }

    /**
     * Только четыре соседние клетки.
     */
    public List<Agent> getNeighbors(int x, int y) {

        List<Agent> result = new ArrayList<>();

        int[][] directions = {
                {0, -1},
                {0, 1},
                {-1, 0},
                {1, 0}
        };

        for (int[] direction : directions) {

            int nx = x + direction[0];
            int ny = y + direction[1];

            Agent agent = getAgent(nx, ny);

            if (agent != null) {
                result.add(agent);
            }
        }

        return result;
    }

    /**
     * Видимость 5x5.
     */
    public Agent findNearestVisible(
            Agent observer,
            Class<? extends Agent> targetClass) {

        Agent nearest = null;
        int bestDistance = Integer.MAX_VALUE;

        for (Agent candidate : agents) {

            if (candidate == observer) {
                continue;
            }

            if (!targetClass.isInstance(candidate)) {
                continue;
            }

            int dx = Math.abs(
                    candidate.getX() - observer.getX());

            int dy = Math.abs(
                    candidate.getY() - observer.getY());

            if (dx <= 2 && dy <= 2) {

                int distance = dx + dy;

                if (distance < bestDistance) {
                    bestDistance = distance;
                    nearest = candidate;
                }
            }
        }

        return nearest;
    }

    /**
     * Один полный шаг экосистемы.
     */
    public void step() {

        List<Agent> turnOrder =
                new ArrayList<>(agents);

        Collections.shuffle(turnOrder, random);

        for (Agent agent : turnOrder) {

            if (!agents.contains(agent)) {
                continue;
            }

            if (!agent.isAlive()) {
                continue;
            }

            agent.act(this);
        }

        List<Agent> deadAgents =
                new ArrayList<>();

        for (Agent agent : agents) {

            if (!agent.isAlive()) {
                deadAgents.add(agent);
            }
        }

        for (Agent dead : deadAgents) {
            removeAgent(dead);
        }
    }

    /**
     * Травоядное съедает соседнее растение.
     *
     * После удаления растения клетка становится свободной.
     */
    public boolean eatPlant(Herbivore herbivore) {

        for (Agent neighbor : getNeighbors(
                herbivore.getX(),
                herbivore.getY())) {

            if (neighbor instanceof Plant) {

                Plant plant = (Plant) neighbor;

                herbivore.addEnergy(
                        Herbivore.FOOD_ENERGY);

                removeAgent(plant);

                return true;
            }
        }

        return false;
    }

    /**
     * Хищник съедает соседнее травоядное.
     */
    public boolean eatHerbivore(Predator predator) {

        for (Agent neighbor : getNeighbors(
                predator.getX(),
                predator.getY())) {

            if (neighbor instanceof Herbivore) {

                Herbivore herbivore =
                        (Herbivore) neighbor;

                predator.addEnergy(
                        Predator.FOOD_ENERGY);

                removeAgent(herbivore);

                return true;
            }
        }

        return false;
    }

    public int countPlants() {
        return countType(Plant.class);
    }

    public int countHerbivores() {
        return countType(Herbivore.class);
    }

    public int countPredators() {
        return countType(Predator.class);
    }

    private int countType(
            Class<? extends Agent> type) {

        int count = 0;

        for (Agent agent : agents) {

            if (type.isInstance(agent)) {
                count++;
            }
        }

        return count;
    }

    public int getTotalEnergy() {

        int total = 0;

        for (Agent agent : agents) {
            total += agent.getEnergy();
        }

        return total;
    }

    public void clear() {

        agents.clear();

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {
                grid[y][x] = null;
            }
        }
    }

    /**
     * Начальные параметры:
     * 375 растений,
     * 40 травоядных,
     * 12 хищников.
     */
    public void initialize(
            int plants,
            int herbivores,
            int predators) {

        clear();

        for (int i = 0; i < plants; i++) {

            addAgentRandomly(
                    new Plant(
                            0,
                            0,
                            Plant.INITIAL_ENERGY));
        }

        for (int i = 0; i < herbivores; i++) {

            addAgentRandomly(
                    new Herbivore(
                            0,
                            0,
                            Herbivore.INITIAL_ENERGY));
        }

        for (int i = 0; i < predators; i++) {

            addAgentRandomly(
                    new Predator(
                            0,
                            0,
                            Predator.INITIAL_ENERGY));
        }
    }
}
