package soham.quiz_app.fragments.create_test;


import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import soham.quiz_app.MainViewModel;
import soham.quiz_app.databinding.FragmentCreateTestBinding;
import soham.quiz_app.utils.CreateQuizUtils;
import soham.quiz_app.utils.models.Question;


public class CreateTest extends Fragment {

    private static final String TAG = "test->CreateTest";
    private FragmentCreateTestBinding _binding;
    private MainViewModel _mainViewModel;
    private CreateQuestionOptionAdapter _optionAdapter;

    private TextWatcher _questionTextChangeListener,
            _quizTextChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this._binding = FragmentCreateTestBinding.inflate(inflater,container,false);
        this._binding.setFragmentCreateTest(this);
        initVariables();
        initObservers();
        initAdapters();
        initListeners();
        return this._binding.getRoot();
    }

    private void initVariables() {
        this._mainViewModel = new ViewModelProvider(this.getActivity()).get(MainViewModel.class);
        if(this._mainViewModel.createQuizUtils == null)
            this._mainViewModel.createQuizUtils = new CreateQuizUtils();
        this._quizTextChangeListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                _mainViewModel.createQuizUtils.Name = editable.toString();
            }
        };
        this._questionTextChangeListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable!=null)
                    _mainViewModel.createQuizUtils.getCurrent().Question = editable.toString();
            }
        };
    }

    private void initObservers() {
        this._mainViewModel.quiz_id.observe(this.getViewLifecycleOwner(),status ->{
            this._showLoading(false);
            if(status>0) {
                this.goToDashboard();
                return;
            }
        });
    }

    private void initAdapters() {
        this._optionAdapter = new CreateQuestionOptionAdapter(position->{
            this._mainViewModel.createQuizUtils.getCurrent().deleteOption(position);
            this._optionAdapter.submitList(this._mainViewModel.createQuizUtils.getCurrent().options);
            this._optionAdapter.notifyItemRemoved(position);
        });
        this._binding.createOptionRecyclerView.setAdapter(this._optionAdapter);
    }

    private void initListeners(){
        this._binding.quizNameInput.addTextChangedListener(this._quizTextChangeListener);
        this._binding.questionTextInput.addTextChangedListener(this._questionTextChangeListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(this._mainViewModel.createQuizUtils.current == -1)        this.next();
        else this.showQuestion(this._mainViewModel.createQuizUtils.getCurrent());
    }

    public void next(){
        Question qs = this._mainViewModel.createQuizUtils.getNext();
        this.showQuestion(qs);
    }

    public void previous(){
        Question qs = this._mainViewModel.createQuizUtils.getPrevious();
        if(qs == null ) return;
        this.showQuestion(qs);
    }

    public void deleteCurrent(){
        this._mainViewModel.createQuizUtils.deleteCurrent();
        if(this._mainViewModel.createQuizUtils.current == 0) this.next();
        else this.previous();
    }

    private void showQuestion(Question qs){
        if(this._mainViewModel.createQuizUtils.Name!= null)
            this._binding.quizNameInput.setText(this._mainViewModel.createQuizUtils.Name);
        if(qs.Question != null)
            this._binding.questionTextInput.setText(qs.Question);
        if(qs.options!=null)
            this._optionAdapter.submitList(qs.options);
    }

    public void done(){
        this._showLoading(true);
        if(this._mainViewModel.createQuizUtils.Name.length()<1){
            this._binding.quizNameInput.setError("Name");
            this._showLoading(false);
            return;
        }
        Question qs = this._mainViewModel.createQuizUtils.validate();
        if(qs != null){
            this._mainViewModel.createQuizUtils.current = this._mainViewModel.createQuizUtils.questions.indexOf(qs);
            this.showQuestion(qs);
            if(qs.Question.length()<1) this._binding.questionTextInput.setError("Enter Question");
            else if(qs.options.size()<1) Toast.makeText(this.getContext(),"Enter options.",Toast.LENGTH_SHORT).show();
            this._showLoading(false);
            return;
        }
        this._mainViewModel.createQuiz();
    }

    private void goToDashboard(){
        findNavController(this).navigateUp();
    }

    public void addOption(){
        this._optionAdapter.submitList(this._mainViewModel.createQuizUtils.getCurrent().addNewOption());
    }

    private void _showLoading(boolean b) {
        int visibility = b ? View.VISIBLE : View.GONE;
        this._binding.loadingPanelIncl.loadingPanel.setVisibility(visibility);
        this.disableOthers(!b);
    }

    private void disableOthers(boolean flag) {
        this._binding.doneBtn.setEnabled(flag);
        this._binding.deleteQuestionBtn.setEnabled(flag);
        this._binding.nextBtn.setEnabled(flag);
        this._binding.previousBtn.setEnabled(flag);
        this._binding.questionInputLayout.setEnabled(flag);
        this._binding.questionTextInput.setEnabled(flag);
        this._binding.quizNameInput.setEnabled(flag);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.removeObservers();
        this.removeListeners();
        this._binding = null;
    }

    private void removeObservers() {
        this._mainViewModel.isLogin.removeObservers(this.getViewLifecycleOwner());
    }

    private void removeListeners() {
        this._binding.quizNameInput.removeTextChangedListener(this._quizTextChangeListener);
        this._binding.questionTextInput.removeTextChangedListener(this._questionTextChangeListener);
        this._quizTextChangeListener = null;
        this._questionTextChangeListener = null;
    }

}