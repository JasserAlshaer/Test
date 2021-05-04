package com.example.spamfilterapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.gmail.Gmail;

public class MainActivity4 extends AppCompatActivity {
    EditText text1;
    EditText text2;
    EditText text3;
    Gmail mService;
    HttpTransport transport;
    DrawerLayout drawerMenuForScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        drawerMenuForScreen=(DrawerLayout)findViewById(R.id.mainApplicationDrawer);
        //define mirror object
         text1=(EditText)findViewById(R.id.mailreciverfield);
         text2=(EditText)findViewById(R.id.mailsubjectfield);
         text3=(EditText)findViewById(R.id.mailtextfield);

    }

    public void onImageiconClick(View view) {
        Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }
    public void onFileIconClick(View view){
    Intent choose=new Intent(Intent.ACTION_GET_CONTENT);
    choose.setType("*/*");
    choose=Intent.createChooser(choose,"Choose a file");
    startActivityForResult(choose,1);
    }
    public void onContactIconClick(View view){
     Intent con=new Intent(Intent.ACTION_PICK,ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
     startActivityForResult(con,1);
    }

    public void onSendClick(View view) {
    String to,subject,bodyText;
    to=text1.getText().toString();
    subject=text2.getText().toString();
    bodyText=text3.getText().toString();
    closeDrawer(drawerMenuForScreen);
    finish();

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, bodyText);
        //need this to prompts email client only
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, MainActivity.accountemail));
    }

    public void onCancelClick(View view){
     finish();
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

}