package io.antoon.mc.multi_hardcore.mixin;

import io.antoon.mc.multi_hardcore.MultiHardcore;
import net.minecraft.network.ClientConnection;
import net.minecraft.stat.Stats;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.WorldSaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Inject(method="<init>", at=@At("TAIL"))
    public void PlayerManager(MinecraftServer server, DynamicRegistryManager.Immutable registryManager, WorldSaveHandler saveHandler, int maxPlayers, CallbackInfo info) {
        // Get all players in playerdata
        String[] ids = saveHandler.getSavedPlayerIds();

        // Iterate each player
        for (int i = 0; i < ids.length; i++) {
            if (!ids[i].endsWith(".dat")) {
                continue;
            }

            // Read the statistics file for the player
            File file = server.getSavePath(WorldSavePath.STATS).toFile();
            File file2 = new File(file, "" + ids[i] + ".json");

            // Parse the statistic file
            ServerStatHandler serverStatHandler = new ServerStatHandler(server, file2);

            // Fetch the number of deaths
            int playerDeaths = serverStatHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS));

            // If a player has died once
            if (playerDeaths > 0) {
                MultiHardcore.LOGGER.info("Some player has died. The hardcore should be considered lost");
                MultiHardcore.playerDied = true;
                break;
            }
        }
    }

    @Inject(method="onPlayerConnect", at=@At("TAIL"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        // Kill the new player connecting if the hardcore is lost.
        if (MultiHardcore.playerDied && player.isAlive() && !player.isSpectator()) {
            player.kill();
        }
    }
}
