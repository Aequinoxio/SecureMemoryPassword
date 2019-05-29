/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SecurePasswordInMemory;

import SecureMemoryPassword.SecureMemoryPasswordException;
import SecureMemoryPassword.SecureMemoryPassword;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author utente
 */
public class SecureMemoryPasswordTest {

    public SecureMemoryPasswordTest() {
    }

//    @BeforeAll
//    public static void setUpClass() {
//    }
//    
//    @AfterAll
//    public static void tearDownClass() {
//    }
//    
//    @BeforeEach
//    public void setUp() {
//    }
//    
//    @AfterEach
//    public void tearDown() {
//    }
//
//    /**
//     * Test of getMaxPasswordLength method, of class SecureMemoryPassword.
//     */
//    @Test
//    public void testGetMaxPasswordLength() {
//        System.out.println("getMaxPasswordLength");
//        int expResult = 0;
//        int result = SecureMemoryPassword.getMaxPasswordLength();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getLength method, of class SecureMemoryPassword.
//     */
//    @Test
//    public void testGetLength() {
//        System.out.println("getLength");
//        SecureMemoryPassword instance = new SecureMemoryPassword();
//        int expResult = 0;
//        int result = instance.getLength();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getPassword method, of class SecureMemoryPassword.
//     */
//    @Test
//    public void testGetPassword() throws Exception {
//        System.out.println("getPassword");
//        char[] passwordExternal = null;
//        SecureMemoryPassword instance = new SecureMemoryPassword();
//        instance.getPassword(passwordExternal);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of setPassword method, of class SecureMemoryPassword.
//     */
//    @Test
//    public void testSetPassword() throws Exception {
//        System.out.println("setPassword");
//        byte[] password = null;
//        SecureMemoryPassword instance = new SecureMemoryPassword();
//        instance.setPassword(password);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    @Test
    public void testGetSetPassword() throws UnsupportedEncodingException, SecureMemoryPasswordException {

        SecureMemoryPassword securePassword = new SecureMemoryPassword();
        String pwd = "pippoplutoepaperino";
        securePassword.setPassword(pwd.getBytes(StandardCharsets.UTF_8));
        byte[] recoveredPwd = new byte[securePassword.getLength()];
        securePassword.getPassword(recoveredPwd);
        String temp = new String(recoveredPwd);
        assertEquals(pwd, temp);
        System.out.println(String.format("pwd: %s - pwd memorizzata %s", pwd, temp));
    }

    @Test
    public void testSetMultiplePassword() throws SecureMemoryPasswordException {
        int numPassword = 10000;
        int numGetPassword = 11; // valori differenti e primi tra loro per evitare interferenze potenziali
        int numSetPassword = 13;
        int passwordLength = 1024;

        byte[] randomBytes = new byte[passwordLength];
        byte[] passwordRecovered = new byte[passwordLength];

        // Per generare una password casuale
        SecureRandom seed = new SecureRandom();
        seed.nextBytes(randomBytes); // Init seed

        SecureMemoryPassword securePassword = new SecureMemoryPassword();

        // Ciclo sul numero di password
        for (int i = 0; i < numPassword; i++) {
            //System.out.println(String.format("Genero password casuale %d", i));

            // Ciclo sul set della stessa password
            // Imposto varie volte la stessa password per testare la randomizzazione del seed e dello suffle buffer
            for (int j = 0; j < numSetPassword; j++) {
                seed.nextBytes(randomBytes);
                securePassword.setPassword(randomBytes);
            }

            // Ciclo sul get della stessa password
            // Recupero varie volte la pwd per testare se il random seed interno rimane costante
            for (int k = 0; k < numGetPassword; k++) {
                securePassword.getPassword(passwordRecovered);
            }

            // Test correttezza get
            assertEquals(passwordLength, randomBytes.length);

            for (int j = 0; j < passwordRecovered.length; j++) {
                assertEquals(passwordRecovered[j], randomBytes[j]);
            }

            //System.out.println("Password: "+new String(randomBytes));
            //System.out.println("test: " + String.valueOf(i));
        }
    }

    @Test
    public void testSetMultiplePasswordTest2() throws SecureMemoryPasswordException {
        SecureRandom seed = new SecureRandom();
        byte[] randomBytes = new byte[512];

        seed.nextBytes(randomBytes); // Init seed

        SecureMemoryPassword securePassword = new SecureMemoryPassword();

        for (int i = 0; i < 100; i++) {
            seed.nextBytes(randomBytes);
            securePassword.setPassword(randomBytes);
            // Recupero 10 volte la pwd per testare se il random seed interno rimane costante
            for (int k = 0; k < 10; k++) {
                byte[] passwordRecovered = new byte[securePassword.getLength()];
                securePassword.getPassword(passwordRecovered);
            }

            // Test
            byte[] passwordRecovered = new byte[securePassword.getLength()];
            securePassword.getPassword(passwordRecovered);
            for (int j = 0; j < securePassword.getLength(); j++) {
                assertEquals(passwordRecovered[j], randomBytes[j]);
            }
            System.out.println("test: " + String.valueOf(i));
        }
    }
    
