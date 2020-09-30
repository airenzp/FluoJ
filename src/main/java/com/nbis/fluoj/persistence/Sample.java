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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author airen
 */
@Entity
@Table(name = "sample", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"idtype"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sample.findAll", query = "SELECT s FROM Sample s"),
    @NamedQuery(name = "Sample.findByIdsample", query = "SELECT s FROM Sample s WHERE s.idsample = :idsample"),
    @NamedQuery(name = "Sample.findByName", query = "SELECT s FROM Sample s WHERE s.name = :name"),
    @NamedQuery(name = "Sample.findByImageThreshold", query = "SELECT s FROM Sample s WHERE s.imageThreshold = :imageThreshold"),
    @NamedQuery(name = "Sample.findByRoisThreshold", query = "SELECT s FROM Sample s WHERE s.roisThreshold = :roisThreshold"),
    @NamedQuery(name = "Sample.findByFillHoles", query = "SELECT s FROM Sample s WHERE s.fillHoles = :fillHoles"),
    @NamedQuery(name = "Sample.findByExpansionRadius", query = "SELECT s FROM Sample s WHERE s.expansionRadius = :expansionRadius")})
public class Sample implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idsample", nullable = false)
    private Short idsample;
    @Column(name = "name", length = 50)
    private String name;
    @Column(name = "image_threshold")
    private Short imageThreshold;
    @Column(name = "rois_threshold")
    private Short roisThreshold;
    @Column(name = "fill_holes")
    private Boolean fillHoles;
    @Column(name = "expansion_radius")
    private Short expansionRadius;
    @OneToMany(mappedBy = "idsample")
    private List<Session> sessionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sample")
    private List<SampleFeature> sampleFeatureList;
    @OneToMany(mappedBy = "idsample")
    private List<Type> typeList;
    @JoinColumn(name = "idimage", referencedColumnName = "idimage")
    @ManyToOne
    private SampleImage idimage;
    @JoinColumn(name = "idseparation", referencedColumnName = "idseparation")
    @ManyToOne
    private Separation idseparation;
    @JoinColumn(name = "idsession", referencedColumnName = "idsession")
    @ManyToOne
    private Session idsession;
    @JoinColumn(name = "idtype", referencedColumnName = "idtype", nullable = false)
    @OneToOne(optional = false)
    private Type idtype;
    @OneToMany(mappedBy = "idsample")
    private List<Filter> filterList;
    @OneToMany(mappedBy = "idsample")
    private List<SampleImage> sampleImageList;

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

    public Short getRoisThreshold() {
        return roisThreshold;
    }

    public void setRoisThreshold(Short roisThreshold) {
        this.roisThreshold = roisThreshold;
    }

    public Boolean getFillHoles() {
        return fillHoles;
    }

    public void setFillHoles(Boolean fillHoles) {
        this.fillHoles = fillHoles;
    }

    public Short getExpansionRadius() {
        return expansionRadius;
    }

    public void setExpansionRadius(Short expansionRadius) {
        this.expansionRadius = expansionRadius;
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

    @XmlTransient
    public List<Type> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<Type> typeList) {
        this.typeList = typeList;
    }

    public SampleImage getIdimage() {
        return idimage;
    }

    public void setIdimage(SampleImage idimage) {
        this.idimage = idimage;
    }

    public Separation getIdseparation() {
        return idseparation;
    }

    public void setIdseparation(Separation idseparation) {
        this.idseparation = idseparation;
    }

    public Session getIdsession() {
        return idsession;
    }

    public void setIdsession(Session idsession) {
        this.idsession = idsession;
    }

    public Type getIdtype() {
        return idtype;
    }

    public void setIdtype(Type idtype) {
        this.idtype = idtype;
    }

    @XmlTransient
    public List<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

    @XmlTransient
    public List<SampleImage> getSampleImageList() {
        return sampleImageList;
    }

    public void setSampleImageList(List<SampleImage> sampleImageList) {
        this.sampleImageList = sampleImageList;
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
        return "com.nbis.fluoj.persistence.Sample[ idsample=" + idsample + " ]";
    }
    
}
