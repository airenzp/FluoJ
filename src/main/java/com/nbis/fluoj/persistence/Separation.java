/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
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
@Table(name = "separation")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Separation.findAll", query = "SELECT s FROM Separation s"),
    @NamedQuery(name = "Separation.findByIdseparation", query = "SELECT s FROM Separation s WHERE s.idseparation = :idseparation"),
    @NamedQuery(name = "Separation.findByName", query = "SELECT s FROM Separation s WHERE s.name = :name")})
public class Separation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idseparation", nullable = false)
    private Short idseparation;
    @Column(name = "name", length = 50)
    private String name;
    @OneToMany(mappedBy = "idseparation")
    private List<Sample> sampleList;

    public Separation() {
    }

    public Separation(Short idseparation) {
        this.idseparation = idseparation;
    }

    public Short getIdseparation() {
        return idseparation;
    }

    public void setIdseparation(Short idseparation) {
        this.idseparation = idseparation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public List<Sample> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<Sample> sampleList) {
        this.sampleList = sampleList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idseparation != null ? idseparation.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Separation)) {
            return false;
        }
        Separation other = (Separation) object;
        if ((this.idseparation == null && other.idseparation != null) || (this.idseparation != null && !this.idseparation.equals(other.idseparation))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.persistence.Separation[ idseparation=" + idseparation + " ]";
    }
    
}
