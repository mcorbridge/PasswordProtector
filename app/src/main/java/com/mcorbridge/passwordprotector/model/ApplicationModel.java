package com.mcorbridge.passwordprotector.model;

import com.mcorbridge.passwordprotector.vo.PasswordDataVO;
import com.mcorbridge.passwordprotector.vo.SettingsVO;

import java.util.ArrayList;
import java.util.Date;

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

    private boolean requestLocalDatabaseRebuild;

    public boolean isRequestLocalDatabaseRebuild() {
        return requestLocalDatabaseRebuild;
    }

    public void setRequestLocalDatabaseRebuild(boolean requestLocalDatabaseRebuild) {
        this.requestLocalDatabaseRebuild = requestLocalDatabaseRebuild;
    }

    // ----------------------------------------------------------
    private ArrayList<PasswordDataVO> decipheredPasswordDataVOs;

    public ArrayList<PasswordDataVO> getDecipheredPasswordDataVOs() {
        return decipheredPasswordDataVOs;
    }

    public void setDecipheredPasswordDataVOs(ArrayList<PasswordDataVO> decipheredPasswordDataVOs) {
        this.decipheredPasswordDataVOs = decipheredPasswordDataVOs;
    }

    // ----------------------------------------------------------
    private int incorrectDecipherAttempts;

    public int getIncorrectDecipherAttempts() {
        return incorrectDecipherAttempts;
    }

    public void setIncorrectDecipherAttempts(int incorrectDecipherAttempts) {
        this.incorrectDecipherAttempts = incorrectDecipherAttempts;
    }

    // ----------------------------------------------------------
    private Date lockoutDate;

    public Date getLockoutDate() {
        return lockoutDate;
    }

    public void setLockoutDate(Date lockoutDate) {
        this.lockoutDate = lockoutDate;
    }

    // ----------------------------------------------------------
    private boolean isNewUser;

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean isNewUser) {
        this.isNewUser = isNewUser;
    }

    // ----------------------------------------------------------
    private boolean isDevMode;

    public boolean isDevMode() {
        return isDevMode;
    }

    public void setDevMode(boolean isDevMode) {
        this.isDevMode = isDevMode;
    }

    // ----------------------------------------------------------
    private boolean isTimeoutAware;

    public boolean isTimeoutAware() {
        return isTimeoutAware;
    }

    public void setTimeoutAware(boolean isTimeoutAware) {
        this.isTimeoutAware = isTimeoutAware;
    }

    // ----------------------------------------------------------
    private SettingsVO settingsVO = new SettingsVO();

    public SettingsVO getSettingsVO() {
        return settingsVO;
    }

    public void setSettingsVO(SettingsVO settingsVO) {
        this.settingsVO = settingsVO;
    }
}
