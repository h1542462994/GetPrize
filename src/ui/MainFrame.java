package ui;

import core.Cache;
import core.History;
import javafx.util.Pair;
import tool.Tool;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.*;

public class MainFrame extends JFrame  {
    private class JReadTable extends JTable{
        public JReadTable(Object[][] data, Object[] columnName){
            super(data,columnName);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private Cache cache = new Cache();
    private JButton buttonMenu1;
    private JButton buttonMenu2;
    private JButton buttonPeopleImport;
    private JButton buttonPeopleExport;
    private JButton buttonSubmit;
    private JTable tablePeople;
    private JTable tableHistory;
    private JTable tableResult;
    private JPanel menuItemPanel;
    private JLabel labelPeopleCount;
    private JLabel labelTitle;
    private JLabel labelError;
    private JTextField textTitle;
    private JTextField textPrize1;
    private JTextField textPrize2;
    private JTextField textPrize3;
    private JPanel card1;
    private JPanel card2;


    public static void main(String args[]){
        MainFrame frame = new MainFrame();
        frame.init();
        frame.registryEvent();
    }

    private void init(){

        this.setTitle("抽奖程序");
        this.add(this.GMainPanel());

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800,600);
        this.setMinimumSize(new Dimension(600,400));
        this.setVisible(true);


        loadData();

        this.updateSubmitState();
    }

    private void loadData() {
        this.cache.readStorage();
        this.textTitle.setText(this.cache.title);
        this.textPrize1.setText(Tool.toDisplayed(this.cache.firstCount));
        this.textPrize2.setText(Tool.toDisplayed(this.cache.secondCount));
        this.textPrize3.setText(Tool.toDisplayed(this.cache.thirdCount));
    }

    private void registryEvent(){
        buttonPeopleImport.addActionListener(e->
        {
            JFileChooser fc = txtFileChooser();
            if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                File file = fc.getSelectedFile();
                cache.peopleTableModel.importFromFile(file);
            }
            updateSubmitState();
        });

        buttonPeopleExport.addActionListener(e->{
            JFileChooser fc = txtFileChooser();
            if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
                File file = fc.getSelectedFile();
                if (!file.getPath().endsWith(".txt")) {
                    file= new File(file.getPath()+".txt");
                }
                cache.peopleTableModel.exportToFile(file);
            }
        });

