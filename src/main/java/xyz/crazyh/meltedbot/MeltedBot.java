package xyz.crazyh.meltedbot;

import com.mojang.logging.LogUtils;
import net.minecraft.SharedConstants;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import xyz.crazyh.meltedbot.command.PlayerCommand;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("meltedbot")
public class MeltedBot {

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public MeltedBot() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        // debug
        //SharedConstants.IS_RUNNING_IN_IDE = true;
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        // LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        /*@SubscribeEvent
        public void onRegisterCommands(RegisterCommandsEvent event) {
            LOGGER.info(String.valueOf(event.getResult()));
        }*/
    }

    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        PlayerCommand.register(event.getServer().getCommands().getDispatcher());
    }
}
