package Main_GUI.lib;

public class PasswordValidator implements InputValidator {
    @Override
    public String validate(String input) {
        if (input.isEmpty()) {
            return "Password is required";
        }
        if (input.length() < 6) {
            return "Password must be at least 6 characters";
        }
        return null;
    }
}
