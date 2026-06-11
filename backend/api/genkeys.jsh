import java.security.*;
import java.util.*;
import java.nio.file.*;

KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
kpg.initialize(2048);
KeyPair kp = kpg.generateKeyPair();

String priv = "-----BEGIN PRIVATE KEY-----\n" + Base64.getMimeEncoder().encodeToString(kp.getPrivate().getEncoded()) + "\n-----END PRIVATE KEY-----";
String pub  = "-----BEGIN PUBLIC KEY-----\n"  + Base64.getMimeEncoder().encodeToString(kp.getPublic().getEncoded())  + "\n-----END PUBLIC KEY-----";

Files.writeString(Path.of("src/main/resources/keys/private.pem"), priv);
Files.writeString(Path.of("src/main/resources/keys/public.pem"), pub);
System.out.println("Keys generated!");
/exit
