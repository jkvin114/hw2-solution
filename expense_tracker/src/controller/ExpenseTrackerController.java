package controller;

import view.ExpenseTrackerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;

import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
import model.Filter.TransactionFilter;

public class ExpenseTrackerController {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  /** 
   * The Controller is applying the Strategy design pattern.
   * This is the has-a relationship with the Strategy class 
   * being used in the applyFilter method.
   */
  private TransactionFilter filter;

  private int tranCount;
  public ExpenseTrackerController(ExpenseTrackerModel model, ExpenseTrackerView view) {
    this.model = model;
    this.view = view;
    this.tranCount=0;
  }
  
  public void addEventListeners() {

	    // Handle add transaction button clicks
	    view.getAddTransactionBtn().addActionListener(e -> {
	      // Get transaction data from view
	      double amount = view.getAmountField();
	      String category = view.getCategoryField();
	      
	      // Call controller to add transaction
	      boolean added = this.addTransaction(amount, category);
	      
	      if (!added) {
	    	  view.showDialog("Invalid amount or category entered");
	      //  JOptionPane.showMessageDialog(view, "Invalid amount or category entered");
	        view.toFront();
	      }
	    });

	      // Add action listener to the "Apply Category Filter" button
	    view.addApplyCategoryFilterListener(e -> {
	      try{
	      String categoryFilterInput = view.getCategoryFilterInput();
	      CategoryFilter categoryFilter = new CategoryFilter(categoryFilterInput);
	      if (categoryFilterInput != null) {
	          // controller.applyCategoryFilter(categoryFilterInput);
	          this.setFilter(categoryFilter);
	          this.applyFilter();
	      }
	     }catch(IllegalArgumentException exception) {
	    	 System.out.print("cate");
	    	 view.showDialog(exception.getMessage());
	 //   JOptionPane.showMessageDialog(view, exception.getMessage());
	    view.toFront();
	   }});


	    // Add action listener to the "Apply Amount Filter" button
	    view.addApplyAmountFilterListener(e -> {
	      try{
	      double amountFilterInput = view.getAmountFilterInput();
	      AmountFilter amountFilter = new AmountFilter(amountFilterInput);
	      if (amountFilterInput != 0.0) {
	          this.setFilter(amountFilter);
	          this.applyFilter();
	      }
	    }catch(IllegalArgumentException exception) {
	    	view.showDialog(exception.getMessage());
	  //  JOptionPane.showMessageDialog(view,exception.getMessage());
	    view.toFront();
	   }});
	    
	    // Add action listener to the "undo" button
	    view.addUndoListener(e -> {
	      try{
	    	  this.applyUndo();
	    }catch(IllegalStateException exception) {
	    	view.showDialog(exception.getMessage());
	    	//JOptionPane.showMessageDialog(view,exception.getMessage());
	    	view.toFront();
	   }});
	    
	    view.addTableSelectionListener(e->{
	    	int[] selectedRow = view.getTable().getSelectedRows();
	    	view.setSelectedRows(selectedRow);
	    });
  }

  public void setFilter(TransactionFilter filter) {
    // Sets the Strategy class being used in the applyFilter method.
    this.filter = filter;
  }

  public void refresh() {
    List<Transaction> transactions = model.getTransactions();
    view.refreshTable(transactions);
  }

  public boolean addTransaction(double amount, String category) {
    if (!InputValidation.isValidAmount(amount)) {
    	view.showDialog("Invalid Amount");
      return false;
    }
    if (!InputValidation.isValidCategory(category)) {
    	view.showDialog("Invalid Category");
    	return false;
    }
    
    Transaction t = new Transaction(amount, category,tranCount);
    this.tranCount++;
    model.addTransaction(t);
    view.getTableModel().addRow(new Object[]{t.getId(),t.getAmount(), t.getCategory(), t.getTimestamp()});
    refresh();
    return true;
  }

  public void applyFilter() {
    //null check for filter
    if(filter!=null){
      // Use the Strategy class to perform the desired filtering
      List<Transaction> transactions = model.getTransactions();
      List<Transaction> filteredTransactions = filter.filter(transactions);
      List<Integer> rowIndexes = new ArrayList<>();
      for (Transaction t : filteredTransactions) {
        int rowIndex = transactions.indexOf(t);
        if (rowIndex != -1) {
          rowIndexes.add(rowIndex);
        }
      }
      view.highlightRows(rowIndexes);
    }
    else{
    	view.showDialog("No filter applied");
     // JOptionPane.showMessageDialog(view, "No filter applied");
      view.toFront();
      }

  }
  
  public void applyUndo() {
	  HashSet<Integer> trans=view.getSelectedTransactions();
	  if(trans.size()==0) throw new IllegalStateException("No transactions are selected!");
	  model.removeTransactionsById(trans);
	  view.applyUndo(model.getTransactions());
  }
}
