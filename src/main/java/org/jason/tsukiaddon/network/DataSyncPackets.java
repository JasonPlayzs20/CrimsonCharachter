package org.jason.tsukiaddon.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jason.tsukiaddon.Tsukiaddon;

public class DataSyncPackets {
    public static final Identifier SYNC_PLAYER_DATA = new Identifier(Tsukiaddon.MOD_ID,"sync_player_data");

    public static final Identifier UPDATE_PLAYER_DATA = new Identifier(Tsukiaddon.MOD_ID,"update_player_data");

    public static void sendToClient(ServerPlayerEntity player, double bondOfLife) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(bondOfLife);
        ServerPlayNetworking.send(player, SYNC_PLAYER_DATA, buf);

    }

//    public static void sendToServer(ClientPlayerEntity player, double bondOfLife) {
//        PacketByteBuf buf = PacketByteBufs.create();
//        buf.writeDouble(bondOfLife);
//        ClientPlayNetworking.send(SYNC_PLAYER_DATA,buf);
//    }
}
