package gui;

import database.DatabaseProcessor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import kta02.domein.Artikel;
import kta02.domein.PackageLocation;
import kta02.warehouse.Warehouse;

public class GUI extends JFrame implements ActionListener
{

    private Boolean DEBUG = kta02.warehouse.Warehouse.DEBUG;
    private JFileChooser file;
    private JCheckBox debugBTN;
    private JLabel debugTXT;

    public GUI()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setTitle("Start AS/RS!");
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);
        setVisible(true);

        // Thanks Google (source: http://goo.gl/XeKYSE )
        File currentExecutable = new File(GUI.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String filePath = currentExecutable.getParentFile().getParentFile().getPath();
        char fileSep = File.separatorChar;
        filePath = filePath + fileSep + "src" + fileSep + "xml";

        // Create the JFileChooser
        file = new JFileChooser(new File(filePath));
        file.addActionListener(this);
        file.setApproveButtonText("Process Order");
        file.setFileFilter(new FileNameExtensionFilter("XML Files", "xml"));
        file.setAcceptAllFileFilterUsed(false);

        debugBTN = new JCheckBox();
        debugTXT = new JLabel("Debug?");

        add(file);
        add(debugTXT);
        add(debugBTN);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae)
    {

        if (ae.getSource() == file)
        {
            if (ae.getActionCommand() == JFileChooser.APPROVE_SELECTION)
            {
                File currentFile = file.getSelectedFile();

                if (!currentFile.exists())
                {
                    throw new NullPointerException("File doesn't exist");
                }

                JOptionPane.showMessageDialog(this, "Not implemented");
                return;
                /*
                 Warehouse.setXMLFile(currentFile);

                 if (debugBTN.isSelected())
                 {
                 Warehouse.getBestelling().print();
                 }
                 DatabaseProcessor dbProcessor = new DatabaseProcessor(bestelling);

                 try
                 {
                    ArrayList<Artikel> cake = dbProcessor.processArticles();
                 System.out.println(cake);
                 } catch (SQLException ex)
                 {
                 System.err.println(ex.getMessage());
                 }*/

            } else if (ae.getActionCommand() == JFileChooser.CANCEL_SELECTION)
            {
                System.exit(0);
            }

        }

    }

    public void setDEBUG(Boolean DEBUG)
    {
        this.DEBUG = DEBUG;
    }
}
