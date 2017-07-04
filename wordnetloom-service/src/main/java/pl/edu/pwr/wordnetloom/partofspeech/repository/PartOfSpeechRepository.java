package pl.edu.pwr.wordnetloom.partofspeech.repository;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import pl.edu.pwr.wordnetloom.common.repository.GenericRepository;
import pl.edu.pwr.wordnetloom.lexicon.model.Lexicon;
import pl.edu.pwr.wordnetloom.partofspeech.model.PartOfSpeech;

@Stateless
public class PartOfSpeechRepository extends GenericRepository<PartOfSpeech> {

    @PersistenceContext
    EntityManager em;

    @Override
    protected Class<PartOfSpeech> getPersistentClass() {
        return PartOfSpeech.class;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<PartOfSpeech> findByLexicon(Lexicon lexicon) {
        Query query = em.createQuery("SELECT DISTINCT lapd.partOfSpeech FROM LexiconAllowedPartOfSpeechDomain lapd WHERE lapd.lexicon = :lexicon");
        return query
                .setParameter("ids", lexicon)
                .getResultList();
    }

}