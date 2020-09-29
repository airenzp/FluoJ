/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author airen
 */
@Embeddable
public class CellFeaturePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "idcell", nullable = false)
    private int idcell;
    @Basic(optional = false)
    @Column(name = "idfeature", nullable = false)
    private short idfeature;

    public CellFeaturePK() {
    }

    public CellFeaturePK(int idcell, short idfeature) {
        this.idcell = idcell;
        this.idfeature = idfeature;
    }

    public int getIdcell() {
        return idcell;
    }

    public void setIdcell(int idcell) {
        this.idcell = idcell;
    }

    public short getIdfeature() {
        return idfeature;
    }

    public void setIdfeature(short idfeature) {
        this.idfeature = idfeature;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idcell;
        hash += (int) idfeature;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CellFeaturePK)) {
            return false;
        }
        CellFeaturePK other = (CellFeaturePK) object;
        if (this.idcell != other.idcell) {
            return false;
        }
        if (this.idfeature != other.idfeature) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.persistence.CellFeaturePK[ idcell=" + idcell + ", idfeature=" + idfeature + " ]";
    }
    
}
