package com.appster.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.apster.common.PixelUtil;
import com.apster.common.UiUtils;

public class SquareImageViewRefill extends ImageView
{
	public SquareImageViewRefill(Context context)
	{
		super(context);
	}

	public SquareImageViewRefill(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public SquareImageViewRefill(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, widthMeasureSpec - PixelUtil.dpToPx(getContext(),45) );
	}
}