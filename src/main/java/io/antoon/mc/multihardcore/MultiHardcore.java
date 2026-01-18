package io.antoon.mc.multihardcore;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiHardcore implements ModInitializer {
	public static final String MOD_ID = "multihardcore";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static DeadPlayersManager deadManager;

	@Override
	public void onInitialize() {
		// When server starts
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			deadManager = new DeadPlayersManager(server); // this feels like too late to initialize
		});


		// Test if this is first time player connect
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			var player = handler.player;
			ServerStatHandler statHandler = player.getStatHandler();
			int leaveGame = statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.LEAVE_GAME));

			if (leaveGame > 0) return;

			// First time this player joined

			/*
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 30 * 60 * 20, 255, true, true));
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 30 * 60 * 20, 255, true, true));
			*/
		});

		// Immediately disconnect player that dies
		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (entity.isPlayer()) {
				ServerPlayerEntity player = (ServerPlayerEntity) entity;
				player.networkHandler.disconnect(Text.empty()
					.append(Text.literal("You died :(").formatted())
					.append("You will not be able to play until the next season"));
			}
		});
	}
}
