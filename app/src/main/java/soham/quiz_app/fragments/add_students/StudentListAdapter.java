package soham.quiz_app.fragments.add_students;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import soham.quiz_app.databinding.StudentListItemBinding;

public class StudentListAdapter extends ListAdapter<String, StudentListAdapter.StudentListViewHolder> {

    private OnStudentActions _callback;

    public StudentListAdapter(OnStudentActions callback) {
        super(new StudentListDiffUtil());
        this._callback = callback;
    }

    @NonNull
    @Override
    public StudentListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentListViewHolder(
                StudentListItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListViewHolder holder, int position) {
        holder.bind(getItem(position));
        holder.binding.studentCard.setOnClickListener(l->this._callback.selectStudent(getItem(position)));
        holder.binding.deleteStudentBtn.setOnClickListener(l->this._callback.deleteStudent(getItem(position)));
    }

    private static class StudentListDiffUtil extends DiffUtil.ItemCallback<String>{

        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    }

    protected class StudentListViewHolder extends RecyclerView.ViewHolder{
        public StudentListItemBinding binding;
        public StudentListViewHolder(@NonNull StudentListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String mail){
            this.binding.studentMailView.setText(mail);
        }
    }

    public interface OnStudentActions{
        void selectStudent(String mail);
        void deleteStudent(String mail);
    }
}
