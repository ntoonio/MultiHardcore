package io.antoon.mc.multihardcore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRules;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiHardcore implements ModInitializer {
	public static final String MOD_ID = "multihardcore";
	public static ArrayList<String> deadPlayers = new ArrayList<String>();

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			var player = handler.player;
			ServerStatHandler statHandler = player.getStatHandler();
			int leaveGame = statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME));

			if (leaveGame > 0) return;

			player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 30 * 60 * 20, 255, true, true));
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 30 * 60 * 20, 255, true, true));
		});

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			setGamerules(server);

			iterateAllPlayerStats(server);
		});
	}

	private static void iterateAllPlayerStats(MinecraftServer server) {
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

	private static void setGamerules(MinecraftServer server) {
		for (ServerWorld world : server.getWorlds()) {
			GameRules rules = world.getGameRules();


			System.out.println("HÄRKOMMERDET:");
			System.out.println(rules.getValue(GameRules.RESPAWN_RADIUS));
		}
	}
}
