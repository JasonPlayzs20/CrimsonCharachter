package org.jason.tsukiaddon.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class PlayerDataHUD {
    private static double bondOfLife = 10.0;
    private static int energy = 0;

    public static void register() {
//        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
//        });

        HudRenderCallback.EVENT.register(((drawContext, v) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.options.hudHidden) return;

            String bondOfLifeText = "Bond Of Life: " + bondOfLife;
            drawContext.drawText(
                    client.textRenderer,
                    bondOfLifeText,
                    10,
                    10,
                    0xFF5555,
                    true
            );

            String energyText = "Energy: " + energy;
            drawContext.drawText(
                    client.textRenderer,
                    energyText,
                    100,
                    10,
                    0xFF4444,
                    true
            );

        }));

    }

    public static void updateEnergy(int energyValue) {
        energy = energyValue;
    }

    public static int getEnergy() {
        return energy;
    }

    public static void updateBondOfLife(double bondOfLifen) {
        bondOfLife = bondOfLifen;
        System.out.println(bondOfLifen);
    }

    public static double getBondOfLife() {
        return bondOfLife;
    }

}
