package pl.edu.pwr.wordnetloom.client.plugins.lexeditor.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import pl.edu.pwr.wordnetloom.client.systems.managers.RelationTypeManager;
import pl.edu.pwr.wordnetloom.client.systems.ui.ButtonExt;
import pl.edu.pwr.wordnetloom.client.systems.ui.ComboBoxPlain;
import pl.edu.pwr.wordnetloom.client.systems.ui.DialogWindow;
import pl.edu.pwr.wordnetloom.client.systems.ui.LabelExt;
import pl.edu.pwr.wordnetloom.client.systems.ui.TextAreaPlain;
import pl.edu.pwr.wordnetloom.client.utils.Labels;
import pl.edu.pwr.wordnetloom.client.workbench.interfaces.Workbench;
import pl.edu.pwr.wordnetloom.partofspeech.model.PartOfSpeech;
import pl.edu.pwr.wordnetloom.relationtype.model.SynsetRelationType;
import pl.edu.pwr.wordnetloom.sense.model.Sense;

public class RelationTypeFrame extends DialogWindow implements ActionListener, KeyListener {

    protected static final long serialVersionUID = 1L;

    // elementu interfejsu uzytkownika
    protected ComboBoxPlain relationType;
    protected ComboBoxPlain relationSubType;
    protected ButtonExt buttonChoose, buttonCancel;
    protected TextAreaPlain description;
    protected JList testsLit;
    protected static ComboBoxPlain parentItem;
    protected ComboBoxPlain middleItem;
    protected static ComboBoxPlain childItem;
    protected SynsetRelationType fixedRelationType;

    protected SynsetRelationType chosenType = null;
    protected ArrayList<SynsetRelationType> mainRelations = null;
    protected Collection<SynsetRelationType> subRelations = null;
    protected static PartOfSpeech pos;

    /**
     * konstruktor
     *
     * @param frame - srodowisko
     * @param type - typ relacji
     * @param pos - czesc mowy
     * @param fixedRelationType - typ relacji ustawiony na sztywno
     * @param parentUnits - jednostki podzedne
     * @param middleUnits - jednostki pośrednie
     * @param childUnits - jednostki nadrzedne
     */
    private RelationTypeFrame(JFrame frame,
            String type,
            PartOfSpeech pos,
            SynsetRelationType fixedRelationType,
            SynsetRelationType suggestedRelationType,
            Sense suggestedUnit,
            Collection<Sense> parentUnits,
            Collection<Sense> middleUnits,
            Collection<Sense> childUnits) {
        super(frame, Labels.RELATION_PARAMS, 650, 500);
        RelationTypeFrame.pos = pos;
        this.fixedRelationType = fixedRelationType;

        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //this.setAlwaysOnTop(true);

        // element nadrzedny
        parentItem = new ComboBoxPlain();
        for (Sense parent : parentUnits) {
            parentItem.addItem(parent.getWord().getWord());
        }

        // element podrzedny
        childItem = new ComboBoxPlain();
        for (Sense child : childUnits) {
            childItem.addItem(child.getWord().getWord());
        }
        //Wybranie zaproponowanej jednostki
        if (suggestedUnit != null) {

            childItem.setSelectedItem(suggestedUnit.getWord().getWord());
            parentItem.setSelectedItem(suggestedUnit.getWord().getWord());
        }

        // element posredni
        middleItem = new ComboBoxPlain();
        if (middleUnits != null && !middleUnits.isEmpty()) {
            for (Sense middle : middleUnits) {
                middleItem.addItem(middle.getWord());
            }
        } else {
            middleItem.setEnabled(false);
        }

        // opis relacji
        description = new TextAreaPlain("");
        description.setRows(6);
        description.setEditable(false);

        // lista testow
        testsLit = new JList();

        // podtyp relacji
        relationSubType = new ComboBoxPlain();
        relationSubType.addKeyListener(this);
        relationSubType.setEnabled(false);

        // typ relacji
        relationType = new ComboBoxPlain();
        relationType.addKeyListener(this);

        // wyswietlenie relacji
        mainRelations = new ArrayList<>();
//        Collection<IRelationType> readRelations = LexicalDA.getHighestRelations(type, pos);
//        for (IRelationType relType : readRelations) {
//            relType = LexicalDA.getEagerRelationTypeByID(relType);
//            if (fixedRelationType == null
//                    || relType.getId().longValue() == fixedRelationType.getId().longValue()
//                    || (fixedRelationType.getParent() != null
//                    && relType.getId().longValue() == fixedRelationType.getParent().getId())) {
//                relationType.addItem(RelationTypeManager.getFullNameFor(relType.getId()));
//                mainRelations.add(relType);
//            }
//        }

        // przycisk wybierz
        buttonChoose = new ButtonExt(Labels.SELECT, this, KeyEvent.VK_W);
        buttonChoose.addKeyListener(this);
        buttonCancel = new ButtonExt(Labels.CANCEL, this, KeyEvent.VK_A);
        buttonCancel.addKeyListener(this);

        relationSubType.addActionListener(this);
        relationType.addActionListener(this);

        // czy sa jakieś relacje
        if (mainRelations.size() > 0) {
            //Ustawienie na sugestię, jeśli istnieje
            if (suggestedRelationType != null) {
                if (suggestedRelationType.getParent() == null) {
                    relationType.setSelectedItem(RelationTypeManager.getFullNameFor(suggestedRelationType.getId()));
                } else {
                    relationType.setSelectedItem(RelationTypeManager.getFullNameFor(suggestedRelationType.getParent().getId()));
                    relationSubType.setSelectedItem(RelationTypeManager.getFullNameFor(suggestedRelationType.getId()));
                }
            } else {
                relationType.setSelectedIndex(0);
            }
            buttonChoose.setEnabled(true);
        } else {
            buttonChoose.setEnabled(false);
        }

        // dopisanie zdarzen
        parentItem.addKeyListener(this);
        parentItem.addActionListener(this);
        middleItem.addKeyListener(this);
        middleItem.addActionListener(this);
        childItem.addKeyListener(this);
        childItem.addActionListener(this);

        // dodanie elemetow UI do okna
        this.add("", new LabelExt(Labels.RELATION_TYPE_COLON, 't', relationType));
        this.add("tab hfill", relationType);
        this.add("br", new LabelExt(Labels.RELATION_SUBTYPE_COLON, 'y', relationType));
        this.add("tab hfill", relationSubType);
        this.add("br", new LabelExt(Labels.RELATION_DESC_COLON, '\0', description));
        this.add("br hfill", new JScrollPane(description));

        this.add("br", new LabelExt(Labels.SOURCE_UNIT_COLON, 'r', parentItem));
        this.add("tab hfill", parentItem);

        if (middleItem.isEnabled()) {
            this.add("br", new LabelExt(Labels.INTERMEDIATE_UNIT_COLON, 'p', parentItem));
            this.add("tab hfill", middleItem);
        }
        this.add("br", new LabelExt(Labels.TARGET_UNIT_COLON, 'd', childItem));
        this.add("tab hfill", childItem);
        this.add("br", new LabelExt(Labels.TESTS_COLON, '\0', testsLit));
        this.add("br hfill vfill", new JScrollPane(testsLit));
        this.add("br center", this.buttonChoose);
        this.add("", this.buttonCancel);
    }

