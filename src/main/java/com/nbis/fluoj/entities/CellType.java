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
@Table(name = "cell_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CellType.findAll", query = "SELECT c FROM CellType c"),
    @NamedQuery(name = "CellType.findByIdcellType", query = "SELECT c FROM CellType c WHERE c.idcellType = :idcellType"),
    @NamedQuery(name = "CellType.findByName", query = "SELECT c FROM CellType c WHERE c.name = :name"),
    @NamedQuery(name = "CellType.findByLabel", query = "SELECT c FROM CellType c WHERE c.label = :label"),
    @NamedQuery(name = "CellType.findByColor", query = "SELECT c FROM CellType c WHERE c.color = :color"),
    @NamedQuery(name = "CellType.findByTrainingMin", query = "SELECT c FROM CellType c WHERE c.trainingMin = :trainingMin")})
public class CellType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idcell_type", nullable = false)
    private Short idcellType;
    @Column(name = "name", length = 50)
    private String name;
    @Column(name = "label", length = 50)
    private String label;
    @Column(name = "color", length = 50)
    private String color;
    @Column(name = "training_min")
    private Short trainingMin;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cellType")
    private List<Probability> probabilityList;

    public CellType() {
    }

    public CellType(Short idcellType) {
        this.idcellType = idcellType;
    }

    public Short getIdcellType() {
        return idcellType;
    }

    public void setIdcellType(Short idcellType) {
        this.idcellType = idcellType;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Short getTrainingMin() {
        return trainingMin;
    }

    public void setTrainingMin(Short trainingMin) {
        this.trainingMin = trainingMin;
    }

    @XmlTransient
    public List<Probability> getProbabilityList() {
        return probabilityList;
    }

    public void setProbabilityList(List<Probability> probabilityList) {
        this.probabilityList = probabilityList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcellType != null ? idcellType.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CellType)) {
            return false;
        }
        CellType other = (CellType) object;
        if ((this.idcellType == null && other.idcellType != null) || (this.idcellType != null && !this.idcellType.equals(other.idcellType))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.entities.CellType[ idcellType=" + idcellType + " ]";
    }
    
}
