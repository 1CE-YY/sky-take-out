package com.sky.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥（必须至少32字节用于HS256算法）
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return JWT token字符串
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 为了兼容旧的短密钥，我们需要确保密钥长度至少32字节
        SecretKey key = getSecretKey(secretKey);

        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 设置jwt的body
        return Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(key, SignatureAlgorithm.HS256)
                // 设置过期时间
                .setExpiration(exp)
                .compact();
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token     加密后的token
     * @return Claims对象
     */
    public static Claims parseJWT(String secretKey, String token) {
        // 为了兼容旧的短密钥，我们需要确保密钥长度至少32字节
        SecretKey key = getSecretKey(secretKey);

        // 得到DefaultJwtParser
        return Jwts.parserBuilder()
                // 设置签名的秘钥
                .setSigningKey(key)
                .build()
                // 设置需要解析的jwt
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取SecretKey对象
     * 如果提供的密钥长度不足32字节，则进行填充以兼容旧配置
     * 
     * @param secretKey 密钥字符串
     * @return SecretKey对象
     */
    private static SecretKey getSecretKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        
        // JJWT 0.11.x 要求 HS256 至少 256 位（32 字节）
        if (keyBytes.length < 32) {
            // 为了向后兼容，填充密钥到32字节
            // 警告：这只是临时方案，生产环境应该使用真正的32字节密钥
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            // 用密钥本身循环填充剩余部分
            for (int i = keyBytes.length; i < 32; i++) {
                paddedKey[i] = keyBytes[i % keyBytes.length];
            }
            keyBytes = paddedKey;
        }
        
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
