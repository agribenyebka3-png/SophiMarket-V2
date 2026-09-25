package fr.sophi.market.jobs;

public final class JobProgress {
    private long xp;
    private int level = 1;

    public long xp() { return xp; }
    public int level() { return level; }

    public long xpForNextLevel() {
        return 250L + (long) level * level * 75L;
    }

    public void addXp(long amount) {
        xp += Math.max(0, amount);
        while (level < 1000 && xp >= xpForNextLevel()) {
            xp -= xpForNextLevel();
            level++;
        }
    }
}
