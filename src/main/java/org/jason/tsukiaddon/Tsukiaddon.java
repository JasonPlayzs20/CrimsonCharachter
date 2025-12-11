package org.jason.tsukiaddon;

import com.google.common.collect.Multimap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jason.tsukiaddon.client.PlayerDataHUD;
import org.jason.tsukiaddon.items.ModItems;
import org.jason.tsukiaddon.network.AnimationPackets;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.jason.tsukiaddon.network.DataSyncPackets.*;

public class Tsukiaddon implements ModInitializer {
    public static String MOD_ID = "tsukiaddon";

    @Override
    public void onInitialize() {
        ModItems.initialize();

        ServerPlayNetworking.registerGlobalReceiver(AnimationPackets.PLAY_ANIMATION, (server, player, handler, buf, responseSender) -> {
            UUID targetUUID = buf.readUuid();
            int comboCount = buf.readInt();

            server.execute(() -> {
                PacketByteBuf outBuf = PacketByteBufs.create();
                outBuf.writeUuid(targetUUID);
                outBuf.writeInt(comboCount);

                for (ServerPlayerEntity tracking : player.getServerWorld().getPlayers()) {
                    if (tracking.squaredDistanceTo(player) < 64 * 64) {
                        ServerPlayNetworking.send(tracking, AnimationPackets.PLAY_ANIMATION, outBuf);
                    }
                }
            });
        });
        registerNetworks();

    }

