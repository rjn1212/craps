package com.gitgames.craps.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {

    private final String id;
    private int bankroll;
    private final int startingBankroll;
    private RoundPhase roundPhase;
    private Integer point;
    private GamePhase gamePhase;
    private final List<Bet> bets = new ArrayList<>();
    private final List<RollRecord> rollHistory = new ArrayList<>();
    private int rollCount;
    private int totalBetsPlaced;
    private final TableSettings settings;
    private String platformUserId;
    private String gameOverReason;
    private final Instant createdAt;
    private Instant lastActivityAt;

    public Game(int startingBankroll, TableSettings settings) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.bankroll = startingBankroll;
        this.startingBankroll = startingBankroll;
        this.roundPhase = RoundPhase.COME_OUT;
        this.point = null;
        this.gamePhase = GamePhase.PLAYING;
        this.rollCount = 0;
        this.totalBetsPlaced = 0;
        this.settings = settings;
        this.createdAt = Instant.now();
        this.lastActivityAt = Instant.now();
    }

    public void addBet(Bet bet) {
        bankroll -= bet.getAmount();
        bets.add(bet);
        totalBetsPlaced++;
        lastActivityAt = Instant.now();
    }

    public void removeBet(String betId) {
        bets.removeIf(b -> {
            if (b.getId().equals(betId) && b.isActive()) {
                bankroll += b.getAmount();
                return true;
            }
            return false;
        });
        lastActivityAt = Instant.now();
    }

    public void addRollRecord(RollRecord record) {
        rollHistory.add(record);
        rollCount++;
        lastActivityAt = Instant.now();
    }

    public void creditBankroll(int amount) {
        bankroll += amount;
    }

    public void clearResolvedBets() {
        bets.removeIf(b -> b.getStatus() != BetStatus.ACTIVE);
    }

    public List<Bet> getActiveBets() {
        return bets.stream().filter(Bet::isActive).toList();
    }

    public int getTotalBetsOnTable() {
        return bets.stream().filter(Bet::isActive).mapToInt(Bet::getAmount).sum();
    }

    public Bet findBetById(String betId) {
        return bets.stream()
            .filter(b -> b.getId().equals(betId))
            .findFirst()
            .orElse(null);
    }

    public Bet findActiveBetByType(BetType type) {
        return bets.stream()
            .filter(b -> b.isActive() && b.getType() == type)
            .findFirst()
            .orElse(null);
    }

    public List<Bet> findActiveBetsByType(BetType type) {
        return bets.stream()
            .filter(b -> b.isActive() && b.getType() == type)
            .toList();
    }

    public boolean hasActiveBetOfType(BetType type) {
        return bets.stream().anyMatch(b -> b.isActive() && b.getType() == type);
    }

    public String getId() { return id; }
    public int getBankroll() { return bankroll; }
    public int getStartingBankroll() { return startingBankroll; }
    public RoundPhase getRoundPhase() { return roundPhase; }
    public void setRoundPhase(RoundPhase roundPhase) { this.roundPhase = roundPhase; }
    public Integer getPoint() { return point; }
    public void setPoint(Integer point) { this.point = point; }
    public GamePhase getGamePhase() { return gamePhase; }
    public void setGamePhase(GamePhase gamePhase) { this.gamePhase = gamePhase; }
    public List<Bet> getBets() { return bets; }
    public List<RollRecord> getRollHistory() { return rollHistory; }
    public int getRollCount() { return rollCount; }
    public int getTotalBetsPlaced() { return totalBetsPlaced; }
    public TableSettings getSettings() { return settings; }
    public String getPlatformUserId() { return platformUserId; }
    public void setPlatformUserId(String platformUserId) { this.platformUserId = platformUserId; }
    public String getGameOverReason() { return gameOverReason; }
    public void setGameOverReason(String gameOverReason) { this.gameOverReason = gameOverReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastActivityAt() { return lastActivityAt; }
}
