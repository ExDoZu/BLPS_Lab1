package com.blps.lab1.controllers.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class ReceiveModerationApprovement {
    private long postId;
    private boolean approved;
}