    /**
     * @author amusial Constructor for derived classes
     * @param frame parent component
     * @param type relation type
     * @param pos part of speech
     * @param fixedRelationType fixed relation type
     *
     */
    protected RelationTypeFrame(JFrame frame,
            String type,
            PartOfSpeech pos,
            SynsetRelationType fixedRelationType) {
        super(frame, Labels.RELATION_PARAMS, 650, 500);
        RelationTypeFrame.pos = pos;
        this.fixedRelationType = fixedRelationType;

        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * wyswietlenie okienka dialogowego
     *
     * @param workbench - srodowisko
     * @param type - typ relacji
     * @param pos - czesc mowy dla ktorej ma byc brana relacja
     * @param parentUnits - jednostki nadrzedne
     * @param childUnits - jednostki podrzedne
     * @return typ relacji albo null
     */
    public static SynsetRelationType showModal(Workbench workbench,
            String type,
            PartOfSpeech pos,
            Collection<Sense> parentUnits,
            Collection<Sense> childUnits) {
        return showModal(workbench.getFrame(), type, pos, null, null, null, parentUnits, null, childUnits);
    }

    /**
     * @param workbench
     * @param type
     * @param pos
     * @param fixedRelationType
     * @param parentUnits
     * @param middleUnits
     * @param childUnits
     * @return typ relacji albo null
     */
    public static SynsetRelationType showModal(Workbench workbench,
            String type,
            PartOfSpeech pos,
            SynsetRelationType fixedRelationType,
            Collection<Sense> parentUnits,
            Collection<Sense> middleUnits,
            Collection<Sense> childUnits) {
        return showModal(workbench.getFrame(), type, pos, fixedRelationType, null, null, parentUnits, middleUnits, childUnits);
    }

    /**
     * konwersja jednostki na liste
     *
     * @param unit - jednostka
     * @return lista zawierajaca jednostek
     */
    public static Collection<Sense> unitToList(Sense unit) {
        Collection<Sense> list = new ArrayList<>();
        list.add(unit);
        return list;
    }

    /**
     * wyswietlenie okienka dialogowego
     *
     * @param frame - srodowisko
     * @param type - typ relacji
     * @param pos - czesc mowy dla ktorej ma byc brana relacja
     * @param relationType - typ relacji jesli jest ustawiona na sztywno
     * @param suggestedRelationType - typ sugerowanej relacji, o ile istnieje
     * @param suggestedUnit
     * @param parentUnits - jednostki nadrzedne
     * @param middleUnits - jednostki posrednie
     * @param childUnits - jednostki podrzedne
     * @return typ relacji albo null
     */
    public static SynsetRelationType showModal(JFrame frame,
            String type,
            PartOfSpeech pos,
            SynsetRelationType relationType,
            SynsetRelationType suggestedRelationType,
            Sense suggestedUnit,
            Collection<Sense> parentUnits,
            Collection<Sense> middleUnits,
            Collection<Sense> childUnits) {
        RelationTypeFrame framew = new RelationTypeFrame(frame, type, pos, relationType, suggestedRelationType, suggestedUnit, parentUnits, middleUnits, childUnits);
        framew.setVisible(true);
        return framew.chosenType;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                event.consume();
                setVisible(false);
                break;
        }
    }

