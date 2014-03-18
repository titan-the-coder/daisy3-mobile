package org.benetech.daisyimgexample;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements
		TextToSpeech.OnInitListener {

	private TextToSpeech ttobj;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ttobj = new TextToSpeech(this, this);
	}

	private void addImageWithName(String name, final String toSpeak) {
		Log.d("MAIN", "Adding image with name " + name);
		String resName = name.split("\\.")[0];
		String uri = "@drawable/" + resName;
		int id = getResources().getIdentifier(uri, null, getPackageName());
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);

		ImageView image = new ImageView(MainActivity.this);
		image.setImageResource(id);

		image.setVisibility(View.VISIBLE);
		image.setFocusable(true);
		image.setFocusableInTouchMode(true);
		image.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				speakText(toSpeak);
			}
		});

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				140, 398);
		layoutParams.setMargins(24, 0, 24, 0);

		linearLayout1.addView(image, layoutParams);
		
		image.requestFocus();
		image.requestFocusFromTouch();
	}

	private void startDaisyRendering() {

		try {
			InputStream inputStream = getApplicationContext().getAssets().open(
					"temp.xml");
			DaisyXmlParser parser = new DaisyXmlParser();
			ImageGroup imgGroup = parser.parse(inputStream);
			Toast.makeText(getApplicationContext(), imgGroup.getCaption(),
					Toast.LENGTH_LONG).show();

			for (DaisyImage img : imgGroup.getImages()) {
				String toSpeak = img.getImageAlt() + "... , ";
				addImageWithName(img.getImageSource(), toSpeak);
				speakText(toSpeak);
				Log.d("Main", "Adding image and calling text to speech");
			}

			speakText("The producers notes on this image are as follows:"
					+ imgGroup.getProdNotes() + "... , ");

		} catch (XmlPullParserException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Could not parse",
					Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Could not parse",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown!
		if (ttobj != null) {
			ttobj.stop();
			ttobj.shutdown();
		}

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void speakText(String toSpeak) {
		ttobj.speak(toSpeak, TextToSpeech.QUEUE_ADD, null);
		ttobj.playSilence(500, TextToSpeech.QUEUE_ADD, null);
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			// Set preferred language to US english.
			// Note that a language may not be available, and the result will
			// indicate this.
			int result = ttobj.setLanguage(Locale.US);
			// Try this someday for some interesting results.
			// int result mTts.setLanguage(Locale.FRANCE);
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Lanuage data is missing or the language is not supported.
				Log.e("MAIN", "Language is not available.");
			} else {
				// Greet the user.
				sayHello();
			}
		} else {
			// Initialization failed.
			Log.e("MAIN", "Could not initialize TextToSpeech.");
		}

	}

	private void sayHello() {
		String hello = "Text to speech engine initialized... , Loading daisy content.";
		speakText(hello);
		startDaisyRendering();
	}
}
