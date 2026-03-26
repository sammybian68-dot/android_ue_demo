package com.example.btphone.ui.contacts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;

import com.example.btphone.R;

/**
 * 右侧字母索引自定义控件。
 * <p>
 * 支持触摸滑动选择字母，弹出放大气泡显示当前字母，并通过
 * {@link OnLetterSelectedListener} 回调通知外部进行列表滚动联动。
 * <p>
 * 气泡位置规则：
 * <ul>
 *   <li>#~A（前两个字母）：气泡固定在顶部</li>
 *   <li>Y~Z（后两个字母）：气泡固定在底部</li>
 *   <li>B~X（中间字母）：气泡跟随手指位置</li>
 * </ul>
 */
public class LetterIndexView extends View {

    /** 索引字母列表，包含 # 和 A-Z 共 27 个字符 */
    @VisibleForTesting
    static final String[] LETTERS = {
            "#", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z"
    };

    private final Paint mLetterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPopupBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPopupTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPopupShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mPopupRect = new RectF();

    private float mLetterHeight;
    private int mSelectedIndex = -1;
    private boolean mIsTouching;

    private int mPopupOuterSize;
    private int mPopupInnerSize;
    private float mPopupRadius;
    private int mPopupOffsetX;
    private float mPopupLetterSize;
    private float mPopupShadowRadius;
    private int mDotSize;

    @Nullable
    private OnLetterSelectedListener mListener;

    /**
     * 字母选择回调接口。
     */
    public interface OnLetterSelectedListener {

        /**
         * 触摸选中某个字母时调用。
         *
         * @param letter 当前选中的字母
         */
        void onLetterSelected(@NonNull String letter);

        /**
         * 手指抬起，字母选择结束时调用。
         */
        void onLetterSelectionFinished();
    }

    public LetterIndexView(Context context) {
        super(context);
        init();
    }

