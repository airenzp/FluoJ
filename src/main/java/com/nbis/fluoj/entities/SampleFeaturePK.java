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
public class SampleFeaturePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "idsample", nullable = false)
    private short idsample;
    @Basic(optional = false)
    @Column(name = "idfeature", nullable = false)
    private short idfeature;

    public SampleFeaturePK() {
    }

    public SampleFeaturePK(short idsample, short idfeature) {
        this.idsample = idsample;
        this.idfeature = idfeature;
    }

    public short getIdsample() {
        return idsample;
    }

    public void setIdsample(short idsample) {
        this.idsample = idsample;
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
        hash += (int) idsample;
        hash += (int) idfeature;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SampleFeaturePK)) {
            return false;
        }
        SampleFeaturePK other = (SampleFeaturePK) object;
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
        return "com.nbis.fluoj.entities.SampleFeaturePK[ idsample=" + idsample + ", idfeature=" + idfeature + " ]";
    }
    
}
