package com.directions.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

@SuppressLint("SetJavaScriptEnabled")
public class Image extends Activity {
	private WebView mainWebView;
	ProgressBar progressBar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		mainWebView = (WebView) findViewById(R.id.webView_image);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_image);
        
        WebSettings webSettings = mainWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        
        mainWebView.setBackgroundColor(Color.TRANSPARENT);
        
        mainWebView.getSettings().setBuiltInZoomControls(true);
        mainWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
		mainWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mainWebView.setScrollbarFadingEnabled(false);
		mainWebView.setWebViewClient(new MyCustomWebViewClient());
        mainWebView.loadUrl("file:///android_asset/error.html");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            
			String value = extras.getString("href");
			mainWebView.loadUrl(value);
        }
	}
	
	  private class MyCustomWebViewClient extends WebViewClient {

			@Override
	    	public void onPageStarted(WebView view, String url, Bitmap favicon) {
	    		// TODO Auto-generated method stub
	    		super.onPageStarted(view, url, favicon);
	    		progressBar.setVisibility(View.VISIBLE);
	            view.setVisibility(View.GONE);
	    	}
			
			@Override
	        public void onPageFinished(WebView view, String url) {
	         // TODO Auto-generated method stub
	         super.onPageFinished(view, url);
	         
	         progressBar.setVisibility(View.GONE);
	         view.setVisibility(View.VISIBLE);
	         
	         
	        }
	    	
	  }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
