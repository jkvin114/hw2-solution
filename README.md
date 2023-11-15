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
- model changes
    - implement `removeTransactionsById()` method that takes list of ids of transactions, and remove those transactions from the list.
- controller changes
    - implemented `applyUndo()` which calls `getSelectedTransactions()` to retrieve transactions that should be removed.
    - If there are no transactions selected, throws IllegalStateException.
    - Otherwise, call `removeTransactionsById()` in the model to update transaction list.
    - Finally, call `applyUndo()` in the view.