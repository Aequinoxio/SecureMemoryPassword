/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import SecureMemoryPassword.*;

/**
 * Non dovrebbe compilare in quanto la classe SecurePasswordInMemory è final
 * @author utente
 */
public class TestSubClass /*extends SecureMemoryPassword non compila. Risultato atteso: OTTIMO */ {

    
    public TestSubClass() {
        super();
        aritest();
    }

    
    
    public final void aritest() {
        System.out.println("Aritest");
    }

}
