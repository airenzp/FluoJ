/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author airen
 */
@Entity
@Table(name = "SAMPLECOREFEATURE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Samplecorefeature.findAll", query = "SELECT s FROM Samplecorefeature s"),
    @NamedQuery(name = "Samplecorefeature.findByIdsample", query = "SELECT s FROM Samplecorefeature s WHERE s.samplecorefeaturePK.idsample = :idsample"),
    @NamedQuery(name = "Samplecorefeature.findByIdfeature", query = "SELECT s FROM Samplecorefeature s WHERE s.samplecorefeaturePK.idfeature = :idfeature"),
    @NamedQuery(name = "Samplecorefeature.findByMin", query = "SELECT s FROM Samplecorefeature s WHERE s.min = :min"),
    @NamedQuery(name = "Samplecorefeature.findByMax", query = "SELECT s FROM Samplecorefeature s WHERE s.max = :max")})
public class Samplecorefeature implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected SamplecorefeaturePK samplecorefeaturePK;
    @Basic(optional = false)
    @Column(name = "MIN")
    private double min;
    @Basic(optional = false)
    @Column(name = "MAX")
    private double max;
    @JoinColumn(name = "IDSAMPLE", referencedColumnName = "IDSAMPLE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Sample sample;
    @JoinColumn(name = "IDFEATURE", referencedColumnName = "IDFEATURE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Feature feature;

    public Samplecorefeature() {
    }

    public Samplecorefeature(SamplecorefeaturePK samplecorefeaturePK) {
        this.samplecorefeaturePK = samplecorefeaturePK;
    }

    public Samplecorefeature(SamplecorefeaturePK samplecorefeaturePK, double min, double max) {
        this.samplecorefeaturePK = samplecorefeaturePK;
        this.min = min;
        this.max = max;
    }

    public Samplecorefeature(int idsample, int idfeature) {
        this.samplecorefeaturePK = new SamplecorefeaturePK(idsample, idfeature);
    }

    public SamplecorefeaturePK getSamplecorefeaturePK() {
        return samplecorefeaturePK;
    }

    public void setSamplecorefeaturePK(SamplecorefeaturePK samplecorefeaturePK) {
        this.samplecorefeaturePK = samplecorefeaturePK;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (samplecorefeaturePK != null ? samplecorefeaturePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Samplecorefeature)) {
            return false;
        }
        Samplecorefeature other = (Samplecorefeature) object;
        if ((this.samplecorefeaturePK == null && other.samplecorefeaturePK != null) || (this.samplecorefeaturePK != null && !this.samplecorefeaturePK.equals(other.samplecorefeaturePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Samplecorefeature[ samplecorefeaturePK=" + samplecorefeaturePK + " ]";
    }
    
}
