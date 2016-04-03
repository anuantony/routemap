package com.directions.sample;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class MainActivity_ShowMap extends Activity {
	private WebView mainWebView;
	private String HomeUrl, AppName, ShareUrl, sdrUrl, ext;
	private VideoView mVideoView;
	private RelativeLayout mContentView;
	private FrameLayout mCustomViewContainer;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
	Integer vidPosition;
	ProgressBar progressBar;

	/*for test*/
	private static final int INPUT_FILE_REQUEST_CODE = 1;
	private static final int FILECHOOSER_RESULTCODE = 1;
	private static final String TAG = MainActivity.class.getSimpleName();
	private WebView webView;
	private WebSettings webSettings;
	private ValueCallback<Uri> mUploadMessage;
	private Uri mCapturedImageURI = null;
	private ValueCallback<Uri[]> mFilePathCallback;
	private String mCameraPhotoPath;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
				super.onActivityResult(requestCode, resultCode, data);
				return;
			}
			Uri[] results = null;
			// Check that the response is a good one
			if (resultCode == Activity.RESULT_OK) {
				if (data == null) {
					// If there is not data, then we may have taken a photo
					if (mCameraPhotoPath != null) {
						results = new Uri[]{Uri.parse(mCameraPhotoPath)};
					}
				} else {
					String dataString = data.getDataString();
					if (dataString != null) {
						results = new Uri[]{Uri.parse(dataString)};
					}
				}
			}
			mFilePathCallback.onReceiveValue(results);
			mFilePathCallback = null;
		} else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
			if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
				super.onActivityResult(requestCode, resultCode, data);
				return;
			}
			if (requestCode == FILECHOOSER_RESULTCODE) {
				if (null == this.mUploadMessage) {
					return;
				}
				Uri result = null;
				try {
					if (resultCode != RESULT_OK) {
						result = null;
					} else {
						// retrieve from the private variable if the intent is null
						result = data == null ? mCapturedImageURI : data.getData();
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "activity :" + e,
							Toast.LENGTH_LONG).show();
				}
				mUploadMessage.onReceiveValue(result);
				mUploadMessage = null;
			}
		}
		return;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({"CutPasteId","NewApi", "SetJavaScriptEnabled"})
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_showmap);

		HomeUrl = getString(R.string.base_url);

		//First share link
		ShareUrl = HomeUrl;

		// App name url
		AppName = getString(R.string.app_name);

		//Find objects with ID
		mainWebView = (WebView) findViewById(R.id.webView1);
        progressBar = (ProgressBar)findViewById(R.id.progressBar1);

        // focus with touch
        mainWebView.setFocusable(true);
        mainWebView.setFocusableInTouchMode(true);
        mainWebView.requestFocus(View.FOCUS_DOWN|View.FOCUS_UP);
        mainWebView.getSettings().setLightTouchEnabled(true);

        // enabled Java Script
        mainWebView.getSettings().setJavaScriptEnabled(true);
        mainWebView.getSettings().setPluginState(PluginState.ON_DEMAND);
        mainWebView.getSettings().setDomStorageEnabled(true);
        //mainWebView.getSettings().setUseWideViewPort(true);

        //Customaze Web View
        mainWebView.setWebViewClient(new MyCustomWebViewClient());
        mainWebView.setWebChromeClient(new MyChromeClient());

        //Webview scroll
        mainWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        //Cache enable
        mainWebView.getSettings().setAppCacheEnabled(true);
        mainWebView.getSettings().setRenderPriority(RenderPriority.HIGH);

        // Load Home url

        mainWebView.loadUrl(HomeUrl);


        mainWebView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_UP:
					if (!v.hasFocus()) {
						v.requestFocus();
						v.requestFocusFromTouch();
					}
					break;
				}
				return false;
			}
		});

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

			String value = extras.getString("url");
			//mainWebView.getSettings().setUseWideViewPort(true);
			mainWebView.loadUrl(value);

        }

        // Menu category
		ImageView text1= (ImageView) findViewById(R.id.title_bar_home);
        text1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent reInt = new Intent(getApplicationContext(),MainActivity.class);
				startActivity(reInt);
				mainWebView.loadUrl(HomeUrl);
			}
		});

        // Menu category
		ImageView text2= (ImageView) findViewById(R.id.title_bar_share);
        text2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, ShareUrl);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Interesting for you!");
                startActivity(Intent.createChooser(intent, "Share via"));
			}
		});
	}

    private class MyCustomWebViewClient extends WebViewClient {


		@Override
    	public void onPageStarted(WebView view, String url, Bitmap favicon) {
    		// TODO Auto-generated method stub
    		super.onPageStarted(view, url, favicon);

			// On load Spinner visible
    		progressBar.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);

    	}

        //@SuppressWarnings("deprecation")
		@Override
        public void onPageFinished(WebView view, String url) {
         // TODO Auto-generated method stub
         super.onPageFinished(view, url);

         // On load Spinner hide
         progressBar.setVisibility(View.GONE);
         view.setVisibility(View.VISIBLE);


        }

        @SuppressLint("NewApi")
		@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	//share page or link
        	ShareUrl = url;

        	// One touch call link <a href="tel:777777777">Call</a>
        	if (url.startsWith("tel:"))
        		{
	               Intent intent = new Intent(Intent.ACTION_DIAL,
	               Uri.parse(url));
	               startActivity(intent);
                }

        		else

        		// One touch sms link <a href="sms:1717171717">Sms</a>
        		if (url.startsWith("sms:"))
        		{
	        		Intent message = new Intent(Intent.ACTION_SENDTO,
	                Uri.parse(url));
	                startActivity(message);
                }

            		// One share text or  url link <a href="share:share text">share text</a>
            	else
            		if (url.startsWith("share:"))
            		{
            			String[] share_link = url.split(":");
            			try {
							String ID= URLDecoder.decode(share_link[1], "UTF-8");
							Intent intent = new Intent(Intent.ACTION_SEND);
			                intent.setType("text/plain");
			                intent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v="+ID);
			                intent.putExtra(Intent.EXTRA_SUBJECT, "Interesting for you!");
			                startActivity(Intent.createChooser(intent, "Share via"));

						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

                    }
        		// One touch image link <a href="test.jpg">Image</a>
          		else

            		if(url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".gif") || url.endsWith(".JPG") || url.endsWith(".jpeg") )
            		{
            			Intent intent=new Intent (MainActivity_ShowMap.this, Image.class);
						intent.putExtra("href", url);
		            	startActivity(intent);


            		}
        		else
        	        // else if it's a 3GP file link
        	        if(url.endsWith(".3gp")){
        	            Uri uri = Uri.parse(url);
        	            Intent intent = new Intent(Intent.ACTION_VIEW);
        	            intent.setDataAndType(uri, "video/3gp");
        	            startActivity(intent);
        	            return true;
        	        }
        	    else
        			//Link mp4 file <a href="http://mosaicdesign.uz/test.mp4">Download mp4</a>
        	    if(url.endsWith(".mp4")){

        			if(url.startsWith("download")){
        				String[] download_link = url.split(":");

            			try {
            				//<a href="download:http://mosaicdesign.uz/test.mp4">Download mp4</a>
							sdrUrl= URLDecoder.decode(download_link[1], "UTF-8")+":"+URLDecoder.decode(download_link[2], "UTF-8");
							ext = MimeTypeMap.getFileExtensionFromUrl(sdrUrl);


			                if (ext != null) {
			                     MimeTypeMap mime = MimeTypeMap.getSingleton();
			                     String mimeType = mime.getMimeTypeFromExtension(ext);

			                     if (mimeType != null) {
			                          if (ext.toLowerCase().contains("mp4")) {
			                               DownloadManager mdDownloadManager = (DownloadManager) MainActivity_ShowMap.this
			                                         .getSystemService(Context.DOWNLOAD_SERVICE);
			                               DownloadManager.Request request = new DownloadManager.Request(
			                                         Uri.parse(sdrUrl));
			                               request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
			                               request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
			                               request.setAllowedOverRoaming(true);
			                               File destinationFile = new File(
			                                         Environment.getExternalStorageDirectory(),
			                                         getFileName(sdrUrl, ext));

			                               request.setDestinationUri(Uri.fromFile(destinationFile));
			                               mdDownloadManager.enqueue(request);

			                          }
			                     }
				        }
	                        return true;
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        			}
	        			else
	        			{
	        	            Uri uri = Uri.parse(url);
	        	            Intent intent = new Intent(Intent.ACTION_VIEW);
	        	            intent.setDataAndType(uri, "video/mp4");
	        	            startActivity(intent);
	        	            return true;
	        			}

        	        }


        	        else

        		//Open MP3 file <a href="http://mosaicdesign.uz/test.mp3">Download mp3</a>
        			if(url.endsWith(".mp3") || url.endsWith(".MP3")){
                		// Download files

            			if(url.startsWith("download")){
            				String[] download_link = url.split(":");

                			try {
                				//<a href="download:http://mosaicdesign.uz/test.mp3">Download mp3</a>
    							sdrUrl= URLDecoder.decode(download_link[1], "UTF-8")+":"+URLDecoder.decode(download_link[2], "UTF-8");

    							//ext=url.substring(url.lastIndexOf("."));
    							ext = MimeTypeMap.getFileExtensionFromUrl(sdrUrl);


    			                if (ext != null) {
    			                     MimeTypeMap mime = MimeTypeMap.getSingleton();
    			                     String mimeType = mime.getMimeTypeFromExtension(ext);

    			                     if (mimeType != null) {
    			                          if (ext.toLowerCase().contains("mp3")) {
    			                               DownloadManager mdDownloadManager = (DownloadManager) MainActivity_ShowMap.this
    			                                         .getSystemService(Context.DOWNLOAD_SERVICE);
    			                               DownloadManager.Request request = new DownloadManager.Request(
    			                                         Uri.parse(sdrUrl));
    			                               request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
    			                               request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
    			                               request.setAllowedOverRoaming(true);
    			                               File destinationFile = new File(
    			                                         Environment.getExternalStorageDirectory(),
    			                                         getFileName(sdrUrl, ext));

    			                               request.setDestinationUri(Uri.fromFile(destinationFile));
    			                               mdDownloadManager.enqueue(request);

    			                          }
    			                     }
    				        }
    	                        return true;
    						} catch (UnsupportedEncodingException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
            			}
            			else
            			{
        	            Uri uri = Uri.parse(url);
        	            Intent intent = new Intent(Intent.ACTION_VIEW);
        	            intent.setDataAndType(uri, "audio/mp3");
        	            startActivity(intent);
        	            return true;
            			}

        	        }

        		//View file < a href="www.text.com/test.txt"> View text</a>
        		if (

        				   url.endsWith(".pdf")
                        || url.endsWith(".txt")
                        || url.endsWith(".doc")
                        || url.endsWith(".docx")
                        || url.endsWith(".xls")
                        || url.endsWith(".xlsx")
                        || url.endsWith(".ppt")
                        || url.endsWith(".pptx")
                        || url.endsWith(".pages")
                        || url.endsWith(".ai")
                        || url.endsWith(".psd")
                        || url.endsWith(".tiff")
                        || url.endsWith(".dxf")
                        || url.endsWith(".svg")
                        || url.endsWith(".eps")
                        || url.endsWith(".ps")
                        || url.endsWith(".ttf")
                        || url.endsWith(".xps")
                        || url.endsWith(".zip")
                        || url.endsWith(".rar")

                  )
        		{
        			if(url.startsWith("download")){
        				String[] download_link = url.split(":");

            			try {

							sdrUrl= URLDecoder.decode(download_link[1], "UTF-8")+":"+URLDecoder.decode(download_link[2], "UTF-8");
							ext = MimeTypeMap.getFileExtensionFromUrl(sdrUrl);


			                if (ext != null) {
			                     MimeTypeMap mime = MimeTypeMap.getSingleton();
			                     String mimeType = mime.getMimeTypeFromExtension(ext);

			                     if (mimeType != null) {

			                               DownloadManager mdDownloadManager = (DownloadManager) MainActivity_ShowMap.this
			                                         .getSystemService(Context.DOWNLOAD_SERVICE);
			                               DownloadManager.Request request = new DownloadManager.Request(
			                                         Uri.parse(sdrUrl));
			                               request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
			                               request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
			                               request.setAllowedOverRoaming(true);
			                               File destinationFile = new File(
			                                         Environment.getExternalStorageDirectory(),
			                                         getFileName(sdrUrl, ext));

			                               request.setDestinationUri(Uri.fromFile(destinationFile));
			                               mdDownloadManager.enqueue(request);


			                     }
				        }
	                        return true;
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
        			}
        			else
        			{
	        		String googleDocs = "https://docs.google.com/viewer?url=";
	        		mainWebView.loadUrl(googleDocs + url);
	        		return true;
        			}
        		}

        		else

        		if(url.startsWith("http:") || url.startsWith("https:"))  	{view.loadUrl(url);	}
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

        		view.loadUrl("file:///android_asset/error.html");

        }
    }

    private class MyChromeClient extends WebChromeClient  implements OnCompletionListener, OnErrorListener {

    	FrameLayout.LayoutParams COVER_SCREEN_GRAVITY_CENTER = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);


//		/**/
// For Android 5.0
public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, FileChooserParams fileChooserParams) {
	// Double check that we don't have any existing callbacks
	if (mFilePathCallback != null) {
		mFilePathCallback.onReceiveValue(null);
	}
	mFilePathCallback = filePath;
	Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
		// Create the File where the photo should go
		File photoFile = null;
//		try {
//			photoFile = createImageFile();
//			takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
//		} catch (IOException ex) {
//			// Error occurred while creating the File
//			Log.e(TAG, "Unable to create Image File", ex);
//		}
		// Continue only if the File was successfully created
		if (photoFile != null) {
			mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(photoFile));
		} else {
			takePictureIntent = null;
		}
	}
	Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
	contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
