/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author airen
 */
@Entity
@Table(name = "session")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Session.findAll", query = "SELECT s FROM Session s"),
    @NamedQuery(name = "Session.findByIdsession", query = "SELECT s FROM Session s WHERE s.idsession = :idsession"),
    @NamedQuery(name = "Session.findByName", query = "SELECT s FROM Session s WHERE s.name = :name"),
    @NamedQuery(name = "Session.findByDate", query = "SELECT s FROM Session s WHERE s.date = :date")})
public class Session implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idsession", nullable = false)
    private Short idsession;
    @Column(name = "name", length = 50)
    private String name;
    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;
    @JoinColumn(name = "idsample", referencedColumnName = "idsample")
    @ManyToOne
    private Sample idsample;

    public Session() {
    }

    public Session(Short idsession) {
        this.idsession = idsession;
    }

    public Short getIdsession() {
        return idsession;
    }

    public void setIdsession(Short idsession) {
        this.idsession = idsession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Sample getIdsample() {
        return idsample;
    }

    public void setIdsample(Sample idsample) {
        this.idsample = idsample;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idsession != null ? idsession.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Session)) {
            return false;
        }
        Session other = (Session) object;
        if ((this.idsession == null && other.idsession != null) || (this.idsession != null && !this.idsession.equals(other.idsession))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.entities.Session[ idsession=" + idsession + " ]";
    }
    
}
