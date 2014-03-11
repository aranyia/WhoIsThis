package hu.Edudroid.WhoIsThis;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowStatistics extends Activity {
	private Cursor cursor;
	private Contact contact = new Contact();
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_statistics_display);
    	
        ContactAPI contactapi = new ContactAPI();
        contactapi.setCr(getContentResolver());
        contactapi.setCur(cursor);
        
        Bundle in = super.getIntent().getExtras();

    	contact.setId(in.getString("id"));
    	contact.setPhone(contactapi.getPhoneNumbers(contact.getId()));
    	contact.setCallHistoryStats(contactapi.getCallHistoryStats(contact.getPhone()));
    	
    	TextView IncomingCount = (TextView) findViewById(R.id.IncomingCount);
    	TextView IncomingDurAll = (TextView) findViewById(R.id.IncomingDurAll);
    	TextView IncomingDurAverage = (TextView) findViewById(R.id.IncomingDurAverage);
    	TextView OutgoingCount = (TextView) findViewById(R.id.OutgoingCount);
    	TextView OutgoingDurAll = (TextView) findViewById(R.id.OutgoingDurAll);
    	TextView OutgoingDurAverage = (TextView) findViewById(R.id.OutgoingDurAverage);
    	TextView MissingCount = (TextView) findViewById(R.id.MissedCount);
    	TextView FailedOutgoingCount = (TextView) findViewById(R.id.FailedOutgoingCount);
    	
    	try{
    	IncomingCount.setText(String.valueOf(contact.getIncomingCount()));
    	IncomingCount.setTextAppearance(this, R.style.CallStatBox_Count);
    	IncomingDurAll.setText(this.createDurationString(contact.getIncomingDuration()));
    	IncomingDurAverage.setText(this.createDurationString(contact.getIncomingDurationAverage()));
    	
    	OutgoingCount.setText(String.valueOf(contact.getOutgoingCount()));
    	OutgoingCount.setTextAppearance(this, R.style.CallStatBox_Count);
    	OutgoingDurAll.setText(this.createDurationString(contact.getOutgoingDuration()));
    	OutgoingDurAverage.setText(this.createDurationString(contact.getOutgoingDurationAverage()));
    	
    	MissingCount.setText(String.valueOf(contact.getMissedCount()));
    	MissingCount.setTextAppearance(this, R.style.CallStatBox_Count);
    	FailedOutgoingCount.setText(String.valueOf(contact.getFailedOutgoingCount()));
    	FailedOutgoingCount.setTextAppearance(this, R.style.CallStatBox_Count);
    	}
    	catch(Exception e){};
    	
    	if(contact.getIncomingCount() == 0 && contact.getOutgoingCount() == 0) {
    		TextView no_data1 = new TextView(this); TextView no_data2 = new TextView(this);
    		no_data1.setText(R.string.Msg_NoData);	no_data2.setText(R.string.Msg_NoData);
    		no_data1.setPadding(12, 20, 12, 20);	no_data2.setPadding(12, 20, 12, 20);
            LinearLayout TargetPieViewA =  (LinearLayout) findViewById(R.id.pie_containerA);
            LinearLayout TargetPieViewB =  (LinearLayout) findViewById(R.id.pie_containerB);
    	    TargetPieViewA.addView(no_data1);
    	    TargetPieViewB.addView(no_data2);
    	}
    	else {
    		createPieChart(225);
    		createPieChart2(225);	
    	}
	}
	
	private void createPieChart(int Size) {
		ArrayList<PieDetailsItem> PieData = new ArrayList<PieDetailsItem>(0);
		PieDetailsItem Item1 = new PieDetailsItem();
		PieDetailsItem Item2 = new PieDetailsItem();
		Item1.Count = contact.getIncomingCount();
		Item1.Color = Color.BLUE;
		Item1.Label = "";
		Item2.Count = contact.getOutgoingCount();
		Item2.Color = Color.GREEN;
		Item2.Label = "";
		PieData.add(Item1);
		PieData.add(Item2);
        //------------------------------------------------------------------------------------------
        // Generating Pie view
        //------------------------------------------------------------------------------------------
        View_PieChart PieChartView = new View_PieChart( this );
        PieChartView.setLayoutParams(new LayoutParams(Size, Size));
        PieChartView.setGeometry(Size, Size, 2, 2, 2, 2, R.drawable.pie_overlay);
        PieChartView.setSkinParams(android.R.color.transparent);
        PieChartView.setData(PieData, contact.getIncomingCount() + contact.getOutgoingCount());
        PieChartView.invalidate();
        //------------------------------------------------------------------------------------------
        // Draw Pie View on Bitmap canvas
        //------------------------------------------------------------------------------------------
        Bitmap mBackgroundImage = Bitmap.createBitmap(Size, Size, Bitmap.Config.ARGB_8888);
        PieChartView.draw(new Canvas(mBackgroundImage));
        PieChartView = null;
        //------------------------------------------------------------------------------------------
        // Create a new ImageView to add to main layout
        //------------------------------------------------------------------------------------------
        ImageView mImageView = new ImageView(this);
	    mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    mImageView.setBackgroundColor(android.R.color.transparent);
	    mImageView.setImageBitmap( mBackgroundImage );
	    //------------------------------------------------------------------------------------------
        // Finaly add Image View to target view
        //------------------------------------------------------------------------------------------
        LinearLayout TargetPieView =  (LinearLayout) findViewById(R.id.pie_containerA);
	    TargetPieView.addView(mImageView);
	}
	
	private void createPieChart2(int Size) {
		ArrayList<PieDetailsItem> PieData = new ArrayList<PieDetailsItem>(0);
		PieDetailsItem Item1 = new PieDetailsItem();
		PieDetailsItem Item2 = new PieDetailsItem();
		Item1.Count = (int) contact.getIncomingDuration();
		Item1.Color = Color.BLUE;
		Item1.Label = "";
		Item2.Count = (int) contact.getOutgoingDuration();
		Item2.Color = Color.GREEN;
		Item2.Label = "";
		PieData.add(Item1);
		PieData.add(Item2);
        //------------------------------------------------------------------------------------------
        // Generating Pie view
        //------------------------------------------------------------------------------------------
        View_PieChart PieChartView = new View_PieChart( this );
        PieChartView.setLayoutParams(new LayoutParams(Size, Size));
        PieChartView.setGeometry(Size, Size, 2, 2, 2, 2, R.drawable.pie_overlay);
        PieChartView.setSkinParams(android.R.color.transparent);
        PieChartView.setData(PieData,(int) (contact.getIncomingDuration() + contact.getOutgoingDuration()));
        PieChartView.invalidate();
        //------------------------------------------------------------------------------------------
        // Draw Pie View on Bitmap canvas
        //------------------------------------------------------------------------------------------
        Bitmap mBackgroundImage = Bitmap.createBitmap(Size, Size, Bitmap.Config.ARGB_8888);
        PieChartView.draw(new Canvas(mBackgroundImage));
        PieChartView = null;
        //------------------------------------------------------------------------------------------
        // Create a new ImageView to add to main layout
        //------------------------------------------------------------------------------------------
        ImageView mImageView = new ImageView(this);
	    mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    mImageView.setBackgroundColor(android.R.color.transparent);
	    mImageView.setImageBitmap( mBackgroundImage );
	    //------------------------------------------------------------------------------------------
        // Finaly add Image View to target view
        //------------------------------------------------------------------------------------------
        LinearLayout TargetPieView =  (LinearLayout) findViewById(R.id.pie_containerB);
	    TargetPieView.addView(mImageView);
	}
	
	private String createDurationString(long duration) {
		int seconds = (int) (duration >= 60 ? duration % 60 : duration);
		int minutes = (int) ((duration = (duration / 60)) >= 60 ? duration % 60 : duration);	
		int hours = (int) (duration = (duration / 60));

		return ((hours == 0) ? "" : (String.valueOf(hours) + ":")) + 
			String.format("%02d:%02d", minutes, seconds);
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	    getWindow().setFormat(PixelFormat.RGBA_8888);
	}
}
