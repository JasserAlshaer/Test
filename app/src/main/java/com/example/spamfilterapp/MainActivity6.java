package com.example.spamfilterapp;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.ModifyMessageRequest;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity6 extends AppCompatActivity {
    Gmail mService;
    HttpTransport transport;
    NotificationManager mNotificationManager;
    DrawerLayout drawerMenuForScreen;
    private ConnectionResult mConnectionResult;
    static String[] SCOPES = {
            GmailScopes.MAIL_GOOGLE_COM
    };
    TextView tab1,tab2,tab3;
    ListView applist;
    ArrayList <String> emails, Snipesset,messagesIds;
    ArrayAdapter myadapter;
    String  mailBody,messageStatus,NofificationText;
    int index;
    double spamPersenteg,notSpamPersentage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);
        tab1=(TextView)findViewById(R.id.tab1);
        tab2=(TextView)findViewById(R.id.tab2);
        tab3=(TextView)findViewById(R.id.tab3);
        tab1.setTextColor(Color.RED);
        applist=(ListView)findViewById(R.id.emailsList);
        drawerMenuForScreen=(DrawerLayout)findViewById(R.id.mainApplicationDrawer);
        //Use Api First Time
        gmailTask task1=new gmailTask();
        task1.execute("in:inbox");

        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab2.setTextColor(Color.BLACK);
                tab3.setTextColor(Color.BLACK);
                emails.clear();
                Snipesset.clear();
                messagesIds.clear();
                //Add Data For Email
                tab1.setTextColor(Color.RED);
                gmailTask task1=new gmailTask();
                task1.execute("in:inbox");
            }
        });
        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //all emails tab
                tab1.setTextColor(Color.BLACK);
                tab3.setTextColor(Color.BLACK);
                tab2.setTextColor(Color.RED);
                emails.clear();
                Snipesset.clear();
                messagesIds.clear();
                gmailTask task1=new gmailTask();
                task1.execute("in:spam");
            }
        });
        tab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tab2.setTextColor(Color.BLACK);
                tab1.setTextColor(Color.BLACK);
                tab3.setTextColor(Color.RED);
                emails.clear();
                Snipesset.clear();
                messagesIds.clear();
                gmailTask task1=new gmailTask();
                task1.execute("in:trash");
                //in:trash
            }
        });
        emails=new ArrayList<>();
        Snipesset =new ArrayList<>();
        messagesIds=new ArrayList<>();
        myadapter=new ArrayAdapter(this,
                R.layout.email_list_design,R.id.mailname,emails
        ){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view= super.getView(position, convertView, parent);
                CircleImageView maimage=(CircleImageView)view.findViewById(R.id.mailpic);
                TextView last=(TextView)view.findViewById(R.id.maillastmassage);
                last.setText(Snipesset.get(position)+"");
                //Fill Screen Content from Gmail Api
                return view;
            }
        };
        applist.setAdapter(myadapter);
        registerForContextMenu(applist);
       //Invoke The On Click Listener
        applist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent moveToSeeBody=new Intent(MainActivity6.this,MainActivity7.class);
                moveToSeeBody.putExtra("messageId",messagesIds.get(position));
                startActivity(moveToSeeBody);
            }
        });
        applist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               index=position;
               return false;
            }
        });
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
      if(v.getId()==R.id.emailsList){
          AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuInfo;
          menu.setHeaderTitle("Make A Choose :");
          String [] menuItem=getResources().getStringArray(R.array.options);
          for(int i=0;i<menuItem.length;i++){
              menu.add(Menu.NONE,i,i,menuItem[i]);
          }
      }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex=item.getItemId();
        String [] menuItem=getResources().getStringArray(R.array.options);
        String menuItemName=menuItem[menuItemIndex];
        switch (menuItemIndex){
            case 0:
                //users.messages.trash
                MoveToTrushTask task5=new MoveToTrushTask();
                task5.execute(messagesIds.get(index));
                break;
          case 1:
              Toast.makeText(MainActivity6.this, "Done", Toast.LENGTH_LONG).show();
              MoveToSpamTask moveToSpamTask=new MoveToSpamTask();
              moveToSpamTask.onPostExecute(messagesIds.get(index));
                break;
            case 2:
                getBodyTask newBody =new getBodyTask();
                newBody.execute(messagesIds.get(index));
                AnalayzeEmail task18=new AnalayzeEmail();
                task18.execute("https://api.uclassify.com/v1/hiba%20alawamleh/appspamfiltering/classify?readkey=LjzwOBotnCgj&text="+mailBody);
                mNotificationManager =
                        (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                break;
            case 3:
                DeleteTask task=new DeleteTask();
                task.execute(messagesIds.get(index));
                Toast.makeText(MainActivity6.this, "Done", Toast.LENGTH_LONG).show();

                break;

        }
        return super.onContextItemSelected(item);
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
    class gmailTask extends AsyncTask<String, Void, String > {

        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity6.this);
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

                ListMessagesResponse listMessagesResponse = mService.users().messages().list(MainActivity.accountemail)
                        .setQ(querys[0])
                        .setMaxResults(Long.valueOf(50))
                        .setIncludeSpamTrash(true)
                        .execute();

                if (listMessagesResponse.size()==0){
                    Toast.makeText(MainActivity6.this, "Your Inbox Is Empty", Toast.LENGTH_SHORT).show();
                }else {
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
                            Log.i("Id",messageText.getId());
                            emails.add(from);
                            Snipesset.add(messageText.getSnippet());
                             messagesIds.add(messageText.getId());
                            Log.i("hr","\n\n *******************************************************");

                        }
                    }

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
    class DeleteTask extends AsyncTask<String, Void, String > {
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity6.this);
            pd.setMessage("Deleting");
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
                mService.users().messages().delete(MainActivity.accountemail,querys[0]).execute();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return "Delete";
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd != null)
            {
                pd.dismiss();
            }
        }
    }
    //****************************************************************
    class MoveToTrushTask extends AsyncTask<String, Void, String > {
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity6.this);
            pd.setMessage("Please Wait ........");
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
                mService.users().messages().trash(MainActivity.accountemail,querys[0]).execute();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return "Trush";
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd != null)
            {
                pd.dismiss();
            }
        }
    }
    //******************************************
    class MoveToSpamTask extends AsyncTask<String, Void, String > {
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity6.this);
            pd.setMessage("Please Wait ........");
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
                ModifyMessageRequest mods = new ModifyMessageRequest();
                List<String>stringList= new ArrayList<>();
                stringList.add("SPAM");
                mods.setAddLabelIds(stringList);
                mods.setRemoveLabelIds(null)  ;
                mService.users().messages().modify(MainActivity.accountemail,querys[0],mods).execute();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return "Trush";
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd != null)
            {
                pd.dismiss();
            }
        }
    }
    class AnalayzeEmail extends AsyncTask<String,Void,String>{
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity6.this);
            pd.setMessage("Please Wait The Email under Processing........");
            pd.show();
        }
        @Override
        protected String doInBackground(String... urls) {
            URL link;
            HttpURLConnection connection=null;
            try {
                link=new URL(urls[0]);
                connection=(HttpURLConnection)link.openConnection();
                InputStream in=connection.getInputStream();
                InputStreamReader streamReader=new InputStreamReader(in);
                String result="";
                int data=streamReader.read();
                while (data !=-1){
                    char current=(char)data;
                    result+=current;
                    data=streamReader.read();
                }
                return result;
            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd != null)
            {
                pd.dismiss();
                try{
                    JSONObject jos=new JSONObject(result);
                    String not=jos.getString("not spam");
                    String spa=jos.getString("spam");
                    notSpamPersentage=Double.parseDouble(not)*100;
                    spamPersenteg=Double.parseDouble(spa)*100;
                    if(notSpamPersentage>99){
                        messageStatus="Safe Message";
                        NofificationText="Analayze Result  Done \n " +
                                "Dear user\n" +
                                "The message you just analyzed  It's safe Message You Can Deal With Message Without Any Problem ";
                    }else{
                        messageStatus="UnSafe Message";
                        NofificationText="Analayze Result \n " +
                                "Dear user\n" +
                                "The message you just analyzed could be an "+ spamPersenteg+"% fake and junk email."+
                                "\n Please Back to Application  To take appropriate action";
                    }

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(MainActivity6.this, "notify_001");
                    Intent ii = new Intent(getApplicationContext(), MainActivity6.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity6.this, 0, ii, 0);
                    NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                    mBuilder.setContentTitle("Your Title");
                    mBuilder.setContentText("Your text");
                    mBuilder.setPriority(Notification.PRIORITY_MAX);
                    mBuilder.setStyle(bigText);



                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        String channelId = "notify_001";
                        NotificationChannel channel = new NotificationChannel(
                                channelId,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_HIGH);
                        mNotificationManager.createNotificationChannel(channel);
                        mBuilder.setChannelId(channelId);
                    }
                    mNotificationManager.notify(0, mBuilder.build());
                }catch (Exception e){

                }
            }
        }
    }
    class getBodyTask extends AsyncTask<String, Void, String > {
        ProgressDialog pd;
        Gmail mService;
        HttpTransport transport;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity6.this);
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
                Message messagetext = mService.users().messages().get(MainActivity.accountemail,querys[0]).setFormat("full").execute();
                byte[] bodyBytes = Base64.decodeBase64(messagetext.getPayload().getParts().get(0).getBody().getData().trim());
                String body = new String(bodyBytes, "UTF-8");
                mailBody=body;
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
                pd.dismiss();
            }
        }
    }
}

