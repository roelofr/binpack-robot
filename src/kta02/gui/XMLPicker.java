package kta02.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import kta02.domein.Artikel;
import kta02.domein.Bestelling;
import kta02.domein.PackageLocation;
import kta02.warehouse.Warehouse;

public class XMLPicker extends JDialog implements ActionListener
{

    private JFileChooser file;
    private JCheckBox debugBTN;
    private JLabel debugTXT;

    private Warehouse warehouse;

    public XMLPicker(Warehouse warehouse)
    {
        this.warehouse = warehouse;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setTitle("Openen");
        setLayout(new BorderLayout(0, 0));
        setLocationRelativeTo(null);
        setVisible(true);

        File myDocuments = new JFileChooser().getFileSystemView().getDefaultDirectory();

        // Create the JFileChooser
        file = new JFileChooser(myDocuments);
        file.addActionListener(this);
        file.setApproveButtonText("Openen");
        file.setDialogType(JFileChooser.OPEN_DIALOG);
        file.setFileFilter(new FileNameExtensionFilter("XML Files", "xml"));

        JLabel selectFile = new JLabel();
        selectFile.setText("Selecteer een order (XML bestand)");
        selectFile.setFont(new Font("Arial", Font.BOLD, 16));

        add(selectFile, BorderLayout.NORTH);
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
                    JOptionPane.showMessageDialog(this, "Het geselecteerde bestand kon niet worden gevonden!", "Bestand niet gevonden", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                warehouse.setXMLFile(currentFile);
                this.setVisible(false);
                this.dispose();

            } else if (ae.getActionCommand().equals(JFileChooser.CANCEL_SELECTION))
            {
                this.setVisible(false);
                this.dispose();
            }

        }

    }
}
