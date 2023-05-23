package gregtech.integration.nuclearcraft.standard;

import gregtech.api.GTValues;
import gregtech.integration.nuclearcraft.NuclearcraftVersion;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import nc.config.NCConfig;
import nc.recipe.AbstractRecipeHandler;
import nc.recipe.NCRecipes;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StandardVersion extends NuclearcraftVersion {

    @Override
    public boolean isOverhauled() {
        return false;
    }

    @Override
    public Object2BooleanMap<String> getGTRecipeConfigs() {
        Object2BooleanMap<String> map = new Object2BooleanOpenHashMap<>();
        boolean[] arr = NCConfig.gtce_recipe_integration;
        map.put("manufactory", arr[0]);
        map.put("isotope_separator", arr[1]);
        map.put("decay_hastener", arr[2]);
        map.put("fuel_reprocessor", arr[3]);
        map.put("alloy_furnace", arr[4]);
        map.put("infuser", arr[5]);
        map.put("melter", arr[6]);
        map.put("supercooler", arr[7]);
        map.put("electrolyser", arr[8]);
        map.put("irradiator", arr[9]);
        map.put("ingot_former", arr[10]);
        map.put("pressurizer", arr[11]);
        map.put("chemical_reactor", arr[12]);
        map.put("salt_mixer", arr[13]);
        map.put("crystallizer", arr[14]);
        map.put("dissolver", arr[15]);
        map.put("extractor", arr[16]);
        map.put("centrifuge", arr[17]);
        map.put("rock_crusher", arr[18]);
        return map;
    }

    @Override
    protected String getItemIngredientString() {
        return "itemIngredients";
    }

    @Override
    protected String getFluidIngredientString() {
        return "fluidIngredients";
    }

    @Override
    protected String getItemProductsString() {
        return "itemProducts";
    }

    @Override
    protected String getFluidProductsString() {
        return "fluidProducts";
    }

    @SuppressWarnings("all")
    @Override
    public List<AbstractRecipeHandler<?>> getHandlers() {
        AbstractRecipeHandler<?>[] handlers = null;
        try {
            Field field = NCRecipes.class.getDeclaredField("processor_recipe_handlers");
            handlers = (AbstractRecipeHandler<?>[]) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        if (handlers == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(handlers);
    }

    @Override
    public String getRecipeName(AbstractRecipeHandler<?> handler) {
        String name = invokeVirtualMethodNoArgs(handler.getClass(), "getRecipeName", String.class);
        if (name == null) {
            return "";
        }
        // trim some formatting off that NC applies
        return name.substring((GTValues.MODID + "_").length());
    }
}
