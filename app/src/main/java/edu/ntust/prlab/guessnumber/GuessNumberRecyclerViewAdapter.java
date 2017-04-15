package edu.ntust.prlab.guessnumber;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class GuessNumberRecyclerViewAdapter extends RecyclerView.Adapter<GuessNumberRecyclerViewAdapter.ViewHolder> {

    private static int NO_ANSWER = -1;

    private int mGuessRange;
    private GuessNumberRecyclerViewAdapter.ItemClickListener mItemClickListener;
    private Drawable mNormalBackground;
    private Drawable mCorrectBackground;
    private Drawable mIncorrectBackground;
    private int mTargetNumber = NO_ANSWER;
    private int mUpperBound;
    private int mLowerBound;

    GuessNumberRecyclerViewAdapter(GuessNumberRecyclerViewAdapter.ItemClickListener itemClickListener,
                                   Drawable normalBackground, Drawable correctBackground, Drawable incorrectBackground) {
        this.mItemClickListener = itemClickListener;
        this.mNormalBackground = normalBackground;
        this.mCorrectBackground = correctBackground;
        this.mIncorrectBackground = incorrectBackground;
    }

    void setGuessRange(int guessRange) {
        this.mGuessRange = guessRange;
    }

    void setUpperBound(int upperBound) {
        this.mUpperBound = upperBound;
    }

    void setLowerBound(int lowerBound) {
        this.mLowerBound = lowerBound;
    }

    void setTargetNumber(int targetNumber) {
        this.mTargetNumber = targetNumber;
    }

    int getUpperBound() {
        return this.mUpperBound;
    }

    int getLowerBound() {
        return this.mLowerBound;
    }

    int getTargetNumber() {
        return this.mTargetNumber;
    }

    @Override
    public GuessNumberRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GuessNumberRecyclerViewAdapter.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_number, parent, false));
    }

    @Override
    public void onBindViewHolder(GuessNumberRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mNumberTextView.setText(String.valueOf(position + 1));
        //分別呈現三種狀態的圖片
        if (position == this.mTargetNumber) {
            holder.mNumberTextView.setBackground(this.mCorrectBackground);
            holder.mNumberTextView.setEnabled(true);
        } else if (position >= this.mLowerBound && position <= this.mUpperBound) {
            holder.mNumberTextView.setBackground(this.mNormalBackground);
            holder.mNumberTextView.setEnabled(true);
        } else {
            holder.mNumberTextView.setBackground(this.mIncorrectBackground);
            holder.mNumberTextView.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return this.mGuessRange;
    }

    interface ItemClickListener {
        void onItemClicked(int index);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNumberTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mNumberTextView = (TextView) itemView.findViewById(R.id.text_number);
            mNumberTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //遊戲結束就不再觸發
                    if (mTargetNumber == NO_ANSWER)
                        mItemClickListener.onItemClicked(getAdapterPosition());
                }
            });
        }

    }
}