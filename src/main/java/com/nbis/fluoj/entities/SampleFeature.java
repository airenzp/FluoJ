/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.entities;

import java.io.Serializable;
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
@Table(name = "sample_feature")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SampleFeature.findAll", query = "SELECT s FROM SampleFeature s"),
    @NamedQuery(name = "SampleFeature.findByIdsample", query = "SELECT s FROM SampleFeature s WHERE s.sampleFeaturePK.idsample = :idsample"),
    @NamedQuery(name = "SampleFeature.findByIdfeature", query = "SELECT s FROM SampleFeature s WHERE s.sampleFeaturePK.idfeature = :idfeature"),
    @NamedQuery(name = "SampleFeature.findByMin", query = "SELECT s FROM SampleFeature s WHERE s.min = :min"),
    @NamedQuery(name = "SampleFeature.findByMax", query = "SELECT s FROM SampleFeature s WHERE s.max = :max"),
    @NamedQuery(name = "SampleFeature.findByActive", query = "SELECT s FROM SampleFeature s WHERE s.active = :active"),
    @NamedQuery(name = "SampleFeature.findByInternal", query = "SELECT s FROM SampleFeature s WHERE s.internal = :internal")})
public class SampleFeature implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected SampleFeaturePK sampleFeaturePK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "min", precision = 8, scale = 8)
    private Float min;
    @Column(name = "max", precision = 8, scale = 8)
    private Float max;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "internal")
    private Boolean internal;
    @JoinColumn(name = "idfeature", referencedColumnName = "idfeature", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Feature feature;
    @JoinColumn(name = "idsample", referencedColumnName = "idsample", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Sample sample;

    public SampleFeature() {
    }

    public SampleFeature(SampleFeaturePK sampleFeaturePK) {
        this.sampleFeaturePK = sampleFeaturePK;
    }

    public SampleFeature(short idsample, short idfeature) {
        this.sampleFeaturePK = new SampleFeaturePK(idsample, idfeature);
    }

    public SampleFeaturePK getSampleFeaturePK() {
        return sampleFeaturePK;
    }

    public void setSampleFeaturePK(SampleFeaturePK sampleFeaturePK) {
        this.sampleFeaturePK = sampleFeaturePK;
    }

    public Float getMin() {
        return min;
    }

    public void setMin(Float min) {
        this.min = min;
    }

    public Float getMax() {
        return max;
    }

    public void setMax(Float max) {
        this.max = max;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sampleFeaturePK != null ? sampleFeaturePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SampleFeature)) {
            return false;
        }
        SampleFeature other = (SampleFeature) object;
        if ((this.sampleFeaturePK == null && other.sampleFeaturePK != null) || (this.sampleFeaturePK != null && !this.sampleFeaturePK.equals(other.sampleFeaturePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.entities.SampleFeature[ sampleFeaturePK=" + sampleFeaturePK + " ]";
    }
    
}
