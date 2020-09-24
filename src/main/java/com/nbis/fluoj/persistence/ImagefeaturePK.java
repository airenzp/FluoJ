/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author airen
 */
@Embeddable
public class ImagefeaturePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "IDIMAGE")
    private int idimage;
    @Basic(optional = false)
    @Column(name = "IDFEATURE")
    private int idfeature;

    public ImagefeaturePK() {
    }

    public ImagefeaturePK(int idimage, int idfeature) {
        this.idimage = idimage;
        this.idfeature = idfeature;
    }

    public int getIdimage() {
        return idimage;
    }

    public void setIdimage(int idimage) {
        this.idimage = idimage;
    }

    public int getIdfeature() {
        return idfeature;
    }

    public void setIdfeature(int idfeature) {
        this.idfeature = idfeature;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idimage;
        hash += (int) idfeature;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ImagefeaturePK)) {
            return false;
        }
        ImagefeaturePK other = (ImagefeaturePK) object;
        if (this.idimage != other.idimage) {
            return false;
        }
        if (this.idfeature != other.idfeature) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.ImagefeaturePK[ idimage=" + idimage + ", idfeature=" + idfeature + " ]";
    }
    
}
