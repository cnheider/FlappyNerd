package net.cnheider.flappynerd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class JumpActivity extends Activity implements SensorEventListener {

  private static final String EXTRA_NERD_NAME = "net.cnheider.flappynerd";
  private static final String EXTRA_IMAGE_ID = "net.cnheider.flappynerd2";
  private static final String EXTRA_GAME_PIN = "net.cnheider.flappynerd4";
  private static final String TAG = JumpActivity.class.getSimpleName();

  private SensorManager mSensorManager;
  private Sensor mSensorAccelerometer;
  private TextView mTextViewNerdName;
  private ImageButton mImageButtonLogo;

  private long startTime = 0;
  private long airTime = 0;
  private boolean goingUp = false;
  private boolean goingDown = false;
  private boolean maybeJump = false;
  private static final int SHAKE_THRESHOLD = 15;

  private String mNerdName;
  private int mResId;

  public static Intent newIntent(Context packageContext, String nerdName, String gamePin, int resId){
    Intent intent = new Intent(packageContext, JumpActivity.class);
    intent.putExtra(EXTRA_NERD_NAME, nerdName);
    intent.putExtra(EXTRA_GAME_PIN, gamePin);
    intent.putExtra(EXTRA_IMAGE_ID, resId );
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pocket);

    mNerdName = (String) getIntent().getSerializableExtra(EXTRA_NERD_NAME);
    mResId = (int) getIntent().getSerializableExtra(EXTRA_IMAGE_ID);

    mImageButtonLogo = (ImageButton) findViewById(R.id.imageButtonLogo2);
    mImageButtonLogo.setBackground(getResources().getDrawable(mResId));
    mImageButtonLogo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mImageButtonLogo.setBackground(getResources().getDrawable(getLogoResId()));
      }
    });

    mTextViewNerdName = (TextView) findViewById(R.id.textView_NerdName);
    Typeface typeface = Typeface.createFromAsset(getAssets(), "PixelFont.ttf");
    mTextViewNerdName.setTypeface(typeface);
    mTextViewNerdName.setText(mNerdName);


    View decorView = getWindow().getDecorView();
    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
    decorView.setSystemUiVisibility(uiOptions);

    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    mSensorManager.registerListener(this, mSensorAccelerometer, mSensorManager.SENSOR_DELAY_GAME);
  }

  @Override
  protected void onResume() {
    mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    View decorView = getWindow().getDecorView();
    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
    decorView.setSystemUiVisibility(uiOptions);
    super.onResume();
  }

  public int getLogoResId(){
    int logoNumber =  1 + (int)(Math.random() * 16);
    Log.d("Flap", "" + logoNumber);
    String logoString = "logo" + logoNumber;
    Log.d("Flap", logoString);

    int resID = getResources().getIdentifier(logoString, "mipmap", getApplicationContext().getPackageName());
    return resID;
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    Sensor mySensor = event.sensor;
    if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){
      //accelerations along the axis
      float x = event.values[0];
      float y = event.values[1];
      float z = event.values[2];

      if(SystemClock.uptimeMillis() - startTime < 1500) {
        if(y > SHAKE_THRESHOLD && !goingUp) {
          Log.d("Flap", "AccY: " + y);
          goingUp = true;
          startTime = SystemClock.uptimeMillis();
          return;
        }
        if(goingUp && y < SHAKE_THRESHOLD && !maybeJump && SystemClock.uptimeMillis() - startTime > 150) {
          Log.d("Flap", "maybe = true");
          maybeJump = true;
          return;
        }
        if(goingUp && maybeJump && y > SHAKE_THRESHOLD && !goingDown) {
          Log.d("Flap", "goingDown = true");
          goingDown = true;
          return;
        }
        if(goingDown && maybeJump && goingUp) {
          airTime = SystemClock.uptimeMillis() - startTime;

          goingUp = false;
          goingDown = false;
          maybeJump = false;
          startTime = SystemClock.uptimeMillis();
          Log.d(TAG, "airtime: " + airTime);
          try {

            NerdDriver.getInstance(this).flap(airTime);


            Log.d("Flap", "Score sent");
          }
          catch(Exception e){
            e.printStackTrace();

          }
          Log.d("Flap", "Method finished");
        }
      }
      else {
        goingUp = false;
        goingDown = false;
        maybeJump = false;
        startTime = SystemClock.uptimeMillis();
      }
    }
  }
  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }
}
