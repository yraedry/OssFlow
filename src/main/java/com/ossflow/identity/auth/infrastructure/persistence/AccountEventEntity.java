package com.ossflow.identity.auth.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "account_events")
public class AccountEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AccountEventEntity() {}

    public AccountEventEntity(Long accountId, String eventType, String ipAddress,
                               String userAgent, Instant createdAt) {
        this.accountId = accountId;
        this.eventType = eventType;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getAccountId() { return accountId; }
    public String getEventType() { return eventType; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public Instant getCreatedAt() { return createdAt; }
}
