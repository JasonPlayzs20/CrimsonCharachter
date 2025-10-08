package org.jason.tsukiaddon;

import net.fabricmc.api.ModInitializer;
import org.jason.tsukiaddon.items.ModItems;

public class Tsukiaddon implements ModInitializer {
    public static String MOD_ID = "tsukiaddon";
    @Override
    public void onInitialize() {
        ModItems.initialize();
    }
}
