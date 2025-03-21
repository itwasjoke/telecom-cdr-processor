package com.itwasjoke.telecom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class CDR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JoinColumn(name="start_time")
    private LocalDateTime startTime;
    @JoinColumn(name="end_time")
    private LocalDateTime endTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="callerNumber", nullable=false)
    private Caller callerNumber;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receiverNumber", nullable=false)
    private Caller receiverNumber;
}
