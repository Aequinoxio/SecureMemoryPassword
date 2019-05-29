/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SecurePasswordInMemory;

import SecureMemoryPassword.SecureMemoryPassword;
import SecureMemoryPassword.SecureMemoryPasswordException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author utente
 */
public class SecurePasswordInMemory {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            SecureMemoryPassword securePassword  =new SecureMemoryPassword();
            String pwd = "pippoplutoepaperino";
            securePassword.setPassword(pwd.getBytes(StandardCharsets.UTF_8));
            byte[] recoveredPwd=new byte[securePassword.getLength()];
            securePassword.getPassword(recoveredPwd);
            String temp = new String(recoveredPwd);
            System.out.println(String.format("pwd: %s - pwd memorizzata %s", pwd,temp));
        } catch (SecureMemoryPasswordException ex) {
            Logger.getLogger(SecurePasswordInMemory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
