package com.mlegeb.app.ui;

import com.mlegeb.app.R;
import com.mlegeb.app.common.MouseManager;
import com.mlegeb.app.transmission.MouseTransmission;
import com.mlegeb.app.transmission.ShutdownTransmission;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class ShutdownActivity extends BaseActivity implements OnClickListener{

	private EditText command;
	private Button sendBtn;
	private ImageButton powerBtn;
	private ImageButton sleepBtn;
	private ShutdownTransmission transmission;
	private LinearLayout mousePanel;
	
	private MouseManager mouseManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shut_down);
		
		transmission = new ShutdownTransmission();
		mouseManager = new MouseManager();
		InitView();
	}
	
	private void InitView(){
		command = (EditText) findViewById(R.id.editText1);
		sendBtn = (Button) findViewById(R.id.button1);
		powerBtn = (ImageButton) findViewById(R.id.imageButton1);
		sleepBtn = (ImageButton) findViewById(R.id.imageButton2);
		
		sendBtn.setOnClickListener(this);
		powerBtn.setOnClickListener(this);
		sleepBtn.setOnClickListener(this);
		
		mousePanel = (LinearLayout) findViewById(R.id.mouse_panel);
		mousePanel.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_MOVE:
					mouseManager.onMouseMove(event);
					break;
				case MotionEvent.ACTION_DOWN:
					mouseManager.onMouseDown(event);
					break;
				case MotionEvent.ACTION_UP:
					mouseManager.onMouseUp(event);
					break;
				}
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.shut_down, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button1:
			transmission.sendCommand(command.getText().toString());
			command.setText("");
			break;
		case R.id.imageButton1:
			transmission.sendCommand("0");
			break;
		case R.id.imageButton2:
			transmission.sendCommand("1");
			break;
		}
	}
}
