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
public class ScellfeaturePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "IDSCELL")
    private int idscell;
    @Basic(optional = false)
    @Column(name = "IDFEATURE")
    private int idfeature;

    public ScellfeaturePK() {
    }

    public ScellfeaturePK(int idscell, int idfeature) {
        this.idscell = idscell;
        this.idfeature = idfeature;
    }

    public int getIdscell() {
        return idscell;
    }

    public void setIdscell(int idscell) {
        this.idscell = idscell;
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
        hash += (int) idscell;
        hash += (int) idfeature;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ScellfeaturePK)) {
            return false;
        }
        ScellfeaturePK other = (ScellfeaturePK) object;
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
        return "persistence.ScellfeaturePK[ idscell=" + idscell + ", idfeature=" + idfeature + " ]";
    }
    
}
