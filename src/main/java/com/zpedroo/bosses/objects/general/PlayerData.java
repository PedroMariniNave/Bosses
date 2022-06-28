package com.zpedroo.bosses.objects.general;

import java.math.BigInteger;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private BigInteger pointsAmount;
    private int killedBossesAmount;
    private boolean update = false;

    public PlayerData(UUID uuid, BigInteger pointsAmount, int killedBossesAmount) {
        this.uuid = uuid;
        this.pointsAmount = pointsAmount;
        this.killedBossesAmount = killedBossesAmount;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public BigInteger getPointsAmount() {
        return pointsAmount;
    }

    public int getKilledBossesAmount() {
        return killedBossesAmount;
    }

    public boolean isQueueUpdate() {
        return update;
    }

    public void addPoints(BigInteger amount) {
        setPointsAmount(this.pointsAmount.add(amount));
    }

    public void removePoints(BigInteger amount) {
        setPointsAmount(this.pointsAmount.subtract(amount));
    }

    public void setPointsAmount(BigInteger amount) {
        this.pointsAmount = amount;
        this.update = true;
    }

    public void addKilledBossesAmount(int amount) {
        setKilledBossesAmount(killedBossesAmount += amount);
    }

    public void setKilledBossesAmount(int amount) {
        this.killedBossesAmount = amount;
        this.update = true;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}