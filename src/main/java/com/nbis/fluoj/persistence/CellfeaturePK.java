/*
 * To change this template, choose Tools | Templates
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
public class CellfeaturePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "IDCELL")
    private int idcell;
    @Basic(optional = false)
    @Column(name = "IDFEATURE")
    private int idfeature;

    public CellfeaturePK() {
    }

    public CellfeaturePK(int idcell, int idfeature) {
        this.idcell = idcell;
        this.idfeature = idfeature;
    }

    public int getIdcell() {
        return idcell;
    }

    public void setIdcell(int idcell) {
        this.idcell = idcell;
    }

    public int getIdfeature() {
        return idfeature;
    }

    public void setIdfeature(int idfeature) {
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
        if (!(object instanceof CellfeaturePK)) {
            return false;
        }
        CellfeaturePK other = (CellfeaturePK) object;
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
        return "persistence.CellfeaturePK[ idcell=" + idcell + ", idfeature=" + idfeature + " ]";
    }
    
}
