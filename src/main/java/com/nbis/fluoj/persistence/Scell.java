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
@Table(name = "SCELL")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Scell.findAll", query = "SELECT s FROM Scell s"),
    @NamedQuery(name = "Scell.findByIdscell", query = "SELECT s FROM Scell s WHERE s.idscell = :idscell"),
    @NamedQuery(name = "Scell.findByXPosition", query = "SELECT s FROM Scell s WHERE s.xPosition = :xPosition"),
    @NamedQuery(name = "Scell.findByYPosition", query = "SELECT s FROM Scell s WHERE s.yPosition = :yPosition"),
    @NamedQuery(name = "Scell.findByDate", query = "SELECT s FROM Scell s WHERE s.date = :date"),
    @NamedQuery(name = "Scell.findByValid", query = "SELECT s FROM Scell s WHERE s.valid = :valid")})
public class Scell implements Serializable {
    @Column(name =     "DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDSCELL")
    private Integer idscell;
    @Column(name = "X_POSITION")
    private Integer xPosition;
    @Column(name = "Y_POSITION")
    private Integer yPosition;
    @Column(name = "VALID")
    private Integer valid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "scell")
    private List<Scellfeature> scellfeatureList;
    @JoinColumn(name = "CLASS", referencedColumnName = "IDTYPE")
    @ManyToOne
    private Type type;
    @JoinColumn(name = "WINNER", referencedColumnName = "IDTYPE")
    @ManyToOne
    private Type type1;
    @JoinColumn(name = "IDSESSION", referencedColumnName = "IDSESSION")
    @ManyToOne
    private Session session;
    @JoinColumn(name = "IDIMAGE", referencedColumnName = "IDIMAGE")
    @ManyToOne
    private Imageresource imageresource;

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

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    @XmlTransient
    public List<Scellfeature> getScellfeatureList() {
        return scellfeatureList;
    }

    public void setScellfeatureList(List<Scellfeature> scellfeatureList) {
        this.scellfeatureList = scellfeatureList;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType1() {
        return type1;
    }

    public void setType1(Type type1) {
        this.type1 = type1;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
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
        return "persistence.Scell[ idscell=" + idscell + " ]";
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
}
