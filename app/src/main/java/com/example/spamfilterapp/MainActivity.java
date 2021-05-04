   package com.example.spamfilterapp;

   import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

   public class MainActivity extends AppCompatActivity {
       private static final int RC_SIGN_IN = 9001;
       private  static final int REQUEST_CODE_TOKEN_AUTH=1337;
       public static GoogleSignInClient mGoogleSignInClient;
       GoogleSignInAccount account;
       GoogleAuthUtil mAuth;
       Context mContext = this;
       public static String accountemail;
       Gmail mService;
       HttpTransport transport;
       JsonFactory jsonFactory;
       static  InputStream is;
       public static  GoogleAccountCredential mCredential;
       private ConnectionResult mConnectionResult;
       static String[] SCOPES = {
               GmailScopes.MAIL_GOOGLE_COM
       };
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
           GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                   .requestScopes(
                   new Scope(GmailScopes.MAIL_GOOGLE_COM))
                   .requestIdToken("827601706886-in0nu6hfdi0heeescec6vnbgkesvi5sr.apps.googleusercontent.com")
                   .requestEmail()
                   .build();
           mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    public void onLoginButtonPressed(View view) throws IOException {
           signIn();
        System.out.println("Hello");
        try {
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
       /**
        * This method is used to process the input and return the statistics.
        *
        * @throws Exception
        */

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, Move To Main Screen and Save All Information about Google Account
            accountemail = account.getEmail();
            mCredential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
            mCredential.setSelectedAccount(account.getAccount());

            Intent moveToMainSecreen=new Intent
                    (MainActivity.this,MainActivity6.class);
            startActivity(moveToMainSecreen);

        } catch (ApiException e) {
            /* When The Sign in Failed Message will be show to user
            *  To inform him
            * */
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(mContext, "SignInFailed", Toast.LENGTH_SHORT).show();
            Log.w("info", "signInResult:failed code=" + e.getStatusCode());
        }
    }

}
