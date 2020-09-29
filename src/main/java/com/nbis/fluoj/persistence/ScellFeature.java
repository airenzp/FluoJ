/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

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
@Table(name = "scell_feature")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ScellFeature.findAll", query = "SELECT s FROM ScellFeature s"),
    @NamedQuery(name = "ScellFeature.findByIdscell", query = "SELECT s FROM ScellFeature s WHERE s.scellFeaturePK.idscell = :idscell"),
    @NamedQuery(name = "ScellFeature.findByIdfeature", query = "SELECT s FROM ScellFeature s WHERE s.scellFeaturePK.idfeature = :idfeature"),
    @NamedQuery(name = "ScellFeature.findByValue", query = "SELECT s FROM ScellFeature s WHERE s.value = :value")})
public class ScellFeature implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ScellFeaturePK scellFeaturePK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "value", precision = 17, scale = 17)
    private Double value;
    @JoinColumn(name = "idfeature", referencedColumnName = "idfeature", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Feature feature;
    @JoinColumn(name = "idscell", referencedColumnName = "idscell", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Scell scell;

    public ScellFeature() {
    }

    public ScellFeature(ScellFeaturePK scellFeaturePK) {
        this.scellFeaturePK = scellFeaturePK;
    }

    public ScellFeature(int idscell, short idfeature) {
        this.scellFeaturePK = new ScellFeaturePK(idscell, idfeature);
    }

    public ScellFeaturePK getScellFeaturePK() {
        return scellFeaturePK;
    }

    public void setScellFeaturePK(ScellFeaturePK scellFeaturePK) {
        this.scellFeaturePK = scellFeaturePK;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Scell getScell() {
        return scell;
    }

    public void setScell(Scell scell) {
        this.scell = scell;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (scellFeaturePK != null ? scellFeaturePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ScellFeature)) {
            return false;
        }
        ScellFeature other = (ScellFeature) object;
        if ((this.scellFeaturePK == null && other.scellFeaturePK != null) || (this.scellFeaturePK != null && !this.scellFeaturePK.equals(other.scellFeaturePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.persistence.ScellFeature[ scellFeaturePK=" + scellFeaturePK + " ]";
    }
    
}
