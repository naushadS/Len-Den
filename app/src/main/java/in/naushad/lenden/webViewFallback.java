package in.naushad.lenden;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class webViewFallback extends AppCompatActivity {
    Toolbar tbWebView;
    private WebView mywebview;
    private String URLToLoad,WebpageTitle,WebpageSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        tbWebView = (Toolbar) findViewById(R.id.tbWebView);
        mywebview = (WebView) findViewById(R.id.mywebview);

        //receive extras from the bundle
        Bundle basket = getIntent().getExtras();
        URLToLoad=basket.getString("URLToOpen");
        WebpageTitle=basket.getString("WebpageTitle");
        WebpageSubtitle=basket.getString("WebpageSubtitle");

        setSupportActionBar(tbWebView);
        getSupportActionBar().setTitle(WebpageTitle);
        getSupportActionBar().setSubtitle(WebpageSubtitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);


        mywebview.loadUrl(URLToLoad);
        mywebview.getSettings().setJavaScriptEnabled(true);

        mywebview.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Toast.makeText(webViewFallback.this,""+error, Toast.LENGTH_SHORT).show();
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //return super.shouldOverrideUrlLoading(view, url);
                mywebview.loadUrl(url);
                return true;
            }

        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mywebview.canGoBack()) {
            mywebview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mywebview.canGoBack()){
                    mywebview.goBack();
                }else {
                    onBackPressed();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
