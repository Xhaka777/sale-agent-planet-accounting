package org.planetaccounting.saleAgent.utils;

import android.content.Intent;
import android.net.Uri;

public class BrowserSupportMethod {

    public static Intent getBrowserIntent(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

}
