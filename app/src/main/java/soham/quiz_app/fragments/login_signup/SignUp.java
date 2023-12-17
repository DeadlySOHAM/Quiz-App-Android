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
import soham.quiz_app.databinding.FragmentSignUpBinding;

public class SignUp extends Fragment {

    private FragmentSignUpBinding _binding;
    private MainViewModel _mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initVariables();
        initObservers();
        this._binding = FragmentSignUpBinding.inflate(inflater,container,false);
        this._binding.setFragmentSignUp(this);
        return this._binding.getRoot();
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
                Toast.makeText(getContext(), "Unable to Sign Up", Toast.LENGTH_SHORT);
                this._showLoading(false);
            }
        });

    }

    private void _goToDashBoard(){
        NavDirections action = soham.quiz_app.fragments.login_signup.SignUpDirections.actionSignUpToDashboard();
        findNavController(this).navigate(action);
    }

    private void _showLoading(boolean flag){
        int visibility = flag ? View.VISIBLE : View.GONE;
        this._binding.loadingPanelIncl.loadingPanel.setVisibility(visibility);
        this.setEnable(!flag);
    }

    private void setEnable(boolean flag) {
        this._binding.signup.setEnabled(flag);

        this._binding.teacherRadioBtn.setEnabled(flag);
        this._binding.studentRadioBtn.setEnabled(flag);

        this._binding.passwordInputLayout.setEnabled(flag);
        this._binding.passwordInputField.setEnabled(flag);

        this._binding.emailInputField.setEnabled(flag);
        this._binding.emailInputLayout.setEnabled(flag);

        this._binding.nameInputField.setEnabled(flag);
        this._binding.nameInputLayout.setEnabled(flag);

        this._binding.instituteInputField.setEnabled(flag);
        this._binding.instituteInputLayout.setEnabled(flag);
    }

    public void signUp(){
        String mail = this._binding.emailInputField.getText().toString(),
                pswd = this._binding.passwordInputField.getText().toString(),
                name = this._binding.nameInputField.getText().toString(),
                institution = this._binding.teacherRadioBtn.isChecked() ?
                        this._binding.instituteInputField.getText().toString():
                        null;
        if(mail.length()<4 || pswd.length()<1 || name.length()<5){
            Toast.makeText(this.getContext(),"Fill the details Properly", Toast.LENGTH_SHORT).show();
            return;
        }
        this._showLoading(true);
        this._mainViewModel.signUp(mail,pswd,name,institution);
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