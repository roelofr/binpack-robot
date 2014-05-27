package kta02.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import kta02.domein.Artikel;
import kta02.domein.Bestelling;
import kta02.domein.PackageLocation;
import kta02.warehouse.Warehouse;

/**
 *
 * @author Roelof
 */
public final class XMLPicker extends JFileChooser
{

    public static void createPickerAndLoader(final Warehouse warehouse, final MainGUI mainGUI)
    {
        new Thread(new Runnable()
        {
            private void sleep(long time)
            {
                try
                {
                    Thread.sleep(time);
                } catch (InterruptedException e)
                {

                }
            }

            @Override
            public void run()
            {
                LoadingDialog ld = new LoadingDialog("Bestandenlijst laden...");
                sleep(100);
                XMLPicker pick = new XMLPicker(warehouse, mainGUI);
                sleep(100);
                ld.dispose();
                pick.openDialog();
            }
        }).start();
    }

    private JFileChooser fileChooser;

    private final Warehouse warehouse;
    private final MainGUI mainGUI;

    private final XMLPicker thisInstance = this;

    public XMLPicker(Warehouse warehouse, MainGUI mainGUI)
    {
        super();

        this.warehouse = warehouse;
        this.mainGUI = mainGUI;

    }

    public void openDialog()
    {
        setFileFilter(new FileNameExtensionFilter("XML Bestanden", "xml"));
        //setAcceptAllFileFilterUsed(false);
        //setFileSelectionMode(JFileChooser.FILES_ONLY);
        addActionListener(new PickerListener());
        showOpenDialog(mainGUI);
    }

    class PickerListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getActionCommand() == JFileChooser.APPROVE_SELECTION)
            {
                File currentFile = getSelectedFile();
                if (currentFile == null)
                {
                    return;
                }
                String extension = "";
                int i = currentFile.getName().lastIndexOf('.');
                if (i > 0)
                {
                    extension = currentFile.getName().substring(i + 1);
                }
                if (!currentFile.exists() || !extension.toLowerCase().equals("xml"))
                {
                    return;
                }
                warehouse.setXMLFile(currentFile);
            }
        }

    }
}
