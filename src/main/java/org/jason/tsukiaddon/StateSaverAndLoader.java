package org.jason.tsukiaddon;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import java.util.HashMap;
import java.util.UUID;

public class StateSaverAndLoader extends PersistentState {
//    public double bondOfLife = 10;

    public HashMap<UUID,Double> playerBondOfLife = new HashMap<>();

    public HashMap<UUID,Integer> playerEnergy = new HashMap<>();

    public HashMap<UUID,Boolean> playerActivated = new HashMap<>();


    public StateSaverAndLoader() {
        super();
    }

//    private StateSaverAndLoader(double bondOfLife) {
//        this.bondOfLife = bondOfLife;
//    }
//    private double getBondOfLife() {
//        return this.bondOfLife;
//    }

//    private static final Codec<StateSaverAndLoader> CODEC =Codec.DOUBLE.fieldOf("bondOfLife").codec().xmap(
//            StateSaverAndLoader::new,
//            StateSaverAndLoader::getBondOfLife
//    );

//    private static final <StateSaverAndLoader> type = new PersistentStateManager()

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound bondOfLifeNbt = new NbtCompound();

        playerBondOfLife.forEach((uuid, bondOfLife) -> {
            bondOfLifeNbt.putDouble(uuid.toString(),bondOfLife);
        });
//        nbt.putDouble("bondOfLife", bondOfLife);
        nbt.put("bondOfLife", bondOfLifeNbt);

        NbtCompound energyNbt = new NbtCompound();
        playerEnergy.forEach((uuid,energy) -> {
            energyNbt.putInt(uuid.toString(),energy);
        });
        nbt.put("energy", energyNbt);
        
        NbtCompound activatedNbt = new NbtCompound();
        playerActivated.forEach((uuid, activated) -> {
            activatedNbt.putBoolean(uuid.toString(), activated);
        });
        nbt.put("activated", activatedNbt);


        return nbt;

    }

    public static StateSaverAndLoader fromNbt(NbtCompound nbt) {
        StateSaverAndLoader state = new StateSaverAndLoader();
        NbtCompound bondOfLifeNbt = nbt.getCompound("bondOfLife");
        bondOfLifeNbt.getKeys().forEach(key -> {
            UUID uuid = UUID.fromString(key);
            double bondOfLife = bondOfLifeNbt.getDouble(key);
            state.playerBondOfLife.put(uuid,bondOfLife);

        });

        NbtCompound energyNbt = nbt.getCompound("energy");
        energyNbt.getKeys().forEach(key -> {
            UUID uuid = UUID.fromString(key);
            int energy = energyNbt.getInt(key);
            state.playerEnergy.put(uuid,energy);
        });

        NbtCompound activatedNbt = nbt.getCompound("activated");
        activatedNbt.getKeys().forEach(key -> {
            UUID uuid = UUID.fromString(key);
            boolean activated = activatedNbt.getBoolean(key);
            state.playerActivated.put(uuid, activated);
        });
//        state.bondOfLife = nbt.getDouble("bondOfLife");
        return state;
    }

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        assert overworld != null;
        return overworld.getPersistentStateManager().getOrCreate(
                StateSaverAndLoader::fromNbt,
                StateSaverAndLoader::new,
                "song_of_the_crimson_moon_data"
        );
    }


    public boolean getActivated(UUID player) {
        return playerActivated.getOrDefault(player,false);
    }

    public void toggleActivated(UUID player) {
        playerActivated.put(player,!playerActivated.get(player));
        markDirty();
    }
    public void setActivated(UUID player, boolean value) {
        playerActivated.put(player,value);
        markDirty();
    }

    public double getBondOfLife(UUID player) {
        return playerBondOfLife.getOrDefault(player,0.0);
    }

    public void setBondOfLife(UUID player, double value) {
        playerBondOfLife.put(player,Math.min(value,20));
        markDirty();
    }

    public int getPlayerEnergy(UUID player) {
        return playerEnergy.getOrDefault(player,0);
    }

    public void setPlayerEnergy(UUID player, int value) {
        playerEnergy.put(player,value);
        markDirty();
    }

    public void addPlayerEnergy(UUID player, int value) {
        playerEnergy.put(player,Math.min(playerEnergy.getOrDefault(player,0) + value, 60));
        markDirty();
    }



}
