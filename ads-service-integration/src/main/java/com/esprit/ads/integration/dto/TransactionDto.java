package com.esprit.ads.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto {
    private String id;

    @JsonProperty("sender_id")
    private String senderId;

    @JsonProperty("receiver_id")
    private String receiverId;

    private BigDecimal amount;
    private String currency;
    private String status;
    private String reference;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public TransactionDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
