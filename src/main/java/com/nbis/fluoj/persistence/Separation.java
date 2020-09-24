/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author airen
 */
@Entity
@Table(name = "SEPARATION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Separation.findAll", query = "SELECT s FROM Separation s"),
    @NamedQuery(name = "Separation.findByIdseparation", query = "SELECT s FROM Separation s WHERE s.idseparation = :idseparation"),
    @NamedQuery(name = "Separation.findByName", query = "SELECT s FROM Separation s WHERE s.name = :name")})
public class Separation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "IDSEPARATION")
    private Integer idseparation;
    @Basic(optional = false)
    @Column(name = "NAME")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "separation")
    private List<Sample> sampleList;

    public Separation() {
    }

    public Separation(Integer idseparation) {
        this.idseparation = idseparation;
    }

    public Separation(Integer idseparation, String name) {
        this.idseparation = idseparation;
        this.name = name;
    }

    public Integer getIdseparation() {
        return idseparation;
    }

    public void setIdseparation(Integer idseparation) {
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
        return name;
    }
    
}
