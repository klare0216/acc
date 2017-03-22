package com.example.acc;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private SensorManager sensorMgr;
	private TextView text;
	private TextView text_2;
	private TextView log;
	private Button button; 
	private Button button_2; 
	private EditText edittext;
	private EditText edittext_2;
	private float distance;
	private float angle;
	private float maxV;
	private float dsec;
	private List <String> output_dat = new ArrayList<String>();
	private float error;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 取得手機感測器服務
		sensorMgr = (SensorManager)getSystemService(SENSOR_SERVICE);
		text = (TextView)findViewById(R.id.textView1);
		text_2 = (TextView)findViewById(R.id.textView2);
		log = (TextView)findViewById(R.id.textView5);
		edittext = (EditText)findViewById(R.id.editText1);
		edittext_2 = (EditText)findViewById(R.id.editText2);
		button = (Button)findViewById(R.id.button1);
		button_2 = (Button)findViewById(R.id.button2);
		log.setText(" ");
		
		button_setup();

		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// 設定button事件
	private void button_setup(){
		button.setOnClickListener(new Button.OnClickListener(){
			 @Override 
	         public void onClick(View v) {
	             distance = Float.valueOf(edittext.getText().toString());
	             angle = Float.valueOf(edittext_2.getText().toString());
	             log.setText("dis: "+ distance+"\nangle:"+angle);
			 }	            
		});
		button_2.setOnClickListener(new Button.OnClickListener(){
			 @Override 
	         public void onClick(View v) {
	             log.setText("dis: "+ distance+"\nangle:"+angle);
	             save();
	             log.setText("save output.txt.");
			 }
	            
		});
	}
	
	private void save(){
		write();
		maxV = 0;
		output_dat.clear();;
	}
	
	// 拿來寫入檔案
	private void write(){
		try{
			// 建立FileOutputStream物件，路徑為SD卡中的output.txt
			 FileOutputStream output = new FileOutputStream("/sdcard/output.txt");
			 while(output_dat.size() != 0){
				output.write(output_dat.get(0).getBytes());
				output.write("\n".getBytes());
			 	output_dat.remove(0);
			 }
			 output.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private double result_a(float x,float y,float z){
		return Math.sqrt((x*x+y*y+z*z));
	}
	
	private boolean isMove(double a){
		if(a < 9.7)
			return true;
		return false;
	}
	
	SensorEventListener listener = new SensorEventListener(){
		@SuppressLint("NewApi")
		public void onSensorChanged(SensorEvent event){
			//Sensor sensor = event.sensor;
			StringBuilder sensorInfo = new StringBuilder();
			DecimalFormat df=new DecimalFormat("#.###");
			float[] values = event.values;
			for(int i = 0;i < values.length;i++)
				sensorInfo.append("加速度["+i+"] = "+df.format(values[i])+"\n");
			double a = result_a(values[0],values[1],values[2]);
			sensorInfo.append("加速度和:"+df.format(a)+"\n");
			if (isMove(a)) sensorInfo.append("移動中");
			else sensorInfo.append("靜止");
			output_dat.add(a+"");
			text.setText(sensorInfo);
			
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
	SensorEventListener listener_G = new SensorEventListener(){
		@Override
		public void onSensorChanged(SensorEvent event){
			//Sensor sensor = event.sensor;
			StringBuilder sensorInfo = new StringBuilder();
			
			float[] values = event.values;
			for(int i = 0;i < values.length;i++)
				sensorInfo.append("-G_values["+i+"] = "+values[i]+"\n");

			text_2.setText(sensorInfo);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	protected void onResume(){
		super.onResume();
		sensorMgr.registerListener(listener,
				sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				20);
		dsec = 20;
		sensorMgr.registerListener(listener_G,
				sensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
				SensorManager.SENSOR_DELAY_UI);
	
	}
}
