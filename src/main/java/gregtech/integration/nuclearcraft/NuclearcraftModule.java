package gregtech.integration.nuclearcraft;

import gregtech.api.GTValues;
import gregtech.api.modules.GregTechModule;
import gregtech.integration.IntegrationModule;
import gregtech.integration.IntegrationSubmodule;
import gregtech.integration.nuclearcraft.overhauled.OverhauledVersion;
import gregtech.integration.nuclearcraft.standard.StandardVersion;
import gregtech.modules.GregTechModules;
import nc.Global;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

@GregTechModule(
        moduleID = GregTechModules.MODULE_NC,
        containerID = GTValues.MODID,
        modDependencies = GTValues.MODID_NC,
        name = "GregTech Nuclearcraft Integration",
        descriptionKey = "gregtech.modules.nc_integration.description"
)
public class NuclearcraftModule extends IntegrationSubmodule {

    /** Initialized in FMLPreInitializationEvent. */
    public static NuclearcraftVersion VERSION;

    //@SuppressWarnings("all") // complains about "is always false"
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (Global.VERSION.contains("2o")) {
            VERSION = new OverhauledVersion();
        } else {
            VERSION = new StandardVersion();
        }
    }

    @Nonnull
    @Override
    public List<Class<?>> getEventBusSubscribers() {
        return Collections.singletonList(NuclearcraftModule.class);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRecipeEvent(RegistryEvent.Register<IRecipe> event) {
        IntegrationModule.logger.info("Patching Nuclearcraft GTCEu integration recipes...");
        NuclearcraftRecipes.init();
    }
}
