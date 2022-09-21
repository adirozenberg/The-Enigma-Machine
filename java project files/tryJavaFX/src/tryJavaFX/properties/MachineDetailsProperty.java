package tryJavaFX.properties;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MachineDetailsProperty {
    final private StringProperty value = new SimpleStringProperty();

    public StringProperty valueProperty() {
        return value;
    }

    //the getter and setter are used for backward compatibility and are not mandatory at all
    public  String getValue() {return value.get();}

    public void setValue(String i) {
        value.set(i);
    }
}
