package soham.quiz_app.fragments.result;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import soham.quiz_app.MainViewModel;
import soham.quiz_app.databinding.FragmentResultBinding;

public class Result extends Fragment {

    private FragmentResultBinding _binding;
    private ResultListAdapter _resultListAdapter;
    private MainViewModel _mainViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this._binding = FragmentResultBinding.inflate(inflater,container,false);
        this.initVariables();
        this.initAdapter();
        return this._binding.getRoot();
    }

    private void initVariables(){
        this._mainViewModel = new ViewModelProvider(this.getActivity()).get(MainViewModel.class);
    }

    private void initAdapter(){
        this._resultListAdapter = new ResultListAdapter();
        this._binding.resultQuestionList.setAdapter(this._resultListAdapter);
        this._resultListAdapter.submitList(this._mainViewModel.rvu.getValue().questions);
        this._resultListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        this._binding.marksScoredView.setText(
                this._mainViewModel.rvu.getValue().getScore()+" / "+this._mainViewModel.rvu.getValue().questions.size()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this._binding = null;
    }

}