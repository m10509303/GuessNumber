package edu.ntust.prlab.guessnumber;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements GuessNumberRecyclerViewAdapter.ItemClickListener {

//    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ANSWER_KEY = "answer";
    private static final String UPPER_BOUND_KEY = "upper";
    private static final String LOWER_BOUND_KEY = "lower";
    private static final String TARGET_KEY = "target";
    private static final String RANGE_KEY = "range";

    private EditText mRangeEditText;
    private RecyclerView mGuessNumberRecyclerView;
    private GuessNumberRecyclerViewAdapter mNumberRecyclerViewAdapter;
    private volatile Toast mLastToastMessage;

    private int mAnswerNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindResources();
        //防止EditText一進入就要求輸入。
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initData(savedInstanceState);
    }

    //跟UI物件綁定並初始化三種狀態的圖片檔。
    private void bindResources() {
        mRangeEditText = (EditText) findViewById(R.id.edit_range);
        mGuessNumberRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_guess_number);
        //依據版本不同取得資源方式也不同。
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mNumberRecyclerViewAdapter = new GuessNumberRecyclerViewAdapter(this,
                    ContextCompat.getDrawable(this, R.drawable.item_normal),
                    ContextCompat.getDrawable(this, R.drawable.item_correct),
                    ContextCompat.getDrawable(this, R.drawable.item_incorrect));
        } else {
            mNumberRecyclerViewAdapter = new GuessNumberRecyclerViewAdapter(this,
                    getResources().getDrawable(R.drawable.item_normal),
                    getResources().getDrawable(R.drawable.item_correct),
                    getResources().getDrawable(R.drawable.item_incorrect)
            );
        }
        mGuessNumberRecyclerView.setAdapter(mNumberRecyclerViewAdapter);
    }

    //供「螢幕旋轉」或「重新開始」設定終極密碼資料。
    private synchronized void setData(int answerNumber, int range, int targetNumber, int lowerBound, int upperBound) {
        mAnswerNumber = answerNumber;
        mNumberRecyclerViewAdapter.setGuessRange(range);
        mNumberRecyclerViewAdapter.setTargetNumber(targetNumber);
        mNumberRecyclerViewAdapter.setLowerBound(lowerBound);
        mNumberRecyclerViewAdapter.setUpperBound(upperBound);
        mNumberRecyclerViewAdapter.notifyDataSetChanged();
    }

    //如果是畫面旋轉，則沿用上一次資料，否則就開始新回合。
    private void initData(Bundle extra) {
        if (extra != null) {
            recover(extra);
        } else {
            refresh();
        }
    }

    private void recover(Bundle extra) {
        setData(extra.getInt(ANSWER_KEY),
                Integer.parseInt(extra.getString(RANGE_KEY)),
                extra.getInt(TARGET_KEY),
                extra.getInt(LOWER_BOUND_KEY),
                extra.getInt(UPPER_BOUND_KEY));
    }

    //刷新資料 && 檢查數字範圍是否合法(>0)
    private static final Random random = new Random();

    private void refresh() {
        String rangeText = String.valueOf(mRangeEditText.getText());
        if (rangeText.matches("[1-9]+\\d*")) {
            int range = Integer.valueOf(rangeText);
            setData(random.nextInt(range), range, -1, 0, range - 1);
            displayMessage("新回合開始！");
//        Log.i(TAG, "Answer : " + (mAnswerNumber + 1));
        } else {
            displayMessage("數字範圍錯誤(至少要大於0)！");
        }
    }

    @Override
    public void onItemClicked(int index) {
        if (index > mAnswerNumber) {
            //如果點選數字大於答案，則更新上界。
            int lastUpperBound = mNumberRecyclerViewAdapter.getUpperBound();
            mNumberRecyclerViewAdapter.setUpperBound(index - 1);
            mNumberRecyclerViewAdapter.notifyItemRangeChanged(index, lastUpperBound - index + 1);
//            Log.i(TAG, "Upper Bound : " + index + " -> " + (lastUpperBound - index + 1));
        } else if (index < mAnswerNumber) {
            //如果點選數字小於答案，則更新下界。
            int lastLowerBound = mNumberRecyclerViewAdapter.getLowerBound();
            mNumberRecyclerViewAdapter.setLowerBound(index + 1);
            mNumberRecyclerViewAdapter.notifyItemRangeChanged(lastLowerBound, index - lastLowerBound + 1);
//            Log.i(TAG, "Lower Bound : " + lastLowerBound + " -> " + (index - lastLowerBound + 1));
        } else {
            //如果點選數字等於答案，則公布答案並結束遊戲。
            mNumberRecyclerViewAdapter.setTargetNumber(index);
            mNumberRecyclerViewAdapter.notifyItemChanged(index);
//            displayMessage("本回合結束，答案就是" + (index + 1) + "！");
        }
    }

    //透過Toast顯示讓玩家知道遊戲狀態。
    private void displayMessage(String message) {
        if (mLastToastMessage != null) mLastToastMessage.cancel();
        mLastToastMessage = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mLastToastMessage.show();
    }

    //如果使用者要求重新開始則重製設定。
    public void onRefreshButtonClicked(View view) {
        refresh();
    }

    //如果螢幕旋轉則保留終極密碼設定以便在onCreate時使用。
    @Override
    protected void onSaveInstanceState(Bundle extra) {
        super.onSaveInstanceState(extra);
        extra.putInt(ANSWER_KEY, mAnswerNumber);
        extra.putString(RANGE_KEY, String.valueOf(mRangeEditText.getText()));
        extra.putInt(UPPER_BOUND_KEY, mNumberRecyclerViewAdapter.getUpperBound());
        extra.putInt(LOWER_BOUND_KEY, mNumberRecyclerViewAdapter.getLowerBound());
        extra.putInt(TARGET_KEY, mNumberRecyclerViewAdapter.getTargetNumber());
    }

}
