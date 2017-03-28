package com.example.acc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
	private float angle;
	private boolean start;
	private int time;
	private double a_max;
	private float dsec;
	private List <String> output_dat = new ArrayList<String>();
	private List <String> output_time = new ArrayList<String>();
	
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
		edittext.setText("0");
		edittext_2.setText("0");
		start = false;
		time = 0;
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
	             angle = Float.valueOf(edittext_2.getText().toString());
	             a_max = 9.8*Math.sin(angle/180*Math.PI);
	             log.setText("坡長 : "+ distance+"\n角度: "+angle+"\n"+"a_max = "+a_max+"\n");
			 }	            
		});
		button_2.setOnClickListener(new Button.OnClickListener(){
			 @Override 
	         public void onClick(View v) {
	             if(start){
	            	 start = false;
	            	 time = 0;
	            	 button_2.setText("START");
	            	 button_2.setBackgroundResource(R.drawable.button_bg);
	            	 //generate_data();
	            	 save();
	             }else {
	            	 start = true;
	            	 button_2.setText("SAVE");
	            	 button_2.setBackgroundResource(R.drawable.button_bg_save);
	             }
			 }
	            
		});
	}
	
	private void save(){
		write();
		output_dat.clear();;
	}
	

	
	private double averange_data(){
		int len = output_dat.size();
		double sum = 0;
		DecimalFormat df=new DecimalFormat("#.###");
		for(int i=0;i<output_dat.size();i++){
			sum = sum + Double.parseDouble(output_dat.get(i));
		}
		return Double.parseDouble(df.format(sum/len));
	}
	
	private double median(){
		List <Double> tmp = new ArrayList<Double>();
		DecimalFormat df=new DecimalFormat("#.###");
		for(int i=25;i<output_dat.size()-26;i++){
			tmp.add(Double.parseDouble(output_dat.get(i)));
		}
		Collections.sort(tmp);
		return  Double.parseDouble(df.format(tmp.get((int)(tmp.size()/2))));
		//return tmp.size()/2;
	}
	
	private void generate_data(){
		//a_max = 9.8 * sin(angle)
		//double a_max = 9.8*Math.sin(angle/180*Math.PI);
		//double factor = 1;
		/*for(int i=0;i<output_dat.size();i++){
			if(Double.parseDouble(output_dat.get(i))>a_max){
				output_dat.remove(i);
				output_time.remove(i);
				i--;
			}
		}*/
		double diff = 10000;
		double factor = 1;
		for(int i=1;i<output_dat.size();i++){
			double tmp_diff ;
			int j=1;
			log.append(output_time.get(i-j).equals("*")+"\n");
			while((i-j-1)>=0 && output_time.get(i-j).equals("*")){
				j++;
				
			}
			
			tmp_diff = Double.parseDouble(output_dat.get(i)) - Double.parseDouble(output_dat.get(i-j));
			//log.append("("+i+","+j+") = "+output_dat.get(i)+", "+output_dat.get(i-j)+"\n");
			log.append("("+i+","+j+") : diff= "+tmp_diff+"\n");
			if(Math.abs(diff*factor) < Math.abs(tmp_diff)){
				// 去掉這個值
				//output_dat.remove(i);
				//output_time.remove(i);
				//i--;
				//log.append("*");
				output_time.set(i-j,"*");
			}else{
				diff = tmp_diff;
			}
		}
		
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
			 
			 log.append("平均: "+averange_data()+"\n");
			 String tmp ="中位數: "+median()+"\n";
			 output.write(tmp.getBytes());
			 
			 while(output_dat.size() != 0){
				output.write(output_time.get(0).getBytes());
				output.write(",".getBytes());
				output.write(output_dat.get(0).getBytes());
				output.write("\n".getBytes());
			 	output_dat.remove(0);
			 	output_time.remove(0);
			 }
			 output.close();
			 log.append(f_name+"_"+count+".txt\n");
			 //log.setText(f_name+"_"+count+".txt");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private double result_a(float x,float y,float z){
		return Math.sqrt((x*x+y*y));
	}
	

	
	SensorEventListener listener = new SensorEventListener(){
		@SuppressLint("NewApi")
		public void onSensorChanged(SensorEvent event){
			//Sensor sensor = event.sensor;
			StringBuilder sensorInfo = new StringBuilder();
			DecimalFormat df=new DecimalFormat("#.###");
			float[] values = event.values;
			sensorInfo.append("加速度 x = "+df.format(values[0])+"\n");
			sensorInfo.append("加速度 y = "+df.format(values[1])+"\n");
			sensorInfo.append("加速度 z = "+df.format(values[2])+"\n");
			double a = result_a(values[0],values[1],values[2]);
			sensorInfo.append("加速度和 = "+df.format(a)+"\n");
			if(start) {
				output_dat.add(a+"");
				output_time.add(time+"");
				time++;
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
			//Sensor sensor = event.sensor;
			StringBuilder sensorInfo = new StringBuilder();
			DecimalFormat df=new DecimalFormat("#.###");
			float[] values = event.values;
			sensorInfo.append("陀螺儀 x = "+df.format(values[0])+"\n");
			sensorInfo.append("陀螺儀 y = "+df.format(values[1])+"\n");
			sensorInfo.append("陀螺儀 z = "+df.format(values[2])+"\n");
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
