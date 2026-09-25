package fr.sophi.market.client;

import com.mojang.blaze3d.platform.InputConstants;
import fr.sophi.market.SophiMarket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid=SophiMarket.MOD_ID, value=Dist.CLIENT)
public final class ClientEvents {
    private static final KeyMapping OPEN = new KeyMapping("key.sophimarket.open", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_O, "SophiMarket");

    @SubscribeEvent public static void registerKeys(RegisterKeyMappingsEvent e){ e.register(OPEN); }

    @SubscribeEvent public static void tick(ClientTickEvent.Post e){
        while(OPEN.consumeClick()) Minecraft.getInstance().setScreen(new MarketScreen());
    }
}
