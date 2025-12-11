package org.jason.tsukiaddon.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jason.tsukiaddon.StateSaverAndLoader;
import org.jason.tsukiaddon.client.PlayerDataHUD;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class BondFireAttackMixin {

    @Shadow public float strideDistance;

    @Inject(method = "attack", at = @At("TAIL"))
    private void applyBondFireEffect(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        player.sendMessage(Text.of("Why"));
        if (player instanceof ServerPlayerEntity serverPlayer &&
                target instanceof LivingEntity victim) {

            StateSaverAndLoader state = StateSaverAndLoader.getServerState(serverPlayer.getServer());
            double bondOfLife = state.getBondOfLife(serverPlayer.getUuid());

            if (bondOfLife > 0 && victim.getWorld() instanceof ServerWorld world) {
                ItemStack weapon = serverPlayer.getMainHandStack();
                double baseAttackDamage = serverPlayer.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                float enchantmentDamage = EnchantmentHelper.getAttackDamage(weapon, victim.getGroup());




                float bondPortion = (float) (bondOfLife*2 * 0.35);
                float weaponPortion = (float) (baseAttackDamage * 0.2);
                float enchantPortion = enchantmentDamage * 0.15f;

                float totalFireDamage = bondPortion + weaponPortion + enchantPortion;

                int particleCount = Math.min(20, (int) (totalFireDamage * 4));
                for (int i = 0; i < particleCount; i++) {
                    double offsetX = (Math.random() - 0.5) * 0.8;
                    double offsetY = Math.random() * victim.getHeight();
                    double offsetZ = (Math.random() - 0.5) * 0.8;

                    world.spawnParticles(
                            ParticleTypes.FLAME,
                            victim.getX() + offsetX,
                            victim.getY() + offsetY,
                            victim.getZ() + offsetZ,
                            1, 0.2, 0.2, 0.2, 0.05
                    );
                    world.spawnParticles(
                            ParticleTypes.FALLING_LAVA,
                            victim.getX() + offsetX,
                            victim.getY() + offsetY,
                            victim.getZ() + offsetZ,
                            1, 0.2, 0.2, 0.2, 0.05
                    );
                }

                victim.damage(serverPlayer.getDamageSources().onFire(), totalFireDamage);

                int fireSeconds = Math.min(5, Math.max(1, (int) (totalFireDamage / 2)));
                victim.setOnFireFor(fireSeconds);
                state.setBondOfLife(player.getUuid(),state.getBondOfLife(player.getUuid())+3);
                PlayerDataHUD.updateBondOfLife(state.getBondOfLife(player.getUuid()));
            }
        }
    }
}
