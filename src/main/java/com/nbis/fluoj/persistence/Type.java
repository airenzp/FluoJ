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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author airen
 */
@Entity
@Table(name = "type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Type.findAll", query = "SELECT t FROM Type t"),
    @NamedQuery(name = "Type.findByIdtype", query = "SELECT t FROM Type t WHERE t.idtype = :idtype"),
    @NamedQuery(name = "Type.findByName", query = "SELECT t FROM Type t WHERE t.name = :name"),
    @NamedQuery(name = "Type.findByLabel", query = "SELECT t FROM Type t WHERE t.label = :label"),
    @NamedQuery(name = "Type.findByTrainingMin", query = "SELECT t FROM Type t WHERE t.trainingMin = :trainingMin"),
    @NamedQuery(name = "Type.findByColor", query = "SELECT t FROM Type t WHERE t.color = :color")})
public class Type implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idtype", nullable = false)
    private Short idtype;
    @Column(name = "name", length = 50)
    private String name;
    @Column(name = "label", length = 50)
    private String label;
    @Column(name = "training_min")
    private Integer trainingMin;
    @Column(name = "color")
    private Integer color;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "type")
    private List<Probability> probabilityList;
    @OneToMany(mappedBy = "idtype")
    private List<Cell> cellList;
    @JoinColumn(name = "idsample", referencedColumnName = "idsample")
    @ManyToOne
    private Sample idsample;
    @OneToOne(mappedBy = "idtype")
    private Sample sample;
    @OneToMany(mappedBy = "idtype")
    private List<Scell> scellList;
    @OneToMany(mappedBy = "winner")
    private List<Scell> scellList1;

    public Type() {
    }

    public Type(Short idtype) {
        this.idtype = idtype;
    }

    public Short getIdtype() {
        return idtype;
    }

    public void setIdtype(Short idtype) {
        this.idtype = idtype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getTrainingMin() {
        return trainingMin;
    }

    public void setTrainingMin(Integer trainingMin) {
        this.trainingMin = trainingMin;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    @XmlTransient
    public List<Probability> getProbabilityList() {
        return probabilityList;
    }

    public void setProbabilityList(List<Probability> probabilityList) {
        this.probabilityList = probabilityList;
    }

    @XmlTransient
    public List<Cell> getCellList() {
        return cellList;
    }

    public void setCellList(List<Cell> cellList) {
        this.cellList = cellList;
    }

    public Sample getIdsample() {
        return idsample;
    }

    public void setIdsample(Sample idsample) {
        this.idsample = idsample;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    @XmlTransient
    public List<Scell> getScellList() {
        return scellList;
    }

    public void setScellList(List<Scell> scellList) {
        this.scellList = scellList;
    }

    @XmlTransient
    public List<Scell> getScellList1() {
        return scellList1;
    }

    public void setScellList1(List<Scell> scellList1) {
        this.scellList1 = scellList1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idtype != null ? idtype.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Type)) {
            return false;
        }
        Type other = (Type) object;
        if ((this.idtype == null && other.idtype != null) || (this.idtype != null && !this.idtype.equals(other.idtype))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.persistence.Type[ idtype=" + idtype + " ]";
    }
    
}
