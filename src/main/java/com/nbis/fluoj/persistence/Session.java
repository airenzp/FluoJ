/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author airen
 */
@Entity
@Table(name = "SESSION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Session.findAll", query = "SELECT s FROM Session s"),
    @NamedQuery(name = "Session.findByIdsession", query = "SELECT s FROM Session s WHERE s.idsession = :idsession"),
    @NamedQuery(name = "Session.findByDate", query = "SELECT s FROM Session s WHERE s.date = :date"),
    @NamedQuery(name = "Session.findByWell", query = "SELECT s FROM Session s WHERE s.well = :well"),
    @NamedQuery(name = "Session.findByName", query = "SELECT s FROM Session s WHERE s.name = :name")})
public class Session implements Serializable {
    @Column(name =     "DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDSESSION")
    private Integer idsession;
    @Column(name = "WELL")
    private String well;
    @Column(name = "NAME")
    private String name;
    @OneToMany(mappedBy = "session")
    private List<Sample> sampleList;
    @OneToMany(mappedBy = "session")
    private List<Scell> scellList;
    @JoinColumn(name = "IDSAMPLE", referencedColumnName = "IDSAMPLE")
    @ManyToOne
    private Sample sample;

    public Session() {
    }

    public Session(Integer idsession) {
        this.idsession = idsession;
    }

    public Integer getIdsession() {
        return idsession;
    }

    public void setIdsession(Integer idsession) {
        this.idsession = idsession;
    }

    public String getWell() {
        return well;
    }

    public void setWell(String well) {
        this.well = well;
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

    @XmlTransient
    public List<Scell> getScellList() {
        return scellList;
    }

    public void setScellList(List<Scell> scellList) {
        this.scellList = scellList;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
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
	if(idsession == null)
	    return name;
        return idsession.toString();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
}
