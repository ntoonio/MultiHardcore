package io.antoon.mc.multi_hardcore.mixin;

import io.antoon.mc.multi_hardcore.MultiHardcore;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

	@Shadow
	public MinecraftServer server;

	@Inject(method="onDeath", at=@At("HEAD"))
	public void onDeath(DamageSource damageSource, CallbackInfo info) {
		MultiHardcore.playerDied = true;

		// Get all the players
		List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();

		// Kill each player
		ServerPlayerEntity player;
		for (int i = 0; i < players.size(); i++) {
			player = players.get(i);

			// Only kill those who aren't already spectators or are already dead
			if (player.isAlive() && !player.isSpectator()) {
				player.kill();
			}
		}
	}
}
