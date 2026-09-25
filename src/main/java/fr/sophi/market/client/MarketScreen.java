package fr.sophi.market.client;

import fr.sophi.market.jobs.JobType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class MarketScreen extends Screen {
    private enum Tab { BUY, SELL, JOBS, ENCHANT }
    private Tab tab = Tab.JOBS;
    private JobType selectedJob = JobType.MINER;

    public MarketScreen() { super(Component.literal("SophiMarket V2")); }

    @Override protected void init() {
        int cx = width / 2;
        int y = 45;
        addRenderableWidget(Button.builder(Component.literal("ACHETER"), b -> tab = Tab.BUY).bounds(cx-205,y,95,24).build());
        addRenderableWidget(Button.builder(Component.literal("VENDRE"), b -> tab = Tab.SELL).bounds(cx-105,y,95,24).build());
        addRenderableWidget(Button.builder(Component.literal("MÉTIERS"), b -> tab = Tab.JOBS).bounds(cx-5,y,95,24).build());
        addRenderableWidget(Button.builder(Component.literal("ENCHANT. / RÉPAR."), b -> tab = Tab.ENCHANT).bounds(cx+95,y,120,24).build());
        int jy = 86;
        int x = cx - 190;
        for (JobType job : JobType.values()) {
            JobType j = job;
            addRenderableWidget(Button.builder(Component.literal(job.icon()+" "+job.displayName()), b -> { tab=Tab.JOBS; selectedJob=j; }).bounds(x,jy,90,24).build());
            x += 95;
        }
    }

    @Override public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);
        int cx=width/2;
        g.fill(cx-225,25,cx+225,height-25,0xE820242B);
        g.drawCenteredString(font,"SOPHIMARKET V2",cx,30,0x55FF55);
        super.render(g,mouseX,mouseY,partialTick);
        if(tab==Tab.JOBS) renderJobs(g,cx);
        else if(tab==Tab.ENCHANT) renderEnchant(g,cx);
        else g.drawCenteredString(font,tab==Tab.BUY?"BOUTIQUE - ACHETER":"BOUTIQUE - VENDRE",cx,130,0xFFFFFF);
    }

    private void renderJobs(GuiGraphics g,int cx) {
        int y=130;
        g.drawCenteredString(font,"MÉTIER : "+selectedJob.displayName().toUpperCase(),cx,y,0x55FF55);
        g.drawString(font,"Niveau 1 / 100",cx-185,y+28,0xFFFFFF);
        g.drawString(font,"XP : 0 / 325   (+XP affiché à chaque action)",cx-185,y+45,0xFFFFFF);
        g.fill(cx-185,y+65,cx+185,y+75,0xFF555D6A);
        g.fill(cx-185,y+65,cx-165,y+75,0xFF55FF55);
        g.drawString(font,"QUÊTE DU JOUR",cx-185,y+100,0xFFFF5555);
        String q=switch(selectedJob){case MINER->"Casser 64 minerais";case FARMER->"Récolter 128 cultures";case HUNTER->"Éliminer 30 créatures";case ALCHEMIST->"Préparer 10 potions";};
        g.drawString(font,q+"   0%",cx-185,y+118,0xFFFFFF);
        g.drawString(font,"Récompense : XP + objets",cx-185,y+138,0xFFFFDD55);
        g.drawString(font,"RÉCOMPENSES DE NIVEAU",cx-185,y+175,0x55FF55);
        g.drawString(font,"Niv. 5  •  Niv. 10  •  Niv. 25  •  Niv. 50  •  Niv. 100",cx-185,y+195,0xFFFFFF);
        g.drawString(font,"Les récompenses validées seront ajoutées directement à l'inventaire.",cx-185,y+215,0xBBBBBB);
    }

    private void renderEnchant(GuiGraphics g,int cx) {
        g.drawCenteredString(font,"ENCHANTEMENT / RÉPARATION",cx,130,0x55FF55);
        g.drawString(font,"Sélectionne ton équipement puis l'enchantement.",cx-185,165,0xFFFFFF);
        g.drawString(font,"Plus le niveau est élevé, plus le prix augmente.",cx-185,185,0xFFFFDD55);
    }

    @Override public boolean isPauseScreen(){ return false; }
}
