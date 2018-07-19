package pl.edu.pwr.wordnetloom.client.plugins.lexeditor.panel;

import com.alee.laf.rootpane.WebFrame;
import pl.edu.pwr.wordnetloom.client.remote.RemoteService;
import pl.edu.pwr.wordnetloom.client.systems.managers.LocalisationManager;
import pl.edu.pwr.wordnetloom.client.systems.ui.MButton;
import pl.edu.pwr.wordnetloom.client.systems.ui.MComponentGroup;
import pl.edu.pwr.wordnetloom.client.utils.Labels;
import pl.edu.pwr.wordnetloom.dictionary.model.Emotion;
import pl.edu.pwr.wordnetloom.dictionary.model.Markedness;
import pl.edu.pwr.wordnetloom.dictionary.model.Valuation;
import pl.edu.pwr.wordnetloom.sense.model.EmotionalAnnotation;
import pl.edu.pwr.wordnetloom.sense.model.Sense;
import pl.edu.pwr.wordnetloom.sense.model.UnitEmotion;
import pl.edu.pwr.wordnetloom.sense.model.UnitValuation;
import pl.edu.pwr.wordnetloom.user.model.User;
import se.datadosen.component.RiverLayout;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

import static se.datadosen.component.RiverLayout.*;

public class EmotionsPropertiesPanel extends JPanel {

    // TODO dorobić etykiety
    private static final String LABEL_MARKEDNESS = "Nacechowanie:";
    private static final String LABEL_EXAMPLE = "Przykłady:";
    private static final String LABEL_NEUTRAL = "Neutralny";
    private static final String LABEL_MARKED = "Nacechowany";
    private static final String LABEL_EMOTIONS = "Emocje:";
    private static final String LABEL_VALUATIONS = "Wartościowanie:";
    private static final String LABEL_STATUS = "Status jednostki:";
    private static final String LABEL_OWNER = "Właściciel:";

    private JRadioButton neutralRadio;
    private JRadioButton notMarkedRadio;
    private JLabel ownerLabel;
    private JComboBox markednessCombo;
    private JTextArea example1;
    private JTextArea example2;

    private JButton saveButton;

    EmotionsListPanel listPanel;

    private EmotionalAnnotation editedAnnotation;

    private Map<Emotion, JCheckBox> emotionsMap = new HashMap<>();
    private Map<Valuation, JCheckBox> valuatesMap = new HashMap<>();

