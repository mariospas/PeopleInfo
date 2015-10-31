package com.code.hypermario.peopleinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.code.hypermario.peopleinfo.R;
import com.code.hypermario.peopleinfo.recyclerview.FeedListActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;


public class MainActivity extends Activity {

    Button btnStartLocationUpdates;
    private TextView info;
    private TextView search_info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    String user_id="dead";
    Intent localIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        btnStartLocationUpdates = (Button)findViewById(R.id.start);

        callbackManager = CallbackManager.Factory.create();
        info = (TextView)findViewById(R.id.info);
        search_info = (TextView)findViewById(R.id.search_info);
        loginButton = (LoginButton)findViewById(R.id.fb_login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
                user_id = new String(loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email",
                "user_about_me", "user_actions.books", "user_birthday", "user_photos"));




        getUserInfo();


//10206972653330586
        String app_id = getString(R.string.facebook_app_id);
        String secret_pass = getString(R.string.secret_pass);
        String access = getString(R.string.access);

        //AccessToken accessToken = new DefaultFacebookClient().obtainAppAccessToken(app_id, secret_pass);
        final AccessToken token = new AccessToken(access,app_id,user_id,null,null,null,null,null);
        localIntent = new Intent(this,FeedListActivity.class);

        this.btnStartLocationUpdates.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {


                GraphRequest request = GraphRequest.newGraphPathRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/search",
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                //FeedListActivity feed = new FeedListActivity(response);
                                //feed.feedlistPresent();

                                writeIDandName(response);

                                startActivity(localIntent);
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("q", "panos tsavalas");
                parameters.putString("type", "user");
                request.setParameters(parameters);
                request.executeAsync();


                /*GraphRequest req = new GraphRequest(AccessToken.getCurrentAccessToken(), "/search?q=coffee&type=place", null, HttpMethod.GET,
                //GraphRequest req = new GraphRequest(AccessToken.getCurrentAccessToken(), "/oauth/access_token?client_id=873854049349423&client_secret=14fe5483912cb054d9988e54a2e812f7&grant_type=client_credentials", null, HttpMethod.GET,
                new GraphRequest.Callback()
                        {
                            @Override
                        public void onCompleted(GraphResponse response)
                            {
                                System.out.println("**** "+response.toString());
                                try {
                                    JSONObject jso = response.getJSONObject();
                                    JSONArray arr = jso.getJSONArray("data");

                                    for (int i = 0; i < (arr.length()); i++) {
                                        JSONObject json_obj = arr.getJSONObject(i);

                                        String id = json_obj.getString("uid");
                                        String name = json_obj.getString("name");
                                        String urlImg = json_obj.getString("pic_square");

                                        System.out.println("**** ID = " + id + " name = " + name + " Url Image = " + urlImg);

                                    }
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }
                            }
                        });
                GraphRequest.executeBatchAsync(req);*/








                /*new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                "/search?"+
                "q=panos%20tsavalas&"+
                "type=user",
                        "/me?fields=albums.limit(5),posts.limit(5)",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    JSONObject jso = response.getJSONObject();
                                    JSONArray arr = jso.getJSONArray("data");

                                    for (int i = 0; i < (arr.length()); i++) {
                                        JSONObject json_obj = arr.getJSONObject(i);

                                        String id = json_obj.getString("uid");
                                        String name = json_obj.getString("name");
                                        String urlImg = json_obj.getString("pic_square");

                                        System.out.println("**** ID = " + id + " name = " + name + " Url Image = " + urlImg);

                                    }
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }
                            }
                        }
                ).executeAsync();*/
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void getUserInfo()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Key Hash : - ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e){}
        catch (NoSuchAlgorithmException e){}
    }


    public void writeIDandName(GraphResponse response)
    {
        String str2 = null;
        File localFile = new File(getFilesDir(), "idandname.txt");
        if (!localFile.exists()) {
            try {
                localFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        System.out.println("**** "+response.toString());
        try {
            JSONObject jso = response.getJSONObject();
            JSONArray arr = jso.getJSONArray("data");

            for (int i = 0; i < (arr.length()); i++) {
                JSONObject json_obj = arr.getJSONObject(i);

                String id = json_obj.getString("id");
                String name = json_obj.getString("name");

                str2 = str2 + new String(id+";"+name+"\n");
                System.out.println("**** ID = " + id + " name = " + name);

            }
        } catch (Throwable t) {
            t.printStackTrace();
        }


        try{
            FileOutputStream localFileOutputStream = openFileOutput("idandname.txt", Context.MODE_PRIVATE);
            localFileOutputStream.write(str2.getBytes());
            localFileOutputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
