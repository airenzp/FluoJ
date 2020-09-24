/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 *
 * @author airen
 */
@Embeddable
public class SamplefilterPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "IDSAMPLE")
    private int idsample;
    @Basic(optional = false)
    @Column(name = "IDFILTER")
    private int idfilter;

    public SamplefilterPK() {
    }

    public SamplefilterPK(int idsample, int idfilter) {
        this.idsample = idsample;
        this.idfilter = idfilter;
    }

    public int getIdsample() {
        return idsample;
    }

    public void setIdsample(int idsample) {
        this.idsample = idsample;
    }

    public int getIdfilter() {
        return idfilter;
    }

    public void setIdfilter(int idfilter) {
        this.idfilter = idfilter;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idsample;
        hash += (int) idfilter;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SamplefilterPK)) {
            return false;
        }
        SamplefilterPK other = (SamplefilterPK) object;
        if (this.idsample != other.idsample) {
            return false;
        }
        if (this.idfilter != other.idfilter) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.SamplefilterPK[ idsample=" + idsample + ", idfilter=" + idfilter + " ]";
    }
    
}
