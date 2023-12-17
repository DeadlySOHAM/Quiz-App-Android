package soham.quiz_app.fragments.login_signup;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;

import soham.quiz_app.MainViewModel;
import soham.quiz_app.databinding.FragmentLoginBinding;

public class Login extends Fragment {

    private FragmentLoginBinding _binding = null;
    private MainViewModel _mainViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initVariables();
        initObservers();
        this._binding = FragmentLoginBinding.inflate(inflater,container,false);
        this._binding.setFragmentLogin(this);
        return this._binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        this._showLoading(true);
        this._mainViewModel.checkLoginStat();
    }

    private void initVariables() {
        this._mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
    }

    private void initObservers() {

        this._mainViewModel.isLogin.observe(this.getViewLifecycleOwner(),stat->{
            if(stat!=null && !stat){
                Toast.makeText(getContext(),"Not Logged In",Toast.LENGTH_SHORT).show();
                this._showLoading(false);
            }   else if(stat!=null && stat){
                this._showLoading(false);
                this._goToDashBoard();
            }
        });

        this._mainViewModel.authToken.observe(this.getViewLifecycleOwner(),token->{
            if(token != null) {
                getActivity().getPreferences(Context.MODE_PRIVATE)
                        .edit()
                        .putString("auth_token",token)
                        .commit();
                this._mainViewModel.checkLoginStat();
            }
            else {
                Toast.makeText(getContext(), "Unable to login", Toast.LENGTH_SHORT);
                this._showLoading(false);
            }
        });
    }

    public void goToSignUp(){
        NavDirections action = soham.quiz_app.fragments.login_signup.LoginDirections.actionLoginToSignUp();
        findNavController(this).navigate(action);
    }

    private void _goToDashBoard() {
        NavDirections action = soham.quiz_app.fragments.login_signup.LoginDirections.actionLoginSignUpToDashboard();
        findNavController(this).navigate(action);
    }

    public void login(){
        String mail = this._binding.emailInputField.getText().toString(),
            pswd = this._binding.passwordInputField.getText().toString();
        boolean isTeacher = this._binding.teacherRadioBtn.isChecked();

        if(mail.length()<1) {
            this._binding.emailInputLayout.setError("Please enter an email id.");
            return;
        } else if(pswd.length()<1) {
            this._binding.passwordInputField.setError("Please enter password.");
            return;
        }

        this._showLoading(true);
        this._mainViewModel.login(mail,pswd,isTeacher);
    }

    private void _showLoading(boolean flag){
        int visibility = flag ? View.VISIBLE : View.GONE;
        this._binding.loadingPanelIncl.loadingPanel.setVisibility(visibility);
        this.setEnable(!flag);
    }

    private void setEnable(boolean flag) {
        this._binding.loginBtn.setEnabled(flag);
        this._binding.signup.setEnabled(flag);
        this._binding.teacherRadioBtn.setEnabled(flag);
        this._binding.studentRadioBtn.setEnabled(flag);
        this._binding.passwordInputField.setEnabled(flag);
        this._binding.emailInputField.setEnabled(flag);
        this._binding.emailInputLayout.setEnabled(flag);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.removeObservers();
        this._binding = null;
    }

    private void removeObservers() {
        this._mainViewModel.isLogin.removeObservers(this.getViewLifecycleOwner());
        this._mainViewModel.authToken.removeObservers(this.getViewLifecycleOwner());
    }
}