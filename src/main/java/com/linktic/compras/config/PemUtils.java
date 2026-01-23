package com.linktic.compras.config;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class PemUtils {

    public static RSAPrivateKey readPrivateKey(InputStream is) throws Exception {
        String key = new String(is.readAllBytes())
                .replaceAll("-----\\w+ PRIVATE KEY-----","")
                .replaceAll("\\s","");
        byte[] decoded = Base64.getDecoder().decode(key);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    public static RSAPublicKey readPublicKey(InputStream is) throws Exception {
        String key = new String(is.readAllBytes())
                .replaceAll("-----\\w+ PUBLIC KEY-----","")
                .replaceAll("\\s","");
        byte[] decoded = Base64.getDecoder().decode(key);
        return (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
    }
}
