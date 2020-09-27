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
public class ScellFeaturePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "idscell", nullable = false)
    private int idscell;
    @Basic(optional = false)
    @Column(name = "idfeature", nullable = false)
    private short idfeature;

    public ScellFeaturePK() {
    }

    public ScellFeaturePK(int idscell, short idfeature) {
        this.idscell = idscell;
        this.idfeature = idfeature;
    }

    public int getIdscell() {
        return idscell;
    }

    public void setIdscell(int idscell) {
        this.idscell = idscell;
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
        hash += (int) idscell;
        hash += (int) idfeature;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ScellFeaturePK)) {
            return false;
        }
        ScellFeaturePK other = (ScellFeaturePK) object;
        if (this.idscell != other.idscell) {
            return false;
        }
        if (this.idfeature != other.idfeature) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.entities.ScellFeaturePK[ idscell=" + idscell + ", idfeature=" + idfeature + " ]";
    }
    
}
