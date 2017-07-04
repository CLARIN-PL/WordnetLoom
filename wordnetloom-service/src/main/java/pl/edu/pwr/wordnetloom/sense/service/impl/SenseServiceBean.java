package pl.edu.pwr.wordnetloom.sense.service.impl;

import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.inject.Inject;
import pl.edu.pwr.wordnetloom.domain.model.Domain;
import pl.edu.pwr.wordnetloom.lexicon.model.Lexicon;
import pl.edu.pwr.wordnetloom.partofspeech.model.PartOfSpeech;
import pl.edu.pwr.wordnetloom.sense.model.Sense;
import pl.edu.pwr.wordnetloom.sense.repository.SenseRepository;
import pl.edu.pwr.wordnetloom.sense.service.SenseServceLocal;
import pl.edu.pwr.wordnetloom.synset.model.Synset;

@Stateless
public class SenseServiceBean implements SenseServceLocal {

    @Inject
    private SenseRepository senseRepository;

    public Sense clone(Sense unit) {
        return senseRepository.clone(unit);
    }

    @Override
    public Sense findById(Long id) {
        return senseRepository.findById(id);
    }

    @Override
    public boolean delete(Sense unit) {
        return senseRepository.d
    }

    /**
     * zaladowanie sysnetow zwiazanych z jednostka leksykalna a nastepnie
     * zwrocenie wyniku
     *
     * @param unit - jednostka dla ktorej maja zostac pobrane synsety
     * @return lista synsetow zwiazanych z jednostka leksyklana
     */
    @Override
    public List<Synset> dbFastGetSynsets(Sense unit, List<Long> lexicons) {
        return local.dbFastGetSynsets(unit, lexicons);
    }

    /**
     * odczytanie synsetow podanego lematu
     *
     * @param lemma - lemat
     * @return lista synsetow
     */
    @Override
    public List<Synset> dbFastGetSynsets(String lemma, List<Long> lexicon) {
        return local.dbFastGetSynsets(lemma, lexicon);
    }

    /**
     * odczytanie ilosci synsetow zwiazanych z jednostka bez loadowania ich
     *
     * @param unit - jednostka dla ktorej dane maja zosta pobrane
     * @return liczba synsetow
     */
    @Override
    public int dbGetSynsetsCount(Sense unit) {
        return local.dbGetSynsetsCount(unit);
    }

    /**
     * usuniecie wszystkich obiektow danego typu ale bez usuwania powiazan
     */
    @Override
    public void dbDeleteAll() {
        local.dbDeleteAll();
    }

    /**
     * odczytanie jednostek spelniajacych podane kryterium
     *
     * @param filter - filtr dla jednostek
     * @return lista jednostek leksykalnych (bez detali)
     */
    @Override
    public List<Sense> dbFastGetUnits(String filter, List<Long> lexicons) {
        return local.dbFastGetUnits(filter, lexicons);
    }

    /**
     * odczytanie jednostek spelniajacych kryterium
     *
     * @param filter - filtr
     * @param pos - czesc mowy albo NULL
     * @param domain - domana albo NULL
     * @param workStates - akceptiowanlen statusy lub NULL
     * @return lista jednostek leksykalnych
     */
    @Override
    public List<Sense> dbFastGetUnits(String filter, PartOfSpeech pos, Domain domain, List<Long> lexicons) {
        return local.dbFastGetUnits(filter, pos, domain, lexicons);
    }

    /**
     * odczytanie jednostek spelniajacych kryterium
     *
     * @param filter - filtr
     * @param pos - czesc mowy albo NULL
     * @param domain - domana albo NULL
     * @param workStates - akceptiowanlen statusy lub NULL
     * @param relationType - typ relacji jakie musza byc zdefiniowane dla
     * jednostek wynikowych
     * @param limitSize - maksymalna liczba zwroconych elementów
     * @param realSize - obiekt w którym zapisywana jest prawdziwa wielkość
     * kolekcji
     * @return lista jednostek leksykalnych
     */
    @Override
    public List<Sense> dbFastGetUnits(String filter, PartOfSpeech pos, Domain domain, RelationType relationType,
            String register, String comment, String example, int limitSize, List<Long> lexicons) {
        return local.dbFastGetUnits(filter, pos, domain, relationType, register, comment, example, limitSize, lexicons);
    }

    /**
     * odczytanie jednostek spelniajacych podane kryterium
     *
     * @param filter - filtr dla jednostek
     * @return lista jednostek leksykalnych (z detalami
     */
    @Override
    public List<Sense> dbFullGetUnits(String filter, List<Long> lexicons) {
        return local.dbFullGetUnits(filter, lexicons);
    }

    /**
     * odczytanie liczby jednostek spelniajacych podane kryterium
     *
     * @param filter - filtr dla jednostek
     * @return liczba jednostek
     */
    @Override
    public int dbGetUnitsCount(String filter) {
        return local.dbGetUnitsCount(filter);
    }

    /**
     * odczytanie jednostek zwiazanych z danym synsetem
     *
     * @param synset - synset dla ktorej jednostki maja zostac odczytane
     * @return lista jednostek synsetu
     */
    @Override
    public List<Sense> dbFastGetUnits(Synset synset, List<Long> lexicons) {
        return local.dbFastGetUnits(synset, lexicons);
    }

