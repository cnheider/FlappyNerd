package com.example.heider.flappynerd;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

  private EditText mNameEditText;
  private EditText mGameEditText;
  private ImageButton mImageButton;
  private ImageButton mLogoImageButton;

  private NerdDriver mNerdDriver;

  private String mNerdName;
  private String mGamePin;
  private int mImageId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    View decorView = getWindow().getDecorView();
    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
    decorView.setSystemUiVisibility(uiOptions);

    mNerdDriver = NerdDriver.getInstance(this);

    mLogoImageButton = (ImageButton) findViewById(R.id.imageButtonLogo);
    mImageId = R.mipmap.logo12;
    mLogoImageButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mImageId = getLogoResId();
            mLogoImageButton.setBackground(getResources().getDrawable(mImageId));
          }
        });

    mNerdName = "";
    mNameEditText= (EditText) findViewById(R.id.NameEditText);
    Typeface typeface = Typeface.createFromAsset(getAssets(), "PixelFont.ttf");
    mNameEditText.setTypeface(typeface);
    mNameEditText.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }
          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            mNerdName = mNameEditText.getText().toString();
          }
          @Override
          public void afterTextChanged(Editable s) {

          }
        });

    if(mNerdName.equals("") || mNerdName == null){
      mNerdName = "Anonymous";
    }

    mGamePin = "";
    mGameEditText= (EditText) findViewById(R.id.GameEditText);
    mGameEditText.setTypeface(typeface);
    mNameEditText.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }
          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            mGamePin = mGameEditText.getText().toString();
          }
          @Override
          public void afterTextChanged(Editable s) {

          }
        });

    if(mGamePin.equals("") || mNerdName == null){
      mGamePin = "meh";
    }

    mImageButton = (ImageButton) findViewById(R.id.imageButton);
    mImageButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mImageButton.setBackgroundResource(R.mipmap.button_on);
            try {
              Intent intent = JumpActivity.newIntent(getApplicationContext(), mNerdName, mGamePin, mImageId);
              startActivity(intent);

              //mNerdDriver.create(mNerdName);
              mNerdDriver.join(mGamePin);
              Log.d(TAG, "Log in request");
            } catch(Exception e){
              Log.d(TAG, e.getStackTrace().toString());
            }
          }
        });
  }
  @Override
  protected void onPause() {
    super.onPause();
  }

  public int getLogoResId(){
    int logoNumber =  1 + (int)(Math.random() * 16);
    Log.d("Flap", "" + logoNumber);
    String logoString = "logo" + logoNumber;
    Log.d("Flap",  logoString);

    int resID = getResources().getIdentifier(logoString, "mipmap", getApplicationContext().getPackageName());
    return resID;
  }

  @Override
  protected void onResume() {
    super.onResume();

    mImageButton.setBackground(getResources().getDrawable(R.mipmap.button_off));
    View decorView = getWindow().getDecorView();
    int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
    decorView.setSystemUiVisibility(uiOptions);
  }
}
