// package test;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.CategoryFilter;
import view.ExpenseTrackerView;


public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;

  @Before
  public void setup() {
    model = new ExpenseTrackerModel();
    view = new ExpenseTrackerView();
    controller = new ExpenseTrackerController(model, view);
    controller.addEventListeners();
    view.setToTestMode();
  }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }


    public void checkTransaction(double amount, String category, Transaction transaction) {
	assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);

    }


    @Test
    public void testAddTransaction() {
    	
    	//TODO: ADD VIEW TESTING!!
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a transaction
        double amount = 50.0;
        String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: List of transactions contains only
	//                 the added transaction	
        assertEquals(1, model.getTransactions().size());
    
        // Check the contents of the list
        Transaction firstTransaction = model.getTransactions().get(0);
		checkTransaction(amount, category, firstTransaction);
	
	// Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);

    }
    
    @Test
    public void testInvalidInputHandling() {
	    // Pre-condition: List of transactions is empty
	    assertEquals(0, model.getTransactions().size());
	
	    // Perform the action: Add and remove a transaction
		double amount = 1100.0;
		String category = "";
		double prevtotal=getTotalCost();
	    //Transaction addedTransaction = new Transaction(amount, category,0);
	    assertEquals(false,controller.addTransaction(amount,category));
	    
	    //Check if its throwing an error correctly..
	    
	    // Post-condition: List of transactions stays empty
	    assertEquals(0, model.getTransactions().size());
	    assertEquals(prevtotal, getTotalCost(), 0.01);
	    assertEquals(view.isDialogVisible(),true);
	    assertEquals(view.getDialogMessage(),"Invalid Amount");
	    setup(); //reset the controller, model, and view. 

    }
    
    
    @Test
    public void testFilterByAmount() {
    // Pre-condition: List of transactions is empty
    assertEquals(0, model.getTransactions().size());
    
    // Perform the action: Add Multiple Transactions. 
    String category = "food";
    assertTrue(controller.addTransaction(23.0, "food"));
    assertTrue(controller.addTransaction(278.0, "other"));
    assertTrue(controller.addTransaction(245.0, "food"));
    assertTrue(controller.addTransaction(249.9, "travel"));
    assertTrue(controller.addTransaction(29.0, category));
    assertTrue(controller.addTransaction(150.4, category));
    assertTrue(controller.addTransaction(223.43, category));
    
    //Filter  Stuff..
    CategoryFilter categoryFilter = new CategoryFilter(category);
    controller.setFilter(categoryFilter);
    //Set the filter.
    controller.applyFilter();
    assertTrue(view.isRowHighlighted(0));
    assertFalse(view.isRowHighlighted(1));
    assertTrue(view.isRowHighlighted(2));
    //check if certain rows are highlighted.
    setup(); //reset the controller, model, and view. 
    
    }

    @Test
    public void testRemoveTransaction() {
    	//TODO: ADD VIEW TESTING!!

        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add and remove a transaction
	double amount = 50.0;
	String category = "food";
        Transaction addedTransaction = new Transaction(amount, category,0);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	//                the added transaction
        assertEquals(1, model.getTransactions().size());
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);

	assertEquals(amount, getTotalCost(), 0.01);
	
	// Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
	    setup(); //reset the controller, model, and view. 

    }
    
    // Post-condition: undo throws exception
    @Test(expected = IllegalStateException.class)
    public void testUndoDisallowedEmptySelection() {
	    setup(); //reset the controller, model, and view. 
	    // Pre-condition: no rows are selected
	    assertEquals(view.getHighlightedRows().length, 0);

		 //  undo button is inactive
		    assertFalse(view.isUndoButtonActive());
	    // Perform the action: attempt undo(should throw exception)
	    controller.applyUndo();
	    setup(); //reset the controller, model, and view. 

	    
    }
    
    // Post-condition: undo throws exception
    @Test(expected = IllegalStateException.class)
    public void testUndoDisallowedLastRowOnly() {
	    setup(); //reset the controller, model, and view. 
	    
        controller.addTransaction(20,"food");
        
        assertEquals(1, model.getTransactions().size());
        
        
	    // Pre-condition: only the last row is selected(which should not be undo-ed)
        List<Integer> selected = new ArrayList<Integer>();
        selected.add(1);
	    view.highlightRows(selected);
        assertEquals(view.getHighlightedRows().length, 1);
     //  undo button is inactive
        assertFalse(view.isUndoButtonActive());
	    
	    // Perform the action: attempt undo (should throw exception)
	    controller.applyUndo();
	    
	    setup(); //reset the controller, model, and view. 

	    
    }


    @Test
    public void testUndoAllowed() {
	    setup(); //reset the controller, model, and view. 
	    
        controller.addTransaction(1, "food"); //id=1
        controller.addTransaction(1, "food"); //id=2
        controller.addTransaction(1, "food"); //id=3
        controller.addTransaction(1, "food"); //id=4
        //total cost =4
	    assertEquals(getTotalCost(),4,0.001);
	    
        List<Integer> selected = new ArrayList<Integer>();
        selected.add(1);
        //simulate row selection
	    view.highlightRows(selected);
	    
	    assertEquals(view.getHighlightedRows().length, 1);
	    assertEquals(view.getSelectedTransactions().size(), 1);
	    //id of the selected transaction is 1
	    assertTrue(view.getSelectedTransactions().contains(1));
	    
	    // Perform the action: attempt undo 
	    controller.applyUndo();
	    
	    // Post-condition: no highlighted rows remaining
	    assertEquals(view.getHighlightedRows().length, 0);
	    assertEquals(view.getSelectedTransactions().size(), 0);
	    
	    
	    //total cost recalculated
	    assertEquals(getTotalCost(), 3,0.001);
	    
	    // Post-condition: 3 transactions left
	    assertEquals( model.getTransactions().size(), 3);
	    
	    // Post-condition: transaction with id=1 is removed and does not exist
	    for(Transaction tran: model.getTransactions()) {
	    	assertNotEquals(1, tran.getId());
	    }
	    
	    setup(); //reset the controller, model, and view. 

	    
    }
    
    
}
