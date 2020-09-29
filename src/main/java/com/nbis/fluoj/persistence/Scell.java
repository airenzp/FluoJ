/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "scell")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Scell.findAll", query = "SELECT s FROM Scell s"),
    @NamedQuery(name = "Scell.findByIdscell", query = "SELECT s FROM Scell s WHERE s.idscell = :idscell"),
    @NamedQuery(name = "Scell.findByX", query = "SELECT s FROM Scell s WHERE s.x = :x"),
    @NamedQuery(name = "Scell.findByY", query = "SELECT s FROM Scell s WHERE s.y = :y"),
    @NamedQuery(name = "Scell.findByDate", query = "SELECT s FROM Scell s WHERE s.date = :date")})
public class Scell implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idscell", nullable = false)
    private Integer idscell;
    @Column(name = "x")
    private Integer x;
    @Column(name = "y")
    private Integer y;
    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;
    @JoinColumn(name = "idimage", referencedColumnName = "idimage")
    @ManyToOne
    private SampleImage idimage;
    @JoinColumn(name = "idsession", referencedColumnName = "idsession")
    @ManyToOne
    private Session idsession;
    @JoinColumn(name = "idtype", referencedColumnName = "idtype")
    @ManyToOne
    private Type idtype;
    @JoinColumn(name = "winner", referencedColumnName = "idtype")
    @ManyToOne
    private Type winner;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "scell")
    private List<ScellFeature> scellFeatureList;

    public Scell() {
    }

    public Scell(Integer idscell) {
        this.idscell = idscell;
    }

    public Integer getIdscell() {
        return idscell;
    }

    public void setIdscell(Integer idscell) {
        this.idscell = idscell;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public SampleImage getIdimage() {
        return idimage;
    }

    public void setIdimage(SampleImage idimage) {
        this.idimage = idimage;
    }

    public Session getIdsession() {
        return idsession;
    }

    public void setIdsession(Session idsession) {
        this.idsession = idsession;
    }

    public Type getIdtype() {
        return idtype;
    }

    public void setIdtype(Type idtype) {
        this.idtype = idtype;
    }

    public Type getWinner() {
        return winner;
    }

    public void setWinner(Type winner) {
        this.winner = winner;
    }

    @XmlTransient
    public List<ScellFeature> getScellFeatureList() {
        return scellFeatureList;
    }

    public void setScellFeatureList(List<ScellFeature> scellFeatureList) {
        this.scellFeatureList = scellFeatureList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idscell != null ? idscell.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Scell)) {
            return false;
        }
        Scell other = (Scell) object;
        if ((this.idscell == null && other.idscell != null) || (this.idscell != null && !this.idscell.equals(other.idscell))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.persistence.Scell[ idscell=" + idscell + " ]";
    }
    
}
