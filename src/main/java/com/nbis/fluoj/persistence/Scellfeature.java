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
@Table(name = "SCELLFEATURE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Scellfeature.findAll", query = "SELECT s FROM Scellfeature s"),
    @NamedQuery(name = "Scellfeature.findByIdscell", query = "SELECT s FROM Scellfeature s WHERE s.scellfeaturePK.idscell = :idscell"),
    @NamedQuery(name = "Scellfeature.findByIdfeature", query = "SELECT s FROM Scellfeature s WHERE s.scellfeaturePK.idfeature = :idfeature"),
    @NamedQuery(name = "Scellfeature.findByValue", query = "SELECT s FROM Scellfeature s WHERE s.value = :value")})
public class Scellfeature implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ScellfeaturePK scellfeaturePK;
    @Basic(optional = false)
    @Column(name = "VALUE")
    private double value;
    @JoinColumn(name = "IDSCELL", referencedColumnName = "IDSCELL", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Scell scell;
    @JoinColumn(name = "IDFEATURE", referencedColumnName = "IDFEATURE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Feature feature;

    public Scellfeature() {
    }

    public Scellfeature(ScellfeaturePK scellfeaturePK) {
        this.scellfeaturePK = scellfeaturePK;
    }

    public Scellfeature(ScellfeaturePK scellfeaturePK, double value) {
        this.scellfeaturePK = scellfeaturePK;
        this.value = value;
    }

    public Scellfeature(int idscell, int idfeature) {
        this.scellfeaturePK = new ScellfeaturePK(idscell, idfeature);
    }

    public ScellfeaturePK getScellfeaturePK() {
        return scellfeaturePK;
    }

    public void setScellfeaturePK(ScellfeaturePK scellfeaturePK) {
        this.scellfeaturePK = scellfeaturePK;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Scell getScell() {
        return scell;
    }

    public void setScell(Scell scell) {
        this.scell = scell;
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
        hash += (scellfeaturePK != null ? scellfeaturePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Scellfeature)) {
            return false;
        }
        Scellfeature other = (Scellfeature) object;
        if ((this.scellfeaturePK == null && other.scellfeaturePK != null) || (this.scellfeaturePK != null && !this.scellfeaturePK.equals(other.scellfeaturePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Scellfeature[ scellfeaturePK=" + scellfeaturePK + " ]";
    }
    
}
