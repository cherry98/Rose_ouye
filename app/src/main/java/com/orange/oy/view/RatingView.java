package com.orange.oy.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;

/**
 * Created by xiedongyan on 2017/12/19.
 * 体验评论星星评分
 */

public class RatingView extends LinearLayout {

    private TextView itemrating_name, itemrating_score;
    private OnRatingViewClickListener listener;
    private RatingBar itemrating_rating;

    public RatingView(Context context) {
        this(context, null);
    }

    public RatingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Tools.loadLayout(this, R.layout.item_rating);
        itemrating_name = (TextView) findViewById(R.id.itemrating_name);
        itemrating_rating = (RatingBar) findViewById(R.id.itemrating_rating);
        itemrating_score = (TextView) findViewById(R.id.itemrating_score);
        int scroeHeight = 0;
        try {
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.rating_notselect);
            scroeHeight = bmp.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (scroeHeight != 0) {
            LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) itemrating_rating.getLayoutParams();
            llp.width = -2;// 包裹内容
            llp.height = scroeHeight;
            itemrating_rating.setLayoutParams(llp);
        }
    }

    public void setData(String name) {
        itemrating_name.setText(name);
        itemrating_rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                Tools.showToast(context, itemrating_name.getText() + ":" + ratingBar.getRating());
                itemrating_score.setText((int) ratingBar.getRating() + "");
                listener.click(itemrating_name.getText().toString(), (int) ratingBar.getRating() + "");
            }
        });
    }

    public void setData2(String name, String score) {
        itemrating_name.setText(name);
        itemrating_score.setText(score);
        itemrating_rating.setRating(Float.parseFloat(score));
        itemrating_rating.setOnRatingBarChangeListener(null);
    }

    public void setOnRatingViewClickListener(OnRatingViewClickListener listener) {
        this.listener = listener;
    }

    public interface OnRatingViewClickListener {
        void click(String text, String score);
    }
}
