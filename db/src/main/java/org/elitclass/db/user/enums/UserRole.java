package org.elitclass.db.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public enum UserRole {
    USER("사용자"),
    ADMIN("관리자");

    private final String status;

}
