/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import java.util.Arrays;
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

import com.nbis.fluoj.classifier.EFeature;

/**
 *
 * @author airen
 */
@Entity
@Table(name = "FEATURE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Feature.findAll", query = "SELECT f FROM Feature f"),
    @NamedQuery(name = "Feature.findByIdfeature", query = "SELECT f FROM Feature f WHERE f.idfeature = :idfeature"),
    @NamedQuery(name = "Feature.findByFeature", query = "SELECT f FROM Feature f WHERE f.feature = :feature"),
    @NamedQuery(name = "Feature.findByDescription", query = "SELECT f FROM Feature f WHERE f.description = :description")})
public class Feature implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDFEATURE")
    private Integer idfeature;
    @Basic(optional = false)
    @Column(name = "FEATURE")
    private String feature;
    @Column(name = "DESCRIPTION")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feature")
    private List<Ftprobability> ftprobabilityList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feature")
    private List<Samplefeature> samplefeatureList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feature")
    private List<Scellfeature> scellfeatureList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "feature")
    private List<Cellfeature> cellfeatureList;

    public Feature() {
    }

    public Feature(Integer idfeature) {
        this.idfeature = idfeature;
    }

    public Feature(Integer idfeature, String feature) {
        this.idfeature = idfeature;
        this.feature = feature;
    }

    public Integer getIdfeature() {
        return idfeature;
    }

    public void setIdfeature(Integer idfeature) {
        this.idfeature = idfeature;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
  
    @XmlTransient
    public List<Ftprobability> getFtprobabilityList() {
        return ftprobabilityList;
    }

    public void setFtprobabilityList(List<Ftprobability> ftprobabilityList) {
        this.ftprobabilityList = ftprobabilityList;
    }

    @XmlTransient
    public List<Samplefeature> getSamplefeatureList() {
        return samplefeatureList;
    }

    public void setSamplefeatureList(List<Samplefeature> samplefeatureList) {
        this.samplefeatureList = samplefeatureList;
    }

    @XmlTransient
    public List<Scellfeature> getScellfeatureList() {
        return scellfeatureList;
    }

    public void setScellfeatureList(List<Scellfeature> scellfeatureList) {
        this.scellfeatureList = scellfeatureList;
    }

    @XmlTransient
    public List<Cellfeature> getCellfeatureList() {
        return cellfeatureList;
    }

    public void setCellfeatureList(List<Cellfeature> cellfeatureList) {
        this.cellfeatureList = cellfeatureList;
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
        return feature;
    }
    
    public boolean isMixed()
    {
    	  return EFeature.isMixed(idfeature);
    }
    
}
