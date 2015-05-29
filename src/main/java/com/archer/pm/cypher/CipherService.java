package com.archer.pm.cypher;



/**
 * A URL builder for links with reset password service.
 */
public interface CipherService {
//    String build(String username, String guid);
//
//    Token verify(String token, String hash);
	
	String encrypt(String token);
	String decrypt(String token);
}