    // TODO zamiast przycisku można przesłać zdarzenie
    public EmotionsPropertiesPanel(WebFrame frame){
        // TODO ustawianie odpowiedniego rozmiaru

        setLayout(new BorderLayout());

        initComponents();
        listPanel = createListPanel();
        JPanel statusPanel = createStatusPanel();
        JPanel ownerPanel = createOwnerPanel();
        JPanel emotionsPanel = createEmotionsPanel(getEmotions());
        JPanel valuationsPanel = createValuationsPanel(getValuations());
        JPanel markednessPanel = createMarkednessPanel();
        JPanel examplesPanel = createExamplesPanel();

        JPanel valuePanel = new JPanel(new RiverLayout());

        valuePanel.add(listPanel);
        valuePanel.add(LINE_BREAK, statusPanel);
        valuePanel.add(TAB_STOP, ownerPanel);
        valuePanel.add(LINE_BREAK, emotionsPanel);
        valuePanel.add(LINE_BREAK, valuationsPanel);
        valuePanel.add(LINE_BREAK, markednessPanel);
        valuePanel.add(LINE_BREAK, examplesPanel);

        add(valuePanel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }

    private void initComponents(){
        final int TEXT_AREA_ROWS = 2;

        neutralRadio = new JRadioButton(LABEL_NEUTRAL);
        neutralRadio.addActionListener(e->setAnnotationEnabled());
        notMarkedRadio = new JRadioButton(LABEL_MARKED);
        notMarkedRadio.addActionListener(e->setAnnotationEnabled());
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(neutralRadio);
        statusGroup.add(notMarkedRadio);

        ownerLabel = new JLabel("");
        markednessCombo = createMarkednessCombo();

        example1 = new JTextArea(); // TOOD dodać caretListener
        example1.setRows(TEXT_AREA_ROWS);
        example1.setLineWrap(true);
        example2 = new JTextArea();
        example2.setRows(TEXT_AREA_ROWS);
        example2.setLineWrap(true);

        saveButton = new JButton(Labels.SAVE);
        saveButton.addActionListener(e -> save());

        setEnableEditing(false);
    }

    private void save() {
        // save all addotations
//        for(EmotionalAnnotation annotation : listPanel.getAnnotations()){
//            saveAnnotation(annotation);
//        }
        saveAnnotation(editedAnnotation);
    }

    private void saveAnnotation(EmotionalAnnotation annotation) {
        setAnnotation(annotation);
        RemoteService.senseRemote.save(annotation);
    }

    private void setAnnotation(EmotionalAnnotation annotation){
        annotation.setEmotionalCharacteristic(canEditingAnnotation());

        Set<UnitEmotion> emotions = new LinkedHashSet<>();
        emotionsMap.forEach((k,v)-> {
            if(v.isSelected()){
                UnitEmotion unitEmotion = new UnitEmotion(annotation, k);
                emotions.add(unitEmotion);
            }
        });
        annotation.setEmotions(emotions);

        Set<UnitValuation> valuations = new LinkedHashSet<>();
        valuatesMap.forEach((k,v)->{
            if(v.isSelected()){
                UnitValuation unitValuation = new UnitValuation(annotation, k);
                valuations.add(unitValuation);
            }
        });
        annotation.setValuations(valuations);

        annotation.setMarkedness((Markedness) markednessCombo.getSelectedItem());
        // TODO ustawienie właściciela
        annotation.setExample1(example1.getText());
        annotation.setExample2(example2.getText());
    }

    private JComboBox createMarkednessCombo() {
        JComboBox comboBox = new JComboBox();
        List<Markedness> markednesses = findMarkedness();
        comboBox.addItem(null);
        for(Markedness markedness : markednesses) {
            comboBox.addItem(markedness);
        }
        comboBox.setRenderer(new MarkednessRenderer());
        return comboBox;
    }

    private List<Markedness> findMarkedness() {
        return (List<Markedness>) RemoteService.dictionaryServiceRemote.findDictionaryByClass(Markedness.class);
    }

    private void setAnnotationEnabled() {
        boolean enabled = canEditingAnnotation();
        if(!enabled) {
            clearFields();
        } else {
            editedAnnotation.setEmotionalCharacteristic(enabled);
            loadAnnotation(editedAnnotation);
        }
        setEnableEditing(enabled);
    }

    private boolean canEditingAnnotation() {
        return notMarkedRadio.isSelected();
    }

    private void setEnableEditing(boolean enabled) {
        emotionsMap.forEach((k, emotionCheckBox) -> emotionCheckBox.setEnabled(enabled));
        valuatesMap.forEach((k, valuationCheckBox) -> valuationCheckBox.setEnabled(enabled));
        markednessCombo.setEnabled(enabled);
        example1.setEnabled(enabled);
        example2.setEnabled(enabled);
    }

    private void clearFields() {
        emotionsMap.forEach((k, emotionCheckBox) -> emotionCheckBox.setSelected(false));
        valuatesMap.forEach((k, valuationCheckBox)->valuationCheckBox.setSelected(false));
        markednessCombo.setSelectedItem(null);
        example1.setText("");
        example2.setText("");
    }

    private JPanel createStatusPanel() {
        final int WIDTH = 275;
        final int HEIGHT = 55;

        JPanel panel = new JPanel(new GridLayout(0,2));
        panel.add(neutralRadio);
        panel.add(notMarkedRadio);

        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setTitledBorder(LABEL_STATUS, panel);
        return panel;
    }

    private JPanel createOwnerPanel() {
        final int WIDTH = 275;
        final int HEIGHT = 55;

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(ownerLabel);

        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setTitledBorder(LABEL_OWNER, panel);
        return panel;
    }

    private JPanel createMarkednessPanel() {
        final int WIDTH = 560;
        final int HEIGHT = 50;

        JPanel panel = new JPanel(new GridLayout(1,1));
        panel.add(markednessCombo);

        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setTitledBorder(LABEL_MARKEDNESS, panel);
        return panel;
    }

    // TODO poprawić ten panel
    private JPanel createExamplesPanel() {
        final int WIDTH = 560;
        final int HEIGHT = 120;
        final int EXAMPLE_WIDTH = 520;
        final int EXAMPLE_HEIGHT = 40;
        final int LABEL_WIDTH = WIDTH - EXAMPLE_WIDTH;
        final String EXAMPLE1 = "#1:";
        final String EXAMPLE2 = "#2:";
        // TODO być może będzie trzeba zmienic układ

        JPanel panel = new JPanel(new RiverLayout());
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        example1.setPreferredSize(new Dimension(EXAMPLE_WIDTH, EXAMPLE_HEIGHT));
        example2.setPreferredSize(new Dimension(EXAMPLE_WIDTH, EXAMPLE_HEIGHT));

        JLabel example1Label = new JLabel(EXAMPLE1);
        example1Label.setSize(LABEL_WIDTH, example1Label.getHeight());
        JLabel example2Label = new JLabel(EXAMPLE2);
        example2Label.setSize(LABEL_WIDTH, example2Label.getHeight());

        panel.add(example1Label);
        panel.add(RiverLayout.HFILL,example1);
        panel.add(RiverLayout.LINE_BREAK,example2Label);
        panel.add(RiverLayout.HFILL, example2);

        setTitledBorder(LABEL_EXAMPLE, panel);
        return panel;
    }

    private EmotionsListPanel createListPanel() {
        final int WIDTH = 560;
        final int HEIGHT = 100;
        EmotionsListPanel panel = new EmotionsListPanel();
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // TODO dorobić etykietę
        setTitledBorder("Adnotacje", panel);
        return panel;
    }

    private void setTitledBorder(final String title, JPanel panel){
        panel.setBorder(new TitledBorder(title));
    }

    public void load(Sense sense) {
        listPanel.loadAnnotations(sense.getId());
    }

    private JPanel createEmotionsPanel(List<Emotion> emotions) {
        final int WIDTH = 560;
        final int HEIGHT = 100;
        final int COLUMNS  = 3;
        // TODO zmienić ten układ na coś bardziej dynamicznego
        JPanel panel = new JPanel(new GridLayout(0, COLUMNS, 0 ,3));

        for(Emotion emotion : emotions) {
            String emotionName = LocalisationManager.getInstance().getLocalisedString(emotion.getName());
            JCheckBox checkBox = new JCheckBox(emotionName);
            panel.add(checkBox);
            emotionsMap.put(emotion, checkBox);
        }

        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        setTitledBorder(LABEL_EMOTIONS, panel);
        return panel;
    }

    private JPanel createValuationsPanel(List<Valuation> valuations) {
        final int WIDTH = 560;
        final int HEIGHT = 120;
        final int COLUMNS = 3;

        JPanel panel = new JPanel(new GridLayout(0, COLUMNS));

        for(Valuation valuation : valuations){
            String valuationName = LocalisationManager.getInstance().getLocalisedString(valuation.getName());
            JCheckBox checkBox = new JCheckBox(valuationName);
            panel.add(checkBox);
            valuatesMap.put(valuation, checkBox);
        }

        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setTitledBorder(LABEL_VALUATIONS, panel);
        return panel;
    }

    public void loadAnnotation(EmotionalAnnotation annotation) {
        // TODO zrobić ładowanie anotacji
        if(annotation != null) {
            this.editedAnnotation = annotation;
            setStatus(annotation);
            setOwner(annotation);
            setEmotions(annotation);
            setValuations(annotation);
            setMarkedness(annotation);

            setEnableEditing(canEditingAnnotation());
            setStatusEnabled(true);
        } else {
            setStatusEnabled(false);
        }
    }

    private void setStatusEnabled(boolean enabled) {
        neutralRadio.setEnabled(enabled);
        notMarkedRadio.setEnabled(enabled);
    }

    private void setMarkedness(EmotionalAnnotation annotation) {
        markednessCombo.setSelectedItem(annotation.getMarkedness());
    }

    // TODO być może będzie trzeba
    private void setStatus(EmotionalAnnotation annotation){
        if(annotation.hasEmotionalCharacteristic()) {
            notMarkedRadio.setSelected(true);
        } else {
            neutralRadio.setSelected(true);
        }
    }

    private void setOwner(EmotionalAnnotation annotation) {
        User owner = annotation.getOwner();
        String ownerName = owner.getFullname();
        ownerLabel.setText(ownerName);
    }

    private void setEmotions(EmotionalAnnotation annotation) {
        // clear emotions checkBoxes
        emotionsMap.forEach((k, emotionsCheckBox) -> emotionsCheckBox.setSelected(false));
        for(UnitEmotion emotion : annotation.getEmotions()){
            emotionsMap.get(emotion.getEmotion()).setSelected(true);
        }
    }

    private void setValuations(EmotionalAnnotation annotation) {
        valuatesMap.forEach((k, valuationCheckBox) -> valuationCheckBox.setSelected(false));
        for(UnitValuation valuation : annotation.getValuations()){
            valuatesMap.get(valuation.getValuation()).setSelected(true);
        }
    }

    private List<Emotion> getEmotions() {
        return (List<Emotion>) RemoteService.dictionaryServiceRemote.findDictionaryByClass(Emotion.class);
    }

    private List<Valuation> getValuations(){
        return (List<Valuation>) RemoteService.dictionaryServiceRemote.findDictionaryByClass(Valuation.class);
    }

    private class EmotionsListPanel extends JPanel {

        private JList annotationsList;
        private DefaultListModel listModel;
        private List<EmotionalAnnotation> annotations;

        public List<EmotionalAnnotation> getAnnotations() {
            return annotations;
        }

        public EmotionsListPanel() {
            setLayout(new BorderLayout());
            initComponents();
            JPanel buttonsPanel = createButtonsPanel();
            add(annotationsList, BorderLayout.CENTER);
            add(buttonsPanel, BorderLayout.EAST);

        }

        private void initComponents(){
            annotationsList = new JList();
            annotationsList.setCellRenderer(new AnnotationRenderer());
            annotationsList.addListSelectionListener(e -> loadSelectedAnnotation());
            listModel = new DefaultListModel();
            annotationsList.setModel(listModel);
        }

        private void loadSelectedAnnotation(){
            EmotionalAnnotation annotation = (EmotionalAnnotation) annotationsList.getSelectedValue();
            EmotionsPropertiesPanel.this.loadAnnotation(annotation);
        }

        private List<EmotionalAnnotation> findEmotionalAnnotation(Long senseId){
            return RemoteService.senseRemote.getEmotionalAnnotations(senseId);
        }

        public void loadAnnotations(Long senseId) {
            listModel.clear();
            if(senseId == null){
                return;
            }
            annotations = findEmotionalAnnotation(senseId);
            for(EmotionalAnnotation annotation : annotations){
                listModel.addElement(annotation);
            }
            if(!annotations.isEmpty()) {
                annotationsList.setSelectedIndex(0);
            }
        }

        private JPanel createButtonsPanel(){
            JButton addButton = MButton.buildAddButton()
                    .withToolTip(Labels.ADD)
                    .withActionListener(e->addEmotion());
            // TODO dorobić etykietę
            JButton removeButton = MButton.buildRemoveButton()
                    .withToolTip("Usuń")
                    .withActionListener(e->removeEmotion());

            MComponentGroup panel = new MComponentGroup(addButton, removeButton);
            panel.withVerticalLayout();
            return panel;
        }

        private void addEmotion(){
            throw new NotImplementedException();
        }

        private void removeEmotion(){
            throw new NotImplementedException();
        }
    }

    private class AnnotationRenderer implements ListCellRenderer<EmotionalAnnotation> {

        private JLabel label = new JLabel();

        @Override
        public Component getListCellRendererComponent(JList<? extends EmotionalAnnotation> list, EmotionalAnnotation value, int index, boolean isSelected, boolean cellHasFocus) {
            label.setText(value.getOwner().getFullname());
            return label;
        }
    }

    private class MarkednessRenderer implements ListCellRenderer<Markedness> {

        private JLabel label = new JLabel();

        @Override
        public Component getListCellRendererComponent(JList<? extends Markedness> list, Markedness value, int index, boolean isSelected, boolean cellHasFocus) {
            if(value == null){
                // TODO dorobić etykietę
                label.setText("Brak");
            } else {
                String name = LocalisationManager.getInstance().getLocalisedString(value.getValue());
                label.setText(name);
            }
            return label;
        }
    }

}