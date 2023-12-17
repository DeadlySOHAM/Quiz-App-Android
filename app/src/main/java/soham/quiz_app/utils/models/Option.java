package soham.quiz_app.utils.models;


import androidx.annotation.NonNull;

public class Option{
    public String value = "";
    public boolean isCorrect = false;

    @NonNull
    @Override
    public String toString() {
        return " {"+this.value+"("+this.isCorrect+")} ";
    }
}