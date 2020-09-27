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
@Table(name = "cell")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cell.findAll", query = "SELECT c FROM Cell c"),
    @NamedQuery(name = "Cell.findByIdcell", query = "SELECT c FROM Cell c WHERE c.idcell = :idcell"),
    @NamedQuery(name = "Cell.findByX", query = "SELECT c FROM Cell c WHERE c.x = :x"),
    @NamedQuery(name = "Cell.findByY", query = "SELECT c FROM Cell c WHERE c.y = :y"),
    @NamedQuery(name = "Cell.findByDate", query = "SELECT c FROM Cell c WHERE c.date = :date")})
public class Cell implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idcell", nullable = false)
    private Integer idcell;
    @Column(name = "x")
    private Short x;
    @Column(name = "y")
    private Short y;
    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cell")
    private List<CellFeature> cellFeatureList;
    @JoinColumn(name = "idimage", referencedColumnName = "idimage")
    @ManyToOne
    private Image idimage;

    public Cell() {
    }

    public Cell(Integer idcell) {
        this.idcell = idcell;
    }

    public Integer getIdcell() {
        return idcell;
    }

    public void setIdcell(Integer idcell) {
        this.idcell = idcell;
    }

    public Short getX() {
        return x;
    }

    public void setX(Short x) {
        this.x = x;
    }

    public Short getY() {
        return y;
    }

    public void setY(Short y) {
        this.y = y;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @XmlTransient
    public List<CellFeature> getCellFeatureList() {
        return cellFeatureList;
    }

    public void setCellFeatureList(List<CellFeature> cellFeatureList) {
        this.cellFeatureList = cellFeatureList;
    }

    public Image getIdimage() {
        return idimage;
    }

    public void setIdimage(Image idimage) {
        this.idimage = idimage;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idcell != null ? idcell.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cell)) {
            return false;
        }
        Cell other = (Cell) object;
        if ((this.idcell == null && other.idcell != null) || (this.idcell != null && !this.idcell.equals(other.idcell))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.entities.Cell[ idcell=" + idcell + " ]";
    }
    
}
