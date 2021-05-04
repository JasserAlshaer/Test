package com.example.spamfilterapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;

public class MainActivity7 extends AppCompatActivity {
    DrawerLayout drawerMenuForScreen;
    String gmailMessageId;
    String mailBody;
    TextView webView;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        drawerMenuForScreen=(DrawerLayout)findViewById(R.id.mainApplicationDrawer);
        Intent res=getIntent();
        gmailMessageId=res.getStringExtra("messageId");
        webView=(TextView)findViewById(R.id.gmailbody);
        getBodyTask taskOne=new getBodyTask();
        taskOne.execute();

    }
    //This method do the opening drawer operation

    //Drawer Menu Method
    public void onDrawerMenuClick(View view){
        openDrawer(drawerMenuForScreen);
    }
    private static void openDrawer(DrawerLayout draw) {
        draw.openDrawer(GravityCompat.START);
    }
    //This method do the closing drawer operation
    private static void closeDrawer(DrawerLayout draw) {
        if(draw.isDrawerOpen(GravityCompat.START)){
            draw.closeDrawer(GravityCompat.START);
        }
    }
    public void onHomeClicked (View view){
        Intent home=new Intent(getApplicationContext(),MainActivity6.class);
        startActivity(home);
    }
    public void onSendEmailClick(View view){
        Intent send=new Intent(getApplicationContext(),MainActivity4.class);
        startActivity(send);
    }
    public void onTrushClicked(View view){
        Intent trush=new Intent(getApplicationContext(),MainActivity5.class);
        startActivity(trush);
    }
    public void onLogoutclicked(View view){
        signOut();
    }
    private void signOut() {
        MainActivity.mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent backtohome=new Intent
                                (getApplicationContext(),MainActivity.class);
                        startActivity(backtohome);
                    }
                });
    }
    class getBodyTask extends AsyncTask<String, Void, String > {
        ProgressDialog pd;
        Gmail mService;
        HttpTransport transport;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity7.this);
            pd.setMessage("Please Wait , Data is Loading");
            pd.show();
        }
        @Override
        protected String doInBackground(String... querys) {
            transport = AndroidHttp.newCompatibleTransport();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    new GsonFactory(),
                    MainActivity.mCredential)
                    .setApplicationName("Spam Filtering")
                    .build();

            try {
               // Message message = mService.users().messages().get(MainActivity.accountemail,gmailMessageId).setFormat("raw").execute();
                Message messagetext = mService.users().messages().get(MainActivity.accountemail,gmailMessageId).setFormat("full").execute();
                Log.i("Head",messagetext.getSnippet());
                byte[] bodyBytes = Base64.decodeBase64(messagetext.getPayload().getParts().get(0).getBody().getData().trim());
                String body = new String(bodyBytes, "UTF-8");
                mailBody=body;
                Log.i("Head",mailBody);
               /* ModifyMessageRequest mods = new ModifyMessageRequest();
                List<String> stringList= new ArrayList<>();
                stringList.add("UNREAD");
                mods.setAddLabelIds(null);
                mods.setRemoveLabelIds(stringList)  ;
                mService.users().messages().modify(MainActivity.accountemail,gmailMessageId,mods).execute();
            */}catch (IOException exception){
                Log.e("BUG", "SheetUpdate IOException"+exception.getMessage());
                exception.printStackTrace();
            }
            return "Gmail";
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd != null)
            {
                pd.dismiss();
                webView.setText(mailBody);
            }
        }
    }
}