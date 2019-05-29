/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SecureMemoryPassword;

/**
 *
 * @author utente
 */
public class SecureMemoryPasswordException extends Exception {

    /**
     * Creates a new instance of <code>SecurePasswordException</code> without
     * detail message.
     */
    public SecureMemoryPasswordException() {
    }

    /**
     * Constructs an instance of <code>SecurePasswordException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public SecureMemoryPasswordException(String msg) {
        super(msg);
    }
}
