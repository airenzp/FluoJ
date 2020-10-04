/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
@Table(name = "probability")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Probability.findAll", query = "SELECT p FROM Probability p"),
    @NamedQuery(name = "Probability.findByX", query = "SELECT p FROM Probability p WHERE p.x = :x"),
    @NamedQuery(name = "Probability.findByFrequence", query = "SELECT p FROM Probability p WHERE p.frequence = :frequence"),
    @NamedQuery(name = "Probability.findByProbability", query = "SELECT p FROM Probability p WHERE p.probability = :probability"),
    @NamedQuery(name = "Probability.findByIdprobability", query = "SELECT p FROM Probability p WHERE p.idprobability = :idprobability")})
public class Probability implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "x")
    private Integer x;
    @Column(name = "frequence")
    private Integer frequence;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "probability", precision = 17, scale = 17)
    private Double probability;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idprobability", nullable = false)
    private Integer idprobability;
    @JoinColumn(name = "idfeature", referencedColumnName = "idfeature", nullable = false)
    @ManyToOne(optional = false)
    private Feature idfeature;
    @JoinColumn(name = "idtype", referencedColumnName = "idtype", nullable = false)
    @ManyToOne(optional = false)
    private Type idtype;

    public Probability() {
    }

    public Probability(Integer idprobability) {
        this.idprobability = idprobability;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getFrequence() {
        return frequence;
    }

    public void setFrequence(Integer frequence) {
        this.frequence = frequence;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public Integer getIdprobability() {
        return idprobability;
    }

    public void setIdprobability(Integer idprobability) {
        this.idprobability = idprobability;
    }

    public Feature getIdfeature() {
        return idfeature;
    }

    public void setIdfeature(Feature idfeature) {
        this.idfeature = idfeature;
    }

    public Type getIdtype() {
        return idtype;
    }

    public void setIdtype(Type idtype) {
        this.idtype = idtype;
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
        if (!(object instanceof Probability)) {
            return false;
        }
        Probability other = (Probability) object;
        if ((this.idprobability == null && other.idprobability != null) || (this.idprobability != null && !this.idprobability.equals(other.idprobability))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.persistence.Probability[ idprobability=" + idprobability + " ]";
    }
    
}
