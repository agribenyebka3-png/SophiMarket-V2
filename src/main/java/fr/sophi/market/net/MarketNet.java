package fr.sophi.market.net;

import fr.sophi.market.SophiMarket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(modid = SophiMarket.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class MarketNet {
    private MarketNet() {}

    public record SellRequest(int slot, int amount) implements CustomPacketPayload {
        public static final Type<SellRequest> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SophiMarket.MOD_ID, "sell_request"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SellRequest> STREAM_CODEC = StreamCodec.of(
                (buf, msg) -> {
                    buf.writeVarInt(msg.slot());
                    buf.writeVarInt(msg.amount());
                },
                buf -> new SellRequest(buf.readVarInt(), buf.readVarInt())
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(SellRequest.TYPE, SellRequest.STREAM_CODEC, MarketNet::sell);
    }

    private static void sell(SellRequest request, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        int slot = request.slot();
        if (slot < 9 || slot >= player.getInventory().getContainerSize()) return;

        ItemStack stack = player.getInventory().getItem(slot);
        if (stack.isEmpty()) return;

        int unitPrice = SellPrices.price(stack);
        if (unitPrice <= 0) return;

        int amount = Math.max(1, Math.min(request.amount(), stack.getCount()));
        stack.shrink(amount);
        MoneyData.add(player, (long) unitPrice * amount);
    }
}
