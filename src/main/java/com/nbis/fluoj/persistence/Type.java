/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author airen
 */
@Entity
@Table(name = "TYPE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Type.findAll", query = "SELECT t FROM Type t"),
    @NamedQuery(name = "Type.findByIdtype", query = "SELECT t FROM Type t WHERE t.idtype = :idtype"),
    @NamedQuery(name = "Type.findByName", query = "SELECT t FROM Type t WHERE t.name = :name"),
    @NamedQuery(name = "Type.findByLabel", query = "SELECT t FROM Type t WHERE t.label = :label"),
    @NamedQuery(name = "Type.findByColor", query = "SELECT t FROM Type t WHERE t.color = :color"),
    @NamedQuery(name = "Type.findByTrainingmin", query = "SELECT t FROM Type t WHERE t.trainingmin = :trainingmin")})
public class Type implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDTYPE")
    private Integer idtype;
    @Basic(optional = false)
    @Column(name = "NAME")
    private String name;
    @Column(name = "LABEL")
    private String label;
    @Column(name = "COLOR")
    private Integer color;
    @Basic(optional = false)
    @Column(name = "TRAININGMIN")
    private int trainingmin;
    @OneToMany(mappedBy = "type")
    private List<Sample> sampleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "type")
    private List<Ftprobability> ftprobabilityList;
    @OneToMany(mappedBy = "type")
    private List<Cell> cellList;
    @OneToMany(mappedBy = "type")
    private List<Scell> scellList;
    @OneToMany(mappedBy = "type1")
    private List<Scell> scellList1;
    @JoinColumn(name = "IDSAMPLE", referencedColumnName = "IDSAMPLE")
    @ManyToOne(optional = false)
    private Sample sample;

    public Type() {
    }

    public Type(Integer idtype) {
        this.idtype = idtype;
    }

    public Type(Integer idtype, String name) {
        this.idtype = idtype;
        this.name = name;
    }

    public Integer getIdtype() {
        return idtype;
    }

    public void setIdtype(Integer idtype) {
        this.idtype = idtype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    @XmlTransient
    public List<Sample> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<Sample> sampleList) {
        this.sampleList = sampleList;
    }

    @XmlTransient
    public List<Ftprobability> getFtprobabilityList() {
        return ftprobabilityList;
    }

    public void setFtprobabilityList(List<Ftprobability> ftprobabilityList) {
        this.ftprobabilityList = ftprobabilityList;
    }

    @XmlTransient
    public List<Cell> getCellList() {
        return cellList;
    }

    public void setCellList(List<Cell> cellList) {
        this.cellList = cellList;
    }

    @XmlTransient
    public List<Scell> getScellList() {
        return scellList;
    }

    public void setScellList(List<Scell> scellList) {
        this.scellList = scellList;
    }

    @XmlTransient
    public List<Scell> getScellList1() {
        return scellList1;
    }

    public void setScellList1(List<Scell> scellList1) {
        this.scellList1 = scellList1;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }
    
    public int getTrainingmin() {
        return trainingmin;
    }

    public void setTrainingmin(int trainingmin) {
        this.trainingmin = trainingmin;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idtype != null ? idtype.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Type)) {
            return false;
        }
        Type other = (Type) object;
        if ((this.idtype == null && other.idtype != null) || (this.idtype != null && !this.idtype.equals(other.idtype))) {
            return false;
        }
        return true;
    }

    
    @Override
    public String toString() {
        return name;
    }
    
}
