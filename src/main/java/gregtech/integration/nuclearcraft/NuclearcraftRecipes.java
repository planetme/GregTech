package gregtech.integration.nuclearcraft;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.common.items.MetaItems;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import nc.config.NCConfig;
import nc.recipe.AbstractRecipeHandler;
import nc.recipe.IRecipe;
import nc.recipe.RecipeHelper;
import nc.recipe.RecipeTupleGenerator;
import nc.recipe.ingredient.*;
import nc.util.NCUtil;
import nc.util.OreDictHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class NuclearcraftRecipes {

    protected static void init() {
        Object2BooleanMap<String> configs = NuclearcraftModule.VERSION.getGTRecipeConfigs();
        System.out.println(configs);
        for (AbstractRecipeHandler<?> handler : NuclearcraftModule.VERSION.getHandlers()) {
            String recipeName = NuclearcraftModule.VERSION.getRecipeName(handler);
            System.out.println("recipeName: " + recipeName);
            if (configs.getBoolean(recipeName)) {
                for (IRecipe recipe : handler.getRecipeList()) {
                    addRecipe(recipeName, recipe);
                }
            }
        }
    }

    private static void addRecipe(String recipeName, IRecipe recipe) {
        RecipeMap<?> recipeMap = null;
        RecipeBuilder<?> builder = null;
        System.out.println(recipeName);

        // todo fix these names for NC and NCO
        switch (recipeName) {
            case "manufactory" -> {
                recipeMap = RecipeMaps.MACERATOR_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 12, 8);
            }
            case "isotope_separator" -> {
                recipeMap = RecipeMaps.THERMAL_CENTRIFUGE_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 48, 160);
            }
            case "fuel_reprocessor" -> {
                recipeMap = RecipeMaps.CENTRIFUGE_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 24, 60);
            }
            case "alloy_furnace" -> {
                recipeMap = RecipeMaps.ALLOY_SMELTER_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 16, 10);
            }
            case "infuser" -> {
                recipeMap = RecipeMaps.CHEMICAL_BATH_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 16, 12);
            }
            case "melter" -> {
                recipeMap = RecipeMaps.EXTRACTOR_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 32, 16);
            }
            case "supercooler" -> {
                recipeMap = RecipeMaps.VACUUM_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 240, 20);
            }
            case "electrolyser" -> {
                recipeMap = RecipeMaps.ELECTROLYZER_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 30, 16);
            }
            case "ingot_former" -> {
                recipeMap = RecipeMaps.FLUID_SOLIDFICATION_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 8, 1);
            }
            case "pressurizer" -> {
                if (isPlateRecipe(recipe)) {
                    recipeMap = RecipeMaps.BENDER_RECIPES;
                    builder = addStats(recipeMap.recipeBuilder(), recipe, 24, 10).circuitMeta(0);
                } else {
                    recipeMap = RecipeMaps.COMPRESSOR_RECIPES;
                    builder = addStats(recipeMap.recipeBuilder(), recipe, 2, 20);
                }
            }
            case "chemical_reactor" -> {
                recipeMap = RecipeMaps.CHEMICAL_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 30, 30);
            }
            case "salt_mixer" -> {
                recipeMap = RecipeMaps.MIXER_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 8, 12);
            }
            case "crystallizer" -> {
                recipeMap = RecipeMaps.CHEMICAL_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 30, 10).circuitMeta(0);
            }
            case "dissolver" -> {
                recipeMap = RecipeMaps.CHEMICAL_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 20, 20).circuitMeta(1);
            }
            case "extractor" -> {
                recipeMap = RecipeMaps.EXTRACTOR_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 16, 12);
            }
            case "centrifuge" -> {
                recipeMap = RecipeMaps.CENTRIFUGE_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 16, 80).circuitMeta(0);
            }
            case "rock_crusher" -> {
                recipeMap = RecipeMaps.MACERATOR_RECIPES;
                builder = addStats(recipeMap.recipeBuilder(), recipe, 20, 12);
            }
        }

        if (recipeMap == null || builder == null) {
            return;
        }

        List<List<ItemStack>> itemInputLists = new ArrayList<>();
        List<List<FluidStack>> fluidInputLists = new ArrayList<>();

        List<IItemIngredient> itemIngredients = NuclearcraftModule.VERSION.getItemIngredients(recipe);
        List<IFluidIngredient> fluidIngredients = NuclearcraftModule.VERSION.getFluidIngredients(recipe);
        List<IItemIngredient> itemProducts = NuclearcraftModule.VERSION.getItemProducts(recipe);
        List<IFluidIngredient> fluidProducts = NuclearcraftModule.VERSION.getFluidProducts(recipe);

        for (IItemIngredient item : itemIngredients) {
            itemInputLists.add(item.getInputStackList());
        }
        for (IFluidIngredient fluid : fluidIngredients) {
            fluidInputLists.add(fluid.getInputStackList());
        }

        int arrSize = itemIngredients.size() + fluidIngredients.size();
        int[] inputNumbers = new int[arrSize];
        Arrays.fill(inputNumbers, 0);

        int[] maxNumbers  = new int[arrSize];
        for (int i = 0; i < itemInputLists.size(); i++) {
            int maxNumber = itemInputLists.get(i).size() - 1;
            if (maxNumber < 0) return;
            maxNumbers[i] = maxNumber;
        }
        for (int i = 0; i < fluidInputLists.size(); i++) {
            int maxNumber = fluidInputLists.get(i).size() - 1;
            if (maxNumber < 0) return;
            maxNumbers[i + itemInputLists.size()] = maxNumber;
        }

        List<Pair<List<ItemStack>, List<FluidStack>>> materialListTuples = new ArrayList<>();

        RecipeTupleGenerator.INSTANCE.generateMaterialListTuples(materialListTuples, maxNumbers, inputNumbers, itemInputLists, fluidInputLists, true);

        for (Pair<List<ItemStack>, List<FluidStack>> materials : materialListTuples) {
            if (isRecipeInvalid(recipeMap, materials.getLeft(), materials.getRight())) {
                return;
            }
        }

        List<RecipeBuilder<?>> builders = new ArrayList<>(); // Holds all the recipe variants
        builders.add(builder);

        for (IItemIngredient input : itemIngredients) {
            if (input instanceof OreIngredient) {
                for (RecipeBuilder<?> builderVariant : builders) {
                    builderVariant.input(((OreIngredient)input).oreName, ((OreIngredient)input).stackSize);
                }
            }
            else {
                List<String> ingredientOreList = new ArrayList<>(); // Hold the different oreDict names
                List<RecipeBuilder<?>> newBuilders = new ArrayList<>();
                for (ItemStack inputVariant : input.getInputStackList()) {
                    if(inputVariant.isEmpty()) continue;
                    Set<String> variantOreList = OreDictHelper.getOreNames(inputVariant);

                    if (!variantOreList.isEmpty()) { // This variant has oreDict entries
                        if (ingredientOreList.containsAll(variantOreList)) {
                            continue;
                        }
                        ingredientOreList.addAll(variantOreList);

                        for (RecipeBuilder<?> recipeBuilder : builders) {
                            newBuilders.add(recipeBuilder.copy().input(variantOreList.iterator().next(), inputVariant.getCount()));
                        }
                    }
                    else {
                        for (RecipeBuilder<?> recipeBuilder : builders) {
                            newBuilders.add(recipeBuilder.copy().inputs(inputVariant));
                        }
                    }
                }
                builders = newBuilders;
            }
        }

        if (recipeMap == RecipeMaps.FLUID_SOLIDFICATION_RECIPES) {
            ItemStack mold = getIngotFormerMold(recipe);
            if (mold != ItemStack.EMPTY) {
                for (RecipeBuilder<?> builderVariant : builders) {
                    builderVariant.notConsumable(mold);
                }
            }
        }

        for (IFluidIngredient input : fluidIngredients) {
            if (input.getInputStackList().isEmpty()) continue;
            for (RecipeBuilder<?> builderVariant : builders) {
                builderVariant.fluidInputs(input.getInputStackList().get(0));
            }
        }

        for (IItemIngredient output : itemProducts) {
            if (output instanceof ChanceItemIngredient) return;
            List<ItemStack> outputStackList = output.getOutputStackList();
            if (outputStackList.isEmpty()) continue;
            for (RecipeBuilder<?> builderVariant : builders) {
                builderVariant.outputs(outputStackList.get(0));
            }
        }

        for (IFluidIngredient output : fluidProducts) {
            if (output instanceof ChanceFluidIngredient) return;
            List<FluidStack> outputStackList = output.getOutputStackList();
            if (outputStackList.isEmpty()) continue;
            for (RecipeBuilder<?> builderVariant : builders) {
                builderVariant.fluidOutputs(outputStackList.get(0));
            }
        }

        boolean built = false;
        for (RecipeBuilder<?> builderVariant : builders) {
            if (!builderVariant.getInputs().isEmpty() || !builderVariant.getFluidInputs().isEmpty()) {
                builderVariant.buildAndRegister();
                built = true;
            }
        }

        if (built && NCConfig.gtce_recipe_logging) {
            NCUtil.getLogger().info("Injected GTCEu " + recipeMap.unlocalizedName + " recipe: " + RecipeHelper.getRecipeString(recipe));
        }
    }

    private static RecipeBuilder<?> addStats(RecipeBuilder<?> builder, IRecipe recipe, int processPower, int processTime) {
        double power = NuclearcraftModule.VERSION.getBaseProcessPower(recipe, processPower);
        double time = NuclearcraftModule.VERSION.getBaseProcessTime(recipe, processTime);
        return builder.EUt(Math.max((int) power, 1)).duration(Math.max((int) time, 1));
    }

    private static boolean isRecipeInvalid(RecipeMap<?> recipeMap, List<ItemStack> inputs, List<FluidStack> fluidInputs) {
        if (fluidInputs.size() > recipeMap.getMaxFluidInputs()) {
            return true;
        } else if (inputs.size() > recipeMap.getMaxInputs()) {
            return true;
        }

        Recipe recipe = recipeMap.findRecipe(Long.MAX_VALUE, inputs, fluidInputs);
        // recipe found -> invalid
        return recipe != null;
    }

    private static boolean isPlateRecipe(IRecipe recipe) {
        List<IItemIngredient> products = NuclearcraftModule.VERSION.getItemProducts(recipe);
        if (products.size() == 0) return false;
        ItemStack output = products.get(0).getStack();
        return output != null && OreDictHelper.hasOrePrefix(output, "plate", "plateDense");
    }

    private static ItemStack getIngotFormerMold(IRecipe recipe) {
        List<IItemIngredient> products = NuclearcraftModule.VERSION.getItemProducts(recipe);
        if (products.size() == 0) return ItemStack.EMPTY;
        ItemStack output = products.get(0).getStack();
        if (output != null) {
            if (OreDictHelper.hasOrePrefix(output, "ingot")) return MetaItems.SHAPE_MOLD_INGOT.getStackForm();
            else if (OreDictHelper.hasOrePrefix(output, "block")) return MetaItems.SHAPE_MOLD_BLOCK.getStackForm();
        }
        return MetaItems.SHAPE_MOLD_BALL.getStackForm();
    }
}
