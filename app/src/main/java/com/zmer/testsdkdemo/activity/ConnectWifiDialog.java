package com.zmer.testsdkdemo.activity;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orange.oy.R;


public class ConnectWifiDialog extends Dialog implements View.OnClickListener{

	private Context mContext;
	private InputMethodManager imm;
	private View inflate;
	private DialogConnectClickListener click;
	private ImageView imgCloseDialog;
	private TextView textSSID;
	private EditText textPSD;
	private String ssid;
	private Button btn_connect_wifi;


	public ConnectWifiDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
		inflate = LayoutInflater.from(context).inflate(R.layout.wifi_dialog, null);
		imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		setContentView(inflate);
		initView();
		setClickListener();
	}
	
	private void setClickListener() {
		imgCloseDialog.setOnClickListener(this);
		btn_connect_wifi.setOnClickListener(this);
	}

	private void initView() {
		imgCloseDialog = (ImageView) findViewById(R.id.img_close_dialog);
		textSSID = (TextView) findViewById(R.id.text_connect_wifi_ssidinfo);
		textPSD = (EditText) findViewById(R.id.text_connect_wifi_psdinfo);
		btn_connect_wifi = (Button) findViewById(R.id.btn_begin_connnect_wifi);

	}

	public void setTitle(String ssid_info){
		ssid = ssid_info;
		textSSID.setText(ssid_info);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_begin_connnect_wifi:
			imm.hideSoftInputFromWindow(textPSD.getWindowToken(), 0);

			String password = textPSD.getText().toString();
			click.clickConfirmBtn(password,ssid);
			break;
			
		case R.id.img_close_dialog:
			dismiss();
			break;

		default:
			break;
		}
		
	}
	
	public interface DialogConnectClickListener{
		public void clickConfirmBtn(String password, String ssid);
	}
	public void setClickConfirmListener(DialogConnectClickListener listener){
		click = listener;
	}

}
