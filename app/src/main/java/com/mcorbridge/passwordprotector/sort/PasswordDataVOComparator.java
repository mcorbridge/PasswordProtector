package com.mcorbridge.passwordprotector.sort;

import com.mcorbridge.passwordprotector.vo.PasswordDataVO;

import java.util.Comparator;

/**
 * Created by Mike on 2/11/2015.
 * copyright Michael D. Corbridge
 */
public class PasswordDataVOComparator implements Comparator<PasswordDataVO> {


    public int compare(PasswordDataVO left, PasswordDataVO right) {
        return left.getTitle().compareTo(right.getTitle());
    }

}
