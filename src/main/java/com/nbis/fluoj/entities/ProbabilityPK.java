/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author airen
 */
@Embeddable
public class ProbabilityPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "idcell_type", nullable = false)
    private short idcellType;
    @Basic(optional = false)
    @Column(name = "idfeature", nullable = false)
    private short idfeature;

    public ProbabilityPK() {
    }

    public ProbabilityPK(short idcellType, short idfeature) {
        this.idcellType = idcellType;
        this.idfeature = idfeature;
    }

    public short getIdcellType() {
        return idcellType;
    }

    public void setIdcellType(short idcellType) {
        this.idcellType = idcellType;
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
        hash += (int) idcellType;
        hash += (int) idfeature;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProbabilityPK)) {
            return false;
        }
        ProbabilityPK other = (ProbabilityPK) object;
        if (this.idcellType != other.idcellType) {
            return false;
        }
        if (this.idfeature != other.idfeature) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.entities.ProbabilityPK[ idcellType=" + idcellType + ", idfeature=" + idfeature + " ]";
    }
    
}
