# Solution files for hw2

## Usability: Undo Functionality

- added `id` field in `Transaction` class.
    - to distinguish which transactions should be deleted with undo.
    - value of id will replace the current “serial” column of the table.
    - Id is assigned from constructor, which is passed from `ExpenseTrackerController`.
    - id is incremental so that no two transactions have duplicate ids.(`ExpenseTrackerController` handles this)
- view changes
    - add “undo” button, which is disabled by default.
    - If user selects or diselects a row, call `setSelectedRows()` method to update the list of currently selected rows, which is stored in `selectedRows`.
        - if`selectedRows` has more than 0 rows, enables undo button. otherwise, disable it.
    - if undo button is clicked, `getSelectedTransactions()` method returns list of selected transactions’ id as a HashSet
    - `applyUndo()`  method will refresh the table, and clear `selectedRows`.
    - Filtering no longer changes the color of the row. Instead, it "selects" the corresponding rows. In this way, it becomes compatiable with undo selection, and user is able to select rows by filter and undo all selections directly.
    - The last row, which displays the total, should not be undo-ed. Therefore, the undo button is inactive when the last row is only row that is selected. Also, When multiple rows are selected, the last row is always ignored when performing the undo.
    - Changes when testing
        - when the view is set to "test mode", it does not really shows the dialog, because showing a dialog will pause the whole application. Instead, when the `showDialog()` function is called, it sets `isDialogShown` field to true, which is used for detecting if the dialog is visible on test-time.
- model changes
    - implement `removeTransactionsById()` method that takes list of ids of transactions, and remove those transactions from the list.
- controller changes
    - implemented `applyUndo()` which calls `getSelectedTransactions()` to retrieve transactions that should be removed.
    - If there are no transactions selected, throws IllegalStateException.
    - Otherwise, call `removeTransactionsById()` in the model to update transaction list.
    - Finally, call `applyUndo()` in the view.