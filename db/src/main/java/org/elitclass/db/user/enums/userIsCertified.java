package org.elitclass.db.user.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum userIsCertified {
    CERTIFIED("인증된 개발자"),
    UNCERTIFIED("비 인증 개발자");

    private final String Description;
}
