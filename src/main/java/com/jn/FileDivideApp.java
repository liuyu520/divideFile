package com.jn;

import com.common.bean.DividedFileBean;
import com.common.thread.SharedMemoryArea;
import com.common.util.SystemHWUtil;
import com.common.util.WindowUtil;
import com.io.hw.file.util.FileUtils;
import com.jn.dict.Constant;
import com.string.widget.util.ValueWidget;
import com.swing.component.AssistPopupTextArea;
import com.swing.component.AssistPopupTextField;
import com.swing.component.ComponentUtil;
import com.swing.dialog.DialogUtil;
import com.swing.dialog.GenericFrame;
import com.swing.messagebox.GUIUtil23;
import com.time.util.TimeHWUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileDivideApp extends GenericFrame {

    public static final String LEVEL_DIVIDE_SIZE_LABEL = "M";
    public static final String LEVEL_DIVIDE_FILE_QUANTITY_LABEL = "个";
    public static final int LEVEL_DIVIDE_SIZE_FLAG = 1;
    public static final int LEVEL_DIVIDE_FILES_QUANTITY_FLAG = 2;
    public static final String LEVEL_DIVIDE_SINGLE_SIZE_RADIO = "单个文件大小";
    public static final String LEVEL_DIVIDE_FILES_QUANTITY_RADIO = "文件个数";
    public static final String DIVIDED_FOLDER = "divided";
    public static final int QUANTITY_LIMIT = 127;
    private static final long serialVersionUID = -2604378941342923081L;
    private JPanel contentPane;
    private JTextField sourceTF;
    private JTextField fileQuantityTF;
    private JTextField targetTF;
    private JLabel fileQuantityLabel;
    private JTextArea resultTA;
    private JRadioButton singleSizeRadioButton;
    private JRadioButton fileQuantityRadioButton;
    private StringBuffer resultBuffer = new StringBuffer();
    /***
     * 分割按钮
     */
    private JButton divideButton;
    private JButton pasteButton;
    private JPanel panel1;
    private JButton cleanupButton;
    private JButton sizeButton;
    private JButton openFolderButton;
    private JButton openFileButton;
    private JButton deleteDividedButton;
    /***
     * 分割后的文件所在文件夹（目录）
     */
    private File targetFolder;
    private JPanel panel;
    private JLabel label;
    private JCheckBox resumeCheckBox;
    private JButton pauseButton;
    /***
     * 是否正在分割
     */
    private boolean isDividingNow = false;

    /**
     * Create the frame.
     */
    public FileDivideApp() {
        DialogUtil.lookAndFeel2();
        setTitle("文件分割器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init33(this);

//		setBounds(100, 100, );
        setLoc(800, 450);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
        gbl_contentPane.rowHeights = new int[]{54, 0, 44, 0, 0, 0, 0, 59, 0};
        gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0,
                Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 1.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        JLabel sourceLabel = new JLabel("原文件");
        GridBagConstraints gbc_sourceLabel = new GridBagConstraints();
        gbc_sourceLabel.insets = new Insets(0, 0, 5, 5);
        gbc_sourceLabel.anchor = GridBagConstraints.EAST;
        gbc_sourceLabel.gridx = 0;
        gbc_sourceLabel.gridy = 0;
        contentPane.add(sourceLabel, gbc_sourceLabel);

        sourceTF = new AssistPopupTextField();
        GridBagConstraints gbc_sourceTF = new GridBagConstraints();
        gbc_sourceTF.gridwidth = 2;
        gbc_sourceTF.insets = new Insets(0, 0, 5, 5);
        gbc_sourceTF.fill = GridBagConstraints.HORIZONTAL;
        gbc_sourceTF.gridx = 1;
        gbc_sourceTF.gridy = 0;
        contentPane.add(sourceTF, gbc_sourceTF);
        sourceTF.setColumns(10);
        drag(sourceTF);
        drag(sourceLabel, sourceTF);
        sourceTF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 回车触发聚焦
                fileQuantityTF.requestFocus();
            }
        });
        sourceTF.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                // System.out.println("remove");
                try {
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                // System.out.println("insert");
                ComponentUtil.assistantTF(sourceTF, e);// Assist path
                // complement

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // System.out.println("change");
            }
        });

        sourceTF.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                String fatherFolder = SystemHWUtil.getParentDir(sourceTF
                        .getText());
                String targetFolderStr = targetTF.getText();
                if (ValueWidget.isNullOrEmpty(targetFolderStr)
                        && !ValueWidget.isNullOrEmpty(fatherFolder)) {
                    if (fatherFolder.endsWith(SystemHWUtil.SEPARATOR)) {
                        targetTF.setText(fatherFolder + DIVIDED_FOLDER);
                    } else {
                        targetTF.setText(fatherFolder + SystemHWUtil.SEPARATOR
                                + DIVIDED_FOLDER);
                    }
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
        });
        JButton browserSourceButton = new JButton(SystemHWUtil.LABEL_BROWSE);
        browserSourceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /* boolean isSuccess = */
                DialogUtil.browser3(sourceTF,
                        JFileChooser.FILES_ONLY, FileDivideApp.this);
            }
        });
        GridBagConstraints gbc_browserSourceButton = new GridBagConstraints();
        gbc_browserSourceButton.insets = new Insets(0, 0, 5, 5);
        gbc_browserSourceButton.gridx = 3;
        gbc_browserSourceButton.gridy = 0;
        contentPane.add(browserSourceButton, gbc_browserSourceButton);

        pasteButton = new JButton(SystemHWUtil.LABEL_PASTE);// for source File
        // Textfield
        pasteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String clip = WindowUtil.getSysClipboardText();
                if (!ValueWidget.isNullOrEmpty(clip)) {
                    sourceTF.setText(clip);
                    sourceTF.requestFocus();
                }
            }
        });
        GridBagConstraints gbc_pasteButton = new GridBagConstraints();
        gbc_pasteButton.insets = new Insets(0, 0, 5, 0);
        gbc_pasteButton.gridx = 4;
        gbc_pasteButton.gridy = 0;
        contentPane.add(pasteButton, gbc_pasteButton);

        panel1 = new JPanel();
        GridBagConstraints gbc_panel1 = new GridBagConstraints();
        gbc_panel1.gridwidth = 3;
        gbc_panel1.insets = new Insets(0, 0, 5, 5);
        gbc_panel1.fill = GridBagConstraints.BOTH;
        gbc_panel1.gridx = 1;
        gbc_panel1.gridy = 1;
        contentPane.add(panel1, gbc_panel1);
        // 清空原文件
        cleanupButton = new JButton("清空");
        cleanupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sourceTF.setText(SystemHWUtil.EMPTY);
                resultBuffer.setLength(0);// clean up StringBuffer
            }
        });
        panel1.add(cleanupButton);
        // 获取原文件的大小
        sizeButton = new JButton("大小");
        sizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sourceFileStr = sourceTF.getText();
                // 无输入
                if (ValueWidget.isNullOrEmpty(sourceFileStr)) {
                    return;
                }
                long size = FileUtils.getFileSize2(sourceFileStr);
                addResult("File \"" + sourceFileStr + "\" has "
                        + FileUtils.formatFileSize(size) + " bytes");
            }
        });
        panel1.add(sizeButton);

        openFileButton = new JButton("浏览原文件");
        openFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileUtils.open_file(sourceTF.getText());
            }
        });
        panel1.add(openFileButton);

        JPanel selectPanel = new JPanel();
        selectPanel.setLayout(new FlowLayout());
        GridBagConstraints gbc_selectPanel = new GridBagConstraints();
        gbc_selectPanel.gridwidth = 2;
        gbc_selectPanel.insets = new Insets(0, 0, 5, 5);
        gbc_selectPanel.fill = GridBagConstraints.NONE;
        gbc_selectPanel.gridx = 0;
        gbc_selectPanel.gridy = 2;
        contentPane.add(selectPanel, gbc_selectPanel);

        singleSizeRadioButton = new JRadioButton(LEVEL_DIVIDE_SINGLE_SIZE_RADIO);
        singleSizeRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileQuantityLabel.setText(LEVEL_DIVIDE_SIZE_LABEL);
                fileQuantityTF.requestFocus();
            }
        });
        selectPanel.add(singleSizeRadioButton);

        fileQuantityRadioButton = new JRadioButton(
                LEVEL_DIVIDE_FILES_QUANTITY_RADIO);
        fileQuantityRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileQuantityLabel.setText(LEVEL_DIVIDE_FILE_QUANTITY_LABEL);
                fileQuantityTF.requestFocus();
            }
        });
        selectPanel.add(fileQuantityRadioButton);

        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(singleSizeRadioButton);
        btnGroup.add(fileQuantityRadioButton);
        singleSizeRadioButton.setSelected(true);

        fileQuantityTF = new AssistPopupTextField();
        GridBagConstraints gbc_fileQuantityTF = new GridBagConstraints();
        gbc_fileQuantityTF.insets = new Insets(0, 0, 5, 5);
        gbc_fileQuantityTF.fill = GridBagConstraints.HORIZONTAL;
        gbc_fileQuantityTF.gridx = 2;
        gbc_fileQuantityTF.gridy = 2;
        contentPane.add(fileQuantityTF, gbc_fileQuantityTF);
        fileQuantityTF.setColumns(10);
        fileQuantityTF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 回车触发聚焦
                targetTF.requestFocus();
            }
        });

        fileQuantityLabel = new JLabel(LEVEL_DIVIDE_SIZE_LABEL);
        GridBagConstraints gbc_fileQuantityLabel = new GridBagConstraints();
        gbc_fileQuantityLabel.insets = new Insets(0, 0, 5, 5);
        gbc_fileQuantityLabel.gridx = 3;
        gbc_fileQuantityLabel.gridy = 2;
        contentPane.add(fileQuantityLabel, gbc_fileQuantityLabel);

        JLabel targetLabel = new JLabel("目标文件夹");
        GridBagConstraints gbc_targetLabel = new GridBagConstraints();
        gbc_targetLabel.insets = new Insets(0, 0, 5, 5);
        gbc_targetLabel.anchor = GridBagConstraints.EAST;
        gbc_targetLabel.gridx = 0;
        gbc_targetLabel.gridy = 3;
        contentPane.add(targetLabel, gbc_targetLabel);

        targetTF = new AssistPopupTextField();
        drag(targetTF);
        drag(targetLabel, targetTF);
        // targetTF.setText("目标文件");
        GridBagConstraints gbc_targetTF = new GridBagConstraints();
        gbc_targetTF.gridwidth = 2;
        gbc_targetTF.insets = new Insets(0, 0, 5, 5);
        gbc_targetTF.fill = GridBagConstraints.HORIZONTAL;
        gbc_targetTF.gridx = 1;
        gbc_targetTF.gridy = 3;
        contentPane.add(targetTF, gbc_targetTF);
        targetTF.setColumns(10);
        targetTF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                divideButton.doClick();
            }
        });

        JButton browserTargetButton_1 = new JButton("浏览");
        browserTargetButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				/* boolean isSuccess = */
                DialogUtil.browser3(targetTF,
                        JFileChooser.DIRECTORIES_ONLY, FileDivideApp.this);
            }
        });
        GridBagConstraints gbc_browserTargetButton_1 = new GridBagConstraints();
        gbc_browserTargetButton_1.insets = new Insets(0, 0, 5, 5);
        gbc_browserTargetButton_1.gridx = 3;
        gbc_browserTargetButton_1.gridy = 3;
        contentPane.add(browserTargetButton_1, gbc_browserTargetButton_1);

        openFolderButton = new JButton("打开文件夹");
        openFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 打开文件夹
                FileUtils.open_directory(targetTF);
            }
        });
        GridBagConstraints gbc_openFolderButton = new GridBagConstraints();
        gbc_openFolderButton.insets = new Insets(0, 0, 5, 0);
        gbc_openFolderButton.gridx = 4;
        gbc_openFolderButton.gridy = 3;
        contentPane.add(openFolderButton, gbc_openFolderButton);

        label = new JLabel("配置");
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.anchor = GridBagConstraints.EAST;
        gbc_label.insets = new Insets(0, 0, 5, 5);
        gbc_label.gridx = 0;
        gbc_label.gridy = 4;
        contentPane.add(label, gbc_label);

        panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.gridwidth = 3;
        gbc_panel.insets = new Insets(0, 0, 5, 5);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 1;
        gbc_panel.gridy = 4;
        contentPane.add(panel, gbc_panel);

        resumeCheckBox = new JCheckBox("续传模式");
        panel.add(resumeCheckBox);

        JPanel dividePanel = new JPanel();
        GridBagConstraints gbc_dividePanel = new GridBagConstraints();
        gbc_dividePanel.gridwidth = 2;
        gbc_dividePanel.insets = new Insets(0, 0, 5, 5);
        gbc_dividePanel.fill = GridBagConstraints.BOTH;
        gbc_dividePanel.gridx = 1;
        gbc_dividePanel.gridy = 5;
        contentPane.add(dividePanel, gbc_dividePanel);

        divideButton = new JButton("分割");
        divideButton.addActionListener(new ActionListener() {
            @SuppressWarnings("resource")
            public void actionPerformed(ActionEvent e) {
                if (!validate3()) {
                    return;
                }
                // Whether to suspend dividing
                SharedMemoryArea.setWillStop(false);
                pauseButton.setEnabled(true);

                final String sourceFileStr = sourceTF.getText();
                final File sourceFile = new File(sourceFileStr);

                final String targetFolderStr = targetTF.getText();
                targetFolder = new File(targetFolderStr);
                // 如果文件夹不存在
                if (!targetFolder.exists()) {// 若目标文件夹不存在，则创建
                    targetFolder.mkdirs();
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        divideEnableBtn(false);
                        try {
                            FileInputStream fin = new FileInputStream(
                                    sourceFile);
                            int divideLevle = getDivideLevel();
//							int totalSize = fin.available();//the code is wrong
                            long totalSize = sourceFile.length();
                            String fileName = sourceFile.getName();
                            System.out.println("totalSize:" + totalSize);
                            /***
                             * The actual size of the file, not including the
                             * sequence number (the first byte)
                             */
                            long singleSize;
                            long mode;
                            int quantity;
                            // 最后一个文件的大小
                            long lastSize;
                            List<DividedFileBean> dividedFiles = null;
                            String quantityStr = fileQuantityTF.getText();

                            if (divideLevle == LEVEL_DIVIDE_FILES_QUANTITY_FLAG) {// 指定文件数量
                                System.out.println("文件的数量");

                                // 分割成多少个文件
                                quantity = Integer.parseInt(quantityStr);
                                if (quantity < 2) {
                                    GUIUtil23.warningDialog("文件个数必须大于1 !");
//									divideButton.setEnabled(true);
//									deleteDividedButton.setEnabled(true);
                                    divideEnableBtn(true);
                                    return;
                                }
                                // 分割的文件个数不能大于127

                                singleSize = totalSize / quantity;
                                mode = totalSize % quantity/* 文件个数 */;
                                // 最后一个文件的大小
                                lastSize = singleSize + mode;
                                if (lastSize > 1000 * 1000 * 1000) {
                                    int result = JOptionPane.showConfirmDialog(null, "单个文件的大小已经超过1 GB,是否继续?", "确认",
                                            JOptionPane.OK_CANCEL_OPTION);
                                    if (result == JOptionPane.CANCEL_OPTION /*取消*/) {
//						                	divideButton.setEnabled(true);
//						                	deleteDividedButton.setEnabled(true);
                                        divideEnableBtn(true);
                                        return;
                                    }

                                }
                            } else {// 指定单个文件的大小
                                // 为什么要减一，因为第一个字节表示序号
                                singleSize = Integer.parseInt(quantityStr) * 1024 * 1024 - 1;
                                if (singleSize >= totalSize) {
                                    GUIUtil23.warningDialog("the size of \""
                                            + sourceFileStr
                                            + "\" is too short.");
                                    DialogUtil.focusSelectAllTF(fileQuantityTF);
//									divideButton.setEnabled(true);
//									deleteDividedButton.setEnabled(true);
                                    divideEnableBtn(true);
                                    return;
                                }
                                quantity = (int) (totalSize / singleSize);
                                System.out.println("单个文件的大小");
                                mode = totalSize % singleSize;
                                if (mode != 0) {
                                    quantity = quantity + 1;
                                    // 最后一个文件的大小
                                    lastSize = mode;
                                } else {
                                    lastSize = singleSize;
                                }
                            }
                            if (quantity > QUANTITY_LIMIT) {
                                GUIUtil23.warningDialog("file quantity is "
                                        + quantity + ", is greater than "
                                        + QUANTITY_LIMIT);
//								divideButton.setEnabled(true);
//								deleteDividedButton.setEnabled(true);
                                divideEnableBtn(true);
                                throw new RuntimeException(
                                        "quantity can't greater than "
                                                + QUANTITY_LIMIT);
                            }
                            // 如果文件的个数为0
                            if (quantity == 0) {
                                GUIUtil23.warningDialog("文件的个数为0");
//								divideButton.setEnabled(true);
//								deleteDividedButton.setEnabled(true);
                                divideEnableBtn(true);
                                return;
                            }
                            setDividingNow(true);
                            // 初始化分割后文件的信息
                            dividedFiles = initializeDividedFile(quantity,
                                    singleSize, fileName, targetFolderStr,
                                    lastSize);

                            boolean isResume = resumeCheckBox.isSelected();// 是否需要续传
                            if (!isResume) {
                                for (DividedFileBean dividedFile : dividedFiles) {
                                    File outPutFile = dividedFile
                                            .getOutPutFile();
                                    if (outPutFile.exists()) {
                                        GUIUtil23.warningDialog("File \""
                                                + outPutFile.getAbsolutePath()
                                                + "\" has exist.");
//										divideButton.setEnabled(true);
//										deleteDividedButton.setEnabled(true);
                                        divideEnableBtn(true);
                                        return;
                                    }
                                }
                            }
                            // 共分割成多少个文件
                            int size = dividedFiles.size();
                            SystemHWUtil.printList(dividedFiles, true, ",", null);
                            resultBuffer.append(
                                    "\"" + sourceFileStr + "\" 分割为" + size
                                            + "个文件：").append(SystemHWUtil.CRLF);
                            System.out.println("start to divide.......");
                            for (int i = 0; i < dividedFiles.size(); i++) {
                                if (SharedMemoryArea.isWillStop()) {//will pause
//									divideButton.setEnabled(true);
//									deleteDividedButton.setEnabled(true);
                                    divideEnableBtn(true);
                                    break;
                                }
                                DividedFileBean dividedFile = dividedFiles
                                        .get(i);
                                int sequence = dividedFile.getSequence();
                                // int startIndex = dividedFile.getStartIndex();
                                long length2 = dividedFile.getLength();
                                // byte[] bytes = new byte[length2 + 1];
                                // fin.read(bytes, startIndex, length2);
                                File outPutFile = dividedFile.getOutPutFile();

                                // 续传的情况分为两种：（1）刚传完一个文件；（2）文件传了一部分
                                if (isResume) {//续传
                                    // 不包含第一个字节
                                    long realLength3;
                                    if (outPutFile.exists()) {// 文件存在,有可能长度为
                                        // zero
                                        realLength3 = outPutFile.length();
                                        if (outPutFile.length() > 0) {// need &&
                                            // realLength3
                                            // <
                                            // length2
                                            // + 1

                                            // realLength3 = realLength3 - 1; //
                                            // Resume，说明文件没有写完
                                            if (realLength3 >= length2 + 1) {// 说明该文件不需要续传
                                                continue;
                                            }
                                        }
                                    } else {// 文件不存在
                                        realLength3 = 0L;
                                    }
                                    // Get the number of bytes which has been
                                    // written
                                    long sumBytesHasWrite = getHasWrittedLength(
                                            dividedFiles, i, realLength3);
                                    fin.skip(sumBytesHasWrite);
                                    isResume = false;
                                    if (realLength3 > 0) {// 该文件未写完，但已经写入了部分
                                        realLength3 = realLength3 - 1;// 删除第一个字节（文件序号）
                                    }
                                    write2DividedFile(fin, outPutFile, length2
                                            - realLength3, sequence, true);
                                } else {
                                    write2DividedFile(fin, outPutFile, length2,
                                            sequence, false);
                                }

                                // FileUtils.writeBytesToFile(bytes,
                                // dividedFile.getOutPutFile());

                                resultBuffer.append(
                                        dividedFile.getOutPutFile()
                                                .getAbsolutePath()).append(
                                        SystemHWUtil.CRLF);
                            }
                            fin.close();
                            System.out.println("divide successfully!");
                            addResult(null);
                            addResult(SystemHWUtil.DIVIDING_LINE);
                            setDividingNow(false);
                        } catch (NumberFormatException e1) {
                            e1.printStackTrace();
                            GUIUtil23.warningDialog(e1.getMessage());
                        } catch (FileNotFoundException e2) {
                            e2.printStackTrace();
                            GUIUtil23.warningDialog(e2.getMessage());
                        } catch (IOException e3) {
                            e3.printStackTrace();
                            GUIUtil23.warningDialog(e3.getMessage());
                        }
//						divideButton.setEnabled(true);
//						deleteDividedButton.setEnabled(true);
                        divideEnableBtn(true);
                    }
                }).start();
            }
        });
        dividePanel.add(divideButton);

        deleteDividedButton = new JButton("删除分割后的文件");
        deleteDividedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ((!ValueWidget.isNullOrEmpty(targetFolder))
                        && targetFolder.exists()) {
                    boolean isSuccess = FileUtils.deleteDir(targetFolder);
                    if (isSuccess) {
                        GUIUtil23.infoDialog("delete successfully!");
                    } else {
                        GUIUtil23.errorDialog("failed.");
                    }

                } else {
                    String errorMesg = "没有可以删除的文件";
                    GUIUtil23.errorDialog(errorMesg);
                }
            }
        });
        dividePanel.add(deleteDividedButton);

        pauseButton = new JButton("暂停");
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SharedMemoryArea.setWillStop(true);
                pauseButton.setEnabled(false);
            }
        });
        dividePanel.add(pauseButton);

        JLabel resultLabel = new JLabel("结果");
        GridBagConstraints gbc_resultLabel = new GridBagConstraints();
        gbc_resultLabel.anchor = GridBagConstraints.EAST;
        gbc_resultLabel.insets = new Insets(0, 0, 5, 5);
        gbc_resultLabel.gridx = 0;
        gbc_resultLabel.gridy = 6;
        contentPane.add(resultLabel, gbc_resultLabel);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 3;
        gbc_scrollPane.gridheight = 2;
        gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 1;
        gbc_scrollPane.gridy = 6;
        contentPane.add(scrollPane, gbc_scrollPane);

        resultTA = new AssistPopupTextArea();
        resultTA.setEditable(false);
        resultTA.setLineWrap(true);
        resultTA.setWrapStyleWord(true);
        // resultTA.setText();
        scrollPane.setViewportView(resultTA);

        JButton cleanUpButton = new JButton(SystemHWUtil.LABEL_CLEANUP);
        cleanUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resultTA.setText("");
                resultBuffer.setLength(0);
            }
        });
        GridBagConstraints gbc_cleanUpButton = new GridBagConstraints();
        gbc_cleanUpButton.insets = new Insets(0, 0, 5, 0);
        gbc_cleanUpButton.gridx = 4;
        gbc_cleanUpButton.gridy = 6;
        contentPane.add(cleanUpButton, gbc_cleanUpButton);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FileDivideApp frame = new FileDivideApp();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /***
     *
     * @param fin
     * @param outPutFile
     * @param length2
     * @param sequence
     * @throws IOException
     */
    public static void write2DividedFile(FileInputStream fin, File outPutFile,
                                         long length2, int sequence) throws IOException {
        write2DividedFile(fin, outPutFile, length2, sequence, false);
    }

    /***
     * 写入第一个字节（序号）
     *
     * @param outPutFile
     * @param sequence
     * @param append
     * @throws IOException
     */
    public static void writeFirstByte(File outPutFile, int sequence,
                                      boolean append) throws IOException {
        FileOutputStream fout = new FileOutputStream(outPutFile, append/* 追加 */);
        // byte firstByte = (byte) sequence;
        if (!(append && outPutFile.exists() && outPutFile.length() > 0)) {//
            fout.write(sequence);// 第一个字节
        }
        fout.close();
    }

    /***
     *
     * @param fin
     * @param outPutFile
     *            : 分割后的文件
     * @param length2
     * @param sequence
     * @param append
     * @throws IOException
     */
    public static void write2DividedFile(FileInputStream fin, File outPutFile,
                                         long length2, int sequence, boolean append) throws IOException {
        if (SharedMemoryArea.isWillStop()) {
            return;
        }
        writeFirstByte(outPutFile, sequence, append);// 写入第一个字节（文件的序号）
        FileUtils.writeFromFile2File(fin, outPutFile, length2, true);// 必须是追加，因为之前已经写了第一个字节
    }

    private void divideEnableBtn(boolean enable) {
        divideButton.setEnabled(enable);
        deleteDividedButton.setEnabled(enable);
        pauseButton.setEnabled(!enable);
    }

    /***
     * Get way split files
     *
     * @return
     */
    private int getDivideLevel() {
        if (singleSizeRadioButton.isSelected()) {
            return LEVEL_DIVIDE_SIZE_FLAG;
        } else {
            return LEVEL_DIVIDE_FILES_QUANTITY_FLAG;
        }
    }

    private void addResult(String message) {
        if (!ValueWidget.isNullOrEmpty(message)) {
            this.resultBuffer.append(message).append(SystemHWUtil.CRLF);
        }
        this.resultTA.setText(this.resultBuffer.toString());
    }

    private boolean validate3() {
        String sourceFileStr = sourceTF.getText();
        if (ValueWidget.isNullOrEmpty(sourceFileStr)) {
            GUIUtil23
                    .warningDialog("source file can not be empty,please select again  !");
            DialogUtil.focusSelectAllTF(sourceTF);
            return false;
        }
        File srcfile = new File(sourceFileStr);
        if (!srcfile.exists()) {
            GUIUtil23
                    .warningDialog("source file does not exist,please select again!");
            DialogUtil.focusSelectAllTF(sourceTF);
            return false;
        }
        if (srcfile.isDirectory()) {
            GUIUtil23
                    .warningDialog("source file must be regular file(No Folder)!");
            DialogUtil.focusSelectAllTF(sourceTF);
            return false;
        }
        String fileQuantityStr = fileQuantityTF.getText();
        int divideLevle = getDivideLevel();
        String name = null;
        if (divideLevle == LEVEL_DIVIDE_FILES_QUANTITY_FLAG) {
            name = LEVEL_DIVIDE_FILES_QUANTITY_RADIO;
        } else {
            name = LEVEL_DIVIDE_SINGLE_SIZE_RADIO;
        }
        if (ValueWidget.isNullOrEmpty(fileQuantityStr)) {

            GUIUtil23.warningDialog(name + " can not be empty,please input  !");
            DialogUtil.focusSelectAllTF(fileQuantityTF);
            return false;
        }
        String targetFolderStr = targetTF.getText();
        if (ValueWidget.isNullOrEmpty(targetFolderStr)) {
            GUIUtil23
                    .warningDialog("target folder can not be empty,please select again  !");
            DialogUtil.focusSelectAllTF(targetTF);
            return false;
        }
        String quantityStr = fileQuantityTF.getText();
        if (!ValueWidget.isInteger(quantityStr)) {
            GUIUtil23.warningDialog("\"" + name + "\" must be digit.");
            DialogUtil.focusSelectAllTF(fileQuantityTF);
            return false;
        }
        return true;
    }

    /***
     * 初始化分割后的文件
     *
     * @param quantity
     * @param singleSize
     * @param fileName
     * @param targetFolderStr
     * @param lastSize
     * @return
     */
    public List<DividedFileBean> initializeDividedFile(int quantity,
                                                       long singleSize, String fileName, String targetFolderStr,
                                                       long lastSize) {
        List<DividedFileBean> dividedFiles = new ArrayList<DividedFileBean>();
        String dateTime = TimeHWUtil.formatDate(new Date(), TimeHWUtil.YYYYMMDD_NO_LINE);
        int maxLength = String.valueOf(quantity).length();
        for (int i = 0; i < quantity; i++) {
            DividedFileBean dividedFile = new DividedFileBean();
            dividedFile.setSequence(i + 1);// start from one

            // 第一个字节存放序号
            dividedFile.setStartIndex(1);
            dividedFile.setLength(singleSize);
            dividedFile.setFileName(fileName + SystemHWUtil.MIDDLE_LINE + dateTime + SystemHWUtil.UNDERLINE
                    +ValueWidget.getIndexNoFormattd(dividedFile.getSequence(), maxLength) + SystemHWUtil.UNDERLINE
                    + String.valueOf(quantity) + Constant.SUFFIX_DIVIDED);
            File outPutFile = new File(targetFolderStr,
                    dividedFile.getFileName());

            dividedFile.setOutPutFile(outPutFile);
            dividedFiles.add(dividedFile);
        }

        // 重置最后一个元素
        int size = dividedFiles.size();
        dividedFiles.get(size - 1).setLength(lastSize);
        return dividedFiles;
    }

    /***
     * Get the number of bytes which has been written
     *
     * @param dividedFiles
     * @param i
     * @param realLength3
     * @return
     */
    public long getHasWrittedLength(List<DividedFileBean> dividedFiles, int i,
                                    long realLength3) {
        long sumBytesHasWrite = 0;
        for (int j = 0; j < i; j++) {
            sumBytesHasWrite += dividedFiles.get(j).getLength();
        }
        if (realLength3 > 0) {
            sumBytesHasWrite += (realLength3 - 1);// 统计共有多少个字节已经写入文件
        }
        return sumBytesHasWrite;
    }

    public synchronized boolean isDividingNow() {
        return isDividingNow;
    }

    public synchronized void setDividingNow(boolean isDividingNow) {
        if (isDividingNow) {
            System.out.println("------------dividing.....");
            setTiming(false);//正在分割时不定时,不执行定时任务
        } else {
            System.out.println("------------already complete.");
            setTiming(true);
            if (!isActived22()) {//分割完成之后,若窗口仍然处于非激活状态,则开始定时
                windowDeactivated2(this);
            }
        }
        this.isDividingNow = isDividingNow;
    }


}
