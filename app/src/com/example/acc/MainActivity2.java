package com.example.acc;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity2 extends Activity {
	private ArrayList <String> output_dat = new ArrayList<String>();
	private ArrayList <String> output_u = new ArrayList<String>();
	private TextView log;
	private float velocity, u;
	private int time;
	private List<Entry> getChartData(ArrayList<String> data){

        List<Entry> chartData = new ArrayList<Entry>();
        for(int i=0;i<data.size();i++){
        	chartData.add(new Entry(Float.parseFloat(data.get(i)),i));
        }



        return chartData;
    }
	
    private List<String> getLabels(int data_size){
        List<String> chartLabels = new ArrayList<String>();
        for(int i=0;i<data_size;i++){
            chartLabels.add(""+i);
        }
        return chartLabels;
    }
    
    private LineData getLineData(){

    	LineDataSet dataSetA = new LineDataSet(getChartData(output_dat), "u");
    	//LineDataSet dataSetB = new LineDataSet(getChartData(output_u), "u");
    	List<LineDataSet> dataSets = new ArrayList<LineDataSet>();
    	dataSetA.setColor(Color.rgb(82, 61, 196));
    	dataSetA.setCircleColor(Color.rgb(82, 61, 196));
    	//dataSetB.setColor(Color.rgb(213, 243, 37));
    	//dataSetB.setCircleColor(Color.rgb(213, 243, 37));
    	dataSetA.setDrawCircleHole(false);
    	//dataSetB.setDrawCircleHole(false);
        dataSets.add(dataSetA); // add the datasets
        //dataSets.add(dataSetB); // add the datasets
        return new LineData(getLabels(output_dat.size()), dataSets);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page2);
    	Intent intent = this.getIntent();
    	output_dat =  (ArrayList<String>)intent.getSerializableExtra("data"); 
    	time = intent.getIntExtra("time",0); 
    	velocity = intent.getFloatExtra("v", 0);
    	u = intent.getFloatExtra("u", 0);
    	//output_u =  (ArrayList<String>)intent.getSerializableExtra("u");  
		LineChart chart_line = (LineChart)findViewById(R.id.chart_line);
        chart_line.setData(getLineData());
        chart_line.invalidate();
        chart_line.animateX(1500);
        chart_line.animateY(1500);
        chart_line.setDescription("");
		log = (TextView)findViewById(R.id.textView1);
		log.append("平均摩擦係數 :"+u);
		log.append("\n離開斜坡速率 :"+velocity);
		log.append("\n耗時 :"+time*0.01);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity2, menu);
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
	
	
}
