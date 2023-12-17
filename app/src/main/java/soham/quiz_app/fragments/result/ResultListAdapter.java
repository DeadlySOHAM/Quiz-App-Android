package soham.quiz_app.fragments.result;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import soham.quiz_app.R;
import soham.quiz_app.databinding.ResultListItemBinding;
import soham.quiz_app.utils.ResultViewUtil;

public class ResultListAdapter extends ListAdapter<ResultViewUtil.Question, ResultListAdapter.ResultListViewHolder> {

    protected ResultListAdapter() {
        super(new ResultListDiffUtil());
    }

    @NonNull
    @Override
    public ResultListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ResultListViewHolder( ResultListItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                    ));
    }

    @Override
    public void onBindViewHolder(@NonNull ResultListViewHolder holder, int position) {
        holder.bind(this.getItem(position));
    }

    private static class ResultListDiffUtil extends DiffUtil.ItemCallback<ResultViewUtil.Question>{

        @Override
        public boolean areItemsTheSame(@NonNull ResultViewUtil.Question oldItem, @NonNull ResultViewUtil.Question newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ResultViewUtil.Question oldItem, @NonNull ResultViewUtil.Question newItem) {
            return false;
        }
    }

    protected class ResultListViewHolder extends RecyclerView.ViewHolder {
        private ResultListItemBinding _binding;
        public ResultListViewHolder(@NonNull ResultListItemBinding binding) {
            super(binding.getRoot());
            this._binding = binding;
        }

        public void bind(ResultViewUtil.Question qs){
            this._binding.questionView.setText(qs.question);
            setSelectedResponseView(qs);
            setUnSelectedResponseView(qs);
        }

        public void setSelectedResponseView(ResultViewUtil.Question qs){
            if(qs.selected.size()<1) {
                this._binding.resultListCard.setBackgroundResource(R.color.grey_unavailable);
                this._binding.yourAnswerHeadingView.setVisibility(View.GONE);
                this._binding.selectedResponseView.setVisibility(View.GONE);
            } else {
                this._binding.yourAnswerHeadingView.setVisibility(View.VISIBLE);
                this._binding.selectedResponseView.setVisibility(View.VISIBLE);
                this._binding.resultListCard.setBackgroundResource(
                        qs.is_correct ? R.color.green_available : R.color.red_unavailable
                );
                String s = "";
                for (String opt:qs.selected)
                    s+=opt+"\n";
                this._binding.selectedResponseView.setText(s);
            }
        }

        public void setUnSelectedResponseView(ResultViewUtil.Question qs){
            if(qs.unselected.size()<1) {
                this._binding.otherOptionsHeadingView.setVisibility(View.GONE);
                this._binding.unselectedResponseView.setVisibility(View.GONE);
            } else {
                this._binding.otherOptionsHeadingView.setVisibility(View.VISIBLE);
                this._binding.unselectedResponseView.setVisibility(View.VISIBLE);
                String s = "";
                for (String opt:qs.unselected)
                    s+=opt+"\n";
                this._binding.unselectedResponseView.setText(s);
            }
        }
    }
}
