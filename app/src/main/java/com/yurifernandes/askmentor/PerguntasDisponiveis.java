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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.AllQuestionQuery;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.amazonaws.mobileconnectors.appsync.sigv4.CognitoUserPoolsAuthProvider;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.Nonnull;

public class PerguntasDisponiveis extends AppCompatActivity implements Observer, NavigationView.OnNavigationItemSelectedListener {

    private AWSAppSyncClient mAWSAppSyncClient;
    private PerguntasSubscription perguntasSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perguntas_disponiveis);

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

        //Navigation Drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Perguntas Disponíveis");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        perguntasSubscription = new PerguntasSubscription(mAWSAppSyncClient); // Escutador de novas perguntas
        perguntasSubscription.addObserver(this);

        query();

        //List View
        /*
        List<String> teste = new ArrayList<>();
        teste.add("Pergunta1");
        teste.add("Pergunta2");
        teste.add("Pergunta3");
        ListView listaTeste = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, teste);
        listaTeste.setAdapter(adapter);
        */
    }

    public void query() {
        mAWSAppSyncClient.query(AllQuestionQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(allQuestionCallback);
    }

    private GraphQLCall.Callback<AllQuestionQuery.Data> allQuestionCallback = new GraphQLCall.Callback<AllQuestionQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<AllQuestionQuery.Data> response) {

            if (!response.hasErrors()) {
                List<String> questionList = new ArrayList<>();
                List<String> questionSenderList = new ArrayList<>();

                for (AllQuestionQuery.AllQuestion question : response.data().allQuestion()) { // Para todas as perguntas
                    if (!question.sender().equals(UserData.getInstance().id)) { // Se a pergunta não for de autoria própria
                        questionList.add(question.content()); // Adiciona na lista de perguntas disponíveis
                        questionSenderList.add(question.sender());
                    }
                }

                if (!questionList.isEmpty()) {
                    updateQuestionList(questionList, questionSenderList);
                }

            } else {
                for (Error error : response.errors()) {
                    Log.e("###Response", error.message());
                }
            }
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("###ERROR", e.toString());
        }
    };

    private void updateQuestionList(final List<String> questionList, final List<String> questionSenderList) {

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, questionList);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView questionsListView = (ListView) findViewById(R.id.listView);
                questionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PerguntasDisponiveis.this);
                        builder.setMessage(questionList.get(i));
                        builder.setPositiveButton("Responder", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                //Toast.makeText(PerguntasDisponiveis.this, "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                                ChatController chatController = ChatController.getInstance();
                                chatController.setmAWSAppSyncClient(mAWSAppSyncClient);
                                chatController.startConversationWith(questionSenderList.get(i));
                                Intent i = new Intent(PerguntasDisponiveis.this, ChatActivity.class); //??
                                startActivity(i);
                                PerguntasDisponiveis.this.finish();
                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                //Toast.makeText(PerguntasDisponiveis.this, "negativo=" + arg1, Toast.LENGTH_SHORT).show();
                            }
                        });
                        AlertDialog alerta = builder.create();
                        alerta.show();
                    }
                });
                questionsListView.setAdapter(adapter);
            }
        });


    }

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
            Intent i = new Intent(this, Login.class);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        perguntasSubscription = null;
        this.finish();
        return true;
    }

    @Override
    public void update(Observable o, Object arg) {
        query();
    }
}