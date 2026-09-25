package fr.sophi.market.jobs;

public record Reward(int level, String itemId, int count, String displayName) {
    public static Reward[] minerRewards() {
        return new Reward[] {
            new Reward(5, "minecraft:iron_ingot", 16, "16 lingots de fer"),
            new Reward(10, "minecraft:diamond", 3, "3 diamants"),
            new Reward(25, "minecraft:diamond_pickaxe", 1, "Pioche en diamant"),
            new Reward(50, "minecraft:netherite_ingot", 1, "Lingot de Netherite"),
            new Reward(100, "minecraft:netherite_pickaxe", 1, "Pioche de prestige")
        };
    }
}
