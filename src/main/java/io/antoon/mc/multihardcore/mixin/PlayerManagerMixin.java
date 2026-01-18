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
		String uuid = configEntry.id().toString();

		if (MultiHardcore.deadManager.hasUuidDied(uuid)) {
			info.setReturnValue(Text.empty()
					.append(Text.literal("You have died :(").formatted())
					.append("You will not be able to play until the next season"));
		}
	}
}
