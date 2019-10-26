package android.bignerdbranch.com;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HelpWebPage extends AppCompatActivity {
    private WebView mWebView;

    public static Intent newIntent(Context packageContext, Uri helpPageUri) {
        Intent intent = new Intent(packageContext, HelpWebPage.class);
        intent.setData(helpPageUri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
                return false;
            }
        });

        mWebView.loadUrl("https://www.wikihow.com/Check-In-on-Facebook");
    }
}
