/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;


import ij.IJ;
import ij.ImagePlus;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.nbis.fluoj.classifier.ConfigurationDB;

/**
 * 
 * @author airen
 */
@Entity
@Table(name = "IMAGERESOURCE")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Imageresource.findAll", query = "SELECT i FROM Imageresource i"),
		@NamedQuery(name = "Imageresource.findByIdimage", query = "SELECT i FROM Imageresource i WHERE i.idimage = :idimage"),
		@NamedQuery(name = "Imageresource.findByName", query = "SELECT i FROM Imageresource i WHERE i.name = :name"),
		@NamedQuery(name = "Imageresource.findByDate", query = "SELECT i FROM Imageresource i WHERE i.date = :date")
	 })
public class Imageresource implements Serializable {
	@Column(name = "DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "imageresource")
	private List<Sample> sampleList;
	@OneToMany(mappedBy = "imageresource")
	private List<Cell> cellList;
	@OneToMany(mappedBy = "imageresource")
	private List<Scell> scellList;
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "IDIMAGE")
	private Integer idimage;
	@Column(name = "NAME")
	private String name;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "imageresource")
	private Collection<Sample> sampleCollection;
	@OneToMany(mappedBy = "imageresource")
	private Collection<Cell> cellCollection;
	@OneToMany(mappedBy = "imageresource")
	private Collection<Scell> scellCollection;

	public Imageresource() {
	}

	public Imageresource(Integer idimage) {
		this.idimage = idimage;
	}

	public Integer getIdimage() {
		return idimage;
	}

	public void setIdimage(Integer idimage) {
		this.idimage = idimage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	@XmlTransient
	public Collection<Sample> getSampleCollection() {
		return sampleCollection;
	}

	public void setSampleCollection(Collection<Sample> sampleCollection) {
		this.sampleCollection = sampleCollection;
	}

	@XmlTransient
	public Collection<Cell> getCellCollection() {
		return cellCollection;
	}

	public void setCellCollection(Collection<Cell> cellCollection) {
		this.cellCollection = cellCollection;
	}

	@XmlTransient
	public Collection<Scell> getScellCollection() {
		return scellCollection;
	}

	public void setScellCollection(Collection<Scell> scellCollection) {
		this.scellCollection = scellCollection;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idimage != null ? idimage.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Imageresource)) {
			return false;
		}
		Imageresource other = (Imageresource) object;
		if ((this.idimage == null && other.idimage != null)
				|| (this.idimage != null && !this.idimage.equals(other.idimage))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		if (idimage == null)
			return null;
		return idimage + ".tif";
	}

	public ImagePlus getImagePlus() {
		if (idimage == null)
			return null;
		return new ImagePlus(getPath());
	}
	
	public String getPath() {
		return ConfigurationDB.imagesdir + File.separator + idimage + ".tif";
		
	}

	@XmlTransient
	public List<Sample> getSampleList() {
		return sampleList;
	}

	public void setSampleList(List<Sample> sampleList) {
		this.sampleList = sampleList;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
