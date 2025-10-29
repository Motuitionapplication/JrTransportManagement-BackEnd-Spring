package com.playschool.management.dto.dashboard;

public class DriverDashboardStatsDto {

    private double totalEarnings;
    private double todayEarnings;
    private int tripsToday;
    private int activeBookings;
    private int unreadMessages;

    public DriverDashboardStatsDto() {
    }

    public DriverDashboardStatsDto(double totalEarnings, double todayEarnings, int tripsToday,
            int activeBookings, int unreadMessages) {
        this.totalEarnings = totalEarnings;
        this.todayEarnings = todayEarnings;
        this.tripsToday = tripsToday;
        this.activeBookings = activeBookings;
        this.unreadMessages = unreadMessages;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public double getTodayEarnings() {
        return todayEarnings;
    }

    public void setTodayEarnings(double todayEarnings) {
        this.todayEarnings = todayEarnings;
    }

    public int getTripsToday() {
        return tripsToday;
    }

    public void setTripsToday(int tripsToday) {
        this.tripsToday = tripsToday;
    }

    public int getActiveBookings() {
        return activeBookings;
    }

    public void setActiveBookings(int activeBookings) {
        this.activeBookings = activeBookings;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }
}