//	contentSelectionIntent.setType("image/*");
	contentSelectionIntent.setType("*/*");
	Intent[] intentArray;
	if (takePictureIntent != null) {
		intentArray = new Intent[]{takePictureIntent};
	} else {
		intentArray = new Intent[0];
	}
	Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
	chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
	chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
	chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
	startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
	return true;
}
		// openFileChooser for Android 3.0+
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
			mUploadMessage = uploadMsg;
			// Create AndroidExampleFolder at sdcard
			// Create AndroidExampleFolder at sdcard
			File imageStorageDir = new File(
					Environment.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_PICTURES)
					, "AndroidExampleFolder");
			if (!imageStorageDir.exists()) {
				// Create AndroidExampleFolder at sdcard
				imageStorageDir.mkdirs();
			}
			// Create camera captured image file path and name
			File file = new File(
					imageStorageDir + File.separator + "IMG_"
							+ String.valueOf(System.currentTimeMillis())
							+ ".jpg");
			File file_ = new File(
					imageStorageDir + File.separator + "VID_"
							+ String.valueOf(System.currentTimeMillis())
							+ ".mp4");
			mCapturedImageURI = Uri.fromFile(file);
			mCapturedImageURI = Uri.fromFile(file_);

			// Camera capture image intent
			final Intent captureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
