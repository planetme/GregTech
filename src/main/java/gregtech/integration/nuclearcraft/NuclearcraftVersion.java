package gregtech.integration.nuclearcraft;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import nc.recipe.AbstractRecipeHandler;
import nc.recipe.IRecipe;
import nc.recipe.ingredient.IFluidIngredient;
import nc.recipe.ingredient.IItemIngredient;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.List;

public abstract class NuclearcraftVersion {

    private final Object2ObjectMap<String, MethodHandle> handleMapMethods = new Object2ObjectOpenHashMap<>();

    public abstract boolean isOverhauled();

    public abstract Object2BooleanMap<String> getGTRecipeConfigs();

    protected abstract String getItemIngredientString();

    protected abstract String getFluidIngredientString();

    protected abstract String getItemProductsString();

    protected abstract String getFluidProductsString();

    public abstract List<AbstractRecipeHandler<?>> getHandlers();

    public double getBaseProcessPower(IRecipe recipe, double defaultPower) {
        Double power = invokeVirtualMethod(recipe.getClass(), "getBaseProcessPower", MethodType.methodType(double.class, double.class), defaultPower);
        return power != null ? power : 0.0D;
    }

    public double getBaseProcessTime(IRecipe recipe, double defaultTime) {
        Double time = invokeVirtualMethod(recipe.getClass(), "getBaseProcessTime", MethodType.methodType(double.class, double.class), defaultTime);
        return time != null ? time : 0.0D;
    }

    /** Assumes that any additional formatting, such as a modname, is removed. */
    public abstract String getRecipeName(AbstractRecipeHandler<?> handler);

    protected <T> T invokeVirtualMethodNoArgs(Class<?> clazz, String methodName, Class<?> returnType) {
        return invokeVirtualMethod(clazz, methodName, MethodType.methodType(returnType));
    }

    @SuppressWarnings("unchecked")
    protected <T> T invokeVirtualMethod(Class<?> clazz, String methodName, MethodType methodType, Object... args) {
        if (!handleMapMethods.containsKey(methodName)) {
            try {
                MethodHandle handle = MethodHandles.publicLookup().findVirtual(clazz, methodName, methodType);
                handleMapMethods.put(methodName, handle);
            } catch (NoSuchMethodException | IllegalAccessException e) {
                handleMapMethods.put(methodName, null);
            }
        }
        MethodHandle handle = handleMapMethods.get(methodName);
        if (handle != null) {
            try {
                if (args == null || args.length == 0) {
                    return (T) handle.invokeExact();
                } else {
                    return (T) handle.invokeWithArguments(args);
                }
            } catch (Throwable ignored) {}
        }
        return null;
    }

    public List<IItemIngredient> getItemIngredients(IRecipe recipe) {
        List<IItemIngredient> itemIngredients = invokeVirtualMethodNoArgs(
                recipe.getClass(), getItemIngredientString(), List.class);
        return itemIngredients != null ? itemIngredients : Collections.emptyList();
    }

    public List<IFluidIngredient> getFluidIngredients(IRecipe recipe) {
        List<IFluidIngredient> fluidIngredients = invokeVirtualMethodNoArgs(
                recipe.getClass(), getFluidIngredientString(), List.class);
        return fluidIngredients != null ? fluidIngredients : Collections.emptyList();
    }

    public List<IItemIngredient> getItemProducts(IRecipe recipe) {
        List<IItemIngredient> itemProducts = invokeVirtualMethodNoArgs(
                recipe.getClass(), getItemProductsString(), List.class);
        return itemProducts != null ? itemProducts : Collections.emptyList();
    }

    public List<IFluidIngredient> getFluidProducts(IRecipe recipe) {
        List<IFluidIngredient> fluidProducts = invokeVirtualMethodNoArgs(
                recipe.getClass(), getFluidProductsString(), List.class);
        return fluidProducts != null ? fluidProducts : Collections.emptyList();
    }
}
