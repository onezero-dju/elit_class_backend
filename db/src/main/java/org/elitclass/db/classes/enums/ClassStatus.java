package org.elitclass.db.classes.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ClassStatus {
    REGISTERED("등록"),
    UNREGISTERED("헤지");

    private final String status;
}
