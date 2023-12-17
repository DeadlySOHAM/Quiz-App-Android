package soham.quiz_app.fragments.create_test;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import soham.quiz_app.databinding.CreateQuestionOptionListItemBinding;
import soham.quiz_app.utils.models.Option;

public class CreateQuestionOptionAdapter extends ListAdapter<Option, CreateQuestionOptionAdapter.CreateQuestionOptionViewHolder> {

    private static final String TAG = "test->OptionAdapter";
    private final OnDeleteCallback _callback;

    public CreateQuestionOptionAdapter(OnDeleteCallback callback) {
        super(new CreateQuestionOptionDiffUtil());
        _callback = callback;
    }

    @Override
    public void submitList(@Nullable List<Option> list) {
        super.submitList(list);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CreateQuestionOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CreateQuestionOptionViewHolder(
                CreateQuestionOptionListItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),parent,false
                )
        );
    }

    @Override
    public void onViewRecycled(@NonNull CreateQuestionOptionViewHolder holder) {
        holder.removeTextWatcher();
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateQuestionOptionViewHolder holder, int position) {
        holder.bind(this.getCurrentList().get(position));
        holder.binding.deleteButton.setOnClickListener(l->{
            this._callback.delete(position);
        });
    }

    private static class CreateQuestionOptionDiffUtil extends DiffUtil.ItemCallback<Option>{

        @Override
        public boolean areItemsTheSame(@NonNull Option oldItem, @NonNull Option newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Option oldItem, @NonNull Option newItem) {
            return oldItem.value.equals(newItem.value);
        }
    }

    protected class CreateQuestionOptionViewHolder extends RecyclerView.ViewHolder {
        public CreateQuestionOptionListItemBinding binding;
        public TextWatcher textWatcher = null ;
        public CreateQuestionOptionViewHolder(CreateQuestionOptionListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(Option opt){
            this.textWatcher = new TextWatcher() {
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
                    opt.value = editable.toString();
                }
            };
            this.binding.optionTextInput.addTextChangedListener(this.textWatcher);
            this.binding.optionTextInput.setText(opt.value);
            this.binding.correctCheckBox.setChecked(opt.isCorrect);
            this.binding.correctCheckBox.setOnClickListener(l-> {
                opt.isCorrect = this.binding.correctCheckBox.isChecked();
            });
        }

        public void removeTextWatcher(){
            if(this.textWatcher !=null)
                this.binding.optionTextInput.removeTextChangedListener(this.textWatcher);
            this.textWatcher = null;
        }
    }

    public interface OnDeleteCallback{
        public void delete(int position);
    }

}
