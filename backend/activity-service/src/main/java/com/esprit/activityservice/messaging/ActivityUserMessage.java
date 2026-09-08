package com.esprit.activityservice.messaging;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Message publié dans la queue "activity-user.queue".
 * Notifie le user-service lors d'actions sur les activités.
 */
public class ActivityUserMessage implements Serializable {

    public enum ActivityAction {
        ACTIVITY_CREATED,
        ACTIVITY_UPDATED,
        ACTIVITY_DELETED
    }

    private Long           activityId;
    private String         activityName;
    private Long           eventId;
    private Long           userId;       // propriétaire de l'événement lié
    private ActivityAction action;
    private String         message;
    private LocalDateTime  sentAt;

    public ActivityUserMessage() {}

    public ActivityUserMessage(Long activityId, String activityName,
                               Long eventId, Long userId,
                               ActivityAction action, String message) {
        this.activityId   = activityId;
        this.activityName = activityName;
        this.eventId      = eventId;
        this.userId       = userId;
        this.action       = action;
        this.message      = message;
        this.sentAt       = LocalDateTime.now();
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public Long getActivityId()                  { return activityId; }
    public void setActivityId(Long activityId)   { this.activityId = activityId; }

    public String getActivityName()                    { return activityName; }
    public void   setActivityName(String activityName) { this.activityName = activityName; }

    public Long getEventId()               { return eventId; }
    public void setEventId(Long eventId)   { this.eventId = eventId; }

    public Long getUserId()              { return userId; }
    public void setUserId(Long userId)   { this.userId = userId; }

    public ActivityAction getAction()                  { return action; }
    public void           setAction(ActivityAction a)  { this.action = a; }

    public String getMessage()                 { return message; }
    public void   setMessage(String message)   { this.message = message; }

    public LocalDateTime getSentAt()                   { return sentAt; }
    public void          setSentAt(LocalDateTime sentAt){ this.sentAt = sentAt; }

    @Override
    public String toString() {
        return "ActivityUserMessage{" +
                "activityId=" + activityId +
                ", activityName='" + activityName + '\'' +
                ", eventId=" + eventId +
                ", userId=" + userId +
                ", action=" + action +
                ", sentAt=" + sentAt +
                '}';
    }
}