        textTitle.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                cache.title = textTitle.getText();
                updateSubmitState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                cache.title = textTitle.getText();
                updateSubmitState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                cache.title = textTitle.getText();
                updateSubmitState();
            }
        });

        textPrize1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textChangingEvent(0);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textChangingEvent(0);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textChangingEvent(0);
            }
        });

        textPrize2.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textChangingEvent(1);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textChangingEvent(1);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textChangingEvent(1);
            }
        });

        textPrize3.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                textChangingEvent(2);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textChangingEvent(2);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                textChangingEvent(2);
            }
        });

        tablePeople.getModel().addTableModelListener(e -> updateSubmitState());

        buttonSubmit.addActionListener(e->{
            History history = cache.getNew();
            cache.push(history);
            int lastIndex = cache.data.size() - 1;
            tableHistory.getSelectionModel().setSelectionInterval(lastIndex,lastIndex);
            cache.saveHistoryStorage();
        });

        tableHistory.getSelectionModel().addListSelectionListener(e -> {
            int row = tableHistory.getSelectedRow();
            History history = cache.data.get(row);
            cache.resultTableModel.update(history);
            labelTitle.setText(history.title);
        });

    }

    private JFileChooser txtFileChooser(){
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setCurrentDirectory(new File("").getAbsoluteFile());
        fc.setFileFilter(new FileNameExtensionFilter("文本文件|*.txt","txt"));
        fc.setMultiSelectionEnabled(false);
        return fc;
    }

    private void textChangingEvent(int index){
        int prize = -1;
        JTextField target;
        if(index == 0){
            target = textPrize1;
        } else if(index == 1){
            target = textPrize2;
        } else {
            target = textPrize3;
        }

        String text = target.getText();

        if (!text.isEmpty()) {
            if (text.length() <= 7 && text.matches("[0-9]*")){
                prize = Integer.valueOf(text);
            }
        }

        System.out.println(prize);

        if(index == 0){
            cache.firstCount = prize;
        } else if(index == 1){
            cache.secondCount = prize;
        } else {
            cache.thirdCount = prize;
        }

        updateSubmitState();

        this.cache.saveNormalStorage();
    }

    private void updateSubmitState(){
        if(cache.canExecute()){
            labelError.setText("");
            buttonSubmit.setEnabled(true);
        } else {
            labelError.setText(cache.error());
            buttonSubmit.setEnabled(false);
        }

        labelPeopleCount.setText(String.format("有效%s人",String.valueOf(cache.peopleTableModel.available().size())));
    }

    //region Views

    private JPanel GMainPanel(){
        JPanel root = new JPanel();
        Pair<JPanel, GridBagConstraints> leftPair = GLeftPanel();
        Pair<JPanel, GridBagConstraints> rightPair = GRightPanel();

        GridBagLayout layout = new GridBagLayout();
        root.setLayout(layout);
        root.add(leftPair.getKey());
        root.add(rightPair.getKey());
        layout.setConstraints(leftPair.getKey(), leftPair.getValue());
        layout.setConstraints(rightPair.getKey(), rightPair.getValue());

        return root;
    }

    private Pair<JPanel, GridBagConstraints> GLeftPanel(){
        JPanel root = new JPanel();
        Pair<JPanel, GridBagConstraints> topPair = GMenuPanel();
        Pair<JPanel, GridBagConstraints> bottomPair = GMenuItemPanel();

        root.setBackground(Color.LIGHT_GRAY);
        root.setPreferredSize(new Dimension(240,-1));
        root.setMinimumSize(new Dimension(240,-1));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 1;
        constraints.weightx = 0.0;
        constraints.weighty = 1.0;

        GridBagLayout layout = new GridBagLayout();
        root.setLayout(layout);

        root.add(topPair.getKey());
        layout.setConstraints(topPair.getKey(),topPair.getValue());
        root.add(bottomPair.getKey());
        menuItemPanel = bottomPair.getKey();
        layout.setConstraints(bottomPair.getKey(), bottomPair.getValue());


        return new Pair<>(root, constraints);
    }

    private Pair<JPanel, GridBagConstraints> GMenuPanel(){
        JPanel root = new JPanel();
        buttonMenu1 = new JButton();
        buttonMenu1.setText("主页面");
        buttonMenu1.addActionListener(e1->{
            CardLayout layout = (CardLayout) menuItemPanel.getLayout();
            layout.first(menuItemPanel);
        });
        buttonMenu2 = new JButton();
        buttonMenu2.setText("名单页面");
        buttonMenu2.addActionListener(e2->{
            CardLayout layout = (CardLayout) menuItemPanel.getLayout();
            layout.last(menuItemPanel);
        });

        root.setPreferredSize(new Dimension(-1, 40));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;

        GridBagLayout layout = new GridBagLayout();
        root.setLayout(layout);

        GridBagConstraints lConstraints = new GridBagConstraints();
        lConstraints.fill = GridBagConstraints.BOTH;
        lConstraints.gridwidth = 1;
        lConstraints.weightx = 1.0;
        lConstraints.weighty = 1.0;
        root.add(buttonMenu1);
        layout.setConstraints(buttonMenu1, lConstraints);

        GridBagConstraints rConstraints = new GridBagConstraints();
        rConstraints.fill = GridBagConstraints.BOTH;
        rConstraints.gridwidth = 0;
        rConstraints.weightx = 1.0;
        rConstraints.weighty = 1.0;
        root.add(buttonMenu2);
        layout.setConstraints(buttonMenu2, rConstraints);

        return new Pair<>(root, constraints);
    }

    private Pair<JPanel, GridBagConstraints> GMenuItemPanel(){
        JPanel root = new JPanel();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 0;
        constraints.weightx = 0.0;
        constraints.weighty = 1.0;


        root.add(GMenu1Panel());
        root.add(GMenu2Panel());
        CardLayout layout = new CardLayout();
        root.setLayout(layout);
        layout.last(root);
        layout.first(root);

        return new Pair<>(root, constraints);
    }

    private JPanel GMenu1Panel(){
        JPanel root = new JPanel();
        JLabel labelTitle = new JLabel();
        labelTitle.setText("标题");
        JLabel labelPrize1 = new JLabel();
        labelPrize1.setText("一等奖");
        JLabel labelPrize2 = new JLabel();
        labelPrize2.setText("二等奖");
        JLabel labelPrize3 = new JLabel();
        labelPrize3.setText("三等奖");
        textTitle = new JTextField();
        textPrize1 = new JTextField();
        textPrize2 = new JTextField();
        textPrize3 = new JTextField();
        labelError = new JLabel();
        labelError.setText("标题不能为空");
        JPanel tablePanel = new JPanel(new BorderLayout());
//        String[] tableTitle = new String[] { "记录"};
//        String[][] data = new String[][] {{"1-FirstElement"}};
        tableHistory = new JTable(cache.historyTableModel);
        tableHistory.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        tableHistory.setRowHeight(24);
        tableHistory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableHistory.getTableHeader().setReorderingAllowed(false);
        tablePanel.add(tableHistory.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(tableHistory, BorderLayout.CENTER);
        buttonSubmit = new JButton();
        buttonSubmit.setText("抽奖");

        root.setBackground(Color.ORANGE);

        GridBagLayout layout = new GridBagLayout();
        root.setLayout(layout);

        labelTitle.setPreferredSize(new Dimension(60,30));
        labelPrize1.setPreferredSize(new Dimension(60,30));
        labelPrize2.setPreferredSize(new Dimension(60,30));
        labelPrize3.setPreferredSize(new Dimension(60,30));
        textTitle.setPreferredSize(new Dimension(-1,30));
        textPrize1.setPreferredSize(new Dimension(-1,30));
        textPrize2.setPreferredSize(new Dimension(-1,30));
        textPrize3.setPreferredSize(new Dimension(-1,30));


        GridBagConstraints constraints1 = new GridBagConstraints();
        constraints1.fill = GridBagConstraints.BOTH;
        constraints1.gridwidth = 1;
        constraints1.weightx = 0.0;
        constraints1.weighty = 0.0;
        constraints1.insets = new Insets(5,10,5,10);

        GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.fill = GridBagConstraints.BOTH;
        constraints2.gridwidth = 0;
        constraints2.weightx = 1.0;
        constraints2.weighty = 0.0;
        constraints2.insets = new Insets(5,10,5,10);

        GridBagConstraints constraints3 = new GridBagConstraints();
        constraints3.fill = GridBagConstraints.BOTH;
        constraints3.gridwidth = 0;
        constraints3.weightx = 1.0;
        constraints3.weighty = 1.0;
        constraints3.insets = new Insets(10,10,10,10);

        root.add(labelTitle);
        layout.setConstraints(labelTitle,constraints1);
        root.add(textTitle);
        layout.setConstraints(textTitle, constraints2);
        root.add(labelPrize1);
        layout.setConstraints(labelPrize1,constraints1);
        root.add(textPrize1);
        layout.setConstraints(textPrize1, constraints2);
        root.add(labelPrize2);
        layout.setConstraints(labelPrize2,constraints1);
        root.add(textPrize2);
        layout.setConstraints(textPrize2, constraints2);
        root.add(labelPrize3);
        layout.setConstraints(labelPrize3,constraints1);
        root.add(textPrize3);
        layout.setConstraints(textPrize3, constraints2);



        root.add(tablePanel);
        layout.setConstraints(tablePanel, constraints3);

        root.add(labelError);
        layout.setConstraints(labelError, constraints2);

        root.add(buttonSubmit);
        layout.setConstraints(buttonSubmit,constraints2);


        return root;
    }

    private JPanel GMenu2Panel(){
        JPanel root = new JPanel();
        buttonPeopleImport = new JButton();
        buttonPeopleImport.setText("导入名单");
        buttonPeopleExport = new JButton();
        buttonPeopleExport.setText("导出名单");
        labelPeopleCount = new JLabel();
        //String[] tableTitle = new String[] { "姓名","手机号码"};
        //String[][] data = new String[][] {{"张三","12345678901"}, {"",""}};
        JPanel tablePanel = new JPanel(new BorderLayout());

        tablePeople = new JTable(cache.peopleTableModel);
        tablePeople.getTableHeader().setReorderingAllowed(false);
        tablePeople.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePanel.add(tablePeople.getTableHeader(),BorderLayout.NORTH);
        tablePanel.add(tablePeople, BorderLayout.CENTER);

        root.setBackground(Color.PINK);

        GridBagLayout layout = new GridBagLayout();
        root.setLayout(layout);

        buttonPeopleImport.setPreferredSize(new Dimension( -1,30));
        GridBagConstraints tConstraints = new GridBagConstraints();
        tConstraints.fill = GridBagConstraints.BOTH;
        tConstraints.gridwidth = 1;
        tConstraints.weightx = 1.0;
        tConstraints.weighty = 0.0;
        tConstraints.insets = new Insets(10,10,10,10);
        root.add(buttonPeopleImport);
        layout.setConstraints(buttonPeopleImport,tConstraints);

        buttonPeopleExport.setPreferredSize(new Dimension(-1,30));
        GridBagConstraints t2Constraints = new GridBagConstraints();
        t2Constraints.fill = GridBagConstraints.BOTH;
        t2Constraints.gridwidth = 0;
        t2Constraints.weightx = 1.0;
        t2Constraints.weighty = 0.0;
        t2Constraints.insets = new Insets(10,10,10,10);
        root.add(buttonPeopleExport);
        layout.setConstraints(buttonPeopleExport,t2Constraints);

        root.add(labelPeopleCount);
        layout.setConstraints(labelPeopleCount, t2Constraints);

        GridBagConstraints bConstraints = new GridBagConstraints();
        bConstraints.fill = GridBagConstraints.BOTH;
        bConstraints.gridwidth = 0;
        bConstraints.weightx = 1.0;
        bConstraints.weighty = 1.0;
        bConstraints.insets = new Insets(10,10,10,10);
        root.add(tablePanel);
        layout.setConstraints(tablePanel, bConstraints);

        return root;
    }

    private Pair<JPanel, GridBagConstraints> GRightPanel(){
        JPanel root = new JPanel();
        JLabel label = new JLabel();
        label.setText("中奖名单");
        label.setFont(new Font(Font.MONOSPACED, Font.BOLD, 48));
        labelTitle = new JLabel();
        labelTitle.setText("无标题");
        labelTitle.setFont(new Font(Font.MONOSPACED, Font.BOLD, 32));

        root.setBackground(Color.WHITE);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        //String[] tableTitle = new String[]{"奖项","姓名","手机号码"};
        //String[][] data = new String[][] {{"一等奖","张三","1234567890"}};
        JPanel tablePanel = new JPanel(new BorderLayout());

        tableResult = new JTable(cache.resultTableModel);
        tableResult.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 30));
        tableResult.setRowHeight(40);
        tableResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableResult.getTableHeader().setReorderingAllowed(false);
        TableColumnModel model = tableResult.getTableHeader().getColumnModel();
        model.getColumn(0).setPreferredWidth(120);
        model.getColumn(1).setPreferredWidth(120);
        model.getColumn(2).setPreferredWidth(300);


        tablePanel.add(tableResult.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(tableResult, BorderLayout.CENTER);


        GridBagLayout layout = new GridBagLayout();
        root.setLayout(layout);

        GridBagConstraints constraints1 = new GridBagConstraints();
        constraints1.fill = GridBagConstraints.BOTH;
        constraints1.gridwidth = 0;
        constraints1.weightx = 1.0;
        constraints1.weighty = 0.0;
        constraints1.insets = new Insets(20,20,20,20);

        GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.fill = GridBagConstraints.BOTH;
        constraints2.gridwidth = 0;
        constraints2.weightx = 1.0;
        constraints2.weighty = 1.0;
        constraints2.insets = new Insets(20,20,20,20);

        root.add(label);
        layout.setConstraints(label, constraints1);
        root.add(labelTitle);
        layout.setConstraints(labelTitle, constraints1);
        root.add(tablePanel);
        layout.setConstraints(tablePanel, constraints2);

        return new Pair<>(root,constraints);
    }

    //endregion


}