//			i.setType("image/*");
			i.setType("*/*");

			// Create file chooser intent
			Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
			// Set camera intent to file chooser
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
					, new Parcelable[] { captureIntent });
			// On select image call onActivityResult method of activity
			startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
		}
		// openFileChooser for Android < 3.0
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			openFileChooser(uploadMsg, "");
		}
		//openFileChooser for other Android versions
		public void openFileChooser(ValueCallback<Uri> uploadMsg,
									String acceptType,
									String capture) {
			openFileChooser(uploadMsg, acceptType);
		}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		// If it wasn't the Back key or there's no web page history, bubble up to the default
		// system behavior (probably exit the activity)
		return false;
	}
//		/**/
    	@Override
	        public void onShowCustomView(View view, int requestedOrientation,
	        		CustomViewCallback callback) // Available in API level 14+, deprecated in API level 18+
	        {
	            onShowCustomView(view, callback);
	            if(Build.VERSION.SDK_INT >=14) {
                    if (view instanceof FrameLayout) {                  
                        mainWebView.addView(view, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER));                   
                        mainWebView.setVisibility(View.VISIBLE);
                    }
                }
	        }


            @Override  
            public void onShowCustomView(View view, CustomViewCallback callback) {

              if (view instanceof FrameLayout) {  

                // mainWebView is the view that the video should've played inside.
                mainWebView = (WebView)findViewById(R.id.webView1);  
                     
                mCustomViewContainer = (FrameLayout) view;  
                mCustomViewCallback = callback;  

                // mainLayout is the root layout that (ex. the layout that contains the webview)
                mContentView = (RelativeLayout)findViewById(R.id.RootLayout);  
                if (mCustomViewContainer.getFocusedChild() instanceof VideoView) {  
                  mVideoView = (VideoView) mCustomViewContainer.getFocusedChild();  
                  
                  // frame.removeView(video);  
                  mContentView.setVisibility(View.GONE);  
                  mCustomViewContainer.setVisibility(View.VISIBLE);  
                  setContentView(mCustomViewContainer);  
                  mVideoView.setOnCompletionListener((OnCompletionListener) this);  
                  mVideoView.setOnErrorListener((OnErrorListener) this);  
                  mVideoView.start();  
       
                }  
              }  
            }
       
            public void onHideCustomView() {
              if (mVideoView == null){  
                return;  
              }else{  
              // Hide the custom view.  
              mVideoView.setVisibility(View.GONE);  
              // Remove the custom view from its container.  
              mCustomViewContainer.removeView(mVideoView);  
              mVideoView = null;  
              mCustomViewContainer.setVisibility(View.GONE);  
              mCustomViewCallback.onCustomViewHidden();  
              // Show the content view.  
              mContentView.setVisibility(View.VISIBLE);  
              }  
            }  
            
            public void onCompletion(MediaPlayer mp) {  
              mp.stop();  
              mCustomViewContainer.setVisibility(View.GONE);  
              onHideCustomView();  
              setContentView(mContentView);  
            }  
            @SuppressWarnings("unused")
			public void onPrepared(MediaPlayer mp) {

            	mCustomViewCallback.onCustomViewHidden();
            }
            public boolean onError(MediaPlayer arg0, int arg1, int arg2) {  
              setContentView(mContentView);  
              return true;  
            }  
    	//Java script alert dialog
    	@Override
    	public boolean onJsAlert(WebView view, String url, String message,
    				final JsResult result) {			
    		new AlertDialog.Builder(MainActivity_ShowMap.this)
    		       .setTitle("Attention !")
    		       .setMessage(message)
    		       .setPositiveButton(R.string.ok,
    		           new AlertDialog.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
    		                     // do your stuff here
    		                     result.confirm();
    		               }
    		           }).setCancelable(false).create().show();
    			return true;			
    		}
    	
    	//Java script confirm dialog
    	@Override
    	public boolean onJsConfirm(WebView view, String url, String message,
    				final JsResult result) {
    		new AlertDialog.Builder(MainActivity_ShowMap.this)
    		       .setTitle("Confirm !")
    		       .setMessage(message)
    		       .setPositiveButton(R.string.ok,
    		           new AlertDialog.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) {
    		                     // do your stuff here
    		                     result.confirm();
    		               }
    		           }).setCancelable(false).create().show();
    			return true;
    		}
    	
    	//Java script Prompt dialog
    	@Override
    	public boolean onJsPrompt(WebView view, String url, String message,
    			String defaultValue, final JsPromptResult result) {
    		new AlertDialog.Builder(MainActivity_ShowMap.this)
    		       .setTitle("Prompt Alert !")
    		       .setMessage(message)
    		       .setPositiveButton(R.string.ok,
    		           new AlertDialog.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) {
    		                     // do your stuff here
    		                     result.confirm();
    		               }
    		           }).setCancelable(false).create().show();
    			return true;
    		}

    };
    
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mainmenu, menu);
		return true;
	}
	
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_refresh:
	    	 mainWebView.loadUrl( "javascript:window.location.reload( true )" );             
	      break;
	    case R.id.action_exit:
		       finish();
		      break;
	    default:
	      break;
	    }

	    return true;
	  }
	  
	  @Override
	  protected void onPause() {

	  if(mCustomViewContainer != null){ 

	  vidPosition = mVideoView.getCurrentPosition();
	  }

	  super.onPause();
	  }

	  @Override
	  protected void onResume() {

	  if(mCustomViewContainer != null){ 

	  mVideoView.seekTo(vidPosition);
	  }
	  super.onResume();
	  }
	  @Override  
      public void onBackPressed() {  
            if(mCustomViewContainer != null){ 
                 
                 mVideoView.stopPlayback();  
                 mCustomViewContainer.setVisibility(View.GONE);  
            
                 if (mVideoView == null){  
              
                      return;  
                 }else{  
                      
                      // Hide the custom view.  
                      mVideoView.setVisibility(View.GONE);  
                      // Remove the custom view from its container.  
                      mCustomViewContainer.removeView(mVideoView);  
                      mVideoView = null;  
                      mCustomViewContainer.setVisibility(View.GONE);  
                      mCustomViewCallback.onCustomViewHidden();  
                      // Show the content view.  
                      mContentView.setVisibility(View.VISIBLE);  
                      setContentView(mContentView);  
                      mCustomViewContainer = null; 
                 }  
            }else if(mainWebView.canGoBack()){
   
                 mainWebView.goBack();
          
            }else{
 
//                 super.onBackPressed();

				exti_function();
			}

      }

	private void exti_function() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						MainActivity_ShowMap.this.finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}

	public String getFileName(String url, String ext1) {
          String filenameWithoutExtension = "";  
          filenameWithoutExtension = String.valueOf(System.currentTimeMillis()+"."+ext1);

          return filenameWithoutExtension;  
     }

}