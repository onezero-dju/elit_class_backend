package org.elitclass.api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    private final Key key;

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }
    private final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;
    private final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60*24*30;

    public String createAccessToken(Long userId,String email, String role){
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("role",role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId){
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public Claims getClaims(String token){
        return Jwts.parser()
                .setSigningKey(key)
                .build().parseClaimsJws(token)
                .getBody();
    }

    public Long getUserId(String token){
        return Long.valueOf(getClaims(token).getSubject());
    }

    public String getEmail(String token){
        return getClaims(token).get("email", String.class);
    }

    public String getRole(String token){
        return getClaims(token).get("role",String.class);
    }

}
