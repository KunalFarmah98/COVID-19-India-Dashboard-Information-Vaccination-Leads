package com.kunalfarmah.covid_19_info_dashboard.util;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.kunalfarmah.covid_19_info_dashboard.R;

public class CustomGoogleSignIn {
    private GoogleSignInOptions gso;
    private static CustomGoogleSignIn mInstance;

    public static synchronized CustomGoogleSignIn getInstance() {
        if (mInstance == null) {
            mInstance = new CustomGoogleSignIn();
        }
        return mInstance;
    }

    public GoogleSignInOptions getGso(Context context) {
        if (gso == null) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
        }
        return gso;
    }


}
