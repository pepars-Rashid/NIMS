import Main_GUI.components.PrimaryButton;
import mapper.JSONException;
import mapper.ObjectMapper;

void main() throws JSONException {
    ObjectMapper mapper = new ObjectMapper();
    List<Integer> list = List.of(1,2,3,4,10);
    mapper.writeValue(new File("TNM_data/test.json"), list);

    PrimaryButton button = new PrimaryButton("hello");
}