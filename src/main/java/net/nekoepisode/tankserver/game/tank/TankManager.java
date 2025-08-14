package net.nekoepisode.tankserver.game.tank;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TankManager {
    private static final TankManager instance = new TankManager();

    private final Map<Integer, Tank> tankMap = new ConcurrentHashMap<>(); // networkId to Tank
    private final Map<Tank, Integer> reverseTankMap = new ConcurrentHashMap<>(); // Tank to networkId

    public void addTank(int networkId, Tank tank) {
        tankMap.put(networkId, tank);
        reverseTankMap.put(tank, networkId);
    }

    public void removeTank(int networkId) {
        Tank tank = tankMap.remove(networkId);
        reverseTankMap.remove(tank);
    }

    public void removeTank(Tank tank) {
        int networkId = reverseTankMap.remove(tank);
        tankMap.remove(networkId);
    }

    public Tank getTank(int networkId) {
        return tankMap.get(networkId);
    }

    public int getNetworkId(Tank tank) {
        return reverseTankMap.get(tank);
    }

    public static TankManager getInstance() {
        synchronized (TankManager.class) {
            return instance;
        }
    }
}
