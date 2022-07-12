package com.zpedroo.bosses.objects.general;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private int killedBossesAmount;
    private boolean update = false;

    public PlayerData(UUID uuid, int killedBossesAmount) {
        this.uuid = uuid;
        this.killedBossesAmount = killedBossesAmount;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int getKilledBossesAmount() {
        return killedBossesAmount;
    }

    public boolean isQueueUpdate() {
        return update;
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