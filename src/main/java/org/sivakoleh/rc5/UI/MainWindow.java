package org.sivakoleh.rc5.UI;

import org.sivakoleh.rc5.IO.FileHelper;
import org.sivakoleh.rc5.logic.RC5CoderCBCPadWrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;

public class MainWindow {
    private final RC5CoderCBCPadWrapper rc5CoderCBCPadWrapper;
    private final FileHelper fileHelper;

    // States
    private byte[] data;
    private byte[] key;
    private byte[] initializationVector;

    // UI components
    private final JFrame jFrame;
    private JPanel jPanel;
    private JButton jChooseDataFileButton;
    private JButton jChooseKeyFileButton;
    private JButton jChooseIVFileButton;
    private JButton jEncryptButton;
    private JButton jDecryptButton;

    // Static fields
    private static final int windowHeight = 300;
    private static final int windowWidth = 900;

    public MainWindow(RC5CoderCBCPadWrapper rc5CoderCBCPadWrapper, FileHelper fileHelper) {
        this.rc5CoderCBCPadWrapper = rc5CoderCBCPadWrapper;
        this.fileHelper = fileHelper;

        jFrame = new JFrame("MD5 Hasher");
        jFrame.setSize(windowWidth, windowHeight);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    public void show() {
        jFrame.setVisible(true);
    }

    private void initComponents() {
        jPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        jPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        jChooseDataFileButton = new JButton("Choose data file");
        jChooseDataFileButton.addActionListener(new ChooseFileButtonActionListener(FileDestination.DATA, System.out));
        jPanel.add(jChooseDataFileButton);

        jChooseKeyFileButton = new JButton("Choose key file");
        jChooseKeyFileButton.addActionListener(new ChooseFileButtonActionListener(FileDestination.KEY, System.out));
        jPanel.add(jChooseKeyFileButton);

        jChooseIVFileButton = new JButton("Choose IV file");
        jChooseIVFileButton.addActionListener(new ChooseFileButtonActionListener(FileDestination.IV, System.out));
        jPanel.add(jChooseIVFileButton);

        jEncryptButton = new JButton("Encrypt data");
        jEncryptButton.addActionListener(new EncryptButtonActionListener());
        jPanel.add(jEncryptButton);

        jDecryptButton = new JButton("Decrypt data");
        jDecryptButton.addActionListener(new DecryptButtonActionListener());
        jPanel.add(jDecryptButton);

        jFrame.add(jPanel, BorderLayout.CENTER);
    }

    private enum FileDestination { DATA, KEY, IV }

    private class ChooseFileButtonActionListener implements ActionListener {

        private final FileDestination fileDestination;
        private final JFileChooser jFileChooser;
        private final PrintStream log;

        public ChooseFileButtonActionListener(FileDestination fileDestination, PrintStream log) {
            this.fileDestination = fileDestination;
            this.jFileChooser = new JFileChooser("./data");
            this.log = log;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            File file = getChosenFile();
            if (file == null) {
                log.println("Aborting");
                return;
            }

            byte[] bytes = fileHelper.readBytes(file);

            switch (fileDestination) {
                case DATA -> data = bytes;
                case KEY -> key = bytes;
                case IV -> initializationVector = bytes;
            }
        }

        private File getChosenFile() {
            File file = null;

            int dialogExitStatus = jFileChooser.showOpenDialog(jFrame);
            if (dialogExitStatus == JFileChooser.APPROVE_OPTION) {
                log.println("File selected successfully");
                file = jFileChooser.getSelectedFile();
            } else {
                log.println("Could not select a file");
            }

            return file;
        }
    }

    private class EncryptButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            byte[] dataEncrypted = rc5CoderCBCPadWrapper.encrypt(data, key, initializationVector);
            fileHelper.writeAsText(dataEncrypted, "./data/dataEncrypted.txt");
        }
    }

    private class DecryptButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            byte[] dataDecrypted = rc5CoderCBCPadWrapper.decrypt(data, key, initializationVector);
            fileHelper.writeAsText(dataDecrypted, "./data/dataDecrypted.txt");
        }
    }
}
