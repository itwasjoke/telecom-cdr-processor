package com.itwasjoke.telecom.dto;

public record UDR(
    String msisdn,
    TotalTime incomingCall,
    TotalTime outcomingCall
) {}
