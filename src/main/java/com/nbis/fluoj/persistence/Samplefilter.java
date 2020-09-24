/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nbis.fluoj.persistence;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author airen
 */
@Entity
@Table(name = "SAMPLEFILTER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Samplefilter.findAll", query = "SELECT s FROM Samplefilter s"),
    @NamedQuery(name = "Samplefilter.findByIdsample", query = "SELECT s FROM Samplefilter s WHERE s.samplefilterPK.idsample = :idsample"),
    @NamedQuery(name = "Samplefilter.findByCommand", query = "SELECT s FROM Samplefilter s WHERE s.command = :command"),
    @NamedQuery(name = "Samplefilter.findByIdfilter", query = "SELECT s FROM Samplefilter s WHERE s.samplefilterPK.idfilter = :idfilter"),
    @NamedQuery(name = "Samplefilter.findByCommandoptions", query = "SELECT s FROM Samplefilter s WHERE s.commandoptions = :commandoptions")})
public class Samplefilter implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected SamplefilterPK samplefilterPK;
    @Basic(optional = false)
    @Column(name = "COMMAND")
    private String command;
    @Column(name = "COMMANDOPTIONS")
    private String commandoptions;
    @JoinColumn(name = "IDSAMPLE", referencedColumnName = "IDSAMPLE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Sample sample;

    public Samplefilter() {
    }

    public Samplefilter(SamplefilterPK samplefilterPK) {
        this.samplefilterPK = samplefilterPK;
    }

    public Samplefilter(SamplefilterPK samplefilterPK, String command) {
        this.samplefilterPK = samplefilterPK;
        this.command = command;
    }

    public Samplefilter(int idsample, int idfilter) {
        this.samplefilterPK = new SamplefilterPK(idsample, idfilter);
    }

    public SamplefilterPK getSamplefilterPK() {
        return samplefilterPK;
    }

    public void setSamplefilterPK(SamplefilterPK samplefilterPK) {
        this.samplefilterPK = samplefilterPK;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommandoptions() {
        return commandoptions;
    }

    public void setCommandoptions(String commandoptions) {
        this.commandoptions = commandoptions;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (samplefilterPK != null ? samplefilterPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Samplefilter)) {
            return false;
        }
        Samplefilter other = (Samplefilter) object;
        if ((this.samplefilterPK == null && other.samplefilterPK != null) || (this.samplefilterPK != null && !this.samplefilterPK.equals(other.samplefilterPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Samplefilter[ samplefilterPK=" + samplefilterPK + " ]";
    }
    
}
