package com.mcorbridge.passwordprotector.interfaces;

/**
 * Created by Mike on 1/21/2015.
 * copyright Michael D. Corbridge
 */
public interface IPasswordActivity {

    void processResults(String results);

    void signOut();

    void showTimeoutWarning();

}
