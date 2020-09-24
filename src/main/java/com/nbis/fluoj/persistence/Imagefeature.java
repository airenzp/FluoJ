/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

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
@Table(name = "IMAGEFEATURE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Imagefeature.findAll", query = "SELECT i FROM Imagefeature i"),
    @NamedQuery(name = "Imagefeature.findByIdimage", query = "SELECT i FROM Imagefeature i WHERE i.imagefeaturePK.idimage = :idimage"),
    @NamedQuery(name = "Imagefeature.findByIdfeature", query = "SELECT i FROM Imagefeature i WHERE i.imagefeaturePK.idfeature = :idfeature"),
    @NamedQuery(name = "Imagefeature.findByMin", query = "SELECT i FROM Imagefeature i WHERE i.min = :min"),
    @NamedQuery(name = "Imagefeature.findByMax", query = "SELECT i FROM Imagefeature i WHERE i.max = :max")})
public class Imagefeature implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ImagefeaturePK imagefeaturePK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "MIN")
    private Double min;
    @Column(name = "MAX")
    private Double max;
    @JoinColumn(name = "IDIMAGE", referencedColumnName = "IDIMAGE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Imageresource imageresource;
    @JoinColumn(name = "IDFEATURE", referencedColumnName = "IDFEATURE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Feature feature;

    public Imagefeature() {
    }

    public Imagefeature(ImagefeaturePK imagefeaturePK) {
        this.imagefeaturePK = imagefeaturePK;
    }

    public Imagefeature(int idimage, int idfeature) {
        this.imagefeaturePK = new ImagefeaturePK(idimage, idfeature);
    }

    public ImagefeaturePK getImagefeaturePK() {
        return imagefeaturePK;
    }

    public void setImagefeaturePK(ImagefeaturePK imagefeaturePK) {
        this.imagefeaturePK = imagefeaturePK;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Imageresource getImageresource() {
        return imageresource;
    }

    public void setImageresource(Imageresource imageresource) {
        this.imageresource = imageresource;
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
        hash += (imagefeaturePK != null ? imagefeaturePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Imagefeature)) {
            return false;
        }
        Imagefeature other = (Imagefeature) object;
        if ((this.imagefeaturePK == null && other.imagefeaturePK != null) || (this.imagefeaturePK != null && !this.imagefeaturePK.equals(other.imagefeaturePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Imagefeature[ imagefeaturePK=" + imagefeaturePK + " ]";
    }
    
}
