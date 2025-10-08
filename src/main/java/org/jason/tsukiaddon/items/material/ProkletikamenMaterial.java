package org.jason.tsukiaddon.items.material;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class ProkletikamenMaterial implements ToolMaterial {
    public static final ProkletikamenMaterial INSTANCE = new ProkletikamenMaterial();
    @Override
    public int getDurability() {
        return 0;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 0;
    }

    @Override
    public float getAttackDamage() {
        return 0;
    }

    @Override
    public int getMiningLevel() {
        return 0;
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return null;
    }
}
