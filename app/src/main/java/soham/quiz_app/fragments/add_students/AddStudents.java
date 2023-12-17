package soham.quiz_app.fragments.add_students;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;

import java.util.regex.Pattern;

import soham.quiz_app.MainViewModel;
import soham.quiz_app.databinding.FragmentAddStudentsBinding;

public class AddStudents
        extends Fragment
        implements StudentListAdapter.OnStudentActions {

    private static final String TAG="test->AddStudents";
    private FragmentAddStudentsBinding _binding;
    private MainViewModel _mainViewModel;
    private StudentListAdapter _studentListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this._binding = FragmentAddStudentsBinding.inflate(inflater,container,false);
        initVariables();
        initAdapters();
        initListeners();
        initObservers();
        return this._binding.getRoot();
    }

    private void initVariables() {
        if(this.getArguments().getInt("quiz_id") == -1)
            findNavController(this).navigateUp();
        this._mainViewModel = new ViewModelProvider(this.getActivity()).get(MainViewModel.class);
        this._binding.setFragmentAddStudents(this);
        this._mainViewModel.rvu.setValue(null);
    }

    private void initObservers() {
        this._mainViewModel.students.observe(this.getViewLifecycleOwner(), students -> {
            this._studentListAdapter.submitList(students);
            this._studentListAdapter.notifyDataSetChanged();
        });
        this._mainViewModel.rvu.observe(this.getViewLifecycleOwner(),resultViewUtil -> {
            if(resultViewUtil == null || resultViewUtil.quiz==null || resultViewUtil.questions.get(0).id==0) return;
            _goToResult();
        });
    }

    private void initAdapters() {
        this._studentListAdapter = new StudentListAdapter(this);
        this._binding.studentListRecyclerView.setAdapter(this._studentListAdapter);
        this._mainViewModel.getStudentList(this.getArguments().getInt("quiz_id"));
    }

    private void initListeners(){
    }

    public void add(){
        String mails = this._binding.studentMailInputField.getText().toString().trim();
        if(mails.length()<=0) return;
        String[] mail = mails.split("\\s+");
        for( String m : mail) {
            if (!this._mainViewModel.students.getValue().contains(m.trim()))
                if(Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$").matcher(m).matches())
                    this._mainViewModel.addStudentToQuiz(this.getArguments().getInt("quiz_id"),m.trim());
        }
    }

    private void _goToResult(){
        NavDirections action = AddStudentsDirections.actionAddStudentsToResult();
        findNavController(this).navigate(action);
    }

    private void _showLoading(boolean b) {
        int visibility = b ? View.VISIBLE : View.GONE;
        this._binding.loadingPanelIncl.loadingPanel.setVisibility(visibility);
        this.disableOthers(!b);
    }

    private void disableOthers(boolean flag) {
        this._binding.addBtn.setEnabled(flag);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.removeObservers();
        this._binding = null;
    }

    private void removeObservers() {
        this._mainViewModel.quiz_id.removeObservers(this.getViewLifecycleOwner());
        this._mainViewModel.rvu.removeObservers(this.getViewLifecycleOwner());
    }

    @Override
    public void selectStudent(String mail) {
        this._showLoading(true);
        this._mainViewModel.getTeacherResult(this.getArguments().getInt("quiz_id"),mail);
    }

    @Override
    public void deleteStudent(String mail) {
        Log.d(TAG,"Deleting Student\n"+this.getArguments().getInt("quiz_id")+"|"+mail);

        this._mainViewModel.removeStudentFromQuiz(
                this.getArguments().getInt("quiz_id"),
                mail
        );
    }
}