    public LetterIndexView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LetterIndexView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLetterPaint.setTextAlign(Paint.Align.CENTER);
        mLetterPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.letter_index_text_size));
        mLetterPaint.setColor(ContextCompat.getColor(getContext(), R.color.dark_hint_text));

        mDotPaint.setColor(ContextCompat.getColor(getContext(), R.color.letter_index_indicator));

        mPopupBgPaint.setColor(ContextCompat.getColor(getContext(), R.color.popup_bg));

        mPopupShadowRadius = getResources().getDimensionPixelSize(R.dimen.popup_shadow_radius);
        mPopupShadowPaint.setColor(ContextCompat.getColor(getContext(), R.color.popup_bg));
        mPopupShadowPaint.setShadowLayer(mPopupShadowRadius, 0, 0, 0x80000000);
        setLayerType(LAYER_TYPE_SOFTWARE, mPopupShadowPaint);

        mPopupTextPaint.setTextAlign(Paint.Align.CENTER);
        mPopupLetterSize = getResources().getDimensionPixelSize(R.dimen.popup_letter_size);
        mPopupTextPaint.setTextSize(mPopupLetterSize);
        mPopupTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.dark_primary_text));

        mPopupOuterSize = getResources().getDimensionPixelSize(R.dimen.popup_outer_size);
        mPopupInnerSize = getResources().getDimensionPixelSize(R.dimen.popup_inner_size);
        mPopupRadius = getResources().getDimensionPixelSize(R.dimen.popup_radius);
        mPopupOffsetX = getResources().getDimensionPixelSize(R.dimen.popup_offset_x);
        mDotSize = getResources().getDimensionPixelSize(R.dimen.letter_dot_size);
    }

    /**
     * 设置字母选择监听器。
     *
     * @param listener 监听器，可以为 null 以取消监听
     */
    public void setOnLetterSelectedListener(@Nullable OnLetterSelectedListener listener) {
        mListener = listener;
    }

    /**
     * 获取当前选中的字母索引。
     *
     * @return 选中索引，-1 表示未选中
     */
    @VisibleForTesting
    int getSelectedIndex() {
        return mSelectedIndex;
    }

    /**
     * 设置当前选中的字母索引（仅测试用）。
     *
     * @param index 字母索引
     */
    @VisibleForTesting
    void setSelectedIndex(int index) {
        mSelectedIndex = index;
        invalidate();
    }

    /**
     * 获取触摸状态（仅测试用）。
     *
     * @return 是否正在触摸
     */
    @VisibleForTesting
    boolean isTouchingState() {
        return mIsTouching;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        mLetterHeight = (float) height / LETTERS.length;
        float centerX = width / 2f;

        Paint.FontMetrics fm = mLetterPaint.getFontMetrics();
        float textCenterOffset = -(fm.ascent + fm.descent) / 2f;

        for (int i = 0; i < LETTERS.length; i++) {
            float letterCenterY = mLetterHeight * i + mLetterHeight / 2f;
            canvas.drawText(LETTERS[i], centerX, letterCenterY + textCenterOffset, mLetterPaint);
        }

        if (mIsTouching && mSelectedIndex >= 0 && mSelectedIndex < LETTERS.length) {
            drawIndicatorDot(canvas, centerX);
            drawPopup(canvas);
        }
    }

    /**
     * 绘制当前选中字母位置的圆形指示器。
     */
    private void drawIndicatorDot(@NonNull Canvas canvas, float centerX) {
        float dotCenterY = mLetterHeight * mSelectedIndex + mLetterHeight / 2f;
        float dotRadius = mDotSize / 2f;
        canvas.drawCircle(centerX, dotCenterY, dotRadius, mDotPaint);
    }

    /**
     * 绘制字母放大弹出气泡。
     * <p>
     * 气泡绘制在控件左侧外部，需要父布局设置 clipChildren=false。
     */
    private void drawPopup(@NonNull Canvas canvas) {
        float letterCenterY = mLetterHeight * mSelectedIndex + mLetterHeight / 2f;
        float popupCenterY = clampPopupCenterY(letterCenterY);

        float innerOffset = (mPopupOuterSize - mPopupInnerSize) / 2f;
        float popupLeft = -mPopupOffsetX;
        float innerLeft = popupLeft + innerOffset;
        float innerTop = popupCenterY - mPopupInnerSize / 2f;

        mPopupRect.set(innerLeft, innerTop,
                innerLeft + mPopupInnerSize, innerTop + mPopupInnerSize);

        canvas.drawRoundRect(mPopupRect, mPopupRadius, mPopupRadius, mPopupShadowPaint);
        canvas.drawRoundRect(mPopupRect, mPopupRadius, mPopupRadius, mPopupBgPaint);

        Paint.FontMetrics pfm = mPopupTextPaint.getFontMetrics();
        float textY = popupCenterY - (pfm.ascent + pfm.descent) / 2f;
        float textX = innerLeft + mPopupInnerSize / 2f;
        canvas.drawText(LETTERS[mSelectedIndex], textX, textY, mPopupTextPaint);
    }

    /**
     * 将气泡中心 Y 坐标约束在控件范围内。
     * <p>
     * 实现了 #~A 固定顶部、Y~Z 固定底部、B~X 跟随手指的行为。
     *
     * @param rawCenterY 未约束的中心 Y
     * @return 约束后的中心 Y
     */
    @VisibleForTesting
    float clampPopupCenterY(float rawCenterY) {
        float minY = mPopupOuterSize / 2f;
        float maxY = getHeight() - mPopupOuterSize / 2f;
        return Math.max(minY, Math.min(rawCenterY, maxY));
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mIsTouching = true;
                updateSelectedLetter(event.getY());
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsTouching = false;
                if (mListener != null) {
                    mListener.onLetterSelectionFinished();
                }
                invalidate();
                return true;

            default:
                return super.onTouchEvent(event);
        }
    }

    /**
     * 根据触摸 Y 坐标更新选中的字母索引并通知监听器。
     *
     * @param touchY 触摸 Y 坐标
     */
    private void updateSelectedLetter(float touchY) {
        int height = getHeight();
        if (height <= 0) {
            return;
        }
        int index = (int) (touchY / height * LETTERS.length);
        index = Math.max(0, Math.min(index, LETTERS.length - 1));

        if (index != mSelectedIndex) {
            mSelectedIndex = index;
            if (mListener != null) {
                mListener.onLetterSelected(LETTERS[mSelectedIndex]);
            }
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
