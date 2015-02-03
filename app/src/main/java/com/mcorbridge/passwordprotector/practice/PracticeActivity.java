package com.mcorbridge.passwordprotector.practice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.mcorbridge.passwordprotector.BaseActivity;
import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.VisualKeyActivity;
import com.mcorbridge.passwordprotector.constants.ApplicationConstants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PracticeActivity extends BaseActivity implements View.OnTouchListener, View.OnDragListener {

    private ViewGroup from;
    private int numDrops;
    private String currentBoxColor = null;
    private String userVisualCipherKey = "";
    private String visualCipherKey = "";
    private HashMap hashMap = new HashMap();
    private int aResourceIds[] = new int[4];
    private RelativeLayout relativeLayout1;
    private RelativeLayout relativeLayout2;
    private RelativeLayout relativeLayout3;
    private RelativeLayout relativeLayout4;
    private int numPracticeAttempts;
    private CountDownTimer countDownTimer; // the countdownTimer runs at start up to show the PopupWindow

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

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
    public boolean onTouch(View v, MotionEvent e) {

        if(numDrops == 4)
            return false;

        ClipData dragData = ClipData.newPlainText("type", getTypeFromResourceId(v));
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(dragData , shadowBuilder, v, 0);
            //v.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
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
            hashMap.put(to,view);

            if(numDrops == 4){
                numPracticeAttempts++;
                System.out.println("visualCipherKey ---> " + visualCipherKey);
                doReset();
            }

        }
        return true;
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

        if(numPracticeAttempts == 1){
            userVisualCipherKey = visualCipherKey;
        }else{
            if(!visualCipherKey.equals(userVisualCipherKey)){
                numPracticeAttempts--;
                new AlertDialog.Builder(this)
                        .setTitle("Alert")
                        .setMessage("This pattern does NOT match your first attempt.\n Each attempt MUST be identical.")
                        .setIcon(R.drawable.alert_icon)
                        .setPositiveButton("Ok, understood.", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
// stub
                            }
                        })
                        .show();
            }
        }

        if(numPracticeAttempts == 10){
            startActivity(new Intent(this, VisualKeyActivity.class));
        }

        numDrops = 0;
        visualCipherKey = "";
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

    // timer allows activity view to be rendered before popup window is created and displayed
    public CountDownTimer getCountDownTimer = new CountDownTimer(600,300) {
        @Override
        public void onTick(long millisUntilFinished) {}
        @Override
        public void onFinish() {
            showPopup(PracticeActivity.this);
        }
    }.start();

    private void showPopup(final Activity context) {

        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int popupHeight = displayMetrics.heightPixels;
        int popupWidth = displayMetrics.widthPixels;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popout_practice_layout, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight-170);
        popup.setFocusable(true);

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY,0,170);

        // Getting a reference to Close button, and close the popup when clicked.
        //todo the submit button on the popup has a transparency that makes the button hard to see?
        Button close = (Button) layout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }
}
