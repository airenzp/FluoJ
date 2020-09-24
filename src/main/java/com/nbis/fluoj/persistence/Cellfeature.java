/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "CELLFEATURE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cellfeature.findAll", query = "SELECT c FROM Cellfeature c"),
    @NamedQuery(name = "Cellfeature.findByIdcell", query = "SELECT c FROM Cellfeature c WHERE c.cellfeaturePK.idcell = :idcell"),
    @NamedQuery(name = "Cellfeature.findByIdfeature", query = "SELECT c FROM Cellfeature c WHERE c.cellfeaturePK.idfeature = :idfeature"),
    @NamedQuery(name = "Cellfeature.findByValue", query = "SELECT c FROM Cellfeature c WHERE c.value = :value")})
public class Cellfeature implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CellfeaturePK cellfeaturePK;
    @Basic(optional = false)
    @Column(name = "VALUE")
    private double value;
    @JoinColumn(name = "IDFEATURE", referencedColumnName = "IDFEATURE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Feature feature;
    @JoinColumn(name = "IDCELL", referencedColumnName = "IDCELL", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Cell cell;

    public Cellfeature() {
    }

    public Cellfeature(CellfeaturePK cellfeaturePK) {
        this.cellfeaturePK = cellfeaturePK;
    }

    public Cellfeature(CellfeaturePK cellfeaturePK, double value) {
        this.cellfeaturePK = cellfeaturePK;
        this.value = value;
    }

    public Cellfeature(int idcell, int idfeature) {
        this.cellfeaturePK = new CellfeaturePK(idcell, idfeature);
    }

    public CellfeaturePK getCellfeaturePK() {
        return cellfeaturePK;
    }

    public void setCellfeaturePK(CellfeaturePK cellfeaturePK) {
        this.cellfeaturePK = cellfeaturePK;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cellfeaturePK != null ? cellfeaturePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cellfeature)) {
            return false;
        }
        Cellfeature other = (Cellfeature) object;
        if ((this.cellfeaturePK == null && other.cellfeaturePK != null) || (this.cellfeaturePK != null && !this.cellfeaturePK.equals(other.cellfeaturePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Cellfeature[ cellfeaturePK=" + cellfeaturePK + " ]";
    }
    
}
