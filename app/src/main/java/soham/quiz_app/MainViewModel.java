package soham.quiz_app;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import soham.quiz_app.utils.CreateQuizUtils;
import soham.quiz_app.utils.DbUtil;
import soham.quiz_app.utils.ResultViewUtil;
import soham.quiz_app.utils.TakeQuizUtil;
import soham.quiz_app.utils.models.DashboardDetails;

public class MainViewModel extends ViewModel {

    private static final String TAG = "test->MainViewModel";

    private ExecutorService _executorService
            = Executors.newSingleThreadExecutor();

    private DbUtil _dbUtil = null;

    final public MutableLiveData<Boolean>
            isDbConnected = new MutableLiveData<Boolean>(null),
            isLogin = new MutableLiveData<Boolean>(null),
            isQuizSubmitted = new MutableLiveData<Boolean>(null);

    final public MutableLiveData<String> authToken
            = new MutableLiveData<String>(null);

    final public MutableLiveData<DashboardDetails> dd
            = new MutableLiveData<DashboardDetails>();

    final public MutableLiveData<Integer> quiz_id
            = new MutableLiveData<Integer>(-2);

    public CreateQuizUtils createQuizUtils = null;

    public MutableLiveData<ArrayList<String>> students
            = new MutableLiveData<ArrayList<String>>(new ArrayList<String>(0));

    public MutableLiveData<TakeQuizUtil> takeQuiz
            = new MutableLiveData<TakeQuizUtil>(null);

    public MutableLiveData<ResultViewUtil> rvu
            = new MutableLiveData<ResultViewUtil>(null);

    public void initDbConnection(String authToken) {
        this._executorService.execute(()->{
            this._dbUtil = DbUtil.getInstance(authToken);
            boolean status = this._dbUtil.connect();
            if(status)
                this.isDbConnected.postValue(this._dbUtil.isConnected());
        });
    }

    public void login(String mail, String password, Boolean isTeacher) {
        this._executorService.execute(()->{
            this.authToken.postValue(this._dbUtil.login(mail,password,isTeacher));
        });
    }

    public void checkLoginStat() {
        this._executorService.execute(()->{
            this.isLogin.postValue(this._dbUtil.isLogin());
        });
    }

    public void logout() {
        this._executorService.execute(()->{
            this.isLogin.postValue(!this._dbUtil.logout());
        });
    }

    public void signUp(String mail, String pswd, String name, String institution) {
        this._executorService.execute(()->{
            this.authToken.postValue(this._dbUtil.signUp(mail, pswd, name, institution));
        });
    }

    public void getDashBoardDetails() {
        this._executorService.execute(()->{
            this.dd.postValue(this._dbUtil.getDashboardDetails());
        });
    }

    public void closeConnection() {
        this._executorService.execute(()->{
            this._dbUtil.close();
        });
    }

    public void createQuiz() {
        Log.d(TAG,"Creating Test : "+this.createQuizUtils);
        this._executorService.execute(()->{
            this.quiz_id.postValue(this._dbUtil.createQuiz(this.createQuizUtils));
        });
    }

    public void deleteQuiz(DashboardDetails.Quiz quiz){
        this._executorService.execute(()->{
            int status = this._dbUtil.delete_quiz(quiz.id);
            if(status!=0) {
                this.dd.getValue().quizes.remove(quiz);
                this.dd.postValue(this.dd.getValue());
                return;
            }
            this.dd.postValue(this._dbUtil.getDashboardDetails());
        });
    }

    public void getStudentList(int quiz_id){
        this._executorService.submit(()->{
            this.students.postValue(this._dbUtil.getStudents(quiz_id));
        });
    }

    public void addStudentToQuiz(int quiz_id,String student_mail){
        this._executorService.submit(()->{
           Log.i(TAG,"Adding "+student_mail);
           String _mail = this._dbUtil.addStudentToQuiz(quiz_id,student_mail);
           if(_mail == null ) return;
           this.students.getValue().add(_mail);
           this.students.postValue(this.students.getValue());
        });
        this.getStudentList(quiz_id);
    }

    public void removeStudentFromQuiz(int quiz_id,String student_mail){
        this._executorService.submit(()->{
            int _mail = this._dbUtil.removeStudentFromQuiz(quiz_id,student_mail);
            if(_mail == -1) return;
            this.students.getValue().remove(student_mail);
            this.students.postValue(this.students.getValue());
        });
        this.getStudentList(quiz_id);
    }

    public void takeQuiz(int quiz_id){
        this._executorService.submit(()->{
            this.takeQuiz.postValue(this._dbUtil.takeQuiz(quiz_id));
        });
    }

    public void submitQuiz(){
        this._executorService.execute(()->{
            this.isQuizSubmitted.postValue(this._dbUtil.submit_quiz(this.takeQuiz.getValue()));
        });
    }

    public void getStudentResult(int quiz_id){
        this._executorService.execute(()->{
            this.rvu.postValue(this._dbUtil.student_response_view(quiz_id));
        });
    }

    public void getTeacherResult(int quiz_id,String student_mail){
        this._executorService.execute(()->{
            this.rvu.postValue(this._dbUtil.teacher_response_view(quiz_id,student_mail));
        });
    }
}
