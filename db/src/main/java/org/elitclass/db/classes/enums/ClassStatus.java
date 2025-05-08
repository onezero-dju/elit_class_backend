package org.elitclass.db.classes.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ClassStatus {
    REGISTERED("등록"),
    UNREGISTERED("해지"),
    Report("신고");

    private final String status;
}
