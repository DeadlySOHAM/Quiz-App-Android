package soham.quiz_app.utils.models;


import java.util.ArrayList;


public class Question{

    public final static String TAG = "test->Question";

    public String Question = "" ;

    public ArrayList<Option> options = null ;

    public Question(){
        this.options = new ArrayList<Option>(0);
    }

    public ArrayList<Option> addNewOption(){
        this.options.add(new Option());
        return this.options;
    }

    public void deleteOption(int i){
        this.options.remove(i);
    }

    @Override
    public String toString() {
        return "\t{\n\tQuestion : "+ this.Question+
                "\n\tOptions : "+this.options+
                "\n\t}";
    }
}
