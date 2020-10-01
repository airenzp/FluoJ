/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "feature")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Feature.findAll", query = "SELECT f FROM Feature f"),
    @NamedQuery(name = "Feature.findByIdfeature", query = "SELECT f FROM Feature f WHERE f.idfeature = :idfeature"),
    @NamedQuery(name = "Feature.findByName", query = "SELECT f FROM Feature f WHERE f.name = :name"),
    @NamedQuery(name = "Feature.findByDescription", query = "SELECT f FROM Feature f WHERE f.description = :description"),
    @NamedQuery(name = "Feature.findByRoi", query = "SELECT f FROM Feature f WHERE f.roi = :roi")})
public class Feature implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idfeature", nullable = false)
    private Short idfeature;
    @Basic(optional = false)
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Column(name = "description", length = 500)
    private String description;
    @Column(name = "roi")
    private Boolean roi;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feature")
    private List<CellFeature> cellFeatureList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feature")
    private List<Probability> probabilityList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feature")
    private List<SampleFeature> sampleFeatureList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feature")
    private List<ScellFeature> scellFeatureList;

    public Feature() {
    }

    public Feature(Short idfeature) {
        this.idfeature = idfeature;
    }

    public Feature(Short idfeature, String name) {
        this.idfeature = idfeature;
        this.name = name;
    }

    public Short getIdfeature() {
        return idfeature;
    }

    public void setIdfeature(Short idfeature) {
        this.idfeature = idfeature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRoi() {
        return roi;
    }

    public void setRoi(Boolean roi) {
        this.roi = roi;
    }

    @XmlTransient
    public List<CellFeature> getCellFeatureList() {
        return cellFeatureList;
    }

    public void setCellFeatureList(List<CellFeature> cellFeatureList) {
        this.cellFeatureList = cellFeatureList;
    }

    @XmlTransient
    public List<Probability> getProbabilityList() {
        return probabilityList;
    }

    public void setProbabilityList(List<Probability> probabilityList) {
        this.probabilityList = probabilityList;
    }

    @XmlTransient
    public List<SampleFeature> getSampleFeatureList() {
        return sampleFeatureList;
    }

    public void setSampleFeatureList(List<SampleFeature> sampleFeatureList) {
        this.sampleFeatureList = sampleFeatureList;
    }

    @XmlTransient
    public List<ScellFeature> getScellFeatureList() {
        return scellFeatureList;
    }

    public void setScellFeatureList(List<ScellFeature> scellFeatureList) {
        this.scellFeatureList = scellFeatureList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idfeature != null ? idfeature.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Feature)) {
            return false;
        }
        Feature other = (Feature) object;
        if ((this.idfeature == null && other.idfeature != null) || (this.idfeature != null && !this.idfeature.equals(other.idfeature))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
