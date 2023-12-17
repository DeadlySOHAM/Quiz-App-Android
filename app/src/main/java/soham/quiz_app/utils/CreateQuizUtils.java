package soham.quiz_app.utils;

import java.util.ArrayList;

import soham.quiz_app.utils.models.Option;
import soham.quiz_app.utils.models.Question;

public class CreateQuizUtils {

    public String Name = "";

    public int current = -1;

    public ArrayList<Question> questions ;

    public CreateQuizUtils(){
        this.questions = new ArrayList<Question>(0);
    }

    public Question getNext(){
        if( this.current == -1 || this.current == this.questions.size() -1 ) {
            Question qs = new Question();
            this.questions.add(qs);
        }
        return this.questions.get(++this.current);
    }

    public Question getPrevious(){
        if(this.current < 1 ) return null;
        if(this.current > this.questions.size()) return null;
        return this.questions.get(--this.current);
    }

    public void deleteCurrent(){
        if(this.current>=this.questions.size()) return;
        this.questions.remove(this.current);
    }

    public Question getCurrent(){
        if(this.current == -1)return null;
        if(this.current >= this.questions.size() ) return null;
        return this.questions.get(this.current);
    }

    @Override
    public String toString() {
        return "{\nName : " +this.Name+
                "\nQuestions:\n"+this.questions+
                "\n}";
    }

    public Question validate() {
        for(Question q : this.questions){
            if(q.Question.length()<1 && q.options.size()<2) {
                this.questions.remove(q);
                continue;
            }else if(q.options.size()>1){
                boolean isAtleastTrue = false;
                for(Option opt : q.options) {
                    if (opt.value.length() < 1) return q;
                    isAtleastTrue = isAtleastTrue ? true : opt.isCorrect;
                }
                if(!isAtleastTrue) return q;
            }else return q;
        }
        return null;
    }
}