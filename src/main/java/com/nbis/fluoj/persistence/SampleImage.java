/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import com.nbis.fluoj.classifier.ConfigurationDB;
import ij.ImagePlus;
import java.io.File;
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
@Table(name = "sample_image")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SampleImage.findAll", query = "SELECT s FROM SampleImage s"),
    @NamedQuery(name = "SampleImage.findByIdimage", query = "SELECT s FROM SampleImage s WHERE s.idimage = :idimage"),
    @NamedQuery(name = "SampleImage.findByDate", query = "SELECT s FROM SampleImage s WHERE s.date = :date"),
    @NamedQuery(name = "SampleImage.findByName", query = "SELECT s FROM SampleImage s WHERE s.name = :name")})
public class SampleImage implements Serializable {

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
    @OneToMany(mappedBy = "idimage")
    private List<Cell> cellList;
    @OneToMany(mappedBy = "idimage")
    private List<Sample> sampleList;
    @JoinColumn(name = "idsample", referencedColumnName = "idsample")
    @ManyToOne
    private Sample idsample;
    @OneToMany(mappedBy = "idimage")
    private List<Scell> scellList;

    public SampleImage() {
    }

    public SampleImage(Integer idimage) {
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

    @XmlTransient
    public List<Cell> getCellList() {
        return cellList;
    }

    public void setCellList(List<Cell> cellList) {
        this.cellList = cellList;
    }

    @XmlTransient
    public List<Sample> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<Sample> sampleList) {
        this.sampleList = sampleList;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idimage != null ? idimage.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SampleImage)) {
            return false;
        }
        SampleImage other = (SampleImage) object;
        if ((this.idimage == null && other.idimage != null) || (this.idimage != null && !this.idimage.equals(other.idimage))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SampleImage[ idimage=" + idimage + " ]";
    }
    
    public ImagePlus getImagePlus() {
        if (idimage == null) {
            return null;
        }
        return new ImagePlus(getPath());
    }

    public String getPath() {
        return ConfigurationDB.imagesdir + File.separator + idimage + ".tif";

    }

    
}
