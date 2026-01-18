package io.antoon.mc.multihardcore;

import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public class DeadPlayersManager {
	private ArrayList<String> deadPlayers = new ArrayList<String>();

	public DeadPlayersManager(MinecraftServer server) {
		loadStatFiles(server);
	}

	public boolean hasUuidDied(String uuid) {
		return deadPlayers.contains(uuid);
	}

	private void loadStatFiles(MinecraftServer server) {
		// Get stats directory
		Path statsDir = server.getSavePath(WorldSavePath.STATS);
		if (!statsDir.toFile().exists()) return;

		File[] statsFiles = statsDir.toFile().listFiles();
		for (File statsFile : statsFiles) {
			// Parse the statistic file
			ServerStatHandler serverStatHandler = new ServerStatHandler(server, statsFile.toPath());

			// Fetch the number of deaths
			int playerDeaths = serverStatHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS));

			if (playerDeaths > 0) {
				String uuid = statsFile.getName().replace(".json", "");
				deadPlayers.add(uuid);
			}
		}
	}
}
