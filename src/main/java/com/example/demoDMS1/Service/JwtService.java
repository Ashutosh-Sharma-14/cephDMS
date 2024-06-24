package com.example.demoDMS1.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "692ECFA1B956ECF42DAC6FFAB3027DE9CB16D1D95E8AEB64AE1B934AA91EF3A6C2E35A36E1B8D9183D97B02058F531305109688FA4A0892CB11ADC3E61AD4782A198C7DD2B11D067ADE4A82B7749A0CFA0054D22BD9BB0FB1F72AA69DE0F4CF86ABEB3B44BB9302186CA71DD1827256AB0DD2C122863BAFD546842A8703ADB36";
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(
            Map<String,Object> extraClaims,
            UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()*1000*60*24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        System.out.println("Extracted username from token: " + username);
        boolean areUserEqual = username.equals(userDetails.getUsername());
        boolean isTokenExpired = isTokenExpired(token);

        System.out.println("Are users equal: " + areUserEqual);
        System.out.println("Is token expired: " + isTokenExpired);

        boolean isTokenValid = areUserEqual && !isTokenExpired;
        System.out.println("is Token valid : " + isTokenValid);
        return isTokenValid;
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
//                this signInKey is used to verify if the sender is who he claims to be
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
