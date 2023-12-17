package soham.quiz_app.utils;

import java.util.ArrayList;



public class TakeQuizUtil{

    public int id= 0;
    public String quiz= null, session = null;
    public ArrayList<Question> questions = null;
    private int current = -1;

    public class Question{
        public int id= 0;
        public String question= null;
        public ArrayList<Option> options = null ;

        public class Option{
            public final int id;
            public final String option;
            public boolean is_selected = false;
            public Option(int id,String option){
                this.id = id;
                this.option = option;
            }
            public String toString(){
                return "{\t\n\t\t"+
                        "Id : "+this.id+"\n\t\t"+
                        "Option : "+this.option+"\n\t\t"+
                        "isSelected : "+this.is_selected+"\n\t\t"+
                        "\t\t}\n";
            }
        }

        public void add(Option opt){
            if(this.options == null){
                this.options = new ArrayList<Option>(){{
                    add(opt);
                }};
                return;
            }
            this.options.add(opt);
        }

        public String toString(){
            return "{\n\t"+
                    "Id : "+this.id+"\n\t"+
                    "Question : "+this.question+"\n\t"+
                    "Option : "+this.options+"\n\t"+
                    "\t}\n";
        }

    }

    public void add(int qs_id,int opt_id, String qs,String opt){

        if(this.questions==null){
            Question q = new Question();
            this.questions = new ArrayList<Question>(){{
                add(q);
            }};
            q.id = qs_id;
            q.question = qs;
            q.add(q.new Option(opt_id, opt));
            return;
        }

        for(Question q : this.questions){
            if(q.id == qs_id){
                q.options.add(q.new Option(opt_id, opt));
                return;
            }
        }

        Question q = new Question();
        this.questions.add(q);
        q.id = qs_id;
        q.question = qs;
        q.add(q.new Option(opt_id, opt));
    }

    public Question getNext(){
        ++current;
        if(current<questions.size())
            return this.questions.get(current);
        current = 0;
        return this.questions.get(current);
    }

    public Question getPrevious(){
        --current;
        if(current>-1)
            return this.questions.get(current);
        current = questions.size()-1;
        return this.questions.get(current);
    }

    public boolean isAtLast(){
        return this.current == this.questions.size()-1;
    }

    @Override
    public String toString() {
        return "{\n"+
                "Id : "+this.id+"\n"+
                "Quiz : "+this.quiz+"\n"+
                "Questions : "+this.questions+"\n"+
                "}";
    }

}
