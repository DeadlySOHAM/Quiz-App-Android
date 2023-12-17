package soham.quiz_app.fragments.dashboard;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import soham.quiz_app.MainViewModel;
import soham.quiz_app.databinding.FragmentDashboardBinding;
import soham.quiz_app.utils.models.DashboardDetails;

public class Dashboard
        extends Fragment
        implements QuizCardListAdapter.OnQuizAction {

    private static final String TAG = "test->Dashboard";
    private FragmentDashboardBinding _binding;
    private MainViewModel _mainViewModel;
    private QuizCardListAdapter _quizListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this._binding = FragmentDashboardBinding.inflate(inflater,container,false);
        this._binding.setFragmentDashboard(this);
        initVariables();
        initAdapters();
        initObservers();
        return this._binding.getRoot();
    }

    private void initVariables() {
        this._mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        this._mainViewModel.createQuizUtils = null;
        this._mainViewModel.takeQuiz.setValue(null);
        this._mainViewModel.rvu.setValue(null);
    }

    private void initAdapters(){
        this._quizListAdapter = new QuizCardListAdapter(this);
        this._quizListAdapter.notifyDataSetChanged();
        this._binding.quizListRecyclerView.setAdapter(this._quizListAdapter);
        new ItemTouchHelper(this.getSimpleCallbackForSwipe()).attachToRecyclerView(this._binding.quizListRecyclerView);
    }

    private void initObservers() {
        this._mainViewModel.isLogin.observe(this.getViewLifecycleOwner(),login -> {
            Log.d(TAG,"Log observer: "+login);
            if(login!=null && !login) {
                this._showLoading(false);
                this._goToLogin();
            }
            if(login)
                this._mainViewModel.getDashBoardDetails();
        });
        this._mainViewModel.dd.observe(this.getViewLifecycleOwner(),dd ->{
            Log.i(TAG,"Dashboard Details : "+dd);
            if(dd == null ) return;
            this._showLoading(false);
            if(dd.isTeacher)    this._binding.addButton.setVisibility(View.VISIBLE);
                else    this._binding.addButton.setVisibility(View.GONE);
            this._binding.userName.setText(dd.name);
            this._quizListAdapter.submitList(dd.quizes);
            this._quizListAdapter.notifyDataSetChanged();
        });
        this._mainViewModel.takeQuiz.observe(this.getViewLifecycleOwner(), takeQuizUtil -> {
            if(takeQuizUtil == null ) return;
            else if(takeQuizUtil.id == 0 ){
                Toast.makeText(this.getContext(),"You are not eligible to participate in the quiz.",Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            this._showLoading(false);
            this._goToExam();
        });
        this._mainViewModel.rvu.observe(this.getViewLifecycleOwner(),resultViewUtil -> {
            if(resultViewUtil == null || resultViewUtil.quiz==null || resultViewUtil.questions.get(0).id==0) return;
            _goToResult();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        this._showLoading(true);
        this._mainViewModel.checkLoginStat();
    }

    public void goToCreateTest(){
        if(!this._mainViewModel.dd.getValue().isTeacher) {
            Toast.makeText(this.getContext(), "Not a teacher to create test", Toast.LENGTH_SHORT).show();
            return;
        }
        NavDirections action = DashboardDirections.actionDashboardToCreateTest();
        findNavController(this).navigate(action);
    }

    private void _goToLogin(){
        NavDirections action = DashboardDirections.actionDashboardToLogin();
        findNavController(this).navigate(action);
    }

    private void _goToAddStudents(int quiz_id){
        if(!this._mainViewModel.dd.getValue().isTeacher) return;
        NavDirections action = DashboardDirections.actionDashboardToAddStudents(quiz_id);
        findNavController(this).navigate(action);
    }

    private void _goToExam(){
        if(this._mainViewModel.dd.getValue().isTeacher) return;
        NavDirections action = DashboardDirections.actionDashboardToExam();
        findNavController(this).navigate(action);
    }

    private void _goToResult(){
        NavDirections action = DashboardDirections.actionDashboardToResult();
        findNavController(this).navigate(action);
    }

    private void _showLoading(boolean b) {
        int visibility = b ? View.VISIBLE : View.GONE;
        this._binding.loadingPanelIncl.loadingPanel.setVisibility(visibility);
        this.disableOthers(!b);
    }

    private void disableOthers(boolean flag) {
        this._binding.logoutBtn.setEnabled(flag);
        this._binding.addButton.setEnabled(flag);
    }

    public void logout(){
        this._showLoading(true);
        this._mainViewModel.logout();
    }

    private void showResult(DashboardDetails.Quiz qz){
        //  if(qz.totalAttempt<=qz.attemptCount){
        //      Toast.makeText(this.getContext(),"Attempt atleast once to see result",Toast.LENGTH_SHORT).show();
        //      return;
        //  }
        this._showLoading(true);
        this._mainViewModel.getStudentResult(qz.id);
    }

    private ItemTouchHelper.SimpleCallback getSimpleCallbackForSwipe(){
        return new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(viewHolder instanceof QuizCardListAdapter.QuizCardViewHolder &&
                    direction == ItemTouchHelper.RIGHT || direction == ItemTouchHelper.LEFT){
                    int position = viewHolder.getAdapterPosition();
                    if(_mainViewModel.dd.getValue().isTeacher)
                        _mainViewModel.deleteQuiz(_quizListAdapter.getCurrentList().get(position));
                    else
                        showResult(_quizListAdapter.getCurrentList().get(position));
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"Something Major Went wrong",Toast.LENGTH_SHORT).show();
                }
            }
        } ;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.removeObservers();
        this._binding = null;
    }

    private void removeObservers() {
        this._mainViewModel.isLogin.removeObservers(this.getViewLifecycleOwner());
        this._mainViewModel.dd.removeObservers(this.getViewLifecycleOwner());
        this._mainViewModel.takeQuiz.removeObservers(this.getViewLifecycleOwner());
        this._mainViewModel.rvu.removeObservers(this.getViewLifecycleOwner());
    }

    @Override
    public void onQuizClick(DashboardDetails.Quiz qz) {
        Log.i(TAG,"Taking exam of quiz : "+qz);
        if(qz.attemptCount <1) {
            Toast.makeText(this.getContext(),"You are not eligible to participate in the quiz.",Toast.LENGTH_SHORT).show();
            return;
        }
        this._showLoading(true);
        this._mainViewModel.takeQuiz(qz.id);
    }

    @Override
    public void onAddStudent(DashboardDetails.Quiz qz) {
        if(!this._mainViewModel.dd.getValue().isTeacher) return;
        Log.i(TAG,"Adding Students for quiz : "+qz);
        this._showLoading(true);
        this._goToAddStudents(qz.id);
    }

    @Override
    public void onShowResult(DashboardDetails.Quiz qz) {
        if(this._mainViewModel.dd.getValue().isTeacher) return;
        Log.i(TAG,"Showing results for quiz : "+qz);
        this._showLoading(true);
        this._mainViewModel.getStudentResult(qz.id);
    }

}
