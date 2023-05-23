package gregtech.integration.nuclearcraft.overhauled;

import gregtech.integration.nuclearcraft.NuclearcraftVersion;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import nc.config.NCConfig;
import nc.recipe.AbstractRecipeHandler;
import nc.recipe.NCRecipes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OverhauledVersion extends NuclearcraftVersion {

    @Override
    public boolean isOverhauled() {
        return true;
    }

    @Override
    public Object2BooleanMap<String> getGTRecipeConfigs() {
        Object2BooleanMap<String> map = new Object2BooleanOpenHashMap<>();
        boolean[] arr = NCConfig.gtce_recipe_integration;
        map.put("manufactory", arr[0]);
        map.put("separator", arr[1]);
        map.put("decay_hastener", arr[2]);
        map.put("fuel_reprocessor", arr[3]);
        map.put("alloy_furnace", arr[4]);
        map.put("infuser", arr[5]);
        map.put("melter", arr[6]);
        map.put("supercooler", arr[7]);
        map.put("electrolyzer", arr[8]);
        map.put("assembler", arr[9]);
        map.put("ingot_former", arr[10]);
        map.put("pressurizer", arr[11]);
        map.put("chemical_reactor", arr[12]);
        map.put("salt_mixer", arr[13]);
        map.put("crystallizer", arr[14]);
        map.put("enricher", arr[15]);
        map.put("extractor", arr[16]);
        map.put("centrifuge", arr[17]);
        map.put("rock_crusher", arr[18]);
        return map;
    }

    @Override
    protected String getItemIngredientString() {
        return "getItemIngredients";
    }

    @Override
    protected String getFluidIngredientString() {
        return "getFluidIngredients";
    }

    @Override
    protected String getItemProductsString() {
        return "getItemProducts";
    }

    @Override
    protected String getFluidProductsString() {
        return "getFluidProducts";
    }

    @SuppressWarnings("all")
    @Override
    public List<AbstractRecipeHandler<?>> getHandlers() {
        Object2ObjectMap<String, AbstractRecipeHandler<?>> handlers = null;
        try {
            Field field = NCRecipes.class.getDeclaredField("RECIPE_HANDLER_MAP");
            handlers = (Object2ObjectMap<String, AbstractRecipeHandler<?>>) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        if (handlers == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(handlers.values());
    }

    @Override
    public String getRecipeName(AbstractRecipeHandler<?> handler) {
        String name = invokeVirtualMethodNoArgs(handler.getClass(), "getName", String.class);
        return name != null ? name : "";
    }
}
