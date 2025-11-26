package org.jason.tsukiaddon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jason.tsukiaddon.client.PlayerDataHUD;
import org.jason.tsukiaddon.items.ModItems;
import org.jason.tsukiaddon.network.AnimationPackets;

import java.util.UUID;

import static org.jason.tsukiaddon.network.DataSyncPackets.UPDATE_PLAYER_DATA;

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

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_PLAYER_DATA, ((minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
            double bondOfLife = packetByteBuf.readDouble();

            StateSaverAndLoader stateSaverAndLoader = StateSaverAndLoader.getServerState(minecraftServer);
            stateSaverAndLoader.setBondOfLife(serverPlayerEntity.getUuid(),bondOfLife);
            PlayerDataHUD.updateBondOfLife(bondOfLife);

        }));

    }
}
