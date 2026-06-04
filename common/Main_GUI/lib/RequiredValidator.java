package Main_GUI.lib;

public class RequiredValidator implements InputValidator {
    @Override
    public String validate(String input) {
        if (input.isEmpty()) {
            return "This field is required";
        }
        return null;
    }
}
