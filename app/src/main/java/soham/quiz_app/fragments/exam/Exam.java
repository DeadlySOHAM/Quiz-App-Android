package soham.quiz_app.fragments.exam;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import soham.quiz_app.HomeButtonReceiver;
import soham.quiz_app.HomeWatcher;
import soham.quiz_app.MainViewModel;
import soham.quiz_app.databinding.FragmentExamBinding;
import soham.quiz_app.utils.TakeQuizUtil.Question;

public class Exam
        extends Fragment
        implements HomeButtonReceiver.OnHomePressedListener {

    private static final String TAG = "test->Exam";
    private FragmentExamBinding _binding;
    private MainViewModel _mainViewModel;
    private HomeWatcher _homeWatcher;
    private AttemptQuestionOptionAdapter _questionOptionAdapter;
    private int _pause_stat = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this._binding = FragmentExamBinding.inflate(inflater, container,false);
        this.initVariables();
        this.initObservers();
        this.initAdapters();
        this.initOnBackButtonPressed();
        return this._binding.getRoot();
    }

    private void initVariables() {
        this._binding.setFragmentExam(this);
        this._binding.setFragmentExam(this);
        this._mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        this._mainViewModel.createQuizUtils = null;
        this._homeWatcher = new HomeWatcher(this.getContext());
    }

    private void initObservers() {
        this._homeWatcher.setOnHomePressedListener(this);
        this._mainViewModel.isQuizSubmitted.observe(this.getViewLifecycleOwner(),stat ->{
            if(stat == null || stat == false ) return;
            Toast.makeText(this.getContext(),"Submitted",Toast.LENGTH_SHORT).show();
            this._mainViewModel.takeQuiz.postValue(null);
            findNavController(this).navigateUp();
        });
    }

    private void initAdapters(){
        this._questionOptionAdapter = new AttemptQuestionOptionAdapter();
        this._binding.attemptOptionRecyclerView.setAdapter(this._questionOptionAdapter);
    }

    private void initOnBackButtonPressed(){
        // Register a listener for implicit back events
        this.requireActivity().getOnBackPressedDispatcher().addCallback(this.getViewLifecycleOwner(),new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.i(TAG,"Back Pressed");
                Toast.makeText(getContext(),"Submit test to go back.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        this._homeWatcher.startWatch();
        this._binding.quizNameView.setText(
                this._mainViewModel.takeQuiz.getValue().quiz
        );
        this.next();
    }

    public void next(){
        this.updateQuestion(this._mainViewModel.takeQuiz.getValue().getNext());
        showSubmitBtn();
    }

    public void previous(){
        this.updateQuestion(this._mainViewModel.takeQuiz.getValue().getPrevious());
        showSubmitBtn();
    }

    private void updateQuestion(Question qs){
        this._binding.questionNameView.setText(qs.question);
        this._questionOptionAdapter.submitList(qs.options);
    }

    private void showSubmitBtn(){
        if(this._mainViewModel.takeQuiz.getValue().isAtLast())
            this._binding.doneBtn.setVisibility(View.VISIBLE);
        else
            this._binding.doneBtn.setVisibility(View.GONE);
    }

    public void submit(){
        this._showLoading(true);
        this._mainViewModel.submitQuiz();
    }

    private void _showLoading(boolean b) {
        int visibility = b ? View.VISIBLE : View.GONE;
        this._binding.loadingPanelIncl.loadingPanel.setVisibility(visibility);
        this.disableOthers(!b);
    }

    private void disableOthers(boolean flag) {
        this._binding.doneBtn.setEnabled(flag);
        this._binding.nextBtn.setEnabled(flag);
        this._binding.previousBtn.setEnabled(flag);
    }

    @Override
    public void onPause() {
        super.onPause();
//        if(this._mainViewModel.isQuizSubmitted.getValue()) {
//            this._mainViewModel.isQuizSubmitted.setValue(false);
//        }
        ++this._pause_stat;
        Toast.makeText(this.getContext(),"Changed Screen : "+this._pause_stat,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.removeObservers();
        this._binding = null;
        this._homeWatcher.stopWatch();
        this._homeWatcher = null;
    }

    private void removeObservers() {
        this._mainViewModel.isQuizSubmitted.removeObservers(this.getViewLifecycleOwner());
    }

    @Override
    public void onHomePressed() {
        Log.i(TAG,"Home Button Pressed");
        Toast.makeText(this.getContext(),"Home Button Pressed",Toast.LENGTH_SHORT).show();
//        if(this._mainViewModel.isQuizSubmitted.getValue()) {
//            this._mainViewModel.isQuizSubmitted.setValue(false);
//        }
    }

    @Override
    public void onHomeLongPressed() {
        Log.i(TAG,"Home Button Long Pressed");
        Toast.makeText(this.getContext(),"Home Button Long Pressed",Toast.LENGTH_SHORT).show();
//        if(this._mainViewModel.isQuizSubmitted.getValue()) {
//            this._mainViewModel.isQuizSubmitted.setValue(false);
//        }
    }

}