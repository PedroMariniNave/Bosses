package com.zpedroo.bosses.objects.spawner;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.zpedroo.bosses.VoltzBosses;
import com.zpedroo.bosses.utils.config.Settings;
import org.bukkit.scheduler.BukkitRunnable;

public class BossSpawnerHologram extends BukkitRunnable {

    private final BossSpawner bossSpawner;

    private Hologram hologram;
    private final String[] hologramLines = Settings.BOSS_SPAWNER_HOLOGRAM;
    private TextLine[] textLines;

    public BossSpawnerHologram(BossSpawner bossSpawner) {
        this.bossSpawner = bossSpawner;
        startTask();
    }

    public void updateHologram() {
        if (hologram == null || hologram.isDeleted()) createHologram();

        for (int i = 0; i < hologramLines.length; i++) {
            textLines[i].setText(bossSpawner.replace(hologramLines[i]));
        }
    }

    private void createHologram() {
        if (hologram != null && !hologram.isDeleted()) return;

        hologram = HologramsAPI.createHologram(VoltzBosses.get(), bossSpawner.getLocation().clone().add(0D, 3.5D, 0D));
        textLines = new TextLine[hologramLines.length];

        for (int i = 0; i < hologramLines.length; i++) {
            textLines[i] = hologram.insertTextLine(i, bossSpawner.replace(hologramLines[i]));
        }
    }

    public void removeHologram() {
        if (hologram == null || hologram.isDeleted()) return;

        hologram.delete();
        hologram = null;
    }

    @Override
    public void run() {
        this.updateHologram();
    }

    private void startTask() {
        this.runTaskTimer(VoltzBosses.get(), 0L, 10L);
    }
}