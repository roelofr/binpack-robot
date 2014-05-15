package kta02.gui;

import database.DatabaseProcessor;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;
import kta02.domein.Bestelling;
import kta02.domein.PackageLocation;
import kta02.warehouse.Warehouse;
import xml.XMLReader;

public class XMLPicker extends JDialog implements ActionListener
{

    private JFileChooser file;
    private JCheckBox debugBTN;
    private JLabel debugTXT;

    public XMLPicker()
    {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setTitle("Start AS/RS!");
        setLayout(new BorderLayout(16, 16));
        setLocationRelativeTo(null);
        setVisible(true);

        File myDocuments = new JFileChooser().getFileSystemView().getDefaultDirectory();

        // Create the JFileChooser
        file = new JFileChooser(myDocuments);
        file.addActionListener(this);
        file.setApproveButtonText("Open XML");
        file.setFileFilter(new FileNameExtensionFilter("XML Files", "xml"));

        add(new JLabel("Please select an XML file to process"), BorderLayout.NORTH);
        add(file, BorderLayout.CENTER);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae)
    {

        if (ae.getSource() == file)
        {
            if (ae.getActionCommand().equals(JFileChooser.APPROVE_SELECTION))
            {
                File currentFile = file.getSelectedFile();

                if (!currentFile.exists())
                {
                    throw new NullPointerException("File doesn't exist");
                }

                XMLReader reader = new XMLReader(currentFile.getPath());

                Bestelling bestelling = reader.readFromXml();

                if (Warehouse.DEBUG)
                {
                    bestelling.print();
                }
                DatabaseProcessor dbProcessor = new DatabaseProcessor(bestelling);

                try
                {
                    ArrayList<PackageLocation> cake = dbProcessor.processArticles();
                    System.out.println(cake);
                } catch (SQLException ex)
                {
                    System.err.println(ex.getMessage());
                }

            } else if (ae.getActionCommand().equals(JFileChooser.CANCEL_SELECTION))
            {
                this.setVisible(false);
                this.dispose();
            }

        }

    }
}
