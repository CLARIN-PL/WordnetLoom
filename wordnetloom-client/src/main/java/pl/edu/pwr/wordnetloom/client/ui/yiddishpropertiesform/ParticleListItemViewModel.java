package pl.edu.pwr.wordnetloom.client.ui.yiddishpropertiesform;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.edu.pwr.wordnetloom.client.model.YiddishParticle;


public class ParticleListItemViewModel implements ViewModel {

    private YiddishParticle field;
    private StringProperty nameProperty = new SimpleStringProperty();

    public ParticleListItemViewModel(YiddishParticle i) {
        this.field = i;
        String txt = i.getParticle().getValue() +" (" + i.getParticle().getType() + ")";
        nameProperty.set(txt);
    }

    public YiddishParticle getItem() {
        return field;
    }

    public Property<String> dictionaryProperty() {
        return nameProperty;
    }
}

