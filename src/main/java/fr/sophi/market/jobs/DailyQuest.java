package fr.sophi.market.jobs;

public record DailyQuest(JobType job, String title, String description, int target, long rewardXp) {
    public static DailyQuest miner() {
        return new DailyQuest(JobType.MINER, "Mineur du jour", "Casse 64 minerais", 64, 750);
    }
    public static DailyQuest farmer() {
        return new DailyQuest(JobType.FARMER, "Récolte du jour", "Récolte 128 cultures", 128, 650);
    }
    public static DailyQuest hunter() {
        return new DailyQuest(JobType.HUNTER, "Chasse du jour", "Élimine 30 créatures", 30, 800);
    }
    public static DailyQuest alchemist() {
        return new DailyQuest(JobType.ALCHEMIST, "Alchimie du jour", "Prépare 10 potions", 10, 900);
    }
}
