package com.mcorbridge.passwordprotector.model;

/**
 * Created by Mike on 1/12/2015.
 * copyright Michael D. Corbridge
 */
public class ApplicationModel {
    // Private constructor. Prevents instantiation from other classes.
    private ApplicationModel() { }


    /**
     * Initializes singleton.
     *
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        private static final ApplicationModel INSTANCE = new ApplicationModel();
    }

    public static ApplicationModel getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // ----------------------------------------------------------

    public static final String APPLICATION_SECRET_KEY = "bostonBruins";

    // ----------------------------------------------------------

    private String visualCipherKey;

    public String getVisualCipherKey() {
        return visualCipherKey;
    }

    public void setVisualCipherKey(String visualCipherKey) {
        this.visualCipherKey = visualCipherKey;
    }

    // ----------------------------------------------------------

    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    // ----------------------------------------------------------

    private String cipher;

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    // ----------------------------------------------------------

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ----------------------------------------------------------

    private boolean isDataConnected;

    public boolean getIsDataConnected() {
        return isDataConnected;
    }

    public void setIsDataConnected(boolean isDataConnected) {
        this.isDataConnected = isDataConnected;
    }

    // ----------------------------------------------------------
}
