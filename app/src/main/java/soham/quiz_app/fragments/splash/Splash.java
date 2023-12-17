package soham.quiz_app.fragments.splash;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;

import soham.quiz_app.MainViewModel;
import soham.quiz_app.databinding.FragmentSplashBinding;

public class Splash extends Fragment {

    private final static String TAG = "test->SplashFragment";
    private FragmentSplashBinding _binding;
    private MainViewModel _mainViewModel;
    private SharedPreferences _shpf;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initVariables();
        initObservers();
        this._binding = FragmentSplashBinding.inflate(inflater,container,false);
        return this._binding.getRoot();
    }

    private void initVariables() {
        this._mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        this._shpf = getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    private void initObservers() {
        this._mainViewModel.isDbConnected.observe(this.getViewLifecycleOwner(),stat->{
            if(stat!=null && stat) {
                this._mainViewModel.checkLoginStat();
                Toast.makeText(getContext(),"Connected",Toast.LENGTH_SHORT).show();
            }
            else if(stat != null && !stat)
                Toast.makeText(getContext(),"Unable to connect",Toast.LENGTH_SHORT).show();
        });
        this._mainViewModel.isLogin.observe(this.getViewLifecycleOwner(),stat->{
            if(stat!=null && stat)
                this._goToDashBoard();
            else if(stat != null && !stat)
                this._goToLogin();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        this._mainViewModel.initDbConnection(this._shpf.getString("auth_token",null));
    }

    private void _goToDashBoard() {
        NavDirections action = soham.quiz_app.fragments.splash.SplashDirections.actionSplashToDashboard();
        findNavController(this).navigate(action);
    }

    private void _goToLogin() {
        NavDirections action = soham.quiz_app.fragments.splash.SplashDirections.actionSplashToLogin();
        findNavController(this).navigate(action);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeObservers();
        this._binding = null;
    }

    private void removeObservers() {
        this._mainViewModel.isLogin.removeObservers(this.getViewLifecycleOwner());
        this._mainViewModel.isDbConnected.removeObservers(this.getViewLifecycleOwner());
    }

}