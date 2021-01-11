package com.project.gonggus.domain.user;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class AuthService {
    private final String JWT_SECRET = "testkey";
    private final Long EXPIRE_DAY = 7L;
    private final Long ONE_DAY = 86400L;

    // 토큰 생성
    public String create (final User user) {
        final JwtBuilder builder = Jwts.builder();
        builder.setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .setSubject("userId")
                .claim("id", user.getId())
                .claim("userId", user.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * EXPIRE_DAY))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET.getBytes());
        final String jwt = builder.compact();
        return jwt;
    }

    // 토큰 검증 후 정보 얻기
    public Map<String, Object> get (final String jwt) {
        Jws<Claims> claims = null;
        try {
            claims = Jwts.parser()
                        .setSigningKey(JWT_SECRET.getBytes())
                        .parseClaimsJws(jwt);
        } catch (final Exception e) {
            throw new RuntimeException();
        }

        return claims.getBody();
    }

    // 비밀번호 SHA-256으로 암호화
    public String encryptString (String input){
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(input.getBytes());
            byte byteData[] = sha.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Cookie setAuthCookie (String token) {
        Cookie cookie = new Cookie("access_token", token);
        cookie.setMaxAge((int)(EXPIRE_DAY * ONE_DAY));
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setDomain(".gonggus.cf");
        return cookie;
    }

    public Cookie findAuthCookie (Cookie[] cookies) {
        for (Cookie c : cookies) {
            if (c.getName().equals("access_token")) {
                return c;
            }
        }
        return null;
    }

    public Cookie removeCookie () {
        Cookie cookie = new Cookie("access_token", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
