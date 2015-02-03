package com.mcorbridge.passwordprotector;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mcorbridge.passwordprotector.constants.ApplicationConstants;
import com.mcorbridge.passwordprotector.model.ApplicationModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VisualKeyActivity extends BaseActivity implements OnTouchListener, OnDragListener {

    private ViewGroup from;
    private int aResourceIds[] = new int[4];
    private int numDrops;
    private String currentBoxColor = null;
    private String visualCipherKey = "";
    private RelativeLayout relativeLayout1;
    private RelativeLayout relativeLayout2;
    private RelativeLayout relativeLayout3;
    private RelativeLayout relativeLayout4;
    private HashMap hashMap = new HashMap();
    private ApplicationModel applicationModel = ApplicationModel.getInstance();
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_key);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        pref = getApplicationContext().getSharedPreferences("PasswordProtector", MODE_PRIVATE);

        findViewById(R.id.red_square).setOnTouchListener(this);
        findViewById(R.id.yellow_square).setOnTouchListener(this);
        findViewById(R.id.blue_square).setOnTouchListener(this);
        findViewById(R.id.black_square).setOnTouchListener(this);
        //findViewById(R.id.top_container).setOnDragListener(this);
        findViewById(R.id.topLeft_drop_square).setOnDragListener(this);
        findViewById(R.id.topRight_drop_square).setOnDragListener(this);
        findViewById(R.id.bottomLeft_drop_square).setOnDragListener(this);
        findViewById(R.id.bottomRight_drop_square).setOnDragListener(this);

        setResourceIdArray();

        addBorder();

        // null the in-memory deciphered password data
        // when a user returns to the key from an ongoing session, then inputs an incorrect visual key
        // the in-memory data must be null or all previously deciphered data will be accessible
        applicationModel.setDecipheredPasswordDataVOs(null);

        //if the app recognises that there have been 5 incorrect attempts at the visual key, the user is locked out for 8 hrs
        if(getLockOut()){
            final Intent intent = new Intent(this,MainActivity.class);
            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage(R.string.lock_out_msg)
                    .setIcon(R.drawable.alert_icon)
                    .setPositiveButton("Ok, got it.", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(intent );
                        }
                    })
                    .show();
            }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.getItem(ApplicationConstants.MENU_ITEM_TEST).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_HOME).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_CREATE).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_READ).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    @Override
    protected void onStop(){
        super.onStop();
        doReset();
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {

        if(numDrops == 4)
            return false;

        ClipData dragData = ClipData.newPlainText("type", getTypeFromResourceId(v));
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            DragShadowBuilder shadowBuilder = new DragShadowBuilder(v);
            v.startDrag(dragData , shadowBuilder, v, 0);
            //v.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }

    private void setResourceIdArray(){
        aResourceIds[0] = findViewById(R.id.blue_square).getId();
        aResourceIds[1] = findViewById(R.id.yellow_square).getId();
        aResourceIds[2] = findViewById(R.id.red_square).getId();
        aResourceIds[3] = findViewById(R.id.black_square).getId();

        relativeLayout1 = (RelativeLayout)findViewById(R.id.topLeft_drop_square);
        relativeLayout2 = (RelativeLayout)findViewById(R.id.topRight_drop_square);
        relativeLayout3 = (RelativeLayout)findViewById(R.id.bottomLeft_drop_square);
        relativeLayout4 = (RelativeLayout)findViewById(R.id.bottomRight_drop_square);
    }

    private String getTypeFromResourceId(View v){
        String type = null;
        switch (v.getId()) {
            case R.id.blue_square:
                type = "blue";
                currentBoxColor = "blue";
                break;
            case R.id.yellow_square:
                type = "yellow";
                currentBoxColor = "yellow";
            break;
            case R.id.red_square:
                type = "red";
                currentBoxColor = "red";
            break;
            case R.id.black_square:
                type = "black";
                currentBoxColor = "black";
            break;
        }
        return type;
    }

    private int getResourceIdFromType(String type){
        int id = 0;
        switch (type) {
            case "blue":
                id = aResourceIds[0];
                break;
            case "yellow":
                id = aResourceIds[1];
                break;
            case "red":
                id = aResourceIds[2];
                break;
            case "black":
                id = aResourceIds[3];
            break;
        }
        return id;
    }

    private int getImageResourceFromString(String type){
        int typeResource = 0;
        switch (type) {
            case "blue":
                typeResource = R.drawable.blue_square;
                break;
            case "yellow":
                typeResource =  R.drawable.yellow_square;
                break;
            case "red":
                typeResource = R.drawable.red_square;
                break;
            case "black":
                typeResource = R.drawable.black_square;
                break;
        }
        return typeResource;
    }

    @Override
    public boolean onDrag(View v, DragEvent e) {
        if (e.getAction()==DragEvent.ACTION_DROP) {
            View view = (View) e.getLocalState();
            ClipData.Item i = e.getClipData().getItemAt(0);
            from = (ViewGroup) view.getParent();
            from.removeView(view);
            RelativeLayout to = (RelativeLayout) v;
            String dropLocation = parseDropLocation(v.getResources().getResourceName(v.getId()));
            visualCipherKey += dropLocation.concat(abbr(currentBoxColor));
            setBackgroundColor(to);
            to.addView(view);
            view.setVisibility(View.VISIBLE);
            addView(i.getText().toString());
            numDrops++;
            if(numDrops == 4){
                applicationModel.setVisualCipherKey(visualCipherKey);
                applicationModel.setCipher(applicationModel.getSecretKey().concat( applicationModel.getVisualCipherKey()));
                //System.out.println("application email ---> " + applicationModel.getEmail());
                //System.out.println("application cipher ---> " + applicationModel.getCipher());

                MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.success);
                mediaPlayer.start(); // no need to call prepare(); create() does that for you

                Intent intent = new Intent(this, PasswordDataActivity.class);
                startActivity(intent);
            }
            // structure to hold references to the target
            hashMap.put(to,view);
        }
        return true;
    }

    private void addView(String type){
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageDrawable(getResources().getDrawable(getImageResourceFromString(type)));
        imageView.setId(getResourceIdFromType(type));
        imageView.setAdjustViewBounds(true);
        RelativeLayout relativeLayout = null;
        switch(currentBoxColor)
        {
            case "blue":
                relativeLayout = (RelativeLayout)findViewById(R.id.blue_square_home);
                break;
            case "yellow":
                relativeLayout = (RelativeLayout)findViewById(R.id.yellow_square_home);
                break;
            case "red":
                relativeLayout = (RelativeLayout)findViewById(R.id.red_square_home);
                break;
            case "black":
                relativeLayout = (RelativeLayout)findViewById(R.id.black_square_home);
                break;
        }

        relativeLayout.addView(imageView);
        imageView.setOnTouchListener(this);
    }

    private String abbr(String color){
        String aColor = "";
        switch(color)
        {
            case "blue":
                aColor = "BE";
            break;
            case "yellow":
                aColor = "YW";
            break;
            case "red":
                aColor = "RD";
            break;
            case "black":
                aColor = "BK";
            break;
        }
      return aColor;
    }

    private void setBackgroundColor(RelativeLayout to){
        switch(this.currentBoxColor)
        {
            case "blue":
                to.setBackgroundColor(Color.BLUE);
                break;
            case "yellow":
                to.setBackgroundColor(Color.YELLOW);
                break;
            case "red":
                to.setBackgroundColor(Color.RED);
                break;
            case "black":
                to.setBackgroundColor(Color.BLACK);
                break;
        }
    }

    private String parseDropLocation(String location){
        String quadrant = location.split("/")[1].split("_")[0];
        String dropLocation = null;
        switch(quadrant)
        {
            case "topLeft":
                dropLocation = "0";
                break;

            case "topRight":
                dropLocation = "1";
                break;

            case "bottomLeft":
                dropLocation = "2";
                break;

            case "bottomRight":
                dropLocation = "3";
                break;
        }

        return dropLocation;
    }



    private void addBorder(){
        ShapeDrawable rectShapeDrawable = new ShapeDrawable();
        Paint paint = rectShapeDrawable.getPaint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        relativeLayout1.setBackground(rectShapeDrawable);
        relativeLayout2.setBackground(rectShapeDrawable);
        relativeLayout3.setBackground(rectShapeDrawable);
        relativeLayout4.setBackground(rectShapeDrawable);
    }

    public void doReset(View v){
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            //System.out.println(pairs.getKey() + " = " + pairs.getValue());
            RelativeLayout to = (RelativeLayout)pairs.getKey();
            ImageView from = (ImageView)pairs.getValue();
            to.removeView(from);
            to.setBackgroundColor(getResources().getColor(R.color.grey));
            it.remove(); // avoids a ConcurrentModificationException
        }

        addBorder();

        numDrops = 0;
        visualCipherKey = "";
    }

    /**
     * called upon the activity onStop event
     */
    public void doReset(){
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            //System.out.println(pairs.getKey() + " = " + pairs.getValue());
            RelativeLayout to = (RelativeLayout)pairs.getKey();
            ImageView from = (ImageView)pairs.getValue();
            to.removeView(from);
            to.setBackgroundColor(getResources().getColor(R.color.grey));
            it.remove(); // avoids a ConcurrentModificationException
        }

        addBorder();

        numDrops = 0;
        visualCipherKey = "";
    }

    private boolean getLockOut(){
        boolean isLockedOut = false;
        String lockOutTime = pref.getString("lockout_time", null);

        if(lockOutTime != null){
            Long lockOutTimeMilli = Long.valueOf(lockOutTime).longValue();
            Calendar calendar = Calendar.getInstance();
            Long timeNowMilli = calendar.getTimeInMillis();
            Long diff = timeNowMilli - lockOutTimeMilli;
            if(diff < ApplicationConstants.MILLI_PER_EIGHT_HR){
                isLockedOut = true;
            }else{
                isLockedOut = false;
                setSharedPreferences("lockout_time",null);
                applicationModel.setIncorrectDecipherAttempts(0);
            }
        }
        return isLockedOut;
    }

    private void setSharedPreferences(String key, String type){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(type, key);  // Saving string
        editor.apply(); // commit changes
    }

}