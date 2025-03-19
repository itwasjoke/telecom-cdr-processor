package com.itwasjoke.telecom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Entity
@Table(name = "caller")
@Getter
@Setter
public class Caller {
    @Id
    private String msisdn;

    @OneToMany(mappedBy="callerNumber")
    private Set<CDR> incomingCalls;

    @OneToMany(mappedBy = "receiverNumber")
    private Set<CDR> outgoingCalls;
}
