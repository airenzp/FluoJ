/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author airen
 */
@Entity
@Table(name = "sample")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sample.findAll", query = "SELECT s FROM Sample s"),
    @NamedQuery(name = "Sample.findByIdsample", query = "SELECT s FROM Sample s WHERE s.idsample = :idsample"),
    @NamedQuery(name = "Sample.findByName", query = "SELECT s FROM Sample s WHERE s.name = :name"),
    @NamedQuery(name = "Sample.findByImageThreshold", query = "SELECT s FROM Sample s WHERE s.imageThreshold = :imageThreshold")})
public class Sample implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idsample", nullable = false)
    private Short idsample;
    @Column(name = "name", length = 50)
    private String name;
    @Column(name = "image_threshold")
    private Short imageThreshold;
    @OneToMany(mappedBy = "idsample")
    private List<Filter> filterList;
    @OneToMany(mappedBy = "idsample")
    private List<Image> imageList;
    @OneToMany(mappedBy = "idsample")
    private List<Session> sessionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sample")
    private List<SampleFeature> sampleFeatureList;

    public Sample() {
    }

    public Sample(Short idsample) {
        this.idsample = idsample;
    }

    public Short getIdsample() {
        return idsample;
    }

    public void setIdsample(Short idsample) {
        this.idsample = idsample;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getImageThreshold() {
        return imageThreshold;
    }

    public void setImageThreshold(Short imageThreshold) {
        this.imageThreshold = imageThreshold;
    }

    @XmlTransient
    public List<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

    @XmlTransient
    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

    @XmlTransient
    public List<Session> getSessionList() {
        return sessionList;
    }

    public void setSessionList(List<Session> sessionList) {
        this.sessionList = sessionList;
    }

    @XmlTransient
    public List<SampleFeature> getSampleFeatureList() {
        return sampleFeatureList;
    }

    public void setSampleFeatureList(List<SampleFeature> sampleFeatureList) {
        this.sampleFeatureList = sampleFeatureList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idsample != null ? idsample.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sample)) {
            return false;
        }
        Sample other = (Sample) object;
        if ((this.idsample == null && other.idsample != null) || (this.idsample != null && !this.idsample.equals(other.idsample))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.entities.Sample[ idsample=" + idsample + " ]";
    }
    
}
