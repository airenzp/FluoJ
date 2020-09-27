/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.entities;

import java.io.Serializable;
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
@Table(name = "cell_feature")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CellFeature.findAll", query = "SELECT c FROM CellFeature c"),
    @NamedQuery(name = "CellFeature.findByIdcell", query = "SELECT c FROM CellFeature c WHERE c.cellFeaturePK.idcell = :idcell"),
    @NamedQuery(name = "CellFeature.findByIdfeature", query = "SELECT c FROM CellFeature c WHERE c.cellFeaturePK.idfeature = :idfeature"),
    @NamedQuery(name = "CellFeature.findByValue", query = "SELECT c FROM CellFeature c WHERE c.value = :value")})
public class CellFeature implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected CellFeaturePK cellFeaturePK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "value", precision = 8, scale = 8)
    private Float value;
    @JoinColumn(name = "idcell", referencedColumnName = "idcell", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Cell cell;
    @JoinColumn(name = "idfeature", referencedColumnName = "idfeature", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Feature feature;

    public CellFeature() {
    }

    public CellFeature(CellFeaturePK cellFeaturePK) {
        this.cellFeaturePK = cellFeaturePK;
    }

    public CellFeature(int idcell, short idfeature) {
        this.cellFeaturePK = new CellFeaturePK(idcell, idfeature);
    }

    public CellFeaturePK getCellFeaturePK() {
        return cellFeaturePK;
    }

    public void setCellFeaturePK(CellFeaturePK cellFeaturePK) {
        this.cellFeaturePK = cellFeaturePK;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cellFeaturePK != null ? cellFeaturePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CellFeature)) {
            return false;
        }
        CellFeature other = (CellFeature) object;
        if ((this.cellFeaturePK == null && other.cellFeaturePK != null) || (this.cellFeaturePK != null && !this.cellFeaturePK.equals(other.cellFeaturePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.entities.CellFeature[ cellFeaturePK=" + cellFeaturePK + " ]";
    }
    
}
