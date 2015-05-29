package com.archer.pm.cypher;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Component;



/**
 * A URL builder for links with reset password service.
 *
 *
 */
@Component
public class EncodePassWordService implements CipherService{
    private static Logger logger = LoggerFactory.getLogger(EncodePassWordService.class);
    private String salt="abc";

    @Autowired
//    @Qualifier(value = "Md5PasswordEncoder")
    private Md5PasswordEncoder encoder;
//    @Autowired
//    private BytesEncryptor bytesEncryptor;

    
    public String encrypt(String token) {
        logger.info("Fyp token is {}", token);
       // Stopwatch stopwatch = new Stopwatch().start();
        try {
          //  String encodedToken = EncryptUtils.encryptText(bytesEncryptor, token);
        //	 final PasswordEncoder encoder = new Md5PasswordEncoder();
            String hashedToken = encoder.encodePassword(token, salt);
            String hashedToken2 =  encoder.encodePassword(token, salt);
            System.out.println("Encoded token is '{}' and hash is '{}'"+ hashedToken+ hashedToken2);
            logger.info("Encoded token is '{}' and hash is '{}'", hashedToken, hashedToken2);
//            StringBuilder sb = new StringBuilder(baseUrl).append(QUESTION_MARK);
//            sb.append(PARAM_TOKEN).append(EQ).append(urlEncode(encodedToken)).append(AND).append(PARAM_HASH).append(EQ);
//            sb.append(urlEncode(hashedToken));
            return hashedToken;
        }  finally {
         //   logger.info("Build fyp url takes {} milliseconds", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
        }
    }

@Override
public String decrypt(String token) {
	// TODO Auto-generated method stub
	return null;
}
    

}
