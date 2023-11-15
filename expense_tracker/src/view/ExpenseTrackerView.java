package view;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import model.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ExpenseTrackerView extends JFrame {

  private JTable transactionsTable;
  private JButton addTransactionBtn;
  private JFormattedTextField amountField;
  private JTextField categoryField;
  private DefaultTableModel model;

  // private JTextField dateFilterField;
  private JTextField categoryFilterField;
  private JButton categoryFilterBtn;
  private boolean joptionpane_shown;

  private JTextField amountFilterField;
  private JButton amountFilterBtn;
  
  
  private JButton undoBtn;

  private ListSelectionModel tableSelectionModel;
  private int[] selectedRows=new int[] {};
  private JOptionPane dialogPane;
  private JDialog dialog;
  
  private final Color HIGHLIGHT_COLOR=new Color(173, 255, 168);
  
  private boolean isTestMode=false;
  private boolean isDialogShown=false;
  public ExpenseTrackerView() {
    setTitle("Expense Tracker"); // Set title
    setSize(600, 400); // Make GUI larger

    String[] columnNames = {"serial", "Amount", "Category", "Date"};
    this.model = new DefaultTableModel(columnNames, 0);

    
    // Create table
    transactionsTable = new JTable(model);
    tableSelectionModel = transactionsTable.getSelectionModel();
    
    addTransactionBtn = new JButton("Add Transaction");

    // Create UI components
    JLabel amountLabel = new JLabel("Amount:");
    NumberFormat format = NumberFormat.getNumberInstance();

    amountField = new JFormattedTextField(format);
    amountField.setColumns(10);

    
    JLabel categoryLabel = new JLabel("Category:");
    categoryField = new JTextField(10);
    

    JLabel categoryFilterLabel = new JLabel("Filter by Category:");
    categoryFilterField = new JTextField(10);
    categoryFilterBtn = new JButton("Filter by Category");

    JLabel amountFilterLabel = new JLabel("Filter by Amount:");
    amountFilterField = new JTextField(10);
    amountFilterBtn = new JButton("Filter by Amount");
  
    undoBtn = new JButton("Undo");

    undoBtn.setEnabled(false);
    // Layout components
    JPanel inputPanel = new JPanel();
    inputPanel.add(amountLabel);
    inputPanel.add(amountField);
    inputPanel.add(categoryLabel); 
    inputPanel.add(categoryField);
    inputPanel.add(addTransactionBtn);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(amountFilterBtn);
    buttonPanel.add(categoryFilterBtn);
    buttonPanel.add(undoBtn);
  
    // Add panels to frame
    add(inputPanel, BorderLayout.NORTH);
    add(new JScrollPane(transactionsTable), BorderLayout.CENTER); 
    add(buttonPanel, BorderLayout.SOUTH);
    dialogPane=new JOptionPane();
    dialog=dialogPane.createDialog(this, "Message");
    
    
    // Set frame properties
    setSize(600, 400); // Increase the size for better visibility
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  
  }
 
  public void showDialog(String msg) {
	  dialogPane.setMessage(msg);
	  if(!isTestMode)
		  dialog.setVisible(true);
	  else
		  isDialogShown=true;
  }
  
  public void closeDialog() {
	  if(!isTestMode)
		  dialog.setVisible(false);
	  else
		  isDialogShown=false;
  }

  
  /***
   * set the view to test mode.
   * in test mode, it does not display a real dialog. instead sidplaying dialog sets "isDialogShown" to true.
   */
  public void setToTestMode() {
	  isTestMode=true;
  }
  /**
   * return if the message dialog is visible
   * @param msg
   */
  public boolean isDialogVisible() {
	  return isDialogShown;
  }
  public String getDialogMessage() {
	  return (String) dialogPane.getMessage();
  }
  public int[] getHighlightedRows() {
	  return transactionsTable.getSelectedRows();
  }
  public boolean isRowHighlighted(int row) {
	  for(int r:getHighlightedRows()) {
		  if(r==row) return true;
	  }
	  return false;
  }
  public boolean isUndoButtonActive() {
	  return undoBtn.isEnabled();
  }

  public DefaultTableModel getTableModel() {
    return model;
  }
  public void setJOptionPaneShown(boolean input) {
	  this.joptionpane_shown = input;
  }
  public boolean getJOptionPaneShown() {
	  return this.joptionpane_shown;
  }
  public JTable getTable() {
	  return transactionsTable;
  }
   

  public JTable getTransactionsTable() {
    return transactionsTable;
  }

  public double getAmountField() {
    if(amountField.getText().isEmpty()) {
      return 0;
    }else {
    double amount = Double.parseDouble(amountField.getText());
    return amount;
    }
  }

  public void setAmountField(JFormattedTextField amountField) {
    this.amountField = amountField;
  }

  
  public String getCategoryField() {
    return categoryField.getText();
  }

  public void setCategoryField(JTextField categoryField) {
    this.categoryField = categoryField;
  }

  public void addApplyCategoryFilterListener(ActionListener listener) {
    categoryFilterBtn.addActionListener(listener);
  }
  
  

  public String getCategoryFilterInput() {
    return JOptionPane.showInputDialog(this, "Enter Category Filter:");
}


  public void addApplyAmountFilterListener(ActionListener listener) {
    amountFilterBtn.addActionListener(listener);
  }
  

  public double getAmountFilterInput() {
    String input = JOptionPane.showInputDialog(this, "Enter Amount Filter:");
    try {
        return Double.parseDouble(input);
    } catch (NumberFormatException e) {
        // Handle parsing error here
        // You can show an error message or return a default value
        return 0.0; // Default value (or any other appropriate value)
    }
  }
  public void setSelectedRows(int[] selected) {
	  selectedRows=selected;
	  if(selected.length==0) {
		  undoBtn.setEnabled(false);
	  }//if the only selected row is the last row(which displays total and should not be undo-ed)
	  else if(selected.length==1 && selected[0]>= model.getRowCount()-1) {
		  undoBtn.setEnabled(false);
	  }
	  else {
		  undoBtn.setEnabled(true);
	  }
  }
  public HashSet<Integer> getSelectedTransactions() {
	  HashSet<Integer> ids=new HashSet<Integer>();
	  
	  for(int i=0;i<selectedRows.length;i++) {
		  
		 //ignore the last row(which displays total and should not be undo-ed)
		 if(selectedRows[i]>=model.getRowCount()-1) continue;
		 
		 ids.add((Integer) model.getValueAt(selectedRows[i], 0)) ;
	  }
	  return ids;
  }
  
  public void addUndoListener(ActionListener listener) {
	    undoBtn.addActionListener(listener);
  }
  public void applyUndo(List<Transaction> transactions) {
	  refreshTable(transactions);
	  selectedRows=new int[] {};
  }
  public void addTableSelectionListener(ListSelectionListener listener) {
	    tableSelectionModel.addListSelectionListener(listener);
  }

  
  public void refreshTable(List<Transaction> transactions) {
      // Clear existing rows
      model.setRowCount(0);
      // Get row count
      int rowNum = model.getRowCount();
      double totalCost=0;
      // Calculate total cost
      for(Transaction t : transactions) {
        totalCost+=t.getAmount();
      }
  
      // Add rows from transactions list
      for(Transaction t : transactions) {
        model.addRow(new Object[]{t.getId(),t.getAmount(), t.getCategory(), t.getTimestamp()}); 
      }
      // Add total row
      Object[] totalRow = {"Total", null, null, totalCost};
      model.addRow(totalRow);
  
      // Fire table update
      transactionsTable.updateUI();
    }  
  

  public JButton getAddTransactionBtn() {
    return addTransactionBtn;
  }


  public void highlightRows(List<Integer> rowIndexes) {
	  for(int row:rowIndexes) {
		  transactionsTable.getSelectionModel().addSelectionInterval(row, row);
	  }
	  
	  transactionsTable.repaint();
	  
	  return;
      // The row indices are being used as hashcodes for the transactions.
      // The row index directly maps to the the transaction index in the list.
//      transactionsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//          @Override
//          public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
//                                                        boolean hasFocus, int row, int column) {
//              Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//              if (rowIndexes.contains(row)) {
//            	  c.setEnabled(true);
//               //   c.setBackground(HIGHLIGHT_COLOR); // Light green
//              } else {
//            	  c.setEnabled(false);
//               //   c.setBackground(table.getBackground());
//              }
//              return c;
//          }
//      });

      
  }


}
