package org.jason.tsukiaddon.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jason.tsukiaddon.StateSaverAndLoader;
import org.jason.tsukiaddon.network.DataSyncPackets;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class PlayerDamageForEnergyMixin {
    @Shadow @Nullable private LivingEntity attacker;

    @Shadow public abstract void stopRiding();

    @Shadow @Nullable protected PlayerEntity attackingPlayer;

    @Inject(method = "damage",at = @At(value = "RETURN"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity.getAttacker() instanceof PlayerEntity) {
            System.out.println(livingEntity);
            if (livingEntity.getServer() != null) {
                MinecraftServer server = livingEntity.getServer();

                StateSaverAndLoader state = StateSaverAndLoader.getServerState(server);
                state.addPlayerEnergy(attacker.getUuid(), (int) (amount * 0.4));
                DataSyncPackets.sendEnergyToClient((ServerPlayerEntity) attacker, state.getPlayerEnergy(attacker.getUuid()));
            }
        }

    }
}
