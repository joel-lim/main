package seedu.address.model.transaction;

public class Budget {
    private Amount budget;
    private Date date;

    public Budget(Amount budget, Date date) {
        this.budget = budget;
        this.date = date;
    }
}