    /**
     * odczytanie zaznaczonej relacji
     *
     * @return zaznaczona relacja
     */
    protected SynsetRelationType getSelectedRelation() {
        if (subRelations != null && subRelations.size() > 0) {
            // jest pod typ
            int index = relationSubType.getSelectedIndex();
            for (SynsetRelationType type : subRelations) {
                if (index-- == 0) {
                    return type;
                }
            }
        } else {
            // brak podtypu
            int index = relationType.getSelectedIndex();
            for (SynsetRelationType type : mainRelations) {
                if (index-- == 0) {
                    return type;
                }
            }
        }
        return null;
    }

    /**
     * wczytanie testow dla podanej relacji
     *
     * @param type - typ relacji
     */
    protected void loadTests(SynsetRelationType type) {
        if (middleItem.getItemCount() == 0) {
            int a = parentItem.getSelectedIndex();
            int b = childItem.getSelectedIndex();

            if (a < 0 || b < 0) {
                return;
            }

//            List<String> tests = LexicalDA.getTests(type,
//                    (parentItem.getItemAt(parentItem.getSelectedIndex())).toString(),
//                    (childItem.getItemAt(childItem.getSelectedIndex())).toString(),
//                    pos);
//            testsLit.setListData(tests.toArray(new String[]{}));
//        } else {
//            List<String> tests = LexicalDA.getTests(type,
//                    (parentItem.getItemAt(parentItem.getSelectedIndex())).toString(),
//                    middleItem.getItemAt(middleItem.getSelectedIndex()).toString(),
//                    pos);
//            tests.addAll(LexicalDA.getTests(type,
//                    (parentItem.getItemAt(parentItem.getSelectedIndex())).toString(),
//                    (childItem.getItemAt(childItem.getSelectedIndex())).toString(),
//                    pos));
//
//            testsLit.setListData(tests.toArray(new String[]{}));
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {

//        if (event.getSource() == buttonChoose) {
//            chosenType = getSelectedRelation();
//            this.setVisible(false);
//
//        } else if (event.getSource() == buttonCancel) {
//            this.setVisible(false);
//
//        } else if (event.getSource() == relationType) {
//            relationSubType.removeAllItems();
//            description.setText("");
//            testsLit.setListData(new String[]{});
//
//            int index = relationType.getSelectedIndex();
//            for (IRelationType type : mainRelations) {
//
//                if (index-- == 0) {
//
//                    subRelations = new ArrayList<>();
//                    Collection<IRelationType> readRelations = LexicalDA.getChildren(type);
//
//                    for (IRelationType relType : readRelations) {
//                        if (fixedRelationType == null || fixedRelationType.getId().longValue() == relType.getId().longValue()) {
//                            relationSubType.addItem(RelationTypeManager.getFullNameFor(relType.getId()));
//                            subRelations.add(relType);
//                        }
//                    }
//                    if (subRelations.size() > 0) {
//                        relationSubType.setSelectedIndex(0);
//                    } else {
//                        loadTests(type);
//                    }
//                    description.setText(type.getDescription().getText());
//                    break;
//                }
//            }
//            relationSubType.setEnabled(subRelations != null && subRelations.size() > 0);
//
//        } else if (event.getSource() == relationSubType || event.getSource() == parentItem || event.getSource() == childItem || event.getSource() == middleItem) {
//
//            testsLit.setListData(new String[]{});
//            IRelationType relation = getSelectedRelation();
//            if (relation != null) {
//                loadTests(relation);
//            }
//        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
    }
}