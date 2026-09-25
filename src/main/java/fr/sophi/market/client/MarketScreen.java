package fr.sophi.market.client;

import fr.sophi.market.jobs.JobType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class MarketScreen extends Screen {
    private enum Tab { BUY, SELL, JOBS, ENCHANT }
    private Tab tab = Tab.JOBS;
    private JobType selectedJob = JobType.MINER;
    private int jobPage = 0;
    private int shopPage = 0;

    public MarketScreen() { super(Component.literal("SophiMarket V2")); }

    @Override protected void init() { rebuildButtons(); }

    private void rebuildButtons() {
        clearWidgets();
        int cx=width/2, y=45;
        addRenderableWidget(Button.builder(Component.literal("ACHETER"), b -> switchTab(Tab.BUY)).bounds(cx-205,y,95,24).build());
        addRenderableWidget(Button.builder(Component.literal("VENDRE"), b -> switchTab(Tab.SELL)).bounds(cx-105,y,95,24).build());
        addRenderableWidget(Button.builder(Component.literal("MÉTIERS"), b -> switchTab(Tab.JOBS)).bounds(cx-5,y,95,24).build());
        addRenderableWidget(Button.builder(Component.literal("ENCHANT. / RÉPAR."), b -> switchTab(Tab.ENCHANT)).bounds(cx+95,y,120,24).build());
        if(tab==Tab.JOBS) addJobButtons(cx);
        if(tab==Tab.BUY || tab==Tab.SELL) addPageButtons(cx);
        if(tab==Tab.ENCHANT) addEnchantButtons(cx);
    }

    private void switchTab(Tab next){ tab=next; shopPage=0; rebuildButtons(); }

    private void addJobButtons(int cx) {
        int x=cx-190;
        for(JobType job:JobType.values()) {
            JobType j=job;
            addRenderableWidget(Button.builder(Component.literal(job.icon()+" "+job.displayName()), b->{selectedJob=j;jobPage=0;}).bounds(x,86,90,24).build());
            x+=95;
        }
        addRenderableWidget(Button.builder(Component.literal("<"),b->{if(jobPage>0)jobPage--;}).bounds(cx-190,365,35,20).build());
        addRenderableWidget(Button.builder(Component.literal("Page suivante >"),b->{if(jobPage<1)jobPage++;}).bounds(cx+65,365,120,20).build());
    }

    private void addPageButtons(int cx){
        addRenderableWidget(Button.builder(Component.literal("<"),b->{if(shopPage>0)shopPage--;}).bounds(cx-190,365,35,20).build());
        addRenderableWidget(Button.builder(Component.literal(">"),b->shopPage++).bounds(cx+150,365,35,20).build());
    }

    private void addEnchantButtons(int cx){
        addRenderableWidget(Button.builder(Component.literal("RÉPARER OBJET EN MAIN"),b->{}).bounds(cx-185,320,170,22).build());
        addRenderableWidget(Button.builder(Component.literal("ENCHANTER OBJET EN MAIN"),b->{}).bounds(cx+15,320,170,22).build());
    }

    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partialTick){
        renderBackground(g,mouseX,mouseY,partialTick);
        int cx=width/2;
        g.fill(cx-225,25,cx+225,height-25,0xE820242B);
        g.drawCenteredString(font,"SOPHIMARKET V2",cx,30,0x55FF55);
        super.render(g,mouseX,mouseY,partialTick);
        switch(tab){case BUY->renderBuy(g,cx);case SELL->renderSell(g,cx);case JOBS->renderJobs(g,cx);case ENCHANT->renderEnchant(g,cx);}
    }

    private void renderBuy(GuiGraphics g,int cx){
        g.drawCenteredString(font,"BOUTIQUE - ACHETER",cx,125,0xFFFFFF);
        String[] items={"Fer x16 — 900$","Or x16 — 1 500$","Diamant x1 — 1 200$","Émeraude x1 — 850$","Netherite x1 — 12 000$","Steak x16 — 400$","Bois x64 — 300$","Pierre x64 — 180$","Pioche diamant — 8 000$","Épée diamant — 7 500$","Armure diamant — 30 000$","Livres enchantés — prix selon enchantement"};
        renderRows(g,cx,items,shopPage);
        g.drawCenteredString(font,"Endium : non disponible à l'achat",cx,345,0xFF7777);
    }

    private void renderSell(GuiGraphics g,int cx){
        g.drawCenteredString(font,"VENDRE - TON INVENTAIRE",cx,125,0xFFFFFF);
        List<String> rows=new ArrayList<>();
        Minecraft mc=Minecraft.getInstance();
        if(mc.player!=null){
            for(ItemStack s:mc.player.getInventory().items){
                if(!s.isEmpty()) rows.add(s.getHoverName().getString()+" x"+s.getCount()+" — vendable");
            }
        }
        if(rows.isEmpty()) rows.add("Aucun objet vendable trouvé dans l'inventaire.");
        renderRows(g,cx,rows.toArray(String[]::new),shopPage);
        g.drawString(font,"La hotbar, l'objet en main et l'armure pourront être protégés lors de Tout vendre.",cx-190,345,0xBBBBBB);
    }

    private void renderRows(GuiGraphics g,int cx,String[] rows,int page){
        int start=page*8, y=155;
        for(int i=start;i<Math.min(rows.length,start+8);i++){g.fill(cx-190,y-3,cx+190,y+15,0x80363B44);g.drawString(font,rows[i],cx-180,y,0xFFFFFF);y+=23;}
        g.drawCenteredString(font,"Page "+(page+1),cx,370,0xAAAAAA);
    }

    private void renderJobs(GuiGraphics g,int cx){
        int y=125;
        g.drawCenteredString(font,"MÉTIER : "+selectedJob.displayName().toUpperCase(),cx,y,0x55FF55);
        g.drawString(font,"Niveau 1 / 100",cx-185,y+25,0xFFFFFF);
        g.drawString(font,"XP : 0 / 325",cx-185,y+42,0xFFFFFF);
        g.fill(cx-185,y+60,cx+185,y+70,0xFF555D6A);g.fill(cx-185,y+60,cx-175,y+70,0xFF55FF55);
        if(jobPage==0){
            String q=switch(selectedJob){case MINER->"Casser 64 minerais";case FARMER->"Récolter 128 cultures";case HUNTER->"Éliminer 30 créatures";case ALCHEMIST->"Préparer 10 potions";};
            g.drawString(font,"QUÊTES DU JOUR",cx-185,y+92,0xFFFF5555);
            g.drawString(font,"1. "+q+" — 0% — +750 XP",cx-185,y+112,0xFFFFFF);
            g.drawString(font,"2. Objectif bonus du métier — 0% — argent + XP",cx-185,y+132,0xFFFFFF);
            g.drawString(font,"3. Défi rare du métier — 0% — objet rare + XP",cx-185,y+152,0xFFFFFF);
        } else {
            g.drawString(font,"RÉCOMPENSES",cx-185,y+92,0x55FF55);
            g.drawString(font,"Niveau 5  — récompense commune",cx-185,y+112,0xFFFFFF);
            g.drawString(font,"Niveau 10 — récompense rare",cx-185,y+132,0xFFFFFF);
            g.drawString(font,"Niveau 25 — outil / équipement",cx-185,y+152,0xFFFFFF);
            g.drawString(font,"Niveau 50 — récompense très rare",cx-185,y+172,0xFFFFDD55);
            g.drawString(font,"Niveau 100 — Stuff Endium complet",cx-185,y+192,0xFF55FFFF);
        }
    }

    private void renderEnchant(GuiGraphics g,int cx){
        g.drawCenteredString(font,"ENCHANTEMENT / RÉPARATION",cx,125,0x55FF55);
        Minecraft mc=Minecraft.getInstance(); ItemStack hand=mc.player==null?ItemStack.EMPTY:mc.player.getMainHandItem();
        g.drawString(font,"Objet en main : "+(hand.isEmpty()?"Aucun":hand.getHoverName().getString()),cx-185,155,0xFFFFFF);
        g.drawString(font,"Enchantements proposés :",cx-185,185,0xFFFFDD55);
        g.drawString(font,"• Efficacité I → V     • Solidité I → III",cx-170,205,0xFFFFFF);
        g.drawString(font,"• Fortune I → III      • Toucher de soie I",cx-170,225,0xFFFFFF);
        g.drawString(font,"• Tranchant I → V      • Protection I → IV",cx-170,245,0xFFFFFF);
        g.drawString(font,"Le prix augmente avec le niveau choisi.",cx-185,280,0xBBBBBB);
    }

    @Override public boolean isPauseScreen(){return false;}
}
