package net.minestom.server.stat;

public class PlayerStatistic {

    private StatisticCategory category;
    private int statisticId;

    public PlayerStatistic(StatisticCategory category, int statisticId) {
        this.category = category;
        this.statisticId = statisticId;
    }

    public PlayerStatistic(StatisticType type) {
        this(StatisticCategory.CUSTOM, type.getId());
    }

    public StatisticCategory getCategory() {
        return category;
    }

    public int getStatisticId() {
        return statisticId;
    }
}
