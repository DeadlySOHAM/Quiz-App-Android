package soham.quiz_app.fragments.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import soham.quiz_app.R;
import soham.quiz_app.databinding.QuizCardItemBinding;
import soham.quiz_app.utils.models.DashboardDetails;

public class QuizCardListAdapter extends ListAdapter<DashboardDetails.Quiz, QuizCardListAdapter.QuizCardViewHolder> {

    private static final String TAG = "test->QuizListAdap";
    private final OnQuizAction _onQuizAction;

    public QuizCardListAdapter(OnQuizAction onQuizAction) {
        super(new QuizCardDiffUtil());
        this._onQuizAction = onQuizAction;
    }

    @NonNull
    @Override
    public QuizCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuizCardViewHolder(
                QuizCardItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false),
                this._onQuizAction
        );
    }

    @Override
    public void onBindViewHolder(@NonNull QuizCardViewHolder holder, int position) {
        holder.bind(getCurrentList().get(position));
    }

    private static class QuizCardDiffUtil extends DiffUtil.ItemCallback<DashboardDetails.Quiz>{

        @Override
        public boolean areItemsTheSame(@NonNull DashboardDetails.Quiz oldItem, @NonNull DashboardDetails.Quiz newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull DashboardDetails.Quiz oldItem, @NonNull DashboardDetails.Quiz newItem) {
            return false;
        }
    }

    protected class QuizCardViewHolder extends RecyclerView.ViewHolder{

        final public QuizCardItemBinding _binding;
        private final OnQuizAction _onQuizAction;

        public QuizCardViewHolder(@NonNull QuizCardItemBinding binding, OnQuizAction onQuizAction) {
            super(binding.getRoot());
            this._binding = binding;
            this._onQuizAction = onQuizAction;
        }

        public void bind(DashboardDetails.Quiz item){
            this._binding.quizName.setText(item.Name);
            this._binding.questionCount.setText(item._question_count+"");

            if(item.Teacher!=null) { // Student
                this._binding.teacherName.setText(item.Teacher);
                this._binding.teacherName.setVisibility(View.VISIBLE);
                  this._binding.resultBtn.setVisibility(item.attemptCount<item.totalAttempt?View.VISIBLE: View.GONE);
                this._binding.addStudentBtn.setVisibility(View.GONE);
                if(item.attemptCount <1)
                    this._binding.quizCard.setBackgroundResource(R.color.red_unavailable);
                else
                    this._binding.quizCard.setBackgroundResource(R.color.green_available);
                this._binding.resultBtn.setOnClickListener(l->{
                    this._onQuizAction.onShowResult(item);
                });
            }
            else { // Teacher
                this._binding.addStudentBtn.setOnClickListener(l->{
                    this._onQuizAction.onAddStudent(item);
                });
                this._binding.teacherName.setVisibility(View.GONE);
                this._binding.resultBtn.setVisibility(View.GONE);
                this._binding.addStudentBtn.setVisibility(View.VISIBLE);
            }

            this._binding.quizCard.setOnClickListener(l->{
                this._onQuizAction.onQuizClick(item);
            });
        }
    }

    public interface OnQuizAction {
        void onQuizClick(DashboardDetails.Quiz qz);
        void onAddStudent(DashboardDetails.Quiz qz);
        void onShowResult(DashboardDetails.Quiz qz);
    }

}
