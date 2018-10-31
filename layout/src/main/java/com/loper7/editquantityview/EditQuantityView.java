package com.loper7.editquantityview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author LOPER7
 * @date 2018/7/3 13:50
 * @Description:
 */

public class EditQuantityView extends LinearLayout {

    //无穷大
    public static final int INFINITY = -1;

    private String TAG = "EditQuantityView";

    //默认按钮padding
    private static int defaultPadding;
    //默认按钮字体大小
    private static int defaultButtonTextSize;

    //按钮 [减] 默认属性
    private static int defaultSubtractTextColor = Color.parseColor("#666666");
    private static String defaultSubtractText = "-";
    private static Drawable defaultSubtractBackgroundRes;

    //输入框 默认属性
    private int defaultEditWidth;
    private int defaultEditTextSize;
    private boolean defaultCanInput = false;
    private int defaultEditTextColor = Color.parseColor("#666666");

    //按钮 [加] 属性
    private int defaultAddTextColor = Color.parseColor("#666666");
    private String defaultAddText = "+";
    private static Drawable defaultAddBackgroundRes;


    //按钮padding
    private int padding;
    //按钮字体大小
    private int buttonTextSize;

    //按钮 [减] 属性
    private TextView tvSubtractButton;
    private int subtractTextColor;
    private boolean subtractCanHide;
    private String subtractText = defaultSubtractText;
    private Drawable subtractImageRes, subtractBackgroundRes;
    private LayoutParams subtractParams;

    //输入框 属性
    private EditText editQuantity;
    private int editTextColor;
    private int editWidth;
    private int editTextSize;
    private boolean canInput;
    private static Drawable editBackgroundRes;
    private LayoutParams editParams;

    //按钮 [加] 属性
    private TextView tvAddButton;
    private int addTextColor;
    private String addText = defaultAddText;
    private Drawable addImageRes, addBackgroundRes;
    private LayoutParams addParams;

    //当前数量
    private int quantity = 0;
    //最大值
    private int maxQuantity = INFINITY;
    //最小值
    private int minQuantity = 0;

    private OnQuantityChangedListener onQuantityChangedListener;

    public EditQuantityView(Context context) {
        super(context);
    }

    public EditQuantityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        if (defaultPadding == 0)
            this.defaultPadding = this.dip2px(context, 10.0F);
        if (defaultButtonTextSize == 0)
            this.defaultButtonTextSize = this.sp2px(context, 16.0F);
        if (defaultEditWidth == 0)
            this.defaultEditWidth = this.dip2px(context, 45.0F);
        if (defaultEditTextSize == 0)
            this.defaultEditTextSize = this.sp2px(context, 16.0F);
        if (defaultSubtractBackgroundRes == null)
            this.defaultSubtractBackgroundRes = ContextCompat.getDrawable(context, R.drawable.shape_edit_sub_bg);
        if (defaultAddBackgroundRes == null)
            this.defaultAddBackgroundRes = ContextCompat.getDrawable(context, R.drawable.shape_edit_add_bg);

