/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

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
@Table(name = "filter")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Filter.findAll", query = "SELECT f FROM Filter f"),
    @NamedQuery(name = "Filter.findByIdfilter", query = "SELECT f FROM Filter f WHERE f.idfilter = :idfilter"),
    @NamedQuery(name = "Filter.findByCommand", query = "SELECT f FROM Filter f WHERE f.command = :command"),
    @NamedQuery(name = "Filter.findByOptions", query = "SELECT f FROM Filter f WHERE f.options = :options")})
public class Filter implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idfilter", nullable = false)
    private Short idfilter;
    @Column(name = "command", length = 500)
    private String command;
    @Column(name = "options", length = 500)
    private String options;
    @JoinColumn(name = "idsample", referencedColumnName = "idsample")
    @ManyToOne
    private Sample idsample;

    public Filter() {
    }

    public Filter(Short idfilter) {
        this.idfilter = idfilter;
    }

    public Short getIdfilter() {
        return idfilter;
    }

    public void setIdfilter(Short idfilter) {
        this.idfilter = idfilter;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Sample getIdsample() {
        return idsample;
    }

    public void setIdsample(Sample idsample) {
        this.idsample = idsample;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idfilter != null ? idfilter.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Filter)) {
            return false;
        }
        Filter other = (Filter) object;
        if ((this.idfilter == null && other.idfilter != null) || (this.idfilter != null && !this.idfilter.equals(other.idfilter))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.nbis.fluoj.persistence.Filter[ idfilter=" + idfilter + " ]";
    }
    
}
