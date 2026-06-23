package uz.muhandisd.amaliyfizika;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

/**
 * "Amaliy Fizika" — 7-8-9 sinf amaliy fizika platformasi.
 *
 * Xavfsizlik tamoyillari:
 *  - Ilova 100% oflayn: barcha kontent APK ichida (assets/index.html), localStorage'da saqlanadi.
 *  - INTERNET ruxsati YO'Q (AndroidManifest'da e'lon qilinmagan) — ilova tarmoqqa umuman chiqa olmaydi.
 *  - WebView faqat ilovaning o'z faylini yuklaydi; tashqi havolalar tizim brauzerida ochiladi.
 *  - file:// URL'lar uchun kross-domen kirish o'chirilgan (xavfsiz standart).
 */
public class MainActivity extends AppCompatActivity {

    private static final String APP_URL = "file:///android_asset/index.html";
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);          // ilova mantig'i uchun zarur
        s.setDomStorageEnabled(true);          // localStorage (darslar, tema, sinf saqlanadi)
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);            // assets/index.html'ni yuklash uchun
        // Kross-fayl/kross-domen kirishni o'chiramiz — xavfsizlik uchun:
        s.setAllowFileAccessFromFileURLs(false);
        s.setAllowUniversalAccessFromFileURLs(false);
        s.setSupportZoom(true);
        s.setBuiltInZoomControls(true);
        s.setDisplayZoomControls(false);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }

        // JSON eksportini ruxsatsiz amalga oshirish uchun ko'prik
        webView.addJavascriptInterface(new ExportBridge(), "AndroidExport");

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            // Ichki sahifa — WebView'da; tashqi havola — tizim brauzerida
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                Uri u = req.getUrl();
                String url = u.toString();
                if (url.startsWith("file://")) return false;
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, u));
                } catch (Exception ignored) {}
                return true;
            }

            // Sahifa yuklangach: blob orqali JSON yuklab olishni Android ko'prigiga yo'naltiramiz
            @Override
            public void onPageFinished(WebView view, String url) {
                view.evaluateJavascript(EXPORT_HOOK_JS, null);
            }
        });

        webView.loadUrl(APP_URL);

        // Orqaga tugmasi: WebView tarixida orqaga, aks holda ilovadan chiqish
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    /** JS tomonda blob-yuklashni ushlab, base64 ko'rinishida Android'ga uzatadi. */
    private class ExportBridge {
        @JavascriptInterface
        public void save(final String fileName, final String base64Data) {
            runOnUiThread(() -> writeAndShare(fileName, base64Data));
        }
    }

    private void writeAndShare(String fileName, String base64Data) {
        try {
            int comma = base64Data.indexOf(',');
            String pure = comma >= 0 ? base64Data.substring(comma + 1) : base64Data;
            byte[] bytes = Base64.decode(pure, Base64.DEFAULT);

            File dir = new File(getExternalFilesDir(null), "exports"); // ruxsat talab qilmaydi
            if (!dir.exists()) dir.mkdirs();
            File out = new File(dir, fileName);
            try (FileOutputStream fos = new FileOutputStream(out)) {
                fos.write(bytes);
            }

            Uri uri = FileProvider.getUriForFile(
                    this, getPackageName() + ".fileprovider", out);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("application/json");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(share, "JSON faylni saqlash / ulashish"));

            Toast.makeText(this, "Saqlandi: " + out.getName(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Eksport xatosi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Veb-ilovadagi blob 'download' bosishini ushlab, AndroidExport.save(...) ga yo'naltiradi
    private static final String EXPORT_HOOK_JS =
        "(function(){" +
        "  if (window.__afExportHooked) return; window.__afExportHooked = true;" +
        "  var origCreate = URL.createObjectURL.bind(URL);" +
        "  var map = {};" +
        "  URL.createObjectURL = function(blob){ var u = origCreate(blob); try{ map[u]=blob; }catch(e){} return u; };" +
        "  document.addEventListener('click', function(ev){" +
        "    var a = ev.target && ev.target.closest ? ev.target.closest('a[download]') : null;" +
        "    if(!a) return;" +
        "    var href = a.getAttribute('href') || '';" +
        "    if(href.indexOf('blob:') !== 0) return;" +
        "    ev.preventDefault();" +
        "    var name = a.getAttribute('download') || 'export.json';" +
        "    var blob = map[href];" +
        "    var fr = new FileReader();" +
        "    fr.onload = function(){ try{ AndroidExport.save(name, fr.result); }catch(e){} };" +
        "    if(blob){ fr.readAsDataURL(blob); }" +
        "    else { fetch(href).then(function(r){return r.blob();}).then(function(b){ fr.readAsDataURL(b); }); }" +
        "  }, true);" +
        "})();";

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
