package com.mcorbridge.passwordprotector.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Mike on 1/26/2015.
 * copyright Michael D. Corbridge
 */
public class ChangeListVO  implements Serializable {


    private Long id;

    private Date dateOfChange;

    private List<Long> listIDsChanged;

    private boolean isChangeListImplemented;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateOfChange() {
        return dateOfChange;
    }

    public void setDateOfChange(Date dateOfChange) {
        this.dateOfChange = dateOfChange;
    }

    public List<Long> getListIDsChanged() {
        return listIDsChanged;
    }

    public void setListIDsChanged(List<Long> listIDsChanged) {
        this.listIDsChanged = listIDsChanged;
    }

    public boolean isChangeListImplemented() {
        return isChangeListImplemented;
    }

    public void setChangeListImplemented(boolean isChangeListImplemented) {
        this.isChangeListImplemented = isChangeListImplemented;
    }
}
