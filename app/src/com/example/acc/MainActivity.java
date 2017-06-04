package com.example.acc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@TargetApi(24)
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
	private float height;
	private boolean start;
	private int time;
	private double a_max;
	private int stop_point;
	private static float g; 
	private float aver_u;
	private float velocity;

	public ArrayList <String> output_dat = new ArrayList<String>();
	public ArrayList <String> output_x = new ArrayList<String>();
	public ArrayList <String> output_y = new ArrayList<String>();
	public ArrayList <String> output_z = new ArrayList<String>();
	public ArrayList <String> output_time = new ArrayList<String>();
	public ArrayList <String> output_u = new ArrayList<String>();

	

	
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
		edittext.setText("0");
		edittext_2.setText("0");
		start = false;
		time = 0;
		stop_point = 0;
		g = (float)9.81;
		aver_u = 0;
		velocity = 0;
		setup();

		
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
	private void setup(){
		button.setOnClickListener(new Button.OnClickListener(){
			 @Override 
	         public void onClick(View v) {
	             distance = Float.valueOf(edittext.getText().toString());
	             height = Float.valueOf(edittext_2.getText().toString());
	             a_max = 9.8*Math.sin(height/180*Math.PI);
	             log.setText("坡長 : "+ distance+"\n高度: "+height+"\n"+"a_max = "+a_max+"\n");
			 }	            
		});

		  button_2.setOnTouchListener(new View.OnTouchListener() {
			     @Override
			     public boolean onTouch(View v, MotionEvent event) {
			        if(event.getAction() == MotionEvent.ACTION_DOWN) {
			        	if(!start) {
			            	 button_2.setText("SAVE");
			            	 button_2.setBackgroundResource(R.drawable.button_bg_save);
			             }
			        	return true;
			        } else if (event.getAction() == MotionEvent.ACTION_UP) {
			            if(start){
			            	 start = false;

			            	 // 改變按鈕
			            	 button_2.setText("START");
			            	 button_2.setBackgroundResource(R.drawable.button_bg);
			            	 // 計算成動摩擦力
			            	 calculate_dynamicFriction();

			            	 // 設定煥頁要傳遞的資料
			            	 Intent intent = new Intent();
			            	 intent.setClass(MainActivity.this,MainActivity2.class);
			            	 intent.putExtra("data", output_u);
			            	 intent.putExtra("v", velocity);
			            	 intent.putExtra("time", stop_point);
			            	 intent.putExtra("u", aver_u);
			            	 //intent.putExtra("data", output_dat);
			            	 // 換頁			        
			            	 startActivityForResult(intent, 0);

			            	 // 儲存資料
			            	 save();
    	 
			             }else{
			            	 start = true;
			             }
			        	return true;
			        }
			        return false;
			     }
		  
		  });
	}
	
	
	
	private void save(){
		write();
		// init
		output_x.clear();
		output_y.clear();
		output_z.clear();
		output_dat.clear();
		output_time.clear();
		output_u.clear();
   	 	time = 0;
	    stop_point = 0;
	    aver_u = 0;
	    velocity = 0;
	}
	

	
	// 拿來寫入檔案
	private void write(){
		try{
			 String f_name = "/sdcard/output";
			 int count = 0;
	    	 File file = new File(f_name+"_"+count+".txt");
		     // find out if the name exists or not.
	    	 while(file.exists()){
		    	 count++;
		    	 file=new File(f_name+"_"+count+".txt");
		     }
		     // write data to file
			 FileOutputStream output = new FileOutputStream(f_name+"_"+count+".txt");
			 

			 log.append(stop_point+"\n");
			 log.append(aver_u+"\n");
        	 log.append("v=" + velocity + "\n");
			 output.write("\n".getBytes());
			 for(int i=0;i<output_dat.size();i++){
				output.write(output_time.get(i).getBytes());
				output.write(",".getBytes());
				output.write(output_x.get(i).getBytes());
				output.write(",".getBytes());
				output.write(output_y.get(i).getBytes());
				output.write(",".getBytes());
				output.write(output_z.get(i).getBytes());
				output.write(",".getBytes());
				output.write(output_dat.get(i).getBytes());
				output.write("\n".getBytes());

			 }
			 output.close();
			 log.append(f_name+"_"+count+".txt\n");
			 log.append("a_max = "+a_max);
			 
			 //log.setText(f_name+"_"+count+".txt");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private double result_a(float x,float y,float z){
		return Math.sqrt((x*x+y*y));
	}
	
	private void calculate_dynamicFriction(){
		float sum = 0;
		for(int i=0;i<stop_point;i++){
			float element = g*height/distance - Math.abs(Float.valueOf(output_y.get(i)));
			output_u.add(element/Math.abs(Float.valueOf(output_z.get(i)))+"");
			sum = sum + element/Math.abs(Float.valueOf(output_z.get(i)));
			velocity = velocity + Math.abs(Float.valueOf(output_y.get(i)))*(float)0.01;
		}
		aver_u = sum/stop_point;
	}
	

	
	SensorEventListener listener = new SensorEventListener(){
		@SuppressLint("NewApi")
		public void onSensorChanged(SensorEvent event){
			StringBuilder sensorInfo = new StringBuilder();
			//DecimalFormat df=new DecimalFormat("#.###");
			float[] values = event.values;	         
	  
	        
			sensorInfo.append("x = "+event.values[0]+"\n");
			sensorInfo.append("y = "+event.values[1]+"\n");
			sensorInfo.append("z = "+event.values[2]+"\n");
			double a = result_a(values[0],values[1],values[2]);
			sensorInfo.append("x+y = "+a);
			
			if(start) {
				output_x.add((event.values[0])+"");
				output_y.add((event.values[1])+"");

				output_dat.add(a+"");
				output_time.add(time+"");
				if(stop_point==0 && a > a_max) 
					stop_point = time;
				time++;

			}else{
				
			}
			
			text.setText(sensorInfo);
			
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
	SensorEventListener listener_G = new SensorEventListener(){
		@SuppressLint("NewApi")
		@Override
		public void onSensorChanged(SensorEvent event){
			StringBuilder sensorInfo = new StringBuilder();

			float[] values = event.values;
			sensorInfo.append("x = "+values[0]+"\n");
			sensorInfo.append("y = "+values[1]+"\n");
			sensorInfo.append("z = "+values[2]+"\n");
			text_2.setText(sensorInfo);
			
			output_z.add((event.values[2])+"");
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
				sensorMgr.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				10000);

		
		sensorMgr.registerListener(listener_G,
				sensorMgr.getDefaultSensor(Sensor.TYPE_GRAVITY),
				10000);
	
	}
}
