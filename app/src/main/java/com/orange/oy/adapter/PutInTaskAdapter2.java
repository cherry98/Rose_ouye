package com.orange.oy.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orange.oy.R;
import com.orange.oy.base.Tools;
import com.orange.oy.info.PutInTaskInfo;
import com.orange.oy.network.Urls;
import com.orange.oy.util.ImageLoader;

import java.util.ArrayList;

import static com.orange.oy.R.id.lin_totalmoney;


public class PutInTaskAdapter2 extends BaseAdapter {
    private Context context;
    private ArrayList<PutInTaskInfo> list;
    private View.OnClickListener onClickListener;
    private String state;//1：草稿箱未发布；2：投放中；3：已结束
    private ImageLoader imageLoader;
    private boolean isDelet;
    private int delWidth = 132;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setDelet(boolean delet) {
        isDelet = delet;
        notifyDataSetChanged();
    }

    public PutInTaskAdapter2(Context context, ArrayList<PutInTaskInfo> list, String state) {
        this.context = context;
        this.list = list;
        this.state = state;
        delWidth = (int) context.getResources().getDimension(R.dimen.task_del_width);
        imageLoader = new ImageLoader(context);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = Tools.loadLayout(context, R.layout.item_putin_task2);
            viewHolder = new ViewHolder();
            viewHolder.swipemenulib = (LinearLayout) convertView.findViewById(R.id.swipemenulib);
            viewHolder.main = (LinearLayout) convertView.findViewById(R.id.main);
            viewHolder.btnDelete = (TextView) convertView.findViewById(R.id.btnDelete);

            viewHolder.itemalltask_target_num = (TextView) convertView.findViewById(R.id.itemalltask_target_num);
            viewHolder.itemalltask_img = (ImageView) convertView.findViewById(R.id.itemalltask_img);
            viewHolder.lin_unit_price = (LinearLayout) convertView.findViewById(R.id.lin_unit_price);
            viewHolder.itemalltask_name = (TextView) convertView.findViewById(R.id.itemalltask_name);
            viewHolder.itemalltask_time1 = (TextView) convertView.findViewById(R.id.itemalltask_time1);
            viewHolder.itemalltask_time2 = (TextView) convertView.findViewById(R.id.itemalltask_time2);
            viewHolder.itemalltask_total_money = (TextView) convertView.findViewById(R.id.itemalltask_total_money);
            viewHolder.item_task_putin = (TextView) convertView.findViewById(R.id.item_task_putin); //投放
            viewHolder.item_looked = (TextView) convertView.findViewById(R.id.item_looked);
            viewHolder.lin_totalmoney = (LinearLayout) convertView.findViewById(lin_totalmoney); //任务总金额
            viewHolder.lin_daishshen = (LinearLayout) convertView.findViewById(R.id.lin_daishshen);

            viewHolder.item_looked2 = (TextView) convertView.findViewById(R.id.item_looked2);
            viewHolder.item_task_next_putin = (TextView) convertView.findViewById(R.id.item_task_next_putin); //再投放
            viewHolder.item_additionalexpenses = (TextView) convertView.findViewById(R.id.item_additionalexpenses); //追加费用
            viewHolder.putIn_task = (LinearLayout) convertView.findViewById(R.id.putIn_task); //发布的任务
            viewHolder.lin_finish_task = (LinearLayout) convertView.findViewById(R.id.lin_finish_task); //
            viewHolder.putin_item_last = convertView.findViewById(R.id.putin_item_last);
            viewHolder.lin_finish_task_n1 = (TextView) convertView.findViewById(R.id.lin_finish_task_n1);
            viewHolder.lin_finish_task_n2 = (TextView) convertView.findViewById(R.id.lin_finish_task_n2);
            viewHolder.lin_finish_task_n3 = (TextView) convertView.findViewById(R.id.lin_finish_task_n3);
            viewHolder.lin_finish_task_n4 = (TextView) convertView.findViewById(R.id.lin_finish_task_n4);
            viewHolder.lin_finish_task_n4_bg = convertView.findViewById(R.id.lin_finish_task_n4_bg);

            viewHolder.total_num = (TextView) convertView.findViewById(R.id.total_num);
            viewHolder.yiling = (TextView) convertView.findViewById(R.id.yiling);
            viewHolder.haveDo = (TextView) convertView.findViewById(R.id.haveDo);
            viewHolder.daishen = (TextView) convertView.findViewById(R.id.daishen);
            viewHolder.daifu = (TextView) convertView.findViewById(R.id.daifu);
            viewHolder.finished = (TextView) convertView.findViewById(R.id.finished);

            viewHolder.total_num2 = (TextView) convertView.findViewById(R.id.total_num2);
            viewHolder.pass_num = (TextView) convertView.findViewById(R.id.pass_num);
            viewHolder.nopass_num = (TextView) convertView.findViewById(R.id.nopass_num);
            viewHolder.reward_money = (TextView) convertView.findViewById(R.id.reward_money);

            viewHolder.lin_times2 = (LinearLayout) convertView.findViewById(R.id.lin_times2);
            viewHolder.lin_times1 = (LinearLayout) convertView.findViewById(R.id.lin_times1);
            viewHolder.itemalltask_money = (TextView) convertView.findViewById(R.id.itemalltask_money);

            viewHolder.item_lin_putin = (LinearLayout) convertView.findViewById(R.id.item_lin_putin);
            viewHolder.item_look = (TextView) convertView.findViewById(R.id.item_look);
            viewHolder.item_putagain = (TextView) convertView.findViewById(R.id.item_putagain);
            viewHolder.item_look2 = (TextView) convertView.findViewById(R.id.item_look2);
            viewHolder.item_putagain2 = (TextView) convertView.findViewById(R.id.item_putagain2);
            viewHolder.itemalltask_people = (LinearLayout) convertView.findViewById(R.id.itemalltask_people);
            viewHolder.itemalltask_people2 = (LinearLayout) convertView.findViewById(R.id.itemalltask_people2);
            viewHolder.itemalltask_get_num2 = (TextView) convertView.findViewById(R.id.itemalltask_get_num2);
            viewHolder.item_task_putined = (TextView) convertView.findViewById(R.id.item_task_putined);
            viewHolder.item_task_edit = (TextView) convertView.findViewById(R.id.item_task_edit);
            viewHolder.itemalltask_get_num = (TextView) convertView.findViewById(R.id.itemalltask_get_num);

            viewHolder.item_looked3 = convertView.findViewById(R.id.item_looked3);

            viewHolder.item_lin_putin_finish = convertView.findViewById(R.id.item_lin_putin_finish);
            viewHolder.itemalltask_people2_finish = convertView.findViewById(R.id.itemalltask_people2_finish);
            viewHolder.lin_unit_price_finish = convertView.findViewById(R.id.lin_unit_price_finish);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.swipemenulib.scrollTo(0, 0);

        if (list != null && !list.isEmpty()) {
            PutInTaskInfo putInTaskInfo = list.get(position);

            //    "activity_type":"活动类型（1：集图活动；2：偶业项目）"
            if (!Tools.isEmpty(putInTaskInfo.getActivity_type()) && putInTaskInfo.getActivity_type().equals("1")) {

                if (!Tools.isEmpty(putInTaskInfo.getProject_total_money())) {
                    viewHolder.itemalltask_get_num.setText("投放总金额：" + "(" + putInTaskInfo.getProject_total_money() + ")" + " 元");
                    // viewHolder.itemalltask_get_num.setText("已参与人数/人： " + putInTaskInfo.getGet_num() + "");
                }

                if ("null".equals(putInTaskInfo.getTemplate_img()) || TextUtils.isEmpty(putInTaskInfo.getTemplate_img())) {
                    viewHolder.itemalltask_img.setImageResource(R.mipmap.ssfrw_button_ji);
                } else {
                    imageLoader.DisplayImage(Urls.ImgIp + putInTaskInfo.getTemplate_img(), viewHolder.itemalltask_img,
                            R.mipmap.ssfrw_button_ji);
                }

                viewHolder.lin_times1.setVisibility(View.VISIBLE);
                viewHolder.lin_times2.setVisibility(View.GONE);
                // viewHolder.lin_totalmoney.setVisibility(View.GONE);
                viewHolder.lin_unit_price.setVisibility(View.GONE);
                viewHolder.putIn_task.setVisibility(View.GONE);
                viewHolder.item_lin_putin.setVisibility(View.VISIBLE);

                if (!Tools.isEmpty(putInTaskInfo.getActivity_status())) { //1：草稿箱未发布；2：投放中；3：已结束

                    if (putInTaskInfo.getActivity_status().equals("2")) {
                        viewHolder.itemalltask_people.setVisibility(View.VISIBLE);
                        viewHolder.lin_totalmoney.setVisibility(View.GONE);
                        //// TODO: 2018/8/29  投放中 是有参与人数的
                        viewHolder.lin_finish_task.setVisibility(View.VISIBLE);
                        viewHolder.item_task_putined.setVisibility(View.GONE);
                        viewHolder.item_look.setVisibility(View.GONE);
                        viewHolder.item_lin_putin.setVisibility(View.VISIBLE);
//                        viewHolder.item_lin_putin_finish.setVisibility(View.VISIBLE);
                        viewHolder.itemalltask_get_num2.setVisibility(View.GONE);
                        viewHolder.item_putagain.setVisibility(View.GONE);

                        viewHolder.lin_finish_task_n1.setText("参与人数");
                        viewHolder.lin_finish_task_n2.setText("广告浏览");
                        viewHolder.lin_finish_task_n3.setText("广告点击");
                        viewHolder.lin_finish_task_n4.setText("赞助商数量");
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewHolder.lin_finish_task_n4_bg.getLayoutParams();
                        lp.weight = 1;
                        viewHolder.lin_finish_task_n4_bg.setLayoutParams(lp);
                        viewHolder.total_num2.setText(putInTaskInfo.getGet_num());
                        viewHolder.pass_num.setText(putInTaskInfo.getAd_show_num());
                        viewHolder.nopass_num.setText(putInTaskInfo.getAd_click_num());
                        viewHolder.reward_money.setText(putInTaskInfo.getSponsor_num());
                    } else if (putInTaskInfo.getActivity_status().equals("1")) {  //草稿箱
                        viewHolder.itemalltask_people.setVisibility(View.VISIBLE);
                        viewHolder.lin_totalmoney.setVisibility(View.VISIBLE);
                        viewHolder.item_task_putin.setVisibility(View.VISIBLE);
                        //===============================删除
                        if (isDelet) {
                            viewHolder.btnDelete.setVisibility(View.VISIBLE);
                            viewHolder.swipemenulib.scrollTo(delWidth, 0);

                            viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    iPutInTask.deleteDraft(position);
                                }
                            });
                            // viewHolder.itemapplyone_runnow.setVisibility(View.GONE);
                            viewHolder.main.setBackgroundResource(R.drawable.itemcorpsnotice_bg2);
                        } else {
                            viewHolder.swipemenulib.scrollTo(0, 0);
                            // viewHolder.itemapplyone_runnow.setVisibility(View.VISIBLE);
                            viewHolder.main.setBackgroundResource(R.drawable.itemcorpsnotice_bg1);
                        }

                        viewHolder.lin_finish_task.setVisibility(View.GONE);
                        viewHolder.item_task_putined.setVisibility(View.GONE);
                        viewHolder.item_look.setVisibility(View.GONE);
                        viewHolder.item_lin_putin.setVisibility(View.GONE);
                        viewHolder.itemalltask_get_num2.setVisibility(View.GONE);
                        viewHolder.item_putagain.setVisibility(View.GONE);
                    } else if (putInTaskInfo.getActivity_status().equals("3")) {
                        viewHolder.lin_totalmoney.setVisibility(View.VISIBLE);
                        viewHolder.itemalltask_people.setVisibility(View.GONE);
                        viewHolder.itemalltask_people2.setVisibility(View.VISIBLE);
                        viewHolder.item_task_putined.setVisibility(View.GONE);
                        viewHolder.lin_finish_task.setVisibility(View.VISIBLE);
                        viewHolder.putIn_task.setVisibility(View.GONE);
                        viewHolder.itemalltask_get_num2.setVisibility(View.GONE);
                        viewHolder.item_task_putined.setVisibility(View.GONE);
                        viewHolder.item_look.setVisibility(View.VISIBLE);
                        viewHolder.item_lin_putin.setVisibility(View.GONE);
                        viewHolder.item_putagain.setVisibility(View.VISIBLE);
                        viewHolder.lin_finish_task_n1.setText("参与人数");
                        viewHolder.lin_finish_task_n2.setText("广告浏览");
                        viewHolder.lin_finish_task_n3.setText("广告点击");
                        viewHolder.lin_finish_task_n4.setText("赞助商数量");
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewHolder.lin_finish_task_n4_bg.getLayoutParams();
                        lp.weight = 1;
                        viewHolder.lin_finish_task_n4_bg.setLayoutParams(lp);
                        viewHolder.total_num2.setText(putInTaskInfo.getGet_num());
                        viewHolder.pass_num.setText(putInTaskInfo.getAd_show_num());
                        viewHolder.nopass_num.setText(putInTaskInfo.getAd_click_num());
                        viewHolder.reward_money.setText(putInTaskInfo.getSponsor_num());

                        viewHolder.item_putagain2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                iPutInTask.PutAgain(position);
                            }
                        });

                    }
                }
                viewHolder.item_look2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iPutInTask.Lookthree(position);
                    }
                });

            } else {

                if (!Tools.isEmpty(putInTaskInfo.getProject_total_money())) {
                    viewHolder.itemalltask_total_money.setText("任务总金额：" + Tools.removePoint(putInTaskInfo.getProject_total_money()) + " 元");
                }
                if ("null".equals(putInTaskInfo.getTemplate_img()) || TextUtils.isEmpty(putInTaskInfo.getTemplate_img())) {
                    viewHolder.itemalltask_img.setImageResource(R.mipmap.round_pai);
                } else {
                    imageLoader.DisplayImage(Urls.ImgIp + putInTaskInfo.getTemplate_img(), viewHolder.itemalltask_img,
                            R.mipmap.round_pai);
                }
                viewHolder.itemalltask_people.setVisibility(View.GONE);
                viewHolder.lin_totalmoney.setVisibility(View.VISIBLE);
                viewHolder.itemalltask_get_num2.setVisibility(View.GONE);
                viewHolder.item_lin_putin.setVisibility(View.GONE);


                if (!Tools.isEmpty(putInTaskInfo.getActivity_status())) { //1：草稿箱未发布；2：投放中；3：已结束

                    if (putInTaskInfo.getActivity_status().equals("1")) {
                        //===============================删除

                        if (isDelet) {
                            viewHolder.btnDelete.setVisibility(View.VISIBLE);
                            viewHolder.swipemenulib.scrollTo(delWidth, 0);
                            viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    iPutInTask.deleteDraft(position);
                                }
                            });
                            viewHolder.main.setBackgroundResource(R.drawable.itemcorpsnotice_bg2);
                        } else {
                            viewHolder.swipemenulib.scrollTo(0, 0);
                            viewHolder.main.setBackgroundResource(R.drawable.itemcorpsnotice_bg1);
                        }


                        viewHolder.lin_finish_task.setVisibility(View.GONE);
                        viewHolder.putIn_task.setVisibility(View.GONE);
                        viewHolder.lin_unit_price.setVisibility(View.GONE);

                        viewHolder.item_task_putin.setVisibility(View.VISIBLE);
                        viewHolder.item_looked.setVisibility(View.GONE);
                        viewHolder.item_task_next_putin.setVisibility(View.GONE);
                        viewHolder.lin_times2.setVisibility(View.VISIBLE);
                        viewHolder.lin_times1.setVisibility(View.GONE);

                    } else if (putInTaskInfo.getActivity_status().equals("2")) {
                        viewHolder.itemalltask_money.setVisibility(View.INVISIBLE);
                        viewHolder.lin_finish_task.setVisibility(View.GONE);
                        viewHolder.putIn_task.setVisibility(View.VISIBLE);
                        viewHolder.lin_unit_price.setVisibility(View.VISIBLE);
                        viewHolder.lin_unit_price_finish.setVisibility(View.VISIBLE);

                        viewHolder.item_task_putin.setVisibility(View.GONE);
                        viewHolder.item_looked.setVisibility(View.GONE);
                        viewHolder.item_task_next_putin.setVisibility(View.GONE);
                        viewHolder.lin_times2.setVisibility(View.GONE);
                        viewHolder.lin_times1.setVisibility(View.VISIBLE);
                    } else if (putInTaskInfo.getActivity_status().equals("3")) {
                        viewHolder.itemalltask_people2.setVisibility(View.VISIBLE);
                        viewHolder.lin_finish_task.setVisibility(View.VISIBLE);
                        viewHolder.putIn_task.setVisibility(View.GONE);
                        viewHolder.lin_unit_price.setVisibility(View.GONE);

                        viewHolder.item_task_putin.setVisibility(View.GONE);

                        viewHolder.item_looked.setVisibility(View.GONE);
                        viewHolder.item_task_next_putin.setVisibility(View.GONE);
                        viewHolder.item_putagain2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                iPutInTask.NextPutIntask(position);
                            }
                        });


                        viewHolder.lin_times2.setVisibility(View.GONE);
                        viewHolder.lin_times1.setVisibility(View.VISIBLE);
                        viewHolder.lin_finish_task_n1.setText("总量");
                        viewHolder.lin_finish_task_n2.setText("通过");
                        viewHolder.lin_finish_task_n3.setText("不通过");
                        viewHolder.lin_finish_task_n4.setText("发放奖励/元");
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewHolder.lin_finish_task_n4_bg.getLayoutParams();
                        lp.weight = 2;
                        viewHolder.lin_finish_task_n4_bg.setLayoutParams(lp);
                        viewHolder.total_num2.setText(putInTaskInfo.getTotal_num());
                        viewHolder.pass_num.setText(putInTaskInfo.getPass_num());
                        viewHolder.nopass_num.setText(putInTaskInfo.getUnpass_num());
                        viewHolder.reward_money.setText(putInTaskInfo.getReward_money());
                    }
                }
                viewHolder.item_look2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iPutInTask.Looktwo(position);
                    }
                });
            }
            viewHolder.total_num.setText(putInTaskInfo.getTotal_num());
            viewHolder.yiling.setText(putInTaskInfo.getGettask_num());
            viewHolder.haveDo.setText(putInTaskInfo.getDone_num());
            viewHolder.daishen.setText(putInTaskInfo.getCheck_num());
            viewHolder.finished.setText(putInTaskInfo.getComplete_num());

            if (!Tools.isEmpty(putInTaskInfo.getActivity_name())) {
                viewHolder.itemalltask_name.setText(putInTaskInfo.getActivity_name());
            }
            if (!Tools.isEmpty(putInTaskInfo.getTarget_num())) {
                viewHolder.itemalltask_target_num.setText("目标参与人数：" + putInTaskInfo.getTarget_num() + "人");
            }

            if (!Tools.isEmpty(putInTaskInfo.getBegin_date()) && !Tools.isEmpty(putInTaskInfo.getEnd_date())) {
                viewHolder.itemalltask_time1.setText("活动起止日期：" + putInTaskInfo.getBegin_date() + "~" + putInTaskInfo.getEnd_date());
                viewHolder.itemalltask_time2.setText("活动起止日期：" + putInTaskInfo.getBegin_date() + "~" + putInTaskInfo.getEnd_date());
            }

            if (!Tools.isEmpty(putInTaskInfo.getGet_num())) {
                // viewHolder.itemalltask_get_num.setText("已参与人数/人： " + putInTaskInfo.getGet_num() + "");
                viewHolder.itemalltask_get_num2.setText("已参与人数/人： " + putInTaskInfo.getGet_num() + " ");
            }


            if (!Tools.isEmpty(putInTaskInfo.getMoney())) {//18/9/12执行单价不显示
//                viewHolder.itemalltask_money.setText("执行单价：" + putInTaskInfo.getMoney() + " 元");
                viewHolder.itemalltask_money.setText("");
            }

            viewHolder.item_task_putin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //投放
                    iPutInTask.PutIntask(position);
                }
            });
            viewHolder.item_additionalexpenses.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //  iPutInTask.FinishTask(position);  结束
                    iPutInTask.additionalexpenses(position);
                }
            });
            viewHolder.item_looked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPutInTask.Look(position);
                }
            });


            viewHolder.item_task_next_putin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPutInTask.NextPutIntask(position); //再投放
                }
            });

            //==============================集图活动按钮=================================//

            viewHolder.item_look.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPutInTask.Lookthree(position);
                }
            });

            viewHolder.item_looked2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPutInTask.Looktwo(position);
                }
            });

            viewHolder.item_looked3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // iPutInTask.Lookthree(position);
                    iPutInTask.additionalexpenses(position);
                }
            });

            viewHolder.item_task_putined.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPutInTask.PutIntasktwo(position);
                }
            });

            viewHolder.item_task_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPutInTask.Edit(position);
                }
            });

            viewHolder.lin_daishshen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPutInTask.Looktwo(position);
                }
            });

            viewHolder.item_putagain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iPutInTask.PutAgain(position);
                }
            });
            viewHolder.item_lin_putin_finish.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    iPutInTask.FinishTask(position);
                }
            });
            viewHolder.itemalltask_people2_finish.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    iPutInTask.FinishTask(position);
                }
            });
            viewHolder.lin_unit_price_finish.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    iPutInTask.FinishTask(position);
                }
            });
        }

        return convertView;
    }

    private IPutInTask iPutInTask;

    public interface IPutInTask {
        void PutIntask(int position);

        void NextPutIntask(int position);

        void Look(int position);

        void Looktwo(int position);


        void Edit(int position);  //其他活动编辑

        void Lookthree(int position);   //集图活动查看

        void PutIntasktwo(int position);  //集图活动投放

        void FinishTask(int position); //结束任务

        void PutAgain(int position);//集图活动再次投放

        void deleteDraft(int position); //草稿箱删除

        void additionalexpenses(int position); //追加费用
    }

    public void setiPutInTask(IPutInTask iPutInTask) {
        this.iPutInTask = iPutInTask;
    }

    class ViewHolder {
        View putin_item_last;
        TextView lin_finish_task_n1, lin_finish_task_n2, lin_finish_task_n3, lin_finish_task_n4, item_look2, item_putagain2;
        View lin_finish_task_n4_bg;
        TextView itemalltask_target_num, itemalltask_name, itemalltask_time1, itemalltask_time2,
                itemalltask_total_money, itemalltask_money;

        TextView item_task_putin, item_looked, item_looked2, item_task_next_putin, item_additionalexpenses;
        ImageView itemcorpstask_ico, itemalltask_img;
        LinearLayout lin_daishshen, lin_totalmoney, lin_unit_price, putIn_task, lin_finish_task, lin_times1, lin_times2;  //执行单价,投放的任务,结束的任务
        TextView total_num, yiling, haveDo, daishen, daifu, finished;
        TextView total_num2, pass_num, nopass_num, reward_money;

        //老投放的页面
        LinearLayout itemalltask_people, itemalltask_people2, item_lin_putin;
        TextView item_look, itemalltask_get_num2, item_task_putined, itemalltask_get_num, item_task_edit;
        TextView item_putagain;
        View item_looked3;
        //删除
        private LinearLayout swipemenulib, main;
        private TextView btnDelete;

        private View item_lin_putin_finish, itemalltask_people2_finish, lin_unit_price_finish;//结束活动
    }
}
