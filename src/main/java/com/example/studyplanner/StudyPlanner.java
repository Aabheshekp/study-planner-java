package com.example.studyplanner;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class StudyPlanner extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Done", "Subject", "Task", "Due date"}, 0) { public Class<?> getColumnClass(int c) { return c == 0 ? Boolean.class : String.class; } public boolean isCellEditable(int r,int c){return c==0;} };
    private final JTable table = new JTable(model);
    private final Path store = Path.of(System.getProperty("user.home"), ".study-planner", "tasks.tsv");
    public StudyPlanner() {
        super("Study Planner"); setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(900,560); setMinimumSize(new Dimension(700,420)); setLocationRelativeTo(null);
        JPanel root=new JPanel(new BorderLayout(16,16));root.setBorder(BorderFactory.createEmptyBorder(22,22,22,22));root.setBackground(new Color(245,247,250));setContentPane(root);
        JLabel title=new JLabel("Study Planner");title.setFont(new Font("SansSerif",Font.BOLD,27));root.add(title,BorderLayout.NORTH);
        table.setRowHeight(30);table.setFillsViewportHeight(true);table.getTableHeader().setReorderingAllowed(false);root.add(new JScrollPane(table),BorderLayout.CENTER);
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.LEFT));JButton add=new JButton("Add task");JButton remove=new JButton("Remove selected");JButton save=new JButton("Save");buttons.add(add);buttons.add(remove);buttons.add(save);root.add(buttons,BorderLayout.SOUTH);
        add.addActionListener(e->addTask());remove.addActionListener(e->removeTask());save.addActionListener(e->saveTasks());table.getModel().addTableModelListener(e->{if(e.getType()==javax.swing.event.TableModelEvent.UPDATE)saveTasks();});loadTasks();
    }
    private void addTask(){JTextField subject=new JTextField(),task=new JTextField(),due=new JTextField(LocalDate.now().plusDays(7).toString());Object[] fields={"Subject",subject,"Task",task,"Due date (YYYY-MM-DD)",due};if(JOptionPane.showConfirmDialog(this,fields,"New study task",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)return;try{LocalDate.parse(due.getText().trim());if(subject.getText().isBlank()||task.getText().isBlank())throw new IllegalArgumentException();model.addRow(new Object[]{false,subject.getText().trim(),task.getText().trim(),due.getText().trim()});saveTasks();}catch(Exception ex){JOptionPane.showMessageDialog(this,"Enter a subject, task, and valid date (YYYY-MM-DD).","Invalid task",JOptionPane.ERROR_MESSAGE);}}
    private void removeTask(){int row=table.getSelectedRow();if(row<0)return;if(JOptionPane.showConfirmDialog(this,"Remove the selected task?","Confirm",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){model.removeRow(row);saveTasks();}}
    private void saveTasks(){try{Files.createDirectories(store.getParent());try(BufferedWriter w=Files.newBufferedWriter(store)){for(int r=0;r<model.getRowCount();r++){String[] values=new String[4];for(int c=0;c<4;c++)values[c]=String.valueOf(model.getValueAt(r,c));w.write(Base64.getEncoder().encodeToString(String.join("\t",values).getBytes(java.nio.charset.StandardCharsets.UTF_8)));w.newLine();}}}catch(IOException ex){JOptionPane.showMessageDialog(this,"Could not save tasks: "+ex.getMessage(),"Save error",JOptionPane.ERROR_MESSAGE);}}
    private void loadTasks(){if(!Files.exists(store))return;try{for(String line:Files.readAllLines(store)){try{String decoded=new String(Base64.getDecoder().decode(line),java.nio.charset.StandardCharsets.UTF_8);String[] v=decoded.split("\t",-1);if(v.length==4)model.addRow(new Object[]{Boolean.parseBoolean(v[0]),v[1],v[2],v[3]});}catch(IllegalArgumentException ignored){}}}catch(IOException ex){JOptionPane.showMessageDialog(this,"Could not read saved tasks: "+ex.getMessage(),"Load error",JOptionPane.ERROR_MESSAGE);}}
    public static void main(String[] args){SwingUtilities.invokeLater(()->new StudyPlanner().setVisible(true));}
}
