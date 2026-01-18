package io.antoon.mc.multihardcore.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.antoon.mc.multihardcore.MultiHardcore;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
	@Inject(method="onDeath", at=@At("TAIL"))
	public void onDeath(DamageSource damageSource, CallbackInfo info) {
		ServerPlayerEntity spe = (ServerPlayerEntity)(Object)this;
		spe.networkHandler.disconnect(Text.of("HAHAHA"));
		MultiHardcore.deadPlayers.add(spe.getUuid().toString());
	}
}
