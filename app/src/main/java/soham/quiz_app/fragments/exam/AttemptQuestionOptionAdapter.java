package soham.quiz_app.fragments.exam;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import soham.quiz_app.databinding.AttemptQuestionOptionListItemBinding;
import soham.quiz_app.utils.TakeQuizUtil.Question.Option;

public class AttemptQuestionOptionAdapter extends ListAdapter<Option, AttemptQuestionOptionAdapter.AttemptQuestionOptionViewHolder> {

    private static final String TAG = "test->OptionAdapter";

    protected AttemptQuestionOptionAdapter() {
        super(new AttemptQuestionOptionDiffUtil());
    }

    @Override
    public void submitList(@Nullable List<Option> list) {
        super.submitList(list);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AttemptQuestionOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AttemptQuestionOptionViewHolder(
                AttemptQuestionOptionListItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),parent,false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull AttemptQuestionOptionViewHolder holder, int position) {
        holder.bind(this.getCurrentList().get(position));
    }

    private static class AttemptQuestionOptionDiffUtil extends DiffUtil.ItemCallback<Option>{

        @Override
        public boolean areItemsTheSame(@NonNull Option oldItem, @NonNull Option newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Option oldItem, @NonNull Option newItem) {
            return oldItem.option.equals(newItem.option);
        }
    }

    protected class AttemptQuestionOptionViewHolder extends RecyclerView.ViewHolder {
        public AttemptQuestionOptionListItemBinding binding;
        public AttemptQuestionOptionViewHolder(AttemptQuestionOptionListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(Option opt){
            this.binding.optionTextView.setText(opt.option);
            this.binding.correctCheckBox.setChecked(opt.is_selected);
            this.binding.correctCheckBox.setOnClickListener(l-> {
                opt.is_selected = this.binding.correctCheckBox.isChecked();
            });
        }
    }

}
