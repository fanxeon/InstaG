package fanx.instag.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.app.ProgressDialog;
import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import android.app.Dialog;
////import android.app.ProgressDialog;
////import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
//import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
////import android.content.Context;
/**
 * Created by SShrestha on 7/09/2015.
 */
public class InstagramSupportLibrary
{
    /**
     *
     * @author Thiago Locatelli <thiago.locatelli@gmail.com>
     * @author Lorensius W. L T <lorenz@londatiga.net>
     *
     */
    public static class InstagramApp
    {

        private InstagramSession mSession;
        private InstagramDialog mDialog;
        private OAuthAuthenticationListener mListener;
        private ProgressDialog mProgress;
        private String mAuthUrl;
        private String mTokenUrl;
        private String mAccessToken;
        private Context mCtx;

        private String mClientId;
        private String mClientSecret;


        private static int WHAT_FINALIZE = 0;
        private static int WHAT_ERROR = 1;
        private static int WHAT_FETCH_INFO = 2;

        /**
         * Callback url, as set in 'Manage OAuth Costumers' page
         * (https://developer.github.com/)
         */

        public static String mCallbackUrl = "";
        private static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
        private static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
        private static final String API_URL = "https://api.instagram.com/v1";

        private static final String TAG = "InstagramAPI";

        public InstagramApp()
        {}

        public InstagramApp(Context context, String clientId, String clientSecret,
                            String callbackUrl) {

            mClientId = clientId;
            mClientSecret = clientSecret;
            mCtx = context;
            mSession = new InstagramSession(context);
            mAccessToken = mSession.getAccessToken();
            mCallbackUrl = callbackUrl;
            mTokenUrl = TOKEN_URL + "?client_id=" + clientId + "&client_secret="
                    + clientSecret + "&redirect_uri=" + mCallbackUrl + "&grant_type=authorization_code";
            mAuthUrl = AUTH_URL + "?client_id=" + clientId + "&redirect_uri="
                    + mCallbackUrl + "&response_type=code&display=touch&scope=likes+comments+relationships";

            InstagramDialog.OAuthDialogListener listener = new InstagramDialog.OAuthDialogListener() {
                @Override
                public void onComplete(String code) {
                    getAccessToken(code);
                }

                @Override
                public void onError(String error) {
                    mListener.onFail(error.toString());
                }
            };

            mDialog = new InstagramDialog(context, mAuthUrl, listener);
            mProgress = new ProgressDialog(context);
            mProgress.setCancelable(false);
        }

        private void getAccessToken(final String code) {
            mProgress.setMessage("Getting access token ...");
            mProgress.show();

            new Thread() {
                @Override
                public void run() {
                    Log.i(TAG, "Getting access token");
                    int what = WHAT_FETCH_INFO;
                    try {
                        URL url = new URL(TOKEN_URL);
                        //URL url = new URL(mTokenUrl + "&code=" + code);
                        Log.i(TAG, "Opening Token URL " + url.toString());
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("POST");
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        //urlConnection.connect();
                        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                        writer.write("client_id=" + mClientId +
                                "&client_secret=" + mClientSecret +
                                "&grant_type=authorization_code" +
                                "&redirect_uri=" + mCallbackUrl +
                                "&code=" + code);
                        writer.flush();
                        String response = streamToString(urlConnection.getInputStream());
                        Log.i(TAG, "response " + response);
                        JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();

                        mAccessToken = jsonObj.getString("access_token");
                        Log.i(TAG, "Got access token: " + mAccessToken);

                        String id = jsonObj.getJSONObject("user").getString("id");
                        String user = jsonObj.getJSONObject("user").getString("username");
                        String name = jsonObj.getJSONObject("user").getString("full_name");
                        String profile_picture = jsonObj.getJSONObject("user").getString("profile_picture");

                        mSession.storeAccessToken(mAccessToken, id, user, name, profile_picture);

                    } catch (Exception ex) {
                        what = WHAT_ERROR;
                        ex.printStackTrace();
                    }

                    mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
                }
            }.start();
        }

