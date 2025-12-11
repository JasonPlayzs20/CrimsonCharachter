package org.jason.tsukiaddon.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jason.tsukiaddon.StateSaverAndLoader;
import org.jason.tsukiaddon.items.ModItems;
import org.jason.tsukiaddon.network.AnimationPackets;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerAttackMixin {

    @Shadow @Final public PlayerScreenHandler playerScreenHandler;

    @Inject(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
    private void onAttack(Entity target, CallbackInfo ci) {
//        System.out.println("mixed");


        PlayerEntity player = (PlayerEntity) (Object) this;
//        System.out.println("Attack mixin triggered! Target = " + target);

        if (player.getMainHandStack().getItem() == ModItems.adamysticus) {
            if (!player.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
                AnimationPackets.sendToTracking(serverPlayer, player.getUuid(), "test");
            }
        }
        if (target != null) {
            StateSaverAndLoader state = new StateSaverAndLoader();

            state.addPlayerEnergy(player.getUuid(),3);
        }
    }


    @ModifyVariable(
            method = "attack",
            at = @At(
                    value = "LOAD",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            ),
            ordinal = 0
    )
    private float captureDamageF(float f) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (player instanceof ServerPlayerEntity serverPlayer) {
            StateSaverAndLoader state = StateSaverAndLoader.getServerState(serverPlayer.getServer());

            // Only reduce damage if NOT activated
            if (!state.getActivated(serverPlayer.getUuid())) {
//                System.out.println(f*0.7f);
                return f * 0.7f;
            }
        }

        return f;  // Normal damage when activated
    }
}
//    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
//    public void onWeakAttack(Entity target, CallbackInfo ci) {
//        PlayerEntity player = (PlayerEntity) (Object) this;
//        StateSaverAndLoader state = new StateSaverAndLoader();
//        if (!state.getActivated(player.getUuid())) {
//
//        }
//    }

//    @Inject()


