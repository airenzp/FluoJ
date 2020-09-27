/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
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
@Table(name = "image")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Image.findAll", query = "SELECT i FROM Image i"),
    @NamedQuery(name = "Image.findByIdimage", query = "SELECT i FROM Image i WHERE i.idimage = :idimage"),
    @NamedQuery(name = "Image.findByDate", query = "SELECT i FROM Image i WHERE i.date = :date"),
    @NamedQuery(name = "Image.findByName", query = "SELECT i FROM Image i WHERE i.name = :name")})
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idimage", nullable = false)
    private Integer idimage;
    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;
    @Column(name = "name", length = 50)
    private String name;
    @JoinColumn(name = "idsample", referencedColumnName = "idsample")
    @ManyToOne
    private Sample idsample;
    @OneToMany(mappedBy = "idimage")
    private List<Scell> scellList;
    @OneToMany(mappedBy = "idimage")
    private List<Cell> cellList;

    public Image() {
    }

    public Image(Integer idimage) {
        this.idimage = idimage;
    }

    public Integer getIdimage() {
        return idimage;
    }

    public void setIdimage(Integer idimage) {
        this.idimage = idimage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sample getIdsample() {
        return idsample;
    }

    public void setIdsample(Sample idsample) {
        this.idsample = idsample;
    }

    @XmlTransient
    public List<Scell> getScellList() {
        return scellList;
    }

    public void setScellList(List<Scell> scellList) {
        this.scellList = scellList;
    }

    @XmlTransient
    public List<Cell> getCellList() {
        return cellList;
    }

    public void setCellList(List<Cell> cellList) {
        this.cellList = cellList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idimage != null ? idimage.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Image)) {
            return false;
        }
        Image other = (Image) object;
        if ((this.idimage == null && other.idimage != null) || (this.idimage != null && !this.idimage.equals(other.idimage))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.entities.Image[ idimage=" + idimage + " ]";
    }
    
}
