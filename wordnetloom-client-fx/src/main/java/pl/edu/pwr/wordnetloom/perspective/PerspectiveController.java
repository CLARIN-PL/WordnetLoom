package pl.edu.pwr.wordnetloom.perspective;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import org.springframework.beans.factory.annotation.Autowired;
import pl.edu.pwr.wordnetloom.remote.Loggable;
import pl.edu.pwr.wordnetloom.search.SearchView;
import pl.edu.pwr.wordnetloom.search.results.SearchResultListView;
import pl.edu.pwr.wordnetloom.visualisation.GraphView;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class PerspectiveController implements Loggable, Initializable {

    @FXML
    private SplitPane leftPanel;

    @FXML
    private SplitPane centerPanel;

    @FXML
    private SplitPane rightPanel;

    @Autowired
    SearchView searchView;

    @Autowired
    SearchResultListView searchResultListView;

    @Autowired
    GraphView graphView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        leftPanel.getItems().add(searchView.getView());
        leftPanel.getItems().add(searchResultListView.getView());
        centerPanel.getItems().add(graphView.getView());
    }
}