        @Test
    public void testSerialization() {
        try {
            SecureMemoryPassword memoryPassword = new SecureMemoryPassword();
            memoryPassword.setPassword("pippo".getBytes(StandardCharsets.UTF_8));

            File outfile = new File("d:\\temp\\SecureMemoryPassword.txt");
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(outfile))) {
                outputStream.writeObject(memoryPassword);
            } catch (IOException ex) {
                Logger.getLogger(SecureMemoryPasswordTest.class.getName()).log(Level.SEVERE, null, ex);
            }

            SecureMemoryPassword memoryPassword1 = null;
            try {
                try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(outfile))) {
                    memoryPassword1 = (SecureMemoryPassword) inputStream.readObject();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SecureMemoryPasswordTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(SecureMemoryPasswordTest.class.getName()).log(Level.SEVERE, null, ex);
            }

            int length = memoryPassword1.getLength();
            byte[] passByte = new byte[length];
            memoryPassword1.getPassword(passByte);
            System.out.println(String.format("Password: %s  - length %d", new String(passByte), length));

        } catch (SecureMemoryPasswordException ex) {
            Logger.getLogger(SecureMemoryPasswordTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    // Da fare con le refrections 
//    @Test
//    public void testShufflePositions() {
//        SecureMemoryPassword memoryPassword = new SecureMemoryPassword();
//        for (int i = 0; i < 10000; i++) {
//            assertEquals(true, memoryPassword.DEBUGShufflePositions());
//        }
//    }
//
//    //@Test
//    public void testShufflePositionsdDistribution() {
//        SecureMemoryPassword memoryPassword = new SecureMemoryPassword();
//
//        // Tabella delle frequenze
//        int[][] intValues = new int[2048][2048]; // Non parametrico, conosco la dimensione del buffer interno ATTENZIONE!!!
//        int[] valuesTemp;
//        for (int i = 0; i < 100000; i++) {
//            valuesTemp = memoryPassword.DEBUGShufflePositionsDistribution();
//            for (int j = 0; j < valuesTemp.length; j++) {
//                intValues[valuesTemp[j]][j]++;
//            }
//        }
//
//        // Sfrutto il fatto che la matrice è quadrata. ATTENZIONE
//        for (int i = 0; i < intValues.length; i++) {
//            int counterZero = 0;
//            int max = 0;
//            int min = Integer.MAX_VALUE;
//            for (int j = 0; j < intValues.length; j++) {
//                if (intValues[i][j] == 0) {
//                    //System.out.println(String.format("il valore %d non compare nelle seguenti posizioni %d", i,j));
//                    counterZero++;
//                }
//                if (intValues[i][j] > max) {
//                    max = intValues[i][j];
//                }
//                if (intValues[i][j] > 0 && intValues[i][j] < min) {
//                    min = intValues[i][j];
//                }
//                //System.out.print(intValue[j]);
//            }
//            System.out.println(String.format("il valore %d non compare %d volte, max: %d min: %d", i, counterZero, max, min));
//
//        }
//    }


    // Non dovrebbe compilare per cui commento la sezione
//    @Test
//    public void testSubclass(){
//        TestSubClass testSubClass = new TestSubClass();
//        testSubClass.aritest();
//    }
    
    
    
//    /**
//     * Metodo per testare il corretto shuffling dell'array Da spostare nell
//     * classe di test ed usare reflections
//     *
//     * @return true se il test ha avuto successo, false altrimenti
//     */
//    public boolean DEBUGShufflePositions() {
//        shufflePositions();
////        for (int i = 0; i < shufflePositions.length; i++) {
////            System.out.println(String.valueOf(shufflePositions[i]));
////        }
//        int[] valuesTemp = Arrays.copyOf(shufflePositions, shufflePositions.length);
////        valuesTemp[5]=12; // test del test 
////        valuesTemp[51]=12;
//        Arrays.sort(valuesTemp);
//        int precval = valuesTemp[0];
//        // Riordino i valori, se la dimensione è diversa ottengo un'eccezione
//        // se la differenza tra un valore ed il successivo è diverso da 1 allora ho saltato qualcosa e ritorno false
//        for (int i = 1; i < shufflePositions.length; i++) {
//            if ((valuesTemp[i] - precval) != 1) {
//                return false;
//            }
//            precval = valuesTemp[i];
//        }
//        return true;
//    }
//
//    /**
//     *
//     * @return
//     */
//    public int[] DEBUGShufflePositionsDistribution() {
//        shufflePositions();
////        for (int i = 0; i < shufflePositions.length; i++) {
////            System.out.println(String.valueOf(shufflePositions[i]));
////        }
//        int[] valuesTemp = Arrays.copyOf(shufflePositions, shufflePositions.length);
//        return valuesTemp;
//    }

}
