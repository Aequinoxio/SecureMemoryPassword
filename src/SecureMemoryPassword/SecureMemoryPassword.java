/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SecureMemoryPassword;

import java.io.ObjectStreamField;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Classe per memorizzare una password in modo offuscato
 * Non sottoclassabile per evitare di far accedere a metodi protetti o privati
 * @author utente
 */
public final class SecureMemoryPassword {

    /**
     * Massima lunghezza per la password memorizzabile 
     */
    private transient static final int MaxPasswordLength = 1024; // Massima lunghezza password memorizzabile

    /**
     * Max internal buffer length
     */
    private transient static final int MAX_BUFFER_LENGTH = 2 * MaxPasswordLength; // Massima lunghezza password memorizzabile
    
    // Per sicurezza rimuovo tutti i campi dagli oggetti serializzabili
    // Per ulteriore sicurezza fare l'overloading anche dei metodi writeobject ecc.
    private static final ObjectStreamField[] serialPersistentFields=null;
    
    private transient final byte[] password;              // buffer dove memorizzerò la password offuscata. la memorizzazione avverrà in un punto casuale al suo interno
    private transient volatile int passwordLength = 0;    // lunghezza pwd
    private transient volatile int passwordStart;         // Posizione nel buffer dove parte la password memorizzata

    private transient final SecureRandom secureRandom;    // Random object per ottenere i valori casuali
    private transient final byte[] randomBuffer;          // Buffer con i valori casuali che userò per offuscare la password
    private transient final int[] shufflePositions;       // Posizioni randomiche dove memorizerò li bytes della password nel passwordbuffer
    
    /**
     * Costruttore Genero un secure random che servirà per le operazioni di
     * offuscamento
     */
    public SecureMemoryPassword() {
        this.password = new byte[MAX_BUFFER_LENGTH];
        this.randomBuffer = new byte[MAX_BUFFER_LENGTH];
        this.shufflePositions = new int[MAX_BUFFER_LENGTH];
        secureRandom = new SecureRandom(); // Seed needed?

        // Genero un certo numero di bytes casuali e poi azzero tutto, in questo modo sono sicuro di aver inizializzato
        // l'oggetto seed. Non genero subito il random seed in quanto, aspettando, l'entropia del SecureRandom dovrebbe incrementare 
        byte[] temp = new byte[MAX_BUFFER_LENGTH];
        secureRandom.nextBytes(temp);
        for (int i = 0; i < temp.length; i++) {
            temp[i] = 0x00;
        }
    }

    /**
     * Ritorna la lunghezza massima della password memorizzabile
     *
     * @return Lunghezza massima della password che può essere memorizzata
     */
    public int getMaxPasswordLength() {
        return MaxPasswordLength;
    }

    /**
     * Ritorna la lunghezza della password memorizzata
     *
     * @return lunghezza della password memorizzata
     */
    public int getLength() {
        return passwordLength;
    }

    /**
     * Restituisce una copia in chiaro della password memorizzata copiandola
     * nell'array passato come parametro
     *
     * @param passwordExternal Array dove verrà copiata la password in chiaro
     * @throws SecureMemoryPasswordException Lanciata se l'array passato non è
     * sufficientemente capiente o se non è stata ancora impostata alcuna
     * password
     * @throws NullPointerException Lanciata se l'array passato è null
     */
    public void getPassword(byte[] passwordExternal) throws SecureMemoryPasswordException {
        checkInternalConsistence(passwordExternal, true);

        // Copia la password in chiaro nell'array passato che deve essere già allocato per la dimensione della password memorizzata
        int index = 0;
        for (int i = 0; i < passwordLength; i++) {
            index = shufflePositions[i + passwordStart];
            passwordExternal[i] = (byte) (this.password[index] ^ randomBuffer[index]);
        }
    }


