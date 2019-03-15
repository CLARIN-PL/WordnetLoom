package pl.edu.pwr.wordnetloom.server.business.yiddish.entity;

import pl.edu.pwr.wordnetloom.server.business.dictionary.entity.*;
import pl.edu.pwr.wordnetloom.server.business.sense.enity.Sense;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "yiddish_sense_extension")
public class YiddishSenseExtension implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sense_id", referencedColumnName = "id", nullable = false)
    private Sense sense;

/*  @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dialectal_id", referencedColumnName = "id")
    private DialectalDictionary dialectalDictionary;*/

    @Enumerated(EnumType.STRING)
    private VariantType variant = VariantType.Yiddish_Primary_Lemma;

    @Column(name = "spelling_latin")
    private String latinSpelling;

    @Column(name = "spelling_yivo")
    private String yivoSpelling;

    @Column(name = "spelling_yiddish")
    private String yiddishSpelling;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "extension_id")
    private Set<Transcription> transcriptions = new LinkedHashSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("id")
    @JoinColumn(name = "extension_id")
    private Set<YiddishDomain> yiddishDomains = new LinkedHashSet<>();

    @Lob
    private String meaning;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grammatical_gender_id", referencedColumnName = "id")
    private GrammaticalGenderDictionary grammaticalGender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "style_id", referencedColumnName = "id")
    private StyleDictionary style;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private StatusDictionary status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lexical_characteristic_id", referencedColumnName = "id")
    private LexicalCharacteristicDictionary lexicalCharacteristic;

    @Lob
    @Column
    private String etymology;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "yiddish_extension_source",
            joinColumns = @JoinColumn(name = "sense_extension_id"),
            inverseJoinColumns = @JoinColumn(name = "source_dictionary_id")
    )
    private Set<SourceDictionary> source = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attested_id", referencedColumnName = "id")
    private AgeDictionary age;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "extension_id")
    private Set<Inflection> inflection = new LinkedHashSet<>();

    @Column(name = "etymological_root")
    private String etymologicalRoot;

    @Lob
    @Column
    private String comment;

    @Lob
    @Column
    private String context;

    @OneToMany(mappedBy = "extension", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("position")
    private Set<Particle> particles = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sense getSense() {
        return sense;
    }

    public void setSense(Sense sense) {
        this.sense = sense;
    }

    public String getLatinSpelling() {
        return latinSpelling;
    }

    public void setLatinSpelling(String latinSpelling) {
        this.latinSpelling = latinSpelling;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public GrammaticalGenderDictionary getGrammaticalGender() {
        return grammaticalGender;
    }

    public void setGrammaticalGender(GrammaticalGenderDictionary grammaticalGender) {
        this.grammaticalGender = grammaticalGender;
    }

    public StyleDictionary getStyle() {
        return style;
    }

    public void setStyle(StyleDictionary style) {
        this.style = style;
    }

    public StatusDictionary getStatus() {
        return status;
    }

    public void setStatus(StatusDictionary status) {
        this.status = status;
    }

    public String getEtymologicalRoot() {
        return etymologicalRoot;
    }

    public void setEtymologicalRoot(String etymologicalRoot) {
        this.etymologicalRoot = etymologicalRoot;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public LexicalCharacteristicDictionary getLexicalCharacteristic() {
        return lexicalCharacteristic;
    }

    public void setLexicalCharacteristic(LexicalCharacteristicDictionary lexicalCharacteristic) {
        this.lexicalCharacteristic = lexicalCharacteristic;
    }

    public Set<Transcription> getTranscriptions() {
        return transcriptions;
    }

    public void setTranscriptions(Set<Transcription> transcriptions) {
        this.transcriptions = transcriptions;
    }

    public Set<Inflection> getInflection() {
        return inflection;
    }

    public void setInflection(Set<Inflection> inflection) {
        this.inflection = inflection;
    }

    public Set<Particle> getParticles() {
        return particles;
    }

    public void setParticles(Set<Particle> particels) {
        this.particles = particels;
    }

    public VariantType getVariant() {
        return variant;
    }

    public void setVariant(VariantType variant) {
        this.variant = variant;
    }

    public Set<YiddishDomain> getYiddishDomains() {
        return yiddishDomains;
    }

    public void setYiddishDomains(Set<YiddishDomain> yiddishDomains) {
        this.yiddishDomains = yiddishDomains;
    }

    public String getYivoSpelling() {
        return yivoSpelling;
    }

    public void setYivoSpelling(String yivoSpelling) {
        this.yivoSpelling = yivoSpelling;
    }

    public String getYiddishSpelling() {
        return yiddishSpelling;
    }

    public void setYiddishSpelling(String yiddishSpelling) {
        this.yiddishSpelling = yiddishSpelling;
    }

    public String getEtymology() {
        return etymology;
    }

    public void setEtymology(String etymology) {
        this.etymology = etymology;
    }

    public Set<SourceDictionary> getSource() {
        return source;
    }

    public void setSource(Set<SourceDictionary> source) {
        this.source = source;
    }

    public AgeDictionary getAge() {
        return age;
    }

    public void setAge(AgeDictionary age) {
        this.age = age;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((latinSpelling == null) ? 0 : latinSpelling.hashCode());
        result = prime * result + ((sense == null) ? 0 : sense.hashCode());
        result = prime * result + ((variant == null) ? 0 : variant.hashCode());
        result = prime * result + ((yiddishSpelling == null) ? 0 : yiddishSpelling.hashCode());
        result = prime * result + ((yivoSpelling == null) ? 0 : yivoSpelling.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        YiddishSenseExtension other = (YiddishSenseExtension) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (latinSpelling == null) {
            if (other.latinSpelling != null)
                return false;
        } else if (!latinSpelling.equals(other.latinSpelling))
            return false;
        if (sense == null) {
            if (other.sense != null)
                return false;
        } else if (!sense.equals(other.sense))
            return false;
        if (variant != other.variant)
            return false;
        if (yiddishSpelling == null) {
            if (other.yiddishSpelling != null)
                return false;
        } else if (!yiddishSpelling.equals(other.yiddishSpelling))
            return false;
        if (yivoSpelling == null) {
            if (other.yivoSpelling != null)
                return false;
        } else if (!yivoSpelling.equals(other.yivoSpelling))
            return false;
        return true;
    }
}
