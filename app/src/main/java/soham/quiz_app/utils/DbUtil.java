package soham.quiz_app.utils;

import android.util.Log;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import soham.quiz_app.utils.models.DashboardDetails;
import soham.quiz_app.utils.models.Option;
import soham.quiz_app.utils.models.Question;

public class DbUtil {

    private static final String TAG = "test->DbUtil";

    private static volatile DbUtil Instance = null;

    private static final String jdbcUrl = "jdbc:mysql://192.168.0.6:3306/quiz",
            username = "Username",
            password = "Password";

    private Connection _connection = null;

    private String _auth_token = null;

    private DbUtil(String auth_token){
        this._auth_token = auth_token;
    }

    public static DbUtil getInstance(String auth_token){
        if(DbUtil.Instance != null ) return DbUtil.Instance;
        DbUtil.Instance = new DbUtil(auth_token);
        return DbUtil.Instance;
    }

    public boolean connect(){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance(); // Load the driver class
            this._connection = DriverManager.getConnection(jdbcUrl, username, password);
            return !this._connection.isClosed();
        } catch (Exception e) {
            Log.e(TAG,"Error in connecting\n"+e.toString());
            this._connection = null;
            return false;
        }
    }

    public boolean isConnected(){
        if(this._connection == null)
            return false;
        try {
            if(this._connection.isClosed()) {
                Log.i(TAG,"Not connected, Trying to reconnect");
                return this.connect();
            }
            else return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public String login(String mail, String password, boolean isTeacher){

        Log.i(TAG,"Logging in : "+mail+"\t"+password+"\t"+isTeacher);

        if(!this.isConnected() ) {
            Log.e(TAG,"Null Connection");
            return null;
        }
        try {
            // Prepare the stored procedure call
            String call = isTeacher?
                    "{call teacher_login('"+mail+"', '"+password+"', ?)}":
                    "{call student_login('"+mail+"', '"+password+"', ?)}";

            CallableStatement callableStatement = this._connection.prepareCall(call);

            // Register the output parameter
            callableStatement.registerOutParameter(1, Types.VARCHAR);

            // Execute the stored procedure
            callableStatement.execute();

            // Get the output parameter value
            this._auth_token = callableStatement.getString(1);
            Log.i(TAG,"Auth Token : "+this._auth_token);

            // Close resources
            callableStatement.close();
        } catch (Exception e) {
            Log.e(TAG,"Error logging in\nauth_Token:"+this._auth_token+"\n"+e.toString());
        }
        return this._auth_token ;
    }

    public boolean logout(){

        Log.i(TAG,"Logging out : "+this._auth_token);

        if(!this.isConnected() || this._auth_token == null ) return true;

        try{
            // Prepare the stored procedure call
            final String call = "{call logout('"+this._auth_token+"', ?)}";
            CallableStatement callableStatement = this._connection.prepareCall(call);

            // Register the output parameter
            callableStatement.registerOutParameter(1, Types.TINYINT);


            // Execute the stored procedure
            callableStatement.execute();

            // Get the output parameter value
            int status = callableStatement.getInt(1);
            Log.i(TAG,"Status : "+ status);

            if(status == 1 ) this._auth_token = null;

            // Close resources
            callableStatement.close();
        }catch(Exception e){
            Log.e(TAG,"Error logging out\n"+e.toString());
        }
        return this._auth_token == null;
    }

    public String signUp(
            String mail, String password,
            String name, String institution
    ){
        Log.i(TAG,"Signing Up\n"+mail+"\t"+name+"\t"+password+"\t"+institution);
        if(!this.isConnected() ) return null;
        try {
            // Prepare the stored procedure call
            String call = institution == null ?
                "{call student_signUp_login('"+mail+"', '"+name+"' , '"+password+"', ?)}":
                "{call teacher_signUp_login('"+mail+"', '"+name+"' , '"+password+"', '"+institution+"' , ?)}";

            CallableStatement callableStatement = _connection.prepareCall(call);

            // Register the output parameter
            callableStatement.registerOutParameter(1, Types.VARCHAR);

            // Execute the stored procedure
            callableStatement.execute();

            // Get the output parameter value
            this._auth_token = callableStatement.getString(1);

            Log.d(TAG,"Signed Up : Auth Token : "+this._auth_token);

            // Close resources
            callableStatement.close();
        } catch (Exception e) {
            Log.e(TAG,"Error SignUp\n"+e.toString());
        }
        return this._auth_token;
    }

    public boolean isLogin(){
        Log.i(TAG,"Checking Login stat");
        if (this._auth_token == null || !this.isConnected())   return false;
        int status = -1;
        try{
            final String call = "{ ? = call is_Login('"+this._auth_token+"') }";
            CallableStatement callableStatement = this._connection.prepareCall(call);

            callableStatement.registerOutParameter(1, Types.INTEGER);

            callableStatement.execute();

            status = callableStatement.getInt(1);

            callableStatement.close();
        }catch(Exception e){
            Log.e(TAG,"Error checking login status\n"+e.toString());
        }
        Log.i(TAG,"Login Stat : "+status);
        return status == 1;
    }

    private boolean isTeacher(){
        if (this._auth_token == null)   return false;
        else if(!this.isConnected()) return false;
        int status = -1;
        try{
            final String call = "{ ? = call is_Teacher('"+this._auth_token+"') }";
            CallableStatement callableStatement = this._connection.prepareCall(call);

            callableStatement.registerOutParameter(1, Types.INTEGER);

            callableStatement.execute();

            status = callableStatement.getInt(1);

            callableStatement.close();

        }catch(Exception e){
            Log.e(TAG,"Error checking login status\n"+e.toString());
        }
        return status == 1;
    }

    public DashboardDetails getDashboardDetails(){
        if(this._auth_token == null)return null;
        else if(!this.isConnected()) return null;
        DashboardDetails dd = null;
        try{
            final String call = "{call dashboard_details(\""+this._auth_token+"\")}";
            ResultSet result = this._connection.createStatement().executeQuery(call);
            ResultSetMetaData resultSetMetaData = result.getMetaData();

            ArrayList<String> cols = new ArrayList<String>(5);

            for ( int i=1; i<=resultSetMetaData.getColumnCount(); i++)
                cols.add(resultSetMetaData.getColumnName(i));

            dd = new DashboardDetails();
            dd.isTeacher = !cols.contains("teacher");
            dd.quizes = new ArrayList<DashboardDetails.Quiz>(0);

            while(result.next()){
                int quiz_id, question_count;
                String quiz_name;
                boolean isTaken;

                dd.name = result.getString("name");

                if(result.getString("quiz") == null) continue;

                quiz_id = result.getInt("quiz_id");
                quiz_name = result.getString("quiz");
                question_count = result.getInt("question_count");

                DashboardDetails.Quiz qz = dd.new Quiz(quiz_id,quiz_name,question_count);
                dd.quizes.add(qz);

                if(!cols.contains("teacher")) continue;

                qz.Teacher = result.getString("teacher");
                qz.attemptCount = result.getInt("attempt_count") ;
                qz.totalAttempt = result.getInt("total_attempt") ;
            }
            cols = null;
        }catch(Exception e){
            Log.d(TAG,"Error getting dashboard details\n"+e.toString());
        }
        return dd;
    }

    public int createQuiz(CreateQuizUtils data){
        int quiz_id = -1;
        if(!this.isConnected()) return -1;
        try{
            final String call = "{call create_quiz('"+this._auth_token+"','"+data.Name+"','"+1+"',?)}";
            CallableStatement callableStatement = this._connection.prepareCall(call);

            callableStatement.registerOutParameter(1, Types.INTEGER);

            callableStatement.execute();

            quiz_id = callableStatement.getInt(1);
            callableStatement.close();

            Log.d(TAG,"Quiz id : "+quiz_id);

            if(quiz_id == -1) return -1;

            for(Question q : data.questions){
                int question_id = this.insertQuestion(quiz_id,q.Question);
                if(question_id == -1) return -1;
                for(Option opt : q.options)
                    this.insertOption(quiz_id,question_id,opt.value,opt.isCorrect);
            }

        }catch(Exception e){
            Log.e(TAG,"adding Quiz\n"+e.toString());
        }
        return quiz_id;
    }

    public int delete_quiz(int quiz_id){
        int status = 0;
        if(!this.isConnected()) return 0;
        try {
            final String call = "{call delete_quiz('" + this._auth_token + "','" + quiz_id + "',?)}";
            CallableStatement callableStatement = this._connection.prepareCall(call);

            callableStatement.registerOutParameter(1, Types.INTEGER);

            callableStatement.execute();

            status = callableStatement.getInt(1);

            callableStatement.close();
        }catch(Exception e){
            Log.e(TAG,"Error Deleting Quiz : "+e.toString());
        }
        return status;
    }

    private int insertQuestion(int quiz_id,String question){
        int question_id = -1;
        if(!this.isConnected()) return -1;
        try{
            final String call = "{call insert_question('"+this._auth_token+"','"+quiz_id+"','"+question+"',?)}";
            CallableStatement callableStatement = this._connection.prepareCall(call);

            callableStatement.registerOutParameter(1, Types.INTEGER);

            callableStatement.execute();

            question_id = callableStatement.getInt(1);
            callableStatement.close();
            Log.d(TAG,"Question id : "+question_id);

        }catch(Exception e){
            Log.e(TAG,"adding Question\n"+e.toString());
        }
        return question_id;
    }

    private int insertOption(int quiz_id,int question_id,String value, boolean is_correct){
        int option_id = -1;
        if(!this.isConnected()) return -1;
        try{
            int isCorrect = is_correct ? 1 : 0;
            final String call =
                    "{call insert_option('"+this._auth_token+"','"+quiz_id+"','"+question_id+"','"+value+"','"+isCorrect+"',?)}";
            CallableStatement callableStatement = this._connection.prepareCall(call);

            callableStatement.registerOutParameter(1, Types.INTEGER);

            callableStatement.execute();

            option_id = callableStatement.getInt(1);
            callableStatement.close();
            Log.d(TAG,"Option id : "+quiz_id);
        }catch(Exception e){
            Log.e(TAG,"Inserting Question :\n"+e.toString());
        }
        return option_id;
    }

    public String addStudentToQuiz(int quiz_id, String student_mail){
        String status = null;
        if(!this.isConnected()) return null;
        try{
            final String call =
                    "{call add_student_to_quiz('"+this._auth_token+"','"+quiz_id+"','"+student_mail+"',?)}";
            CallableStatement callableStatement = this._connection.prepareCall(call);

            callableStatement.registerOutParameter(1, Types.INTEGER);

            callableStatement.execute();

            status = callableStatement.getString(1);
            callableStatement.close();
            Log.i(TAG,"Added Student to quiz : "+status+"|"+student_mail);
        }catch(Exception e){
            Log.e(TAG,"Not added Student to quiz : "+status+"|"+student_mail+"\n"+e.toString());
        }
        return status;
    }

    public int removeStudentFromQuiz(int quiz_id, String student_mail) {
        int status = -1;
        if(!this.isConnected()) return -1;
        try{
            final String call =
                    "{call remove_student_from_quiz('"+this._auth_token+"','"+quiz_id+"','"+student_mail+"',?)}";
            CallableStatement callableStatement = this._connection.prepareCall(call);

            callableStatement.registerOutParameter(1, Types.INTEGER);

            callableStatement.execute();

            status = callableStatement.getInt(1);
            callableStatement.close();
            Log.i(TAG,"Deleting Student from quiz : "+status+"|"+student_mail);
        }catch(Exception e){
            Log.e(TAG,"Not Deleting Student from quiz : "+status+"|"+student_mail+"\n"+e.toString());
        }
        return status;
    }

    public ArrayList<String> getStudents(int quiz_id){
        ArrayList<String> studentList = new ArrayList<String>(0);
        if(!this.isConnected()) return studentList;
        try{
            if(!this.isTeacher()) return studentList;
            final String call = "call get_students('"+_auth_token+"','"+quiz_id+"')";
            ResultSet result = this._connection.createStatement().executeQuery(call);
            while(result.next())
                studentList.add(result.getString("_student_mail"));
        }catch(Exception e){
            Log.e(TAG,"Error getting student lis\nt"+e.toString());
        }
        return studentList;
    }

    public TakeQuizUtil takeQuiz(int quiz_id){
        TakeQuizUtil tq = new TakeQuizUtil();
        if(!this.isConnected()) return tq;
        try{
            final String call = "{call take_quiz('"+this._auth_token+"','"+quiz_id+"')}";
            ResultSet table = this._connection.createStatement().executeQuery(call);

            Log.e(TAG,"Taking Quiz :");
            while(table.next()){
                tq.session = table.getString("session");
                tq.quiz = table.getString("quiz");
                tq.id = table.getInt("quiz_id");
                tq.add(
                        table.getInt("question_id"),
                        table.getInt("option_id"),
                        table.getString("question"),
                        table.getString("option")
                );
            }
            Log.i(TAG,tq.toString());
        }catch(Exception e){
            Log.e(TAG,"Error Taking Quiz"+"\n"+e.toString());
        }
        return tq;
    }


    public boolean submit_quiz(TakeQuizUtil tq){
        if(!this.isConnected()) return false;
        for(int qs=0; qs<tq.questions.size(); qs++) {
            for (int opt = 0; opt < tq.questions.get(qs).options.size(); opt++) {
                if (tq.questions.get(qs).options.get(opt).is_selected) {
                    try {
                        final String queryString = "{call submit_answer('" + this._auth_token + "','" +tq.session + "','"+ tq.id + "','" + tq.questions.get(qs).id + "','" + tq.questions.get(qs).options.get(opt).id + "',?)}";
                        CallableStatement callableStatement = this._connection.prepareCall(queryString);
                        callableStatement.registerOutParameter(1, Types.INTEGER);
                        callableStatement.execute();
                        System.out.println("Submitted answer.");
                        System.out.println( tq.questions.get(qs).question + "\t" + tq.questions.get(qs).options.get(opt).option + "\t" + callableStatement.getInt(1));
                        if(callableStatement.getInt(1)==-1)
                            return false;
                    } catch (Exception e) {
                        System.out.println( "Error submitting quiz\n" + e);
                        System.out.println( tq.questions.get(qs).question + "\t" + tq.questions.get(qs).options.get(opt).option);
                    }
                }
            }
        }
        this.deleteSession(tq.session, tq.id);
        return true;
    }

    private void deleteSession(String session_id, int quiz_id){
        if(!this.isConnected()) return ;
        try{
            final String call =
                    "{call delete_session('"+this._auth_token+"','"+session_id+"',"+quiz_id+")}";
            CallableStatement callableStatement = this._connection.prepareCall(call);

            callableStatement.execute();

            callableStatement.close();
            System.out.println("Deleting Session "+session_id);
        }catch(Exception e){
            System.out.println("Not Deleting Session "+session_id);
            e.printStackTrace();
        }
        return ;
    }


    public ResultViewUtil student_response_view(int quiz_id){
        ResultViewUtil rvu = new ResultViewUtil();
        if(!this.isConnected()) return null;
        if(this.isTeacher()) return null;
        try{
            final String call ="{call student_response_view('"+_auth_token+"','"+quiz_id+"')}";
            ResultSet table = _connection.createStatement().executeQuery(call);
            while(table.next()){
                rvu.quiz = table.getString("quiz");
                rvu.add(
                        table.getInt("question_id"),
                        table.getString("question"),
                        table.getString("option"),
                        table.getInt("selected")!=0,
                        table.getInt("is_correct")==1
                );
            }
            Log.i(TAG,"Student Response : "+rvu);
        }catch(Exception e){
            Log.e(TAG,"Error getting response view for student");
        }
        return rvu;
    }

    public ResultViewUtil teacher_response_view(int quiz_id, String student_mail){
        ResultViewUtil rvu = new ResultViewUtil();
        if(!this.isConnected()) return null;
        if(!this.isTeacher()) return null;
        try{
            final String call ="{call teacher_response_view('"+_auth_token+"','"+quiz_id+"','"+student_mail+"')}";
            ResultSet table = _connection.createStatement().executeQuery(call);

            while(table.next()){
                rvu.quiz = table.getString("quiz");
                rvu.add(
                        table.getInt("question_id"),
                        table.getString("question"),
                        table.getString("option"),
                        table.getInt("selected")!=0,
                        table.getInt("is_correct")==1
                );
            }
            Log.i(TAG,"Teacher view :\n"+rvu);
        }catch(Exception e){
            Log.e(TAG,"Error getting response view for teacher");
        }
        return rvu;
    }

    public boolean close(){
        try {
            if(!this.isConnected())
                this._connection.close();
            Log.d(TAG,"Closed connection");
            return true;
        } catch (Exception e) {
            Log.e(TAG,"Error closing connection\n"+e.toString());
            return false;
        }
    }

}