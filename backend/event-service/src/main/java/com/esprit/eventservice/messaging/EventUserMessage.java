package com.esprit.eventservice.messaging;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Message envoyé dans la queue "event-user.queue" à destination du user-service.
 * Représente une notification d'événement (création, mise à jour, annulation…).
 */
public class EventUserMessage implements Serializable {

    public enum EventAction {
        EVENT_CREATED,
        EVENT_UPDATED,
        EVENT_CANCELLED,
        EVENT_ARCHIVED
    }

    private Long        eventId;
    private String      eventTitle;
    private Long        userId;
    private String      userEmail;
    private EventAction action;
    private String      message;
    private LocalDateTime sentAt;

    public EventUserMessage() {}

    public EventUserMessage(Long eventId, String eventTitle,
                            Long userId, String userEmail,
                            EventAction action, String message) {
        this.eventId    = eventId;
        this.eventTitle = eventTitle;
        this.userId     = userId;
        this.userEmail  = userEmail;
        this.action     = action;
        this.message    = message;
        this.sentAt     = LocalDateTime.now();
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public Long getEventId()              { return eventId; }
    public void setEventId(Long eventId)  { this.eventId = eventId; }

    public String getEventTitle()                  { return eventTitle; }
    public void   setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public Long getUserId()             { return userId; }
    public void setUserId(Long userId)  { this.userId = userId; }

    public String getUserEmail()                 { return userEmail; }
    public void   setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public EventAction getAction()               { return action; }
    public void        setAction(EventAction a)  { this.action = a; }

    public String getMessage()                { return message; }
    public void   setMessage(String message)  { this.message = message; }

    public LocalDateTime getSentAt()                { return sentAt; }
    public void          setSentAt(LocalDateTime t) { this.sentAt = t; }

    @Override
    public String toString() {
        return "EventUserMessage{" +
                "eventId=" + eventId +
                ", eventTitle='" + eventTitle + '\'' +
                ", userId=" + userId +
                ", action=" + action +
                ", sentAt=" + sentAt +
                '}';
    }
}