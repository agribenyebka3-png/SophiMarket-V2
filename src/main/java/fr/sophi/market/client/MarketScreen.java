package fr.sophi.market.client;

import fr.sophi.market.jobs.JobType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MarketScreen extends Screen {
    private enum Tab { BUY, SELL, JOBS, ENCHANT }
    private record ShopEntry(ItemStack icon,String name,String id,long price) {}
    private record SellEntry(ItemStack icon,String name,int slot) {}

    private Tab tab=Tab.BUY;
    private JobType selectedJob=JobType.MINER;
    private int jobPage=0,shopPage=0,quantity=1,enchantLevel=1;
    private String enchantName="efficiency", search="";
    private EditBox searchBox;

    private static final ShopEntry[] SHOP={
        new ShopEntry(new ItemStack(Items.IRON_INGOT),"Fer","minecraft:iron_ingot",900),
        new ShopEntry(new ItemStack(Items.GOLD_INGOT),"Or","minecraft:gold_ingot",1500),
        new ShopEntry(new ItemStack(Items.DIAMOND),"Diamant","minecraft:diamond",1200),
        new ShopEntry(new ItemStack(Items.EMERALD),"Émeraude","minecraft:emerald",850),
        new ShopEntry(new ItemStack(Items.NETHERITE_INGOT),"Netherite","minecraft:netherite_ingot",12000),
        new ShopEntry(new ItemStack(Items.COOKED_BEEF),"Steak","minecraft:cooked_beef",25),
        new ShopEntry(new ItemStack(Items.OAK_LOG),"Bois","minecraft:oak_log",5),
        new ShopEntry(new ItemStack(Items.STONE),"Pierre","minecraft:stone",3)
    };

    public MarketScreen(){super(Component.literal("SophiMarket V2"));}
    @Override protected void init(){rebuildButtons();}

    /* Commande volontairement unique : /sophimarketmod évite le conflit avec l'ancien plugin /market. */
    private void command(String args){
        Minecraft mc=Minecraft.getInstance();
        if(mc.player!=null&&mc.player.connection!=null) mc.player.connection.sendCommand("sophimarketmod "+args);
    }

    private void rebuildButtons(){
        clearWidgets(); int cx=width/2,y=45;
        addRenderableWidget(Button.builder(Component.literal("ACHETER"),b->switchTab(Tab.BUY)).bounds(cx-205,y,95,24).build());
        addRenderableWidget(Button.builder(Component.literal("VENDRE"),b->switchTab(Tab.SELL)).bounds(cx-105,y,95,24).build());
        addRenderableWidget(Button.builder(Component.literal("MÉTIERS"),b->switchTab(Tab.JOBS)).bounds(cx-5,y,95,24).build());
        addRenderableWidget(Button.builder(Component.literal("ENCHANT. / RÉPAR."),b->switchTab(Tab.ENCHANT)).bounds(cx+95,y,120,24).build());
        addRenderableWidget(Button.builder(Component.literal("💰 VOIR MON ARGENT"),b->command("money")).bounds(cx-205,75,145,20).build());
        if(tab==Tab.BUY||tab==Tab.SELL){
            searchBox=new EditBox(font,cx-45,75,230,20,Component.literal("Rechercher"));
            searchBox.setHint(Component.literal("Rechercher un objet...")); searchBox.setValue(search);
            searchBox.setResponder(v->{search=v;shopPage=0;}); addRenderableWidget(searchBox);
        }
        if(tab==Tab.BUY)addBuyButtons(cx); if(tab==Tab.SELL)addSellButtons(cx); if(tab==Tab.JOBS)addJobButtons(cx); if(tab==Tab.ENCHANT)addEnchantButtons(cx);
    }
    private void switchTab(Tab n){tab=n;shopPage=0;rebuildButtons();}
    private boolean match(String s){return search.isBlank()||s.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT));}
    private List<ShopEntry> buyEntries(){List<ShopEntry> r=new ArrayList<>();for(ShopEntry e:SHOP)if(match(e.name())||match(e.id()))r.add(e);return r;}

    private void addBuyButtons(int cx){
        List<ShopEntry> rows=buyEntries();int start=shopPage*6,y=145;
        for(int i=start;i<Math.min(rows.size(),start+6);i++){ShopEntry e=rows.get(i);addRenderableWidget(Button.builder(Component.literal("ACHETER"),b->command("buy "+e.id()+" "+quantity)).bounds(cx+105,y-3,78,20).build());y+=30;}
        addRenderableWidget(Button.builder(Component.literal("-"),b->quantity=Math.max(1,quantity-1)).bounds(cx-70,335,28,20).build());
        addRenderableWidget(Button.builder(Component.literal("+"),b->quantity=Math.min(2304,quantity+1)).bounds(cx+42,335,28,20).build());
        addRenderableWidget(Button.builder(Component.literal("x16"),b->quantity=16).bounds(cx+80,335,42,20).build());
        addRenderableWidget(Button.builder(Component.literal("x64"),b->quantity=64).bounds(cx+127,335,42,20).build());
        addRenderableWidget(Button.builder(Component.literal("<"),b->{if(shopPage>0){shopPage--;rebuildButtons();}}).bounds(cx-190,365,35,20).build());
        addRenderableWidget(Button.builder(Component.literal(">"),b->{if((shopPage+1)*6<rows.size()){shopPage++;rebuildButtons();}}).bounds(cx+150,365,35,20).build());
    }

    private List<SellEntry> sellEntries(){
        List<SellEntry> r=new ArrayList<>();Minecraft mc=Minecraft.getInstance();
        if(mc.player!=null){var inv=mc.player.getInventory();for(int slot=9;slot<inv.items.size();slot++){ItemStack s=inv.items.get(slot);if(!s.isEmpty()&&match(s.getHoverName().getString()))r.add(new SellEntry(s.copy(),s.getHoverName().getString()+" x"+s.getCount(),slot));}}
        return r;
    }
    private void addSellButtons(int cx){
        List<SellEntry> rows=sellEntries();int start=shopPage*6,y=145;
        for(int i=start;i<Math.min(rows.size(),start+6);i++){SellEntry e=rows.get(i);addRenderableWidget(Button.builder(Component.literal("VENDRE"),b->command("sellslot "+e.slot())).bounds(cx+105,y-3,78,20).build());y+=30;}
        addRenderableWidget(Button.builder(Component.literal("TOUT VENDRE (hors hotbar)"),b->command("sellall")).bounds(cx-95,335,190,20).build());
        addRenderableWidget(Button.builder(Component.literal("<"),b->{if(shopPage>0){shopPage--;rebuildButtons();}}).bounds(cx-190,365,35,20).build());
        addRenderableWidget(Button.builder(Component.literal(">"),b->{if((shopPage+1)*6<rows.size()){shopPage++;rebuildButtons();}}).bounds(cx+150,365,35,20).build());
    }

    private void addJobButtons(int cx){int x=cx-190;for(JobType job:JobType.values()){JobType j=job;addRenderableWidget(Button.builder(Component.literal(job.icon()+" "+job.displayName()),b->{selectedJob=j;jobPage=0;}).bounds(x,105,90,24).build());x+=95;}addRenderableWidget(Button.builder(Component.literal("Quêtes"),b->jobPage=0).bounds(cx-190,365,90,20).build());addRenderableWidget(Button.builder(Component.literal("Récompenses"),b->jobPage=1).bounds(cx+95,365,90,20).build());}
    private void addEnchantButtons(int cx){addRenderableWidget(Button.builder(Component.literal("RÉPARER"),b->command("repair")).bounds(cx-185,320,110,22).build());String[] ns={"efficiency","fortune","unbreaking","mending","sharpness","protection"};int x=cx-185;for(String n:ns){String c=n;addRenderableWidget(Button.builder(Component.literal(shortName(n)),b->enchantName=c).bounds(x,265,58,20).build());x+=61;}addRenderableWidget(Button.builder(Component.literal("Niv -"),b->enchantLevel=Math.max(1,enchantLevel-1)).bounds(cx-60,320,55,22).build());addRenderableWidget(Button.builder(Component.literal("Niv +"),b->enchantLevel=Math.min(5,enchantLevel+1)).bounds(cx+5,320,55,22).build());addRenderableWidget(Button.builder(Component.literal("ENCHANTER"),b->command("enchant "+enchantName+" "+enchantLevel)).bounds(cx+75,320,110,22).build());}
    private String shortName(String n){return switch(n){case"efficiency"->"Eff.";case"fortune"->"Fort.";case"unbreaking"->"Solid.";case"mending"->"Mend.";case"sharpness"->"Tranch.";case"protection"->"Prot.";default->n;};}

    @Override public void render(GuiGraphics g,int mx,int my,float pt){renderBackground(g,mx,my,pt);int cx=width/2;g.fill(cx-225,25,cx+225,height-25,0xE820242B);g.drawCenteredString(font,"SOPHIMARKET V2",cx,30,0x55FF55);super.render(g,mx,my,pt);switch(tab){case BUY->renderBuy(g,cx);case SELL->renderSell(g,cx);case JOBS->renderJobs(g,cx);case ENCHANT->renderEnchant(g,cx);}}
    private void renderBuy(GuiGraphics g,int cx){g.drawCenteredString(font,"BOUTIQUE - ACHETER",cx,112,0xFFFFFF);List<ShopEntry> rows=buyEntries();int start=shopPage*6,y=145;for(int i=start;i<Math.min(rows.size(),start+6);i++){ShopEntry e=rows.get(i);g.fill(cx-190,y-4,cx+95,y+20,0x80363B44);g.renderItem(e.icon(),cx-184,y);g.drawString(font,e.name()+" — "+e.price()+"$ / unité",cx-160,y+4,0xFFFFFF);y+=30;}if(rows.isEmpty())g.drawCenteredString(font,"Aucun résultat",cx,175,0xBBBBBB);g.drawCenteredString(font,"Quantité : "+quantity,cx,338,0xFFFF55);g.drawString(font,"Endium : non disponible à l'achat",cx-190,392,0xFF7777);}
    private void renderSell(GuiGraphics g,int cx){g.drawCenteredString(font,"VENDRE - TON INVENTAIRE",cx,112,0xFFFFFF);List<SellEntry> rows=sellEntries();int start=shopPage*6,y=145;if(rows.isEmpty())g.drawCenteredString(font,"Aucun objet correspondant (hotbar protégée).",cx,170,0xBBBBBB);for(int i=start;i<Math.min(rows.size(),start+6);i++){SellEntry e=rows.get(i);g.fill(cx-190,y-4,cx+95,y+20,0x80363B44);g.renderItem(e.icon(),cx-184,y);g.drawString(font,e.name(),cx-160,y+4,0xFFFFFF);y+=30;}}
    private void renderJobs(GuiGraphics g,int cx){int y=145;g.drawCenteredString(font,"MÉTIER : "+selectedJob.displayName().toUpperCase(),cx,y,0x55FF55);g.drawString(font,"Niveau 1 / 100",cx-185,y+25,0xFFFFFF);g.drawString(font,"XP : 0 / 325",cx-185,y+42,0xFFFFFF);if(jobPage==0){String q=switch(selectedJob){case MINER->"Casser 64 minerais";case FARMER->"Récolter 128 cultures";case HUNTER->"Éliminer 30 créatures";case ALCHEMIST->"Préparer 10 potions";};g.drawString(font,"QUÊTES DU JOUR",cx-185,y+85,0xFFFF5555);g.drawString(font,q+" — 0% — +750 XP",cx-185,y+108,0xFFFFFF);}else{g.drawString(font,"RÉCOMPENSES",cx-185,y+85,0x55FF55);g.drawString(font,"Niv. 5 / 10 / 25 / 50 : récompenses progressives",cx-185,y+108,0xFFFFFF);g.drawString(font,"Niveau 100 : Stuff Endium complet",cx-185,y+132,0xFF55FFFF);}}
    private void renderEnchant(GuiGraphics g,int cx){g.drawCenteredString(font,"ENCHANTEMENT / RÉPARATION",cx,112,0x55FF55);Minecraft mc=Minecraft.getInstance();ItemStack hand=mc.player==null?ItemStack.EMPTY:mc.player.getMainHandItem();if(!hand.isEmpty())g.renderItem(hand,cx-185,145);g.drawString(font,"Objet en main : "+(hand.isEmpty()?"Aucun":hand.getHoverName().getString()),cx-160,150,0xFFFFFF);g.drawString(font,"Choisi : "+enchantName+" niveau "+enchantLevel,cx-185,230,0xFFFFDD55);g.drawString(font,"Actions via SophiMarketServer 6.0 (/sophimarketmod).",cx-185,355,0xBBBBBB);}
    @Override public boolean isPauseScreen(){return false;}
}
