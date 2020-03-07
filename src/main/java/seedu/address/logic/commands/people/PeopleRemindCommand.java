package seedu.address.logic.commands.people;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliPrefix.PEOPLE_COMMAND_TYPE;

import java.util.List;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.User;
import seedu.address.model.reminder.Reminder;

/**
 * Reminds a person, who is identified by a specific index in the address book,
 * to return his or her debt to the user.
 */
public class PeopleRemindCommand extends Command {

    public static final String COMMAND_WORD = "remind";

    public static final String MESSAGE_REMIND_SUCCESS = "Reminded %1$s to return %2$s!\n"
            + "Sharkie has sent a copy of the reminder to your email!";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Reminds a person to return you "
            + "his or her unpaid debt.\nParameters: <index> (must be a positive integer)\n"
            + "Example: " + PEOPLE_COMMAND_TYPE + " "
            + COMMAND_WORD + " 4 ";

    private static final Logger logger = LogsCenter.getLogger(PeopleRemindCommand.class);

    private final Index targetIndex;

    public PeopleRemindCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToBeReminded = lastShownList.get(targetIndex.getZeroBased());

        if (personToBeReminded.getLoans().getTotal().isZero()) {
            throw new CommandException(String.format(Messages.MESSAGE_PERSON_WITH_ZERO_LOAN,
                    personToBeReminded.getName()));
        }

        if (model.isUserDataNull()) {
            throw new CommandException(Messages.MESSAGE_EMPTY_USER_DATA);
        }

        User user = model.getUserData().getUser();
        Reminder reminder = new Reminder(user, personToBeReminded);
        logger.info("Sending reminder...");

        try {
            reminder.sendReminder();
        } catch (MessagingException e) {
            throw new CommandException("Error occurs while sending email:\n" + e.getMessage()
                    + "\nPlease make sure that you are connected to the internet.\n"
                    + "Please check that the receiver's email address is a valid email!");
        }

        return new CommandResult(String.format(MESSAGE_REMIND_SUCCESS, personToBeReminded.getName(),
                personToBeReminded.getLoans().getTotal()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PeopleRemindCommand // instanceof handles nulls
                && targetIndex.equals(((PeopleRemindCommand) other).targetIndex)); // state check
    }
}

