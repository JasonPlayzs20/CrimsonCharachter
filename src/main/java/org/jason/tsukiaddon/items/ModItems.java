package org.jason.tsukiaddon.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jason.tsukiaddon.Tsukiaddon;
import org.jason.tsukiaddon.items.material.*;

public class ModItems {

    public static Item register(Item item, String id) {
        Identifier itemID = new Identifier(Tsukiaddon.MOD_ID, id);
        Item registereditem = Registry.register(Registries.ITEM, itemID, item);
        return registereditem;
    }
    public static final RegistryKey<ItemGroup> tsukiGroupKey = RegistryKey.of(Registries.ITEM_GROUP.getKey(), new Identifier(Tsukiaddon.MOD_ID,"tsuki_group"));
    public static final ItemGroup TSUKI_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.prokletikamen)).displayName(Text.translatable("itemGroup.tsukiaddon")).build();



    public static final Item adamysticus = register(new SwordItem(AdamysticusMaterial.INSTANCE,5,-2.2F,new FabricItemSettings()),"adamysticus_sword");
    public static final Item drevenavatra = register(new SwordItem(DrevenavatraMaterial.INSTANCE,6,-2.2F,new FabricItemSettings()),"drevenavatra_sword");
    public static final Item infernuferreus = register(new SwordItem(InfernuferreusMaterial.INSTANCE,7,-2.2F,new FabricItemSettings()),"inferuferreus_sword");
    public static final Item moartelafoc = register(new SwordItem(MoartelafocMaterial.INSTANCE,8,-2.2F,new FabricItemSettings()), "moartelafoc_sword");
    public static final Item prokletikamen = register(new SwordItem(ProkletikamenMaterial.INSTANCE,9,-2.2F,new FabricItemSettings()),"prokletikamen_sword");

    public static final Item emberheartFragment = register(new Item(new FabricItemSettings()),"emberheart_fragment");


    public static void initialize() {
        Registry.register(Registries.ITEM_GROUP, tsukiGroupKey, TSUKI_GROUP);
        ItemGroupEvents.modifyEntriesEvent(tsukiGroupKey).register(itemgroup -> {
            itemgroup.add(adamysticus);
            itemgroup.add(drevenavatra);
            itemgroup.add(infernuferreus);
            itemgroup.add(moartelafoc);
            itemgroup.add(prokletikamen);
        });
    }
}
