package com.esprit.inscriptionservice.messaging;

import com.esprit.inscriptionservice.entities.InscriptionStatus;
import java.time.LocalDateTime;

/**
 * Message envoyé dans la queue RabbitMQ vers le user-service.
 * Chaque action (INSCRIPTION_SUBMITTED, INSCRIPTION_ACCEPTED, INSCRIPTION_REJECTED)
 * utilise ce même objet — le champ "action" distingue le type d'événement.
 */
public class InscriptionEvent {

    public enum Action {
        INSCRIPTION_SUBMITTED,
        INSCRIPTION_ACCEPTED,
        INSCRIPTION_REJECTED
    }

    private Action            action;
    private Long              inscriptionId;
    private Long              userId;
    private Long              eventId;
    private String            participantNom;
    private String            participantPrenom;
    private String            participantEmail;
    private InscriptionStatus status;
    private String            eventTitle;
    private LocalDateTime     registrationDate;
    private String            badgeImagePath;   // renseigné uniquement si ACCEPTED

    public InscriptionEvent() {}

    // ─── Getters / Setters ────────────────────────────────────────────────

    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }

    public Long getInscriptionId() { return inscriptionId; }
    public void setInscriptionId(Long inscriptionId) { this.inscriptionId = inscriptionId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getParticipantNom() { return participantNom; }
    public void setParticipantNom(String participantNom) { this.participantNom = participantNom; }

    public String getParticipantPrenom() { return participantPrenom; }
    public void setParticipantPrenom(String participantPrenom) { this.participantPrenom = participantPrenom; }

    public String getParticipantEmail() { return participantEmail; }
    public void setParticipantEmail(String participantEmail) { this.participantEmail = participantEmail; }

    public InscriptionStatus getStatus() { return status; }
    public void setStatus(InscriptionStatus status) { this.status = status; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public String getBadgeImagePath() { return badgeImagePath; }
    public void setBadgeImagePath(String badgeImagePath) { this.badgeImagePath = badgeImagePath; }
}
