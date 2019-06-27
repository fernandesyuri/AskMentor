package com.yurifernandes.askmentor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.AllUserQuery;
import com.amazonaws.amplify.generated.graphql.CreateQuestionMutation;
import com.amazonaws.amplify.generated.graphql.SubscribeToNewQuestionSubscription;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private AppSyncSubscriptionCall subscriptionWatcher;
    private AWSAppSyncClient mAWSAppSyncClient;
    private TextView name, email;
    private ImageButton camera;
    private EditText etQuestion;
    private Button btnQuestion;
    final Map<String, String> attributes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        etQuestion = findViewById(R.id.editTextQuestion);
        btnQuestion = findViewById(R.id.buttonQuestion);
        btnQuestion.setOnClickListener(this);

        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .cognitoUserPoolsAuthProvider(new CognitoUserPoolsAuthProvider() {
                    @Override
                    public String getLatestAuthToken() {
                        try {
                            return AWSMobileClient.getInstance().getTokens().getIdToken().getTokenString();
                        } catch (Exception e) {
                            Log.e("APPSYNC_ERROR", e.getLocalizedMessage());
                            return e.getLocalizedMessage();
                        }
                    }
                }).build();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Início");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        camera = (ImageButton) findViewById(R.id.imageButton);
        camera.setOnClickListener(this);

        AWSMobileClient.getInstance().getUserAttributes(new Callback<Map<String, String>>() {
            @Override
            public void onResult(Map<String, String> result) {
                name = (TextView) findViewById(R.id.navName);
                name.setText(result.get("name"));

                email = (TextView) findViewById(R.id.navEmail);
                email.setText(result.get("email"));
            }

            @Override
            public void onError(Exception e) {
            }
        });

        SubscribeToNewQuestionSubscription subscription = SubscribeToNewQuestionSubscription.builder().build();
        subscriptionWatcher = mAWSAppSyncClient.subscribe(subscription);
        subscriptionWatcher.execute(subCallback);

        // query();
    }

    private AppSyncSubscriptionCall.Callback subCallback = new AppSyncSubscriptionCall.Callback() {
        @Override
        public void onResponse(@Nonnull Response response) {
            Log.i("###Response", response.data().toString());
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("###Error", e.toString());
        }

        @Override
        public void onCompleted() {
            Log.i("###Completed", "Subscription completed");
        }
    };

    public void query() {
        mAWSAppSyncClient.query(AllUserQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(allUserCallback);
    }

    private GraphQLCall.Callback<AllUserQuery.Data> allUserCallback = new GraphQLCall.Callback<AllUserQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<AllUserQuery.Data> response) {
            for (AllUserQuery.AllUser user : response.data().allUser()) {
                Log.i("###Online", user.username());
            }
            //Log.i("###Results", response.data().allUser());
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("###ERROR", e.toString());
        }
    };


    public void mutation() {

        mAWSAppSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .cognitoUserPoolsAuthProvider(new CognitoUserPoolsAuthProvider() {
                    @Override
                    public String getLatestAuthToken() {
                        try {
                            return AWSMobileClient.getInstance().getTokens().getIdToken().getTokenString();
                        } catch (Exception e) {
                            Log.e("APPSYNC_ERROR", e.getLocalizedMessage());
                            return e.getLocalizedMessage();
                        }
                    }
                }).build();

        CreateQuestionMutation createQuestionMutation = CreateQuestionMutation.builder()
                .content(etQuestion.getText().toString())
                .createdAt(new Timestamp(System.currentTimeMillis()).toString())
                .sender(AWSMobileClient.getInstance().getUsername())
                .build();

        mAWSAppSyncClient.mutate(createQuestionMutation).enqueue(mutationCallback);
    }

    private GraphQLCall.Callback<CreateQuestionMutation.Data> mutationCallback = new GraphQLCall.Callback<CreateQuestionMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateQuestionMutation.Data> response) {
            if (response.hasErrors()) {
                for (Error error : response.errors()) {
                    Log.i("###ERROR", error.message());
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                        builder.setMessage("Perguntada enviada com sucesso");
                        AlertDialog alerta = builder.create();
                        alerta.show();
                    }
                });
                Log.i("###Results", "Added Question");
            }
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("###Error", e.toString());
        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent i = new Intent(this, Home.class);
            startActivity(i);
        } else if (id == R.id.nav_questions) {
            Intent i = new Intent(this, Perguntas.class);
            startActivity(i);
        } else if (id == R.id.nav_answers) {
            Intent i = new Intent(this, Respostas.class);
            startActivity(i);
        } else if (id == R.id.nav_available_questions) {
            Intent i = new Intent(this, PerguntasDisponiveis.class);
            startActivity(i);
        } else if (id == R.id.nav_account) {
            Intent i = new Intent(this, Conta.class);
            startActivity(i);
        } else if (id == R.id.nav_exit) {
            AWSMobileClient.getInstance().signOut();
            Intent i = new Intent(this, Login.class);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btnQuestion) {
            Log.v("###BtnPress", "btnQuestion");
            mutation();
        }
        if (v == camera) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
        }
    }
}
