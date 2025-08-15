package net.nekoepisode.tankserver.game.tank;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class TankManager {
    private static final TankManager instance = new TankManager();
    private static final Random random = new Random();

    private final Map<Integer, Tank> tankMap = new ConcurrentHashMap<>();
    private final Map<Tank, Integer> reverseTankMap = new ConcurrentHashMap<>();

    public synchronized void addTank(int networkId, Tank tank) {
        if (tank == null) {
            throw new IllegalArgumentException("Tank cannot be null");
        }
        tankMap.put(networkId, tank);
        reverseTankMap.put(tank, networkId);
    }

    public synchronized void removeTank(int networkId) {
        Tank tank = tankMap.remove(networkId);
        if (tank != null) {
            reverseTankMap.remove(tank);
        }
    }

    public synchronized void removeTank(Tank tank) {
        if (tank == null) return;

        Integer networkId = reverseTankMap.remove(tank);
        if (networkId != null) {
            tankMap.remove(networkId);
        }
    }

    public Tank getTank(int networkId) {
        return tankMap.get(networkId);
    }

    public Integer getNetworkId(Tank tank) {
        return reverseTankMap.get(tank);
    }

    public int getTankCount() {
        return tankMap.size();
    }

    public int getRandomNetworkId() {
        int attempts = 0;
        while (attempts < 100) {
            int id = random.nextInt(Integer.MAX_VALUE - 1) + 1;
            if (!tankMap.containsKey(id)) {
                return id;
            }
            attempts++;
        }
        throw new IllegalStateException("Could not find available network ID");
    }

    public static TankManager getInstance() {
        return instance;
    }
}