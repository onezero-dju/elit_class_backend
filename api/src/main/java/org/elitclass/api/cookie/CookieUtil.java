package org.elitclass.api.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static void addJwtCookie(HttpServletResponse response, String name, String token, int maxAge) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }
}