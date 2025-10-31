package com.playschool.management.dto.dashboard;

import java.time.LocalDateTime;

public class DriverMessageSummaryDto {

    private String conversationId;
    private String subject;
    private int unreadCount;
    private LocalDateTime lastMessageAt;

    public DriverMessageSummaryDto() {
    }

    public DriverMessageSummaryDto(String conversationId, String subject, int unreadCount,
            LocalDateTime lastMessageAt) {
        this.conversationId = conversationId;
        this.subject = subject;
        this.unreadCount = unreadCount;
        this.lastMessageAt = lastMessageAt;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
}
