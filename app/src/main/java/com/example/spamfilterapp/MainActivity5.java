package com.example.spamfilterapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class  MainActivity5 extends AppCompatActivity {
    DrawerLayout drawerMenuForScreen;
    ArrayList <String> outbox,qoutation;
    ArrayAdapter myadapter;
    Gmail mService;
    HttpTransport transport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        outbox=new ArrayList();
        qoutation=new ArrayList<>();
        drawerMenuForScreen=(DrawerLayout)findViewById(R.id.mainApplicationDrawer);
        ListView trushList = (ListView) findViewById(R.id.trushEmailList);
         myadapter=new ArrayAdapter(this,
                R.layout.email_list_design,R.id.mailname,outbox
        ){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view= super.getView(position, convertView, parent);
                CircleImageView maimage=(CircleImageView)view.findViewById(R.id.mailpic);
                TextView last=(TextView)view.findViewById(R.id.maillastmassage);
                last.setText(qoutation.get(position));
                //Fill Screen Content from Gmail Api
                return view;
            }
        };
        trushList.setAdapter(myadapter);
        gmailTask2 getoutbox=new gmailTask2();
        getoutbox.execute("in:sent");
    }
        //Drawer Menu Method
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

    class gmailTask2 extends AsyncTask<String, Void, String > {
        int unreadNumber,message;
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity5.this);
            pd.setMessage("Loading");
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
                List<String> message2 = new ArrayList();
                ListMessagesResponse listMessagesResponse = mService.users().messages().list(MainActivity.accountemail)
                        .setQ(querys[0])
                        .setMaxResults(Long.valueOf(50))
                        .setIncludeSpamTrash(true)
                        .execute();
                List<Message> messageList = listMessagesResponse.getMessages();

                for (Message message : messageList) {
                    Message messageText = mService.users().messages().get(MainActivity.accountemail, message.getId()).setFormat("full").execute();
                    //Get Headers
                    List<MessagePartHeader> headers = messageText.getPayload().getHeaders();
                    String from="";
                    for (MessagePartHeader header:headers){
                        if(header.getName().equals("From")){
                            from=header.getValue();
                            break;
                        }
                    }

                    Log.i("Reciver",MainActivity.accountemail);
                    Log.i("From",from);
                    Log.i("Label",messageText.getLabelIds().get(0));
                    Log.i("Snippeset",messageText.getSnippet());
                    outbox.add(from);
                    qoutation.add(messageText.getSnippet());
                    Log.i("hr","\n\n *******************************************************");

                }

                // Message sendMail=mService.users().messages().send(MainActivity.accountemail,);
            }catch (IOException exception){
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
                myadapter.notifyDataSetChanged();
                pd.dismiss();
            }
        }
    }
}