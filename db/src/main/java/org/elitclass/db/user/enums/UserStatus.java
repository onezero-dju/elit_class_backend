package org.elitclass.db.user.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserStatus {
    REGISTERED("등록"),
    UNREGISTERED("해지");

    private final String status;
}