    /**
     * Imposta una nuova password da memorizzare annullando la precedente.
     * Rigenero il buffer casuale per memorizzarla
     *
     * @param password Array di byte che verranno memorizzati
     * @throws SecureMemoryPasswordException Lanciata se la password passata è
     * troppo lunga
     * @throws NullPointerException Lanciata se l'array della password non è
     * allocato
     *
     */
    public void setPassword(byte[] password) throws SecureMemoryPasswordException, NullPointerException {
        checkInternalConsistence(password, false);

        // Ogni volta che memorizzo una password randomizzo i buffer e la posizione
        // Per sicurezza azzero tutto prima di rigenerare i valori casuali
        for (int i = 0; i < MAX_BUFFER_LENGTH; i++) {
            this.randomBuffer[i] = 0x00;
            this.password[i] = 0x00;
            this.shufflePositions[i] = 0;
        }

        // Imposto i buffer randomici
        secureRandom.nextBytes(this.randomBuffer);
        secureRandom.nextBytes(this.password);

        // Randomizzo il punto da dove parto per prendere le posizioni casuali dello shufflebuffer 
        // per  salvataggio della password nel password buffer
        // Genero una posizione casuale tra 0 e MaxPasswordLength e la memorizzo
        shufflePositions();
        
        // Offusco e disperdo la password
        // Prendo un punto a caso nello shuffle buffer
        passwordStart = secureRandom.nextInt(MAX_BUFFER_LENGTH - password.length - 1);
        
        int index = 0;
        for (int i = 0; i < password.length; i++) {
            index = shufflePositions[i + passwordStart]; // Indice casuale dove mettere un byte della password
            this.password[index] = (byte) (password[i] ^ randomBuffer[index]);
        }

        this.passwordLength = password.length;
    }

        private void checkInternalConsistence(byte[] passwordExternal, boolean getOperation) throws SecureMemoryPasswordException {
        if (passwordExternal == null) {
            throw new NullPointerException("The container cannot be null.");
        }

        if (passwordLength == 0 && getOperation) {
            throw new SecureMemoryPasswordException("Password not yet set.");
        }

        if (passwordExternal.length != this.passwordLength && getOperation) {
            throw new SecureMemoryPasswordException("Different container length. Cannot store password in provided container.");
        }

        if (passwordExternal.length > MaxPasswordLength && !getOperation) {
            throw new SecureMemoryPasswordException("Password too long, cannot store.");
        }
    }

    /**
     * Randomizza il buffer delle posizioni in cui mettere i valori offuscati della password
     */
    private void shufflePositions() {

        // Inizializzo con il progressivo delle posizioni
        for (int i = 0; i < shufflePositions.length; i++) {
            shufflePositions[i] = i;
        }

        int lastIndex ;
        int tempIndex = 0;
        int tempVal = 0;

//        // Tecnica 1 - Knuth (gli indici da scambiare sono scelti dall'inizio in un insieme decrescente in cardinalità)
//        int lastIndex = shufflePositions.length;
//        for (int i = 0; i < shufflePositions.length-1; i++) {
//            tempIndex = i+secureRandom.nextInt(lastIndex); // Randomizzo le posizioni del random buffer da cui prendo i valori casuali
//            tempVal=shufflePositions[tempIndex];
//            shufflePositions[tempIndex]=shufflePositions[i];
//            shufflePositions[i] = tempVal;            
//            lastIndex--;
//        }        
        
        //Tecnica 2 - Knuth GAB Modification (gli indici casuali sono scelti dal fondo in un insieme decrescente in cardinalità)
        lastIndex = shufflePositions.length-1;
        for (int i = 0; i < shufflePositions.length - 1; i++) {
            tempIndex = secureRandom.nextInt(lastIndex); // Randomizzo le posizioni del random buffer da cui prendo i valori casuali
            tempVal = shufflePositions[tempIndex];
            shufflePositions[tempIndex] = shufflePositions[lastIndex];
            shufflePositions[lastIndex] = tempVal;
            lastIndex--;
        }
        
//        // Tecnica 3 - Algoritmo standard delle collections
//        Integer[]tempArray = new Integer[MaxBufferLength];
//        for (int i=0;i<MaxBufferLength;i++){
//            tempArray[i]=i;
//            
//        }
//        List<Integer> tempList = Arrays.asList(tempArray);
//
//        Collections.shuffle(tempList, secureRandom);
//        tempArray= (Integer[]) tempList.toArray();
//        for (int i=0;i<shufflePositions.length;i++){
//            shufflePositions[i]=tempArray[i];
//        }
    }
}
