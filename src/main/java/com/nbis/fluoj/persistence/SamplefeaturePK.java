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
public class SamplefeaturePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "IDSAMPLE")
    private int idsample;
    @Basic(optional = false)
    @Column(name = "IDFEATURE")
    private int idfeature;

    public SamplefeaturePK() {
    }

    public SamplefeaturePK(int idsample, int idfeature) {
        this.idsample = idsample;
        this.idfeature = idfeature;
    }

    public int getIdsample() {
        return idsample;
    }

    public void setIdsample(int idsample) {
        this.idsample = idsample;
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
        hash += (int) idsample;
        hash += (int) idfeature;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SamplefeaturePK)) {
            return false;
        }
        SamplefeaturePK other = (SamplefeaturePK) object;
        if (this.idsample != other.idsample) {
            return false;
        }
        if (this.idfeature != other.idfeature) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.SamplefeaturePK[ idsample=" + idsample + ", idfeature=" + idfeature + " ]";
    }
    
}
