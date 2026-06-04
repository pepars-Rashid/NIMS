package Main_GUI.lib;

// Example validators
public class EmailValidator implements InputValidator {
    @Override
    public String validate(String input) {
        if (input.isEmpty()) {
            return "Email is required";
        }
        if (!input.matches("^[A-Za-z0-9+_.-]+@(.+)$")) { // regex representation of the basic look of the email
            return "Please enter a valid email address";
        }
        return null;
    }
}
