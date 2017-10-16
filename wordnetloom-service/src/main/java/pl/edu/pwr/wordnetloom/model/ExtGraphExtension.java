package pl.edu.pwr.wordnetloom.model;

import java.awt.Color;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import pl.edu.pwr.wordnetloom.dto.CustomColor;

@Entity
@IdClass(ExtGraphPK.class)
@Table(name = "extgraphextension")
@NamedQueries({
	@NamedQuery(name = "ExtGraphExtension.dbFullGet",
			query = "SELECT e FROM ExtGraphExtension e"),
	@NamedQuery(name = "ExtGraphExtension.dbFullGetIDs",
			query = "SELECT e FROM ExtGraphExtension e WHERE e.extGraph.id IN (:ids)"),
	@NamedQuery(name = "ExtGraphExtension.deleteForSynset",
			query = "DELETE FROM ExtGraphExtension e WHERE e.extGraph.id IN (SELECT ee.id FROM ExtGraph ee WHERE ee.synset.id = :synset)"),
})
public class ExtGraphExtension implements Serializable{

	private static final long serialVersionUID = -6545962357461967746L;

	@Id
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "EXTGRAPH_ID", referencedColumnName = "id")
	private ExtGraph extGraph;

	@Id
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "REL_ID", referencedColumnName = "id")
	private RelationType relationType;

	@Id
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "UNIT_ID", referencedColumnName = "id")
	private Sense lexicalUnit;

	@Column(name = "rank")
	private Double rank;

	@Basic
	@Column(name = "base", columnDefinition = "BIT", length = 1)
	private Boolean base;

	@Override
	public boolean equals(Object o){
		if (this == o) return true;
		if (o == null || !(o instanceof ExtGraphExtension))
			return false;
		ExtGraphExtension e = (ExtGraphExtension) o;
	
		if(extGraph == null || relationType == null || lexicalUnit == null) return false;
		return extGraph.equals(e.getExtGraph()) && relationType.equals(e.getRelationType()) && lexicalUnit.equals(e.getLexicalUnit());
	}

	@Override
	public int hashCode(){
		int hashCode =
				(extGraph == null? 0: extGraph.hashCode()) +
				(relationType == null? 0: relationType.hashCode()) +
				(lexicalUnit == null? 0: lexicalUnit.hashCode());
		if(hashCode == 0)
			return super.hashCode();
		return hashCode;
	}

	@Override
	public String toString(){
		return super.toString();
	}

	public ExtGraph getExtGraph() {
		return extGraph;
	}

	public void setExtGraph(ExtGraph extGraph) {
		this.extGraph = extGraph;
	}

	public RelationType getRelationType() {
		return relationType;
	}

	public void setRelationType(RelationType relationType) {
		this.relationType = relationType;
	}

	public Sense getLexicalUnit() {
		return lexicalUnit;
	}

	public void setLexicalUnit(Sense lexicalUnit) {
		this.lexicalUnit = lexicalUnit;
	}

	public Double getRank() {
		return rank;
	}

	public void setRank(Double rank) {
		this.rank = rank;
	}

	public Boolean getBase() {
		return base;
	}

	public void setBase(Boolean base) {
		this.base = base;
	}

	public Color getColor(){
		return new Color(
				CustomColor.extensionColorBase.getRed()+(int)(rank*75),
				CustomColor.extensionColorBase.getGreen()+(int)(rank*155),
				CustomColor.extensionColorBase.getBlue()
				);
	}
}