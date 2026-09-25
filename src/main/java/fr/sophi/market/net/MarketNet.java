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

@EventBusSubscriber(modid=SophiMarket.MODID,bus=EventBusSubscriber.Bus.MOD)
public final class MarketNet {
 public record SellRequest(int slot,int amount) implements CustomPacketPayload {
  public static final Type<SellRequest> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath(SophiMarket.MODID,"sell_request"));
  public static final StreamCodec<RegistryFriendlyByteBuf,SellRequest> STREAM_CODEC=StreamCodec.of((b,m)->{b.writeVarInt(m.slot);b.writeVarInt(m.amount);},b->new SellRequest(b.readVarInt(),b.readVarInt()));
  public Type<? extends CustomPacketPayload> type(){return TYPE;}
 }
 @SubscribeEvent public static void register(RegisterPayloadHandlersEvent e){e.registrar("1").playToServer(SellRequest.TYPE,SellRequest.STREAM_CODEC,MarketNet::sell);}
 private static void sell(SellRequest m,IPayloadContext c){c.enqueueWork(()->{if(!(c.player() instanceof ServerPlayer p))return; if(m.slot<9||m.slot>=p.getInventory().getContainerSize())return; ItemStack s=p.getInventory().getItem(m.slot); if(s.isEmpty())return; int n=Math.max(1,Math.min(m.amount,s.getCount())); int price=SellPrices.price(s); if(price<=0)return; s.shrink(n); MoneyData.add(p,(long)price*n);});}
}