        private void fetchUserName() {
            mProgress.setMessage("Finalizing ...");

            new Thread() {
                @Override
                public void run() {
                    Log.i(TAG, "Fetching user info");
                    int what = WHAT_FINALIZE;
                    try {
                        URL url = new URL(API_URL + "/users/" + mSession.getId() + "/?access_token=" + mAccessToken);

                        Log.d(TAG, "Opening URL " + url.toString());
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoInput(true);
                        urlConnection.connect();
                        String response = streamToString(urlConnection.getInputStream());
                        System.out.println(response);
                        JSONObject jsonObj = (JSONObject) new JSONTokener(response).nextValue();
                        String name = jsonObj.getJSONObject("data").getString("full_name");
                        String bio = jsonObj.getJSONObject("data").getString("bio");
                        Log.i(TAG, "Got name: " + name + ", bio [" + bio + "]");
                    } catch (Exception ex) {
                        what = WHAT_ERROR;
                        ex.printStackTrace();
                    }

                    mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
                }
            }.start();

        }


        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == WHAT_ERROR) {
                    mProgress.dismiss();
                    if(msg.arg1 == 1) {
                        mListener.onFail("Failed to get access token");
                    }
                    else if(msg.arg1 == 2) {
                        mListener.onFail("Failed to get user information");
                    }
                }
                else if(msg.what == WHAT_FETCH_INFO) {
                    fetchUserName();
                }
                else {
                    mProgress.dismiss();
                    mListener.onSuccess();
                }
            }
        };

        public boolean hasAccessToken() {
            return (mAccessToken == null) ? false : true;
        }

        public void setListener(OAuthAuthenticationListener listener) {
            mListener = listener;
        }

        public String getUserName() {
            return mSession.getUsername();
        }

        public String getId() {
            return mSession.getId();
        }

        public String getName() {
            return mSession.getName();
        }

        public String getmAccessToken() {
            return hasAccessToken()? null: mAccessToken;
        }

        public void authorize() {
            //Intent webAuthIntent = new Intent(Intent.ACTION_VIEW);
            //webAuthIntent.setData(Uri.parse(AUTH_URL));
            //mCtx.startActivity(webAuthIntent);
            mDialog.show();
        }

        private String streamToString(InputStream is) throws IOException {
            String str = "";

            if (is != null) {
                StringBuilder sb = new StringBuilder();
                String line;

                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is));

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    reader.close();
                } finally {
                    is.close();
                }

                str = sb.toString();
            }

            return str;
        }

        public void resetAccessToken() {
            if (mAccessToken != null) {
                mSession.resetAccessToken();
                mAccessToken = null;
            }
        }

        public String getAccessToken()
        {
            return mSession.getAccessToken();
        }

        public interface OAuthAuthenticationListener {
            public abstract void onSuccess();

            public abstract void onFail(String error);
        }
    }

    /**
     * Display 37Signals authentication dialog.
     *
     * @author Thiago Locatelli <thiago.locatelli@gmail.com>
     * @author Lorensius W. L T <lorenz@londatiga.net>
     *
     */
    public static  class InstagramDialog extends Dialog {

        static final float[] DIMENSIONS_LANDSCAPE = { 460, 260 };
        static final float[] DIMENSIONS_PORTRAIT = { 282, 420 };
        static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
        static final int MARGIN = 4;
        static final int PADDING = 2;

        private String mUrl;
        private OAuthDialogListener mListener;
        private ProgressDialog mSpinner;
        private WebView mWebView;
        private LinearLayout mContent;
        private TextView mTitle;

        private static final String TAG = "Instagram-WebView";

        public InstagramDialog(Context context, String url,
                               OAuthDialogListener listener) {
            super(context);

            mUrl = url;
            mListener = listener;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mSpinner = new ProgressDialog(getContext());
            mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mSpinner.setMessage("Loading...");
            mContent = new LinearLayout(getContext());
            mContent.setOrientation(LinearLayout.VERTICAL);
            setUpTitle();
            setUpWebView();

            Display display = getWindow().getWindowManager().getDefaultDisplay();
            final float scale = getContext().getResources().getDisplayMetrics().density;
            float[] dimensions = (display.getWidth() < display.getHeight()) ? DIMENSIONS_PORTRAIT
                    : DIMENSIONS_LANDSCAPE;

            addContentView(mContent, new FrameLayout.LayoutParams(
                    (int) (dimensions[0] * scale + 0.5f), (int) (dimensions[1]
                    * scale + 0.5f)));
            CookieSyncManager.createInstance(getContext());
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
        }

        private void setUpTitle() {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            mTitle = new TextView(getContext());
            mTitle.setText("Instagram");
            mTitle.setTextColor(Color.WHITE);
            mTitle.setTypeface(Typeface.DEFAULT_BOLD);
            mTitle.setBackgroundColor(Color.BLACK);
            mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
            mContent.addView(mTitle);
        }

        private void setUpWebView() {
            mWebView = new WebView(getContext());
            mWebView.setVerticalScrollBarEnabled(false);
            mWebView.setHorizontalScrollBarEnabled(false);
            mWebView.setWebViewClient(new OAuthWebViewClient());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.loadUrl(mUrl);
            mWebView.setLayoutParams(FILL);
            mContent.addView(mWebView);
        }

        private class OAuthWebViewClient extends WebViewClient {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "Redirecting URL " + url);

                if (url.startsWith(InstagramApp.mCallbackUrl)) {
                    String urls[] = url.split("=");
                    mListener.onComplete(urls[1]);
                    InstagramDialog.this.dismiss();
                    return true;
                }
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                Log.d(TAG, "Page error: " + description);

                super.onReceivedError(view, errorCode, description, failingUrl);
                mListener.onError(description);
                InstagramDialog.this.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "Loading URL: " + url);

                super.onPageStarted(view, url, favicon);
                mSpinner.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String title = mWebView.getTitle();
                if (title != null && title.length() > 0) {
                    mTitle.setText(title);
                }
                Log.d(TAG, "onPageFinished URL: " + url);
                mSpinner.dismiss();
            }

        }

        public interface OAuthDialogListener {
            public abstract void onComplete(String accessToken);
            public abstract void onError(String error);
        }

    }

    /**
     * Manage access token and user name. Uses shared preferences to store access
     * token and user name.
     *
     * @author Thiago Locatelli <thiago.locatelli@gmail.com>
     * @author Lorensius W. L T <lorenz@londatiga.net>
     *
     */
    public static class InstagramSession {

        private SharedPreferences sharedPref;
        private Editor editor;

        private static final String SHARED = "Instagram_Preferences";
        private static final String API_USERNAME = "username";
        private static final String API_ID = "id";
        private static final String API_NAME = "name";
        private static final String API_ACCESS_TOKEN = "access_token";
        private static final String PROFILE_PICTURE = "profile_picture";


        public InstagramSession(Context context) {
            sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
            editor = sharedPref.edit();
        }

        /**
         *
         * @param -accessToken
         * @param -expireToken
         * @param -expiresIn
         * @param -username
         */
        public void storeAccessToken(String accessToken, String id, String username, String name, String profile_picture) {
            editor.putString(API_ID, id);
            editor.putString(API_NAME, name);
            editor.putString(API_ACCESS_TOKEN, accessToken);
            editor.putString(API_USERNAME, username);
            editor.putString(PROFILE_PICTURE, profile_picture);
            editor.commit();
        }

        public void storeAccessToken(String accessToken) {
            editor.putString(API_ACCESS_TOKEN, accessToken);
            editor.commit();
        }

        /**
         * Reset access token and user name
         */
        public void resetAccessToken() {
            editor.putString(API_ID, null);
            editor.putString(API_NAME, null);
            editor.putString(API_ACCESS_TOKEN, null);
            editor.putString(API_USERNAME, null);
            editor.putString(PROFILE_PICTURE, null);
            editor.commit();
        }

        /**
         * Get user name
         *
         * @return User name
         */
        public String getUsername() {
            return sharedPref.getString(API_USERNAME, null);
        }

        /**
         *
         * @return
         */
        public String getId() {
            return sharedPref.getString(API_ID, null);
        }

        /**
         *
         * @return
         */
        public String getName() {
            return sharedPref.getString(API_NAME, null);
        }

        /**
         *
         * @return
         */
        public String getProfilePicture() {
            return sharedPref.getString(PROFILE_PICTURE, null);
        }


        /**
         * Get access token
         *
         * @return Access token
         */
        public String getAccessToken() {
            return sharedPref.getString(API_ACCESS_TOKEN, null);
        }



    }

}
