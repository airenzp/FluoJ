/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import ij.ImagePlus;

import java.awt.Image;
import java.io.File;
import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.nbis.fluoj.classifier.ConfigurationDB;

@Entity
@Table(name = "SAMPLE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sample.findAll", query = "SELECT s FROM Sample s"),
    @NamedQuery(name = "Sample.findByIdsample", query = "SELECT s FROM Sample s WHERE s.idsample = :idsample"),
    @NamedQuery(name = "Sample.findByName", query = "SELECT s FROM Sample s WHERE s.name = :name"),
    @NamedQuery(name = "Sample.findByThreshold", query = "SELECT s FROM Sample s WHERE s.threshold = :threshold"),
    @NamedQuery(name = "Sample.findByRoismax", query = "SELECT s FROM Sample s WHERE s.roismax = :roismax"),
	@NamedQuery(name = "Sample.findByFillholes", query = "SELECT s FROM Sample s WHERE s.fillholes = :fillholes"),
	@NamedQuery(name = "Sample.findByRoisthreshold", query = "SELECT s FROM Sample s WHERE s.roisthreshold = :roisthreshold")})
public class Sample implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDSAMPLE")
    private Integer idsample;
    @Column(name = "NAME")
    private String name;
    @Basic(optional = false)
    @Column(name = "THRESHOLD")
    private int threshold;
    @JoinColumn(name = "SEPARATION", referencedColumnName = "IDSEPARATION")
    @ManyToOne(optional = false)
    private Separation separation;
    @Column(name = "ROISMax")
    private Integer roismax;
    @Basic(optional = false)
    @Column(name = "FILLHOLES")
    private int fillholes;
    @Column(name = "EXPANSIONRADIUS")
    private Integer expansionradius;
    @Column(name = "ROISTHRESHOLD")
    private Integer roisthreshold;
    @JoinColumn(name = "DTYPE", referencedColumnName = "IDTYPE")
    @ManyToOne
    private Type type;
    @JoinColumn(name = "PSESSION", referencedColumnName = "IDSESSION")
    @ManyToOne
    private Session session;
    @JoinColumn(name = "IDIMAGE", referencedColumnName = "IDIMAGE")
    @ManyToOne(optional = false)
    private Imageresource imageresource;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sample")
    private List<Samplefilter> samplefilterList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sample")
    private List<Cell> cellList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sample")
    private List<Samplecorefeature> samplecorefeatureList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sample")
    private List<Samplefeature> samplefeatureList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sample")
    private List<Session> sessionList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sample")
    private List<Type> typeList;

    public Sample() {
    }

    public Sample(Integer idsample) {
        this.idsample = idsample;
    }

    public Sample(Integer idsample, int threshold, int fillholes) {
        this.idsample = idsample;
        this.threshold = threshold;
        this.fillholes = fillholes;
    }

    public Integer getIdsample() {
        return idsample;
    }

    public void setIdsample(Integer idsample) {
        this.idsample = idsample;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public Separation getSeparation() {
        return separation;
    }

    public void setSeparation(Separation separation) {
        this.separation = separation;
    }

    public Integer getRoismax() {
        return roismax;
    }

    public void setRoismax(Integer roismax) {
        this.roismax = roismax;
    }

    public int getFillholes() {
        return fillholes;
    }

    public void setFillholes(int fillholes) {
        this.fillholes = fillholes;
    }


    public Integer getExpansionradius() {
        return expansionradius;
    }

    public void setExpansionradius(Integer expansionradius) {
        this.expansionradius = expansionradius;
    }

    public Integer getRoisthreshold() {
        return roisthreshold;
    }

    public void setRoisthreshold(Integer roisthreshold) {
        this.roisthreshold = roisthreshold;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
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

    @XmlTransient
    public List<Cell> getCellList() {
        return cellList;
    }

    public void setCellList(List<Cell> cellList) {
        this.cellList = cellList;
    }

    @XmlTransient
    public List<Samplefeature> getSamplefeatureList() {
        return samplefeatureList;
    }

    public void setSamplefeatureList(List<Samplefeature> samplefeatureList) {
        this.samplefeatureList = samplefeatureList;
    }

    @XmlTransient
    public List<Session> getSessionList() {
        return sessionList;
    }

    public void setSessionList(List<Session> sessionList) {
        this.sessionList = sessionList;
    }

    @XmlTransient
    public List<Type> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<Type> typeList) {
        this.typeList = typeList;
    }
    
    @XmlTransient
    public List<Samplefilter> getSamplefilterList() {
        return samplefilterList;
    }

    public void setSamplefilterList(List<Samplefilter> samplefilterList) {
        this.samplefilterList = samplefilterList;
    }

    @XmlTransient
    public List<Samplecorefeature> getSamplecorefeatureList() {
        return samplecorefeatureList;
    }

    public void setSamplecorefeatureList(List<Samplecorefeature> samplecorefeatureList) {
        this.samplecorefeatureList = samplecorefeatureList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idsample != null ? idsample.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sample)) {
            return false;
        }
        Sample other = (Sample) object;
        if ((this.idsample == null && other.idsample != null) || (this.idsample != null && !this.idsample.equals(other.idsample))) {
            return false;
        }
        return true;
    }

    @Override
 	public String toString() {
 		return name;
 	}

 	public Icon getIcon() {

 		Icon icon;
 		String file;
 		if (getImageresource() == null
 				|| !(new File(getImageresource().getPath()).exists()))
 			file = ConfigurationDB.resourcesdir + File.separator + "no-image.jpg";
 		else
 			file = getImageresource().getPath();
 		Image image = new ImagePlus(file).getImage().getScaledInstance(100,
 				100, Image.SCALE_SMOOTH);
 		icon = new ImageIcon(image);

 		return icon;
 	}
 	
 	public static Icon getDefaultIcon()
 	{
 		String file = "plugins/FluoJ/resources" + File.separator + "no-image.jpg";
 		Image image = new ImagePlus(file).getImage().getScaledInstance(100,
 				100, Image.SCALE_SMOOTH);
 		return  new ImageIcon(image);
 	}
 	
 	public int getBordersThreshold()
 	{
 		return (int)(2.5 * getThreshold());
 	}
 	
 	
}
