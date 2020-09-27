/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "scell")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Scell.findAll", query = "SELECT s FROM Scell s"),
    @NamedQuery(name = "Scell.findByIdscell", query = "SELECT s FROM Scell s WHERE s.idscell = :idscell"),
    @NamedQuery(name = "Scell.findByX", query = "SELECT s FROM Scell s WHERE s.x = :x"),
    @NamedQuery(name = "Scell.findByY", query = "SELECT s FROM Scell s WHERE s.y = :y")})
public class Scell implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idscell", nullable = false)
    private Integer idscell;
    @Column(name = "x")
    private Short x;
    @Column(name = "y")
    private Short y;
    @JoinColumn(name = "idimage", referencedColumnName = "idimage")
    @ManyToOne
    private Image idimage;

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

    public Image getIdimage() {
        return idimage;
    }

    public void setIdimage(Image idimage) {
        this.idimage = idimage;
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
        return "com.nbis.fluoj.entities.Scell[ idscell=" + idscell + " ]";
    }
    
}
