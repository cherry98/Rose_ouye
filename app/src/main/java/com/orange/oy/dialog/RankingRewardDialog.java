package com.orange.oy.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.util.ImageLoader;

/**
 * Created by cherr on 2018/7/17.
 * 排名奖励 V3.18
 */

public class RankingRewardDialog extends LinearLayout implements View.OnClickListener {

    private TextView dialogranking_prize, dialogranking_desc;
    private ImageView dialogranking_img;
    private OnRankingRewardLisener onRankingRewardLisener;

    public RankingRewardDialog(Context context) {
        super(context);
        Tools.loadLayout(this, R.layout.dialog_rankingreward);
        dialogranking_prize = (TextView) findViewById(R.id.dialogranking_prize);
        dialogranking_desc = (TextView) findViewById(R.id.dialogranking_desc);
        dialogranking_img = (ImageView) findViewById(R.id.dialogranking_img);
        findViewById(R.id.dialogranking_submit).setOnClickListener(this);
    }

    private static AlertDialog dialog;

    public static RankingRewardDialog showDialog(Context context, String prize, String desc, String desc_1,
                                                 String imgUrl, OnRankingRewardLisener onRankingRewardLisener) {
        RankingRewardDialog rewardDialog = new RankingRewardDialog(context);
        rewardDialog.setData(prize, desc, desc_1, imgUrl);
        rewardDialog.setOnRankingRewardLisener(onRankingRewardLisener);
        dialog = new AlertDialog.Builder(context, R.style.DialogTheme).setCancelable(false).create();
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        dialog.show();
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        dialog.addContentView(rewardDialog, layoutParams);
        return rewardDialog;
    }

    private void setData(String prize, String desc, String desc_1, String imgUrl) {
        dialogranking_prize.setText(prize);
        int start = desc.indexOf(desc_1);
        int end = start + desc_1.length();
        SpannableStringBuilder builder = new SpannableStringBuilder(desc);
        builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.makesure)),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        dialogranking_desc.setText(builder);
        ImageLoader imageLoader = new ImageLoader(getContext());
        imageLoader.DisplayImage(imgUrl, dialogranking_img);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialogranking_submit) {
            onRankingRewardLisener.obtainReward();
        }
    }

    public interface OnRankingRewardLisener {
        void obtainReward();
    }

    public void setOnRankingRewardLisener(OnRankingRewardLisener onRankingRewardLisener) {
        this.onRankingRewardLisener = onRankingRewardLisener;
    }
}