    public static void registerNetworks() {
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_PLAYER_BOL_DATA, ((minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            double bondOfLife = packetByteBuf.readDouble();

            StateSaverAndLoader stateSaverAndLoader = StateSaverAndLoader.getServerState(minecraftServer);
            stateSaverAndLoader.setBondOfLife(serverPlayerEntity.getUuid(), bondOfLife);
            PlayerDataHUD.updateBondOfLife(bondOfLife);

        }));

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_PLAYER_ENERGY_DATA, ((minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            int energy = packetByteBuf.readInt();
            StateSaverAndLoader stateSaverAndLoader = StateSaverAndLoader.getServerState(minecraftServer);
            stateSaverAndLoader.setPlayerEnergy(serverPlayerEntity.getUuid(), energy);
            PlayerDataHUD.updateEnergy(energy);
        }));

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_PLAYER_ACTIVATED_DATA, ((minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            boolean activated = packetByteBuf.readBoolean();
            StateSaverAndLoader stateSaverAndLoader = StateSaverAndLoader.getServerState(minecraftServer);
            stateSaverAndLoader.setActivated(serverPlayerEntity.getUuid(), activated);
            PlayerDataHUD.setActivated(activated);
            minecraftServer.execute(() -> {
                burst(serverPlayerEntity, activated);
                if (!activated) {
                    //is not activated now
                    serverPlayerEntity.heal((float) (stateSaverAndLoader.getBondOfLife(serverPlayerEntity.getUuid()) * 1.7));
                } else {
//                    serverPlayerEntity.getAttributes().addTemporaryModifiers();
//                    serverPlayerEntity.Attr
                    stateSaverAndLoader.setBondOfLife(serverPlayerEntity.getUuid(), 3);
                    PlayerDataHUD.updateBondOfLife(stateSaverAndLoader.getBondOfLife(serverPlayerEntity.getUuid()));
                    serverPlayerEntity.getHungerManager().setFoodLevel(19);
                    serverPlayerEntity.getHungerManager().setSaturationLevel(19);
                }
            });
        }));

    }

    public static void burst(ServerPlayerEntity crimson, boolean activated) {
        burstPhase1(crimson);
        if (activated) {
            //do big burst
//            crimson.sendMessage(Text.of("Burst activated!"));
        }
    }

    public static void burstPhase1(ServerPlayerEntity crimson) {
        MinecraftServer server = crimson.getServer();
        ArrayList<LivingEntity> entities = new ArrayList<>();
        crimson.getWorld().getOtherEntities(crimson, new Box(crimson.getX() - 10, crimson.getY() - 10, crimson.getZ() - 10, crimson.getX() + 10, crimson.getY() + 10, crimson.getZ() + 10)).forEach(entity -> {
            if (!(entity instanceof LivingEntity)) {
                return;
            }
            LivingEntity livingEntity = (LivingEntity) entity;
            if (entities.size() == 8) {
                return;
            }
            if (livingEntity instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity) entity;
                if (playerEntity.getInventory().contains(new ItemStack(ModItems.nightsoulblessing, 1))) {
                    //do 2
                    return;
                } else if (playerEntity.getInventory().contains(new ItemStack(ModItems.soulsblessing, 1))) {
                    //do 1
                }
            }
            entities.add(livingEntity);
        });
        Vec3d playerPos = crimson.getPos();
        entities.forEach(entity -> {
            entity.setGlowing(true);
        });
        AtomicInteger index = new AtomicInteger(0);
        server.execute(() -> {
           scheduleRepeatingTask(server,() -> {
//               crimson.sendMessage(Text.of("Burst "));
               if (index.get() >= entities.size()) {
                   return true;
               }
               LivingEntity livingEntity = (LivingEntity) entities.get(index.getAndIncrement());
               Random random = new Random();
               double targetX = livingEntity.getX() + random.nextDouble(-3, 3);
               double targetY = livingEntity.getY() + random.nextDouble(0, 3);
               double targetZ = livingEntity.getZ() + random.nextDouble(-3, 3);

               double deltaX = livingEntity.getX() - targetX;
               double deltaY = livingEntity.getEyeY() - targetY;
               double deltaZ = livingEntity.getZ() - targetZ;

               float yaw = (float) (Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
               double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
               float pitch = (float) (Math.atan2(deltaY, horizontalDistance) * (180 / Math.PI)) * -1;

               crimson.teleport((ServerWorld) livingEntity.getWorld(), targetX, targetY, targetZ, yaw, pitch);

               double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
               double speed = 1.5;

               double velocityX = (deltaX / distance) * speed;
               double velocityY = (deltaY / distance) * speed;
               double velocityZ = (deltaZ / distance) * speed;

               crimson.setVelocity(velocityX, velocityY, velocityZ);
               crimson.velocityModified = true;
               livingEntity.setGlowing(false);
               StateSaverAndLoader stateSaverAndLoader = new StateSaverAndLoader();

               RegistryKey<DamageType> fireKey = DamageTypes.LAVA;

               DamageSource fireSource = new DamageSource(
                       crimson.getWorld().getRegistryManager()
                               .get(RegistryKeys.DAMAGE_TYPE)
                               .entryOf(fireKey),
                       crimson,
                       crimson   // Causing entity (who to credit for the kill)
               );

               livingEntity.damage(fireSource, (float)(Math.max(stateSaverAndLoader.getPlayerEnergy(crimson.getUuid())/60*180,15)+stateSaverAndLoader.getBondOfLife(crimson.getUuid())*2.5));
               System.out.println(livingEntity);
               return false;

           },5);
        });

//                forEach(player -> {
//            BlockPos position1 = player.getBlockPos();
//            BlockPos position2 = crimson.getBlockPos();
//            double distance = Math.sqrt(Math.sqrt((Math.abs(position1.getX() - position2.getX()) + Math.abs(position1.getZ() - position2.getZ()))) + Math.abs(position1.getY() - position2.getY()) + Math.abs(position1.getZ() - position2.getZ()));
//            if (distance < 15) {
//
//            }
//        });
    }


    public static void scheduleRepeatingTask(MinecraftServer server, Supplier<Boolean> task, int delayTicks) {
        server.execute(new Runnable() {
            private int ticksLeft = delayTicks;

            @Override
            public void run() {
                ticksLeft--;

                if (ticksLeft <= 0) {
                    boolean shouldStop = task.get();

                    if (!shouldStop) {
                        ticksLeft = delayTicks; // Reset timer
                        server.execute(this); // Schedule next iteration
                    }
                } else {
                    server.execute(this); // Continue counting down
                }
            }
        });
    }

}
