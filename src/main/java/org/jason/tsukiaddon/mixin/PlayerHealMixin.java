package org.jason.tsukiaddon.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jason.tsukiaddon.StateSaverAndLoader;
import org.jason.tsukiaddon.network.DataSyncPackets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;


import java.util.Objects;
import java.util.UUID;


//DrawContext
@Mixin(LivingEntity.class)
public class PlayerHealMixin {
    @Redirect(method = "heal", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"))
    private void redirectSetHealth(LivingEntity instance, float health) {
        if (instance.isPlayer()) {
            UUID uuid = instance.getUuid();
//            instance.sendMessage(Text.literal("YO"));
            StateSaverAndLoader state = StateSaverAndLoader.getServerState(Objects.requireNonNull(instance.getServer()));
            if (state.getBondOfLife(uuid) > 0) {
//                instance.sendMessage(Text.literal("YO2"));
                double currentBond = Math.max(0,state.getBondOfLife(uuid)-health);
                state.setBondOfLife(uuid, currentBond);
                DataSyncPackets.sendBondOfLifeToClient((ServerPlayerEntity) instance,currentBond);
                return;
            }
        }
//        if (instance.getName().toString() == "uzunuro") {
//            instance.setHealth(0);
//        }
        instance.setHealth(instance.getHealth() + health);


    }
}
