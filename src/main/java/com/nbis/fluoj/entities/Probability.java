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
@Table(name = "probability")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Probability.findAll", query = "SELECT p FROM Probability p"),
    @NamedQuery(name = "Probability.findByIdcellType", query = "SELECT p FROM Probability p WHERE p.probabilityPK.idcellType = :idcellType"),
    @NamedQuery(name = "Probability.findByIdfeature", query = "SELECT p FROM Probability p WHERE p.probabilityPK.idfeature = :idfeature"),
    @NamedQuery(name = "Probability.findByX", query = "SELECT p FROM Probability p WHERE p.x = :x"),
    @NamedQuery(name = "Probability.findByFrequence", query = "SELECT p FROM Probability p WHERE p.frequence = :frequence"),
    @NamedQuery(name = "Probability.findByProbability", query = "SELECT p FROM Probability p WHERE p.probability = :probability")})
public class Probability implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ProbabilityPK probabilityPK;
    @Column(name = "x")
    private Short x;
    @Column(name = "frequence")
    private Integer frequence;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "probability", precision = 8, scale = 8)
    private Float probability;
    @JoinColumn(name = "idcell_type", referencedColumnName = "idcell_type", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private CellType cellType;
    @JoinColumn(name = "idfeature", referencedColumnName = "idfeature", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Feature feature;

    public Probability() {
    }

    public Probability(ProbabilityPK probabilityPK) {
        this.probabilityPK = probabilityPK;
    }

    public Probability(short idcellType, short idfeature) {
        this.probabilityPK = new ProbabilityPK(idcellType, idfeature);
    }

    public ProbabilityPK getProbabilityPK() {
        return probabilityPK;
    }

    public void setProbabilityPK(ProbabilityPK probabilityPK) {
        this.probabilityPK = probabilityPK;
    }

    public Short getX() {
        return x;
    }

    public void setX(Short x) {
        this.x = x;
    }

    public Integer getFrequence() {
        return frequence;
    }

    public void setFrequence(Integer frequence) {
        this.frequence = frequence;
    }

    public Float getProbability() {
        return probability;
    }

    public void setProbability(Float probability) {
        this.probability = probability;
    }

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
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
        hash += (probabilityPK != null ? probabilityPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Probability)) {
            return false;
        }
        Probability other = (Probability) object;
        if ((this.probabilityPK == null && other.probabilityPK != null) || (this.probabilityPK != null && !this.probabilityPK.equals(other.probabilityPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.entities.Probability[ probabilityPK=" + probabilityPK + " ]";
    }
    
}
