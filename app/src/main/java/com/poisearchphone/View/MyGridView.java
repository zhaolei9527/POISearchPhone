package com.poisearchphone.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * com.wenguoyi.View
 *
 * @author 赵磊
 * @date 2018/5/16
 * 功能描述：
 */
public class MyGridView extends GridView {

    public MyGridView(Context context) {
        super(context);

    }
    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}