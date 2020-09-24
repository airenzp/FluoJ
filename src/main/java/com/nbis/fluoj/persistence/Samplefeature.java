/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author airen
 */
@Entity
@Table(name = "SAMPLEFEATURE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Samplefeature.findAll", query = "SELECT s FROM Samplefeature s"),
    @NamedQuery(name = "Samplefeature.findByIdsample", query = "SELECT s FROM Samplefeature s WHERE s.samplefeaturePK.idsample = :idsample"),
    @NamedQuery(name = "Samplefeature.findByIdfeature", query = "SELECT s FROM Samplefeature s WHERE s.samplefeaturePK.idfeature = :idfeature"),
    @NamedQuery(name = "Samplefeature.findByMin", query = "SELECT s FROM Samplefeature s WHERE s.min = :min"),
    @NamedQuery(name = "Samplefeature.findByMax", query = "SELECT s FROM Samplefeature s WHERE s.max = :max"),
	@NamedQuery(name = "Samplefeature.findByUseonclassification", query = "SELECT s FROM Samplefeature s WHERE s.useonclassification = :useonclassification")})
public class Samplefeature implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected SamplefeaturePK samplefeaturePK;
    @Basic(optional = false)
    @Column(name = "MIN")
    private double min;
    @Basic(optional = false)
    @Column(name = "MAX")
    private double max;
    @Column(name = "USEONCLASSIFICATION")
    private int useonclassification;
    @JoinColumn(name = "IDSAMPLE", referencedColumnName = "IDSAMPLE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Sample sample;
    @JoinColumn(name = "IDFEATURE", referencedColumnName = "IDFEATURE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Feature feature;

    public Samplefeature() {
    }

    public Samplefeature(SamplefeaturePK samplefeaturePK) {
        this.samplefeaturePK = samplefeaturePK;
    }

    public Samplefeature(SamplefeaturePK samplefeaturePK, double min, double max) {
        this.samplefeaturePK = samplefeaturePK;
        this.min = min;
        this.max = max;
    }

    public Samplefeature(int idsample, int idfeature) {
        this.samplefeaturePK = new SamplefeaturePK(idsample, idfeature);
    }

    public SamplefeaturePK getSamplefeaturePK() {
        return samplefeaturePK;
    }

    public void setSamplefeaturePK(SamplefeaturePK samplefeaturePK) {
        this.samplefeaturePK = samplefeaturePK;
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

    public int getUseonclassification() {
        return useonclassification;
    }

    public void setUseonclassification(int useonclassification) {
        this.useonclassification = useonclassification;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (samplefeaturePK != null ? samplefeaturePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Samplefeature)) {
            return false;
        }
        Samplefeature other = (Samplefeature) object;
        if ((this.samplefeaturePK == null && other.samplefeaturePK != null) || (this.samplefeaturePK != null && !this.samplefeaturePK.equals(other.samplefeaturePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Samplefeature[ samplefeaturePK=" + samplefeaturePK + " ]";
    }
    
}
