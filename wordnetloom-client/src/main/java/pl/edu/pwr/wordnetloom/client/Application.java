package pl.edu.pwr.wordnetloom.client;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.rootpane.WebFrame;
import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;
import pl.edu.pwr.wordnetloom.client.plugins.login.window.LoginWindow;
import pl.edu.pwr.wordnetloom.client.remote.RemoteConnectionProvider;
import pl.edu.pwr.wordnetloom.client.remote.RemoteService;
import pl.edu.pwr.wordnetloom.client.systems.managers.*;
import pl.edu.pwr.wordnetloom.client.utils.Messages;
import pl.edu.pwr.wordnetloom.client.workbench.implementation.PanelWorkbench;
import pl.edu.pwr.wordnetloom.client.workbench.interfaces.Loggable;
import pl.edu.pwr.wordnetloom.client.workbench.interfaces.Workbench;

import javax.swing.*;
import java.lang.Thread.UncaughtExceptionHandler;


public class Application implements Loggable {

    public static final String PROGRAM_NAME_VERSION = "WordnetLoom 2.0";

    public static void main(String[] args) {

        WebLookAndFeel.install();
        IconFontSwing.register(FontAwesome.getIconFont());
        Application app = new Application();
        app.login();

    }

    private void login() {
        LoginWindow login = new LoginWindow(new WebFrame());
        login.btnOkActionListener(l -> {
            if (login.login()) {
                LocalisationManager.getInstance().load(login.getSelectedLanguage());
                initialise();
            }
        });
        login.setVisible(true);
    }

    private void start() {
        try {

            Workbench workbench = new PanelWorkbench(PROGRAM_NAME_VERSION);
            workbench.refreshUserBar(RemoteConnectionProvider.getInstance().getUser());
            workbench.setVisible(true);

        } catch (Exception ex) {
            logger().error("Workbench initialization error:", ex);
        }
    }

    private void initialise() {

        Thread managers = new Thread(() -> {

            LexiconManager.getInstance().loadLexicons(RemoteService.lexiconServiceRemote.findAll());
            PartOfSpeechManager.getInstance();
            DomainManager.getInstance();
            RelationTypeManager.getInstance().loadRelationTypes(RemoteService.relationTypeRemote.findAll());
            start();
        }, "Mangers Thread");

        managers.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            private boolean first = true;

            @Override
            public void uncaughtException(Thread t, Throwable tt) {
                if (first) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null,
                                Messages.ERROR_UNABLE_TO_CONNECT_TO_SERVER,
                                Application.PROGRAM_NAME_VERSION,
                                JOptionPane.ERROR_MESSAGE);
                    });
                    first = false;
                }
                try {
                    Thread.sleep(13000);
                } catch (InterruptedException e) {
                }
                System.exit(1);
            }
        });

        managers.start();
    }


}
