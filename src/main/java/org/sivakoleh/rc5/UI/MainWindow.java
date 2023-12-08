package org.sivakoleh.rc5.UI;

import org.sivakoleh.rc5.IO.FileHelper;
import org.sivakoleh.rc5.logic.DataGenerator;
import org.sivakoleh.rc5.logic.KeySize;
import org.sivakoleh.rc5.logic.RC5CoderCBCPadWrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import org.apache.commons.lang3.time.StopWatch;

public class MainWindow {
    private final RC5CoderCBCPadWrapper rc5CoderCBCPadWrapper;
    private final KeySize keySize;
    private final DataGenerator dataGenerator;
    private final FileHelper fileHelper;

    // States
    private byte[] data;

    // UI components
    private final JFrame jFrame;
    private JPanel jPanel;
    private JButton jChooseDataFileButton;
    private JTextField jInputKeyPhraseTextField;
    private JButton jEncryptButton;
    private JButton jDecryptButton;

    // Static fields
    private static final int windowHeight = 200;
    private static final int windowWidth = 700;

    public MainWindow(
            RC5CoderCBCPadWrapper rc5CoderCBCPadWrapper,
            KeySize keySize,
            DataGenerator dataGenerator,
            FileHelper fileHelper) {

        this.rc5CoderCBCPadWrapper = rc5CoderCBCPadWrapper;
        this.keySize = keySize;
        this.dataGenerator = dataGenerator;
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
        jPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        jPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        jChooseDataFileButton = new JButton("Choose data file");
        jChooseDataFileButton.addActionListener(new ChooseDataFileButtonActionListener(System.out));
        jPanel.add(jChooseDataFileButton);

        jInputKeyPhraseTextField = new JTextField("Input key..");
        jPanel.add(jInputKeyPhraseTextField);

        jEncryptButton = new JButton("Encrypt data");
        jEncryptButton.addActionListener(new EncryptButtonActionListener(System.out));
        jPanel.add(jEncryptButton);

        jDecryptButton = new JButton("Decrypt data");
        jDecryptButton.addActionListener(new DecryptButtonActionListener(System.out));
        jPanel.add(jDecryptButton);

        jFrame.add(jPanel, BorderLayout.CENTER);
    }

    private class ChooseDataFileButtonActionListener implements ActionListener {
        private final JFileChooser jFileChooser;
        private final PrintStream log;

        public ChooseDataFileButtonActionListener(PrintStream log) {
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

            data = fileHelper.readBytes(file);
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
        private final PrintStream log;
        private final StopWatch stopWatch;

        public EncryptButtonActionListener(PrintStream log) {
            this.log = log;
            this.stopWatch = new StopWatch();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            log.println("Starting timer..");
            stopWatch.start();

            // Key
            String keyPhrase = jInputKeyPhraseTextField.getText();
            byte[] key = dataGenerator.generateKey(keyPhrase, keySize);

            // IV
            int blockSize = rc5CoderCBCPadWrapper.getBlockSize();
            byte[] initializationVector = dataGenerator.generateInitializationVector(blockSize);

            byte[] dataEncrypted = rc5CoderCBCPadWrapper.encrypt(data, key, initializationVector);
            fileHelper.write(dataEncrypted, "./data/dataEncrypted.txt");

            stopWatch.stop();
            log.println("Time elapsed: " + stopWatch);
        }
    }

    private class DecryptButtonActionListener implements ActionListener {
        private final PrintStream log;
        private final StopWatch stopWatch;

        public DecryptButtonActionListener(PrintStream log) {
            this.log = log;
            this.stopWatch = new StopWatch();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            log.println("Starting timer..");
            stopWatch.start();

            // Key
            String keyPhrase = jInputKeyPhraseTextField.getText();
            byte[] key = dataGenerator.generateKey(keyPhrase, keySize);

            byte[] dataDecrypted = rc5CoderCBCPadWrapper.decrypt(data, key);
            fileHelper.write(dataDecrypted, "./data/dataDecrypted.txt");

            stopWatch.stop();
            log.println("Time elapsed: " + stopWatch);
        }
    }
}