    /**
     * odczytanie jednostek zwiazanych z danym synsetem
     *
     * @param synset - synset dla ktorej jednostki maja zostac odczytane
     * @param limit - ograniczeniena liczbe jednostek do odczytania, zero
     * oznacza brak limitu
     * @return lista jednostek synsetu
     */
    @Override
    public List<Sense> dbFullGetUnits(Synset synset,
            int limit, List<Long> lexicons) {
        return local.dbFullGetUnits(synset, limit, lexicons);
    }

    /**
     * odczytanie jednostek zwiazanych z danym synsetem
     *
     * @param synset - synset dla ktorej jednostki maja zostac odczytane
     * @param limit - ograniczeniena liczbe jednostek do odczytania
     * @return lista jednostek synsetu
     */
    @Override
    public List<Sense> dbFastGetUnits(Synset synset,
            int limit, List<Long> lexicons) {
        return local.dbFastGetUnits(synset, limit, lexicons);
    }

    /**
     * odczytanie jednostek zwiazanych z danym synsetem
     *
     * @param synset - synset dla ktorej jednostki maja zostac odczytane
     * @return lista jednostek synsetu
     */
    @Override
    public int dbGetUnitsCount(Synset synset, List<Long> lexicons) {
        return local.dbGetUnitsCount(synset, lexicons);
    }

    /**
     * Odczytuje pierwszy wolny wariant dla danego lematu i kategorii
     * gramatycznej.
     *
     * @param lemma
     * @param pos
     * @return int
     */
    @Override
    public int dbGetNextVariant(String lemma, PartOfSpeech pos) {
        return local.dbGetNextVariant(lemma, pos);
    }

    /**
     * usuniecie jednostek leksykalnych
     *
     * @param list - lista jednostek leksykalnych
     * @return liczba usunietych jednostek
     */
    @Override
    public int dbDelete(List<Sense> list, String owner) {
        return local.dbDelete(list, owner);
    }

    /**
     * odczytanie liczby wykorzystanych jednostek
     *
     * @return liczba wykorzystanych jednostek
     */
    @Override
    public int dbUsedUnitsCount() {
        return local.dbUsedUnitsCount();
    }

    /**
     * odczytanie liczby jednostek w bazie
     *
     * @return liczba jednostek
     */
    @Override
    public int dbGetUnitsCount() {
        return local.dbGetUnitsCount();
    }

    /**
     * odczytanie jednostek nie majacych odmienionych form
     *
     * @return lista jednostek
     */
    @Override
    public List<Sense> dbGetUnitsWithoutForms() {
        return local.dbGetUnitsWithoutForms();
    }

    /**
     * Odczytuje najwyższy numerek wariantu dla podanego lematu
     *
     * @param word
     * @return int
     */
    @Override
    public int dbGetHighestVariant(String word, List<Long> lexicons) {
        return local.dbGetHighestVariant(word, lexicons);
    }

    /**
     * odczytanie jednostek nie należących do żadnego synsetu
     *
     * @param filter - filtr dla lematu
     * @return lista jednostek
     */
    @Override
    public List<Sense> dbGetUnitsNotInAnySynset(String filter, PartOfSpeech pos) {
        return local.dbGetUnitsNotInAnySynset(filter, pos);
    }

    /**
     * odczytanie jednostek, które występują w więcej niż jednym synsecie
     *
     * @return lista jednostek
     */
    @Override
    public List<Sense> dbGetUnitsAppearingInMoreThanOneSynset() {
        return local.dbGetUnitsAppearingInMoreThanOneSynset();
    }

    /**
     * Sprawdza, czy jednostka należy do jakiegokolwiek synsetu
     *
     * @param unit - jednostka
     * @return TRUE jeśli należy do synsetu
     */
    @Override
    public boolean dbInAnySynset(Sense unit) {
        return local.dbInAnySynset(unit);
    }

    public String getSensAtrribute(Sense sense, String nazwaPola) {
        return local.getSenseAtrribute(sense, nazwaPola);
    }

    public void setSenseAtrribute(Sense sense, String key, String value) {
        local.setSenseAtrribute(sense, key, value);
    }

    public Sense updateSense(Sense sense) {
        return local.updateSense(sense);
    }

    @Override
    public Set<Long> dbUsedUnitsIDs() {
        return local.dbUsedUnitsIDs();
    }

    @Override
    public List<Lexicon> getLexiconsFromList(List<Long> lexicons) {
        return local.getLexiconsByIds(lexicons);
    }

    @Override
    public List<Long> getAllLemasForLexicon(List<Long> lexicon) {
        return local.getAllLemmasForLexicon(lexicon);
    }

    @Override
    public List<Sense> getSensesByLemmaID(long id, long lexicon) {
        return local.getSensesForLemmaID(id, lexicon);
    }

    @Override
    public List<Sense> dbFastGetUnits(String filter,
            pl.edu.pwr.wordnetloom.model.uby.enums.PartOfSpeech pos,
            Domain domain, RelationType relationType, String register,
            String comment, String example, int limitSize, List<Long> lexicons) {

        return local.dbFastGetUnitsUby(filter, pos, domain, relationType, register, comment, example, limitSize, lexicons);
    }
}