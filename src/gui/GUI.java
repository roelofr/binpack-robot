package gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import kta02.domein.Bestelling;
import xml.XMLReader;

public class GUI extends JFrame implements ActionListener
{

    private JFileChooser file;

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
        file.setApproveButtonText("Run AS/RS!");
        file.setFileFilter(new FileNameExtensionFilter("XML Files", "xml"));

        add(file);

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

                XMLReader reader = new XMLReader(currentFile.getPath());

                Bestelling bestelling = reader.readFromXml();

                // debug
                bestelling.print();
            }
            else if (ae.getActionCommand() == JFileChooser.CANCEL_SELECTION)
            {
                System.exit(0);
            }

        }

    }
}
