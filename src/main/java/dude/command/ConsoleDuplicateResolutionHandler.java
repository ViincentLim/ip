package dude.command;

import java.util.Locale;

import dude.ui.Ui;

/**
 * Collects duplicate-resolution choices from the command-line UI.
 */
public class ConsoleDuplicateResolutionHandler implements DuplicateResolutionHandler {
    private final Ui ui;

    /**
     * Creates a console conflict handler.
     *
     * @param ui UI used to display the conflict and read responses.
     */
    public ConsoleDuplicateResolutionHandler(Ui ui) {
        this.ui = ui;
    }

    @Override
    public DuplicateResolution resolve(DuplicateTaskConflict conflict) {
        ui.showDuplicateConflict(conflict);
        while (true) {
            ui.showDuplicatePrompt();
            String choice = ui.readInputLine();
            if (choice == null) {
                return DuplicateResolution.cancel();
            }
            String normalized = choice.trim().toLowerCase(Locale.ROOT);
            if (isEditChoice(normalized)) {
                return resolveEdit(conflict);
            }
            if (isAddChoice(normalized)) {
                return DuplicateResolution.add();
            }
            if (isCancelChoice(normalized)) {
                return DuplicateResolution.cancel();
            }
            ui.showInvalidDuplicateChoice();
        }
    }

    private DuplicateResolution resolveEdit(DuplicateTaskConflict conflict) {
        if (conflict.matches().size() == 1) {
            return DuplicateResolution.edit(conflict.matches().get(0).index());
        }

        while (true) {
            ui.showDuplicateSelection();
            String input = ui.readInputLine();
            if (input == null) {
                return DuplicateResolution.cancel();
            }
            try {
                int selectedNumber = Integer.parseInt(input.trim());
                for (DuplicateTaskConflict.DuplicateMatch match : conflict.matches()) {
                    if (selectedNumber == match.index() + 1) {
                        return DuplicateResolution.edit(match.index());
                    }
                }
            } catch (NumberFormatException ignored) {
                // The prompt is repeated below for an invalid task number.
            }
            ui.showInvalidDuplicateSelection();
        }
    }

    private static boolean isEditChoice(String choice) {
        return "e".equals(choice) || "ed".equals(choice) || "edi".equals(choice)
                || "edit".equals(choice);
    }

    private static boolean isAddChoice(String choice) {
        return "a".equals(choice) || "ad".equals(choice) || "add".equals(choice);
    }

    private static boolean isCancelChoice(String choice) {
        return "c".equals(choice) || "ca".equals(choice) || "can".equals(choice)
                || "canc".equals(choice) || "cance".equals(choice) || "cancel".equals(choice);
    }
}
