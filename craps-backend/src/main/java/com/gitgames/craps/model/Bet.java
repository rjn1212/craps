package com.gitgames.craps.model;

import java.util.UUID;

public class Bet {

    private final String id;
    private final BetType type;
    private int amount;
    private BetStatus status;
    private Integer pointNumber;
    private boolean on;
    private final int createdOnRoll;

    public Bet(BetType type, int amount, int createdOnRoll) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.type = type;
        this.amount = amount;
        this.status = BetStatus.ACTIVE;
        this.pointNumber = null;
        this.on = true;
        this.createdOnRoll = createdOnRoll;
    }

    public String getId() { return id; }
    public BetType getType() { return type; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public BetStatus getStatus() { return status; }
    public void setStatus(BetStatus status) { this.status = status; }
    public Integer getPointNumber() { return pointNumber; }
    public void setPointNumber(Integer pointNumber) { this.pointNumber = pointNumber; }
    public boolean isOn() { return on; }
    public void setOn(boolean on) { this.on = on; }
    public int getCreatedOnRoll() { return createdOnRoll; }

    public boolean isActive() { return status == BetStatus.ACTIVE; }
}
