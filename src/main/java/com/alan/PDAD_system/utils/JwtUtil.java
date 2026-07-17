package com.alan.PDAD_system.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {
    //私钥 / 生成签名的时候使用的秘钥secret，一般可以从本地配置
    // 文件中读取，切记这个秘钥不能外露，只在服务端使用，在任何场
    // 景都不应该流露出去。一旦客户端得知这个secret, 那就意味着
    // 客户端是可以自我签发jwt了。
    private final static String secret_key = "6LdjpKUqAAAAAPZLoaGu-_wtUSVtxP1I7SYhKe5p";

    //jwt所有人
    private final static String subject = "lisi";

    public static String genToken(Map<String, Object> claims, long expirationMinutes) {
        long expirationMillis = expirationMinutes * 60 * 1000;  // 转换为毫秒
        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(claims)      // 载荷信息
                .setIssuedAt(new Date())       // 设置iat: jwt的签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis)) // 设置exp：jwt过期时间为1200秒
                .setSubject(subject)    //设置sub：代表这个jwt所面向的用户，所有人
                .signWith(SignatureAlgorithm.HS256, secret_key); //设置签名：通过签名算法和秘钥生成签名
              // 开始压缩为xxxxx.yyyyy.zzzzz 格式的jwt token
        return jwtBuilder.compact();
    }

    //parseToken方法根据传入的JWT令牌token，校验令牌是否
    // 有效，并返回其中的有效载荷部分
    public static Map<String,Object> parseToken(String token){
        try {
            return Jwts.parser()
                    .setSigningKey(secret_key)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // Token 无效或解析失败
            throw new IllegalArgumentException("Token 无效或已过期");
        }
    }
    // 获取用户 ID
    public static String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret_key)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", String.class);
    }


}
