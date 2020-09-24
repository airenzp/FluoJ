/*
 * To change this template, choose Tools | Templates
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
@Table(name = "CELL")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cell.findAll", query = "SELECT c FROM Cell c"),
    @NamedQuery(name = "Cell.findByIdcell", query = "SELECT c FROM Cell c WHERE c.idcell = :idcell"),
    @NamedQuery(name = "Cell.findByXPosition", query = "SELECT c FROM Cell c WHERE c.xPosition = :xPosition"),
    @NamedQuery(name = "Cell.findByYPosition", query = "SELECT c FROM Cell c WHERE c.yPosition = :yPosition"),
    @NamedQuery(name = "Cell.findByDate", query = "SELECT c FROM Cell c WHERE c.date = :date")})
public class Cell implements Serializable {
    @Column(name =     "DATE")
    @Temporal(TemporalType.DATE)
    private Date date;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDCELL")
    private Integer idcell;
    @Column(name = "X_POSITION")
    private Integer xPosition;
    @Column(name = "Y_POSITION")
    private Integer yPosition;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cell")
    private List<Cellfeature> cellfeatureList;
    @JoinColumn(name = "CLASS", referencedColumnName = "IDTYPE")
    @ManyToOne
    private Type type;
    @JoinColumn(name = "IDSAMPLE", referencedColumnName = "IDSAMPLE")
    @ManyToOne(optional = false)
    private Sample sample;
    @JoinColumn(name = "IDIMAGE", referencedColumnName = "IDIMAGE")
    @ManyToOne
    private Imageresource imageresource;

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

    public Integer getXPosition() {
        return xPosition;
    }

    public void setXPosition(Integer xPosition) {
        this.xPosition = xPosition;
    }

    public Integer getYPosition() {
        return yPosition;
    }

    public void setYPosition(Integer yPosition) {
        this.yPosition = yPosition;
    }

    @XmlTransient
    public List<Cellfeature> getCellfeatureList() {
        return cellfeatureList;
    }

    public void setCellfeatureList(List<Cellfeature> cellfeatureList) {
        this.cellfeatureList = cellfeatureList;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Imageresource getImageresource() {
        return imageresource;
    }

    public void setImageresource(Imageresource imageresource) {
        this.imageresource = imageresource;
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
        return "persistence.Cell[ idcell=" + idcell + " ]";
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
}
