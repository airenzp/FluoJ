/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "FTPROBABILITY")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ftprobability.findAll", query = "SELECT f FROM Ftprobability f"),
    @NamedQuery(name = "Ftprobability.findByIdprobability", query = "SELECT f FROM Ftprobability f WHERE f.idprobability = :idprobability"),
    @NamedQuery(name = "Ftprobability.findByProbability", query = "SELECT f FROM Ftprobability f WHERE f.probability = :probability"),
    @NamedQuery(name = "Ftprobability.findByFrequence", query = "SELECT f FROM Ftprobability f WHERE f.frequence = :frequence"),
    @NamedQuery(name = "Ftprobability.findByX", query = "SELECT f FROM Ftprobability f WHERE f.x = :x")})
public class Ftprobability implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDPROBABILITY")
    private Integer idprobability;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "PROBABILITY")
    private Double probability;
    @Column(name = "FREQUENCE")
    private Integer frequence;
    @Column(name = "X")
    private Integer x;
    @JoinColumn(name = "IDTYPE", referencedColumnName = "IDTYPE")
    @ManyToOne(optional = false)
    private Type type;
    @JoinColumn(name = "IDFEATURE", referencedColumnName = "IDFEATURE")
    @ManyToOne(optional = false)
    private Feature feature;

    public Ftprobability() {
    }

    public Ftprobability(Integer idprobability) {
        this.idprobability = idprobability;
    }

    public Integer getIdprobability() {
        return idprobability;
    }

    public void setIdprobability(Integer idprobability) {
        this.idprobability = idprobability;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public Integer getFrequence() {
        return frequence;
    }

    public void setFrequence(Integer frequence) {
        this.frequence = frequence;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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
        hash += (idprobability != null ? idprobability.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ftprobability)) {
            return false;
        }
        Ftprobability other = (Ftprobability) object;
        if ((this.idprobability == null && other.idprobability != null) || (this.idprobability != null && !this.idprobability.equals(other.idprobability))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Ftprobability[ idprobability=" + idprobability + " ]";
    }
    
}
