package org.jason.tsukiaddon.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class PlayerDataHUD {
    private static double bondOfLife = 0.0;
    private static int energy = 0;
    private static boolean activated = false;


    public static void register() {
//        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
//        });

        HudRenderCallback.EVENT.register(((drawContext, v) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.options.hudHidden) return;

            String bondOfLifeText = "Bond Of Life: " + Math.round(bondOfLife*100.0)/100.0;
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
                    130,
                    10,
                    0xFF4444,
                    true
            );

            String activatedText = "Activated: " + activated;
            drawContext.drawText(
                    client.textRenderer,
                    activatedText,
                    200,
                    10,
                    0xFF4444,
                    true
            );

        }));

    }

    public static boolean getActivated() {
        return activated;
    }

    public static void flipActivated() {
        activated = !activated;
    }
    public static void setActivated(boolean activateds) {
        activated = activateds;
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
