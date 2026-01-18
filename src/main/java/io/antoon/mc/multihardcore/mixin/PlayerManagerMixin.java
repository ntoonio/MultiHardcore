package io.antoon.mc.multihardcore.mixin;

import java.net.SocketAddress;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.antoon.mc.multihardcore.MultiHardcore;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

	@Inject(method="checkCanJoin", at=@At("HEAD"), cancellable = true)
	public void checkCanJoin(SocketAddress address, PlayerConfigEntry configEntry, CallbackInfoReturnable<Text> info) {
		if (MultiHardcore.deadPlayers.contains(configEntry.id().toString())) {
			info.setReturnValue(Text.of("You have died :("));
		}
	}

	/*@Inject(method="onPlayerConnect", at=@At("TAIL"), cancellable = true)
	public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo info) {
		//player.getStatHandler().getStat(Stat<T>.getOrCreateStatCriterion(name))
	}*/
}