        this.getAttr(attrs);
        this.initLayout();
    }


    /**
     * 加载自定义属性
     *
     * @param attrs 自定属性
     */
    private void getAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EditQuantityView);
        padding = (int) typedArray.getDimension(R.styleable.EditQuantityView_eButtonPadding, defaultPadding);
        buttonTextSize = (int) typedArray.getDimension(R.styleable.EditQuantityView_eButtonTextSize, defaultButtonTextSize);
        editWidth = (int) typedArray.getDimension(R.styleable.EditQuantityView_eEditWidth, defaultEditWidth);
        editTextSize = (int) typedArray.getDimension(R.styleable.EditQuantityView_eEditTextSize, defaultEditTextSize);
        editTextColor = typedArray.getColor(R.styleable.EditQuantityView_eEditTextColor, defaultEditTextColor);
        canInput = typedArray.getBoolean(R.styleable.EditQuantityView_eCanInput, defaultCanInput);
        subtractTextColor = typedArray.getColor(R.styleable.EditQuantityView_eSubtractTextColor, defaultSubtractTextColor);
        subtractText = (String) typedArray.getText(R.styleable.EditQuantityView_eSubtractText);
        subtractText = TextUtils.isEmpty(subtractText) ? defaultSubtractText : subtractText;
        subtractImageRes = typedArray.getDrawable(R.styleable.EditQuantityView_eSubtractImageRes);
        subtractCanHide = typedArray.getBoolean(R.styleable.EditQuantityView_eSubtractCanHide, false);
        subtractBackgroundRes = typedArray.getDrawable(R.styleable.EditQuantityView_eSubtractBackgroundRes);
        if (subtractImageRes == null && subtractBackgroundRes == null)
            subtractBackgroundRes = defaultSubtractBackgroundRes;
        addTextColor = typedArray.getColor(R.styleable.EditQuantityView_eAddTextColor, defaultAddTextColor);
        addText = (String) typedArray.getText(R.styleable.EditQuantityView_eAddText);
        addText = TextUtils.isEmpty(addText) ? defaultAddText : addText;
        addImageRes = typedArray.getDrawable(R.styleable.EditQuantityView_eAddImageRes);
        addBackgroundRes = typedArray.getDrawable(R.styleable.EditQuantityView_eAddBackgroundRes);
        if (addBackgroundRes == null && addImageRes == null)
            addBackgroundRes = defaultAddBackgroundRes;
        quantity = typedArray.getInt(R.styleable.EditQuantityView_eQuantity, minQuantity);
        quantity = quantity < minQuantity ? minQuantity : quantity;
        maxQuantity = typedArray.getInt(R.styleable.EditQuantityView_eMaxQuantity, INFINITY);
        maxQuantity = (maxQuantity <= 0 && maxQuantity != -1) ? INFINITY : maxQuantity;
        minQuantity = typedArray.getInt(R.styleable.EditQuantityView_eMinQuantity, 0);
        minQuantity = minQuantity < 0 ? 0 : minQuantity;
        editBackgroundRes = typedArray.getDrawable(R.styleable.EditQuantityView_eEditBackgroundRes);

        typedArray.recycle();
    }

    /**
     * 加载布局View
     */
    private void initLayout() {
        this.initSubtractButton();
        this.initEditView();
        this.initAddButton();
    }

    /**
     * 初始化[减]按钮
     */
    @SuppressLint("NewApi")
    private void initSubtractButton() {
        subtractParams = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        tvSubtractButton = new TextView(getContext());
        tvSubtractButton.setId(View.generateViewId());
        tvSubtractButton.setGravity(Gravity.CENTER);
        tvSubtractButton.setPadding(padding, 0, padding, 0);
        tvSubtractButton.setLayoutParams(subtractParams);
        tvSubtractButton.setBackground(subtractBackgroundRes);
        tvSubtractButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) buttonTextSize);
        if (subtractTextColor != 0)
            tvSubtractButton.setTextColor(subtractTextColor);
        tvSubtractButton.setText(subtractText);
        if (subtractImageRes != null) {
            tvSubtractButton.setText("");
            this.setCompoundDrawable(tvSubtractButton, subtractImageRes);
        }

        tvSubtractButton.setVisibility((quantity <= 0 && subtractCanHide) ? INVISIBLE : VISIBLE);

        if (quantity <= minQuantity) {
            tvSubtractButton.setClickable(false);
            tvSubtractButton.setTextColor(Color.parseColor("#E6E6E6"));
        } else {
            tvSubtractButton.setClickable(true);
            tvSubtractButton.setTextColor(subtractTextColor);
        }

        tvSubtractButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractQuantity();
            }
        });

        this.addView(tvSubtractButton);
    }

    /**
     * 初始化[输入框]
     */
    @SuppressLint("NewApi")
    private void initEditView() {
        editParams = new LayoutParams(editWidth, RelativeLayout.LayoutParams.MATCH_PARENT);
        editQuantity = new EditText(getContext());
        editQuantity.setId(View.generateViewId());
        editQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        editQuantity.setGravity(Gravity.CENTER);
        editQuantity.setText(quantity > 0 ? quantity + "" : (subtractCanHide ? "" : "0"));
        editQuantity.setBackground(editBackgroundRes);
        editQuantity.setPadding(0, 0, 0, 0);
        editQuantity.setCursorVisible(false);
        editQuantity.setLayoutParams(editParams);
        editQuantity.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) editTextSize);
        if (editTextColor != 0)
            editQuantity.setTextColor(editTextColor);

        editQuantity.setClickable(canInput);

        editQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    int value = Integer.parseInt(s.toString());
                    if (value < minQuantity) {
                        editQuantity.setText(String.valueOf(minQuantity));
                        editQuantity.setSelection(editQuantity.getText().length());
                        editQuantity.requestFocus();
                    } else if (value > maxQuantity && maxQuantity != INFINITY) {
                        editQuantity.setText(String.valueOf(maxQuantity));
                        editQuantity.setSelection(editQuantity.getText().length());
                        editQuantity.requestFocus();
                    }
                    quantity = Integer.parseInt(editQuantity.getText().toString());
                    if (onQuantityChangedListener != null)
                        onQuantityChangedListener.onQuantityChanged(quantity);
                } else {
                    quantity = minQuantity;
                    if (quantity > 0) {
                        editQuantity.setText(String.valueOf(quantity));
                        editQuantity.setSelection(editQuantity.getText().length());
                        editQuantity.requestFocus();
                    }
                }
                tvSubtractButton.setVisibility((quantity <= 0 && subtractCanHide) ? INVISIBLE : VISIBLE);

                if (quantity <= 0)
                    closeKeyboard((Activity) getContext());

                if (quantity <= minQuantity) {
                    tvSubtractButton.setClickable(false);
                    tvSubtractButton.setTextColor(Color.parseColor("#E6E6E6"));
                } else {
                    tvSubtractButton.setClickable(true);
                    tvSubtractButton.setTextColor(subtractTextColor);
                }

                if (quantity >= maxQuantity && maxQuantity != INFINITY) {
                    tvAddButton.setClickable(false);
                    tvAddButton.setTextColor(Color.parseColor("#E6E6E6"));
                } else {
                    tvAddButton.setClickable(true);
                    tvAddButton.setTextColor(addTextColor);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1 && "0".equals(s.toString())) {
                    s.clear();
                }
            }
        });

        this.addView(editQuantity);

    }

    /**
     * 初始化[加]按钮
     */
    @SuppressLint("NewApi")
    private void initAddButton() {
        addParams = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        tvAddButton = new TextView(getContext());
        tvAddButton.setId(View.generateViewId());
        tvAddButton.setGravity(Gravity.CENTER);
        tvAddButton.setPadding(padding, 0, padding, 0);
        tvAddButton.setLayoutParams(addParams);
        tvAddButton.setBackground(addBackgroundRes);
        tvAddButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) buttonTextSize);
        if (addTextColor != 0)
            tvAddButton.setTextColor(addTextColor);
        tvAddButton.setText(addText);
        if (addImageRes != null) {
            tvAddButton.setText("");
            this.setCompoundDrawable(tvAddButton, addImageRes);
        }

        if (quantity >= maxQuantity && maxQuantity != INFINITY) {
            tvAddButton.setClickable(false);
            tvAddButton.setTextColor(Color.parseColor("#E6E6E6"));
        } else {
            tvAddButton.setClickable(true);
            tvAddButton.setTextColor(addTextColor);
        }

        tvAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuantity();
            }
        });

        this.addView(tvAddButton);
    }


    /**
     * 是否允许输入
     *
     * @param canInput
     */
    public void setCanInput(boolean canInput) {
        this.canInput = canInput;
        if (editQuantity == null)
            return;
        editQuantity.setClickable(canInput);
    }

    /**
     * 设置当前数量
     *
     * @param quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (editQuantity == null)
            return;
        editQuantity.setText(String.valueOf(quantity));
    }

    /**
     * 设置最大值
     *
     * @param maxQuantity
     */
    public void setMax(int maxQuantity) {
        this.maxQuantity = maxQuantity;
        if (quantity > maxQuantity)
            setQuantity(maxQuantity);
    }

    /**
     * 设置最小值
     *
     * @param minQuantity
     */
    public void setMin(int minQuantity) {
        this.minQuantity = minQuantity;
        if (quantity < minQuantity)
            setQuantity(minQuantity);
    }

    /**
     * 增加数量
     */
    private void addQuantity() {
        if (maxQuantity != INFINITY && maxQuantity <= quantity)
            return;
        quantity++;
        editQuantity.setText(String.valueOf(quantity));

    }

    /**
     * 减少数量
     */
    private void subtractQuantity() {
        if (minQuantity >= quantity)
            return;
        quantity--;
        editQuantity.setText(String.valueOf(quantity));
    }


    /**
     * 根据手机的分辨率dp 转成px(像素)
     */
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率sp 转成px(像素)
     */
    private static int sp2px(Context context, float spValue) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5F);
    }

    /**
     * 关闭软键盘
     *
     * @param activity
     */
    private void closeKeyboard(Activity activity) {
        if (activity == null)
            return;

        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        editQuantity.clearFocus();
    }

    /**
     * textView上设置图片
     *
     * @param tv       textView上设置图片
     * @param drawable 图片
     */
    private void setCompoundDrawable(TextView tv, Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        tv.setCompoundDrawables(drawable, null, null, null);//画在左边
    }


    public interface OnQuantityChangedListener {
        void onQuantityChanged(int quantity);
    }

    public void setOnQuantityChangedListener(OnQuantityChangedListener onQuantityChangedListener) {
        this.onQuantityChangedListener = onQuantityChangedListener;
    }
}
