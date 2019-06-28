package com.yurifernandes.askmentor;

import android.content.Intent;
import android.util.Log;

import com.amazonaws.amplify.generated.graphql.CreateConversationMutation;
import com.amazonaws.amplify.generated.graphql.CreateMessageMutation;
import com.amazonaws.amplify.generated.graphql.CreateUserConversationsMutation;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.sql.Timestamp;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.Nonnull;

public class ChatController extends Observable implements Observer {

    private static ChatController instance;
    private AWSAppSyncClient mAWSAppSyncClient;
    private AppSyncSubscriptionCall subscriptionWatcher;
    private String anotherUserId;
    public String anotherUserRealName;
    private String conversationId;
    private CreateConversationMutation auxCreateConversationMutation;
    private MessagesSubscription messagesSubscription;

    private ChatController() {
    }

    public static synchronized ChatController getInstance() {
        if (instance == null) {
            instance = new ChatController();
        }
        return instance;
    }

    public void setmAWSAppSyncClient(AWSAppSyncClient mAWSAppSyncClient) {
        this.mAWSAppSyncClient = mAWSAppSyncClient;
    }

    public void startConversationWith(String userId) {

        if (!userId.equals(UserData.getInstance().id)) {
            CreateConversationMutation createConversationMutation = CreateConversationMutation.builder()
                    .createdAt(new Timestamp(System.currentTimeMillis()).toString())
                    .name(new Timestamp(System.currentTimeMillis()).toString())
                    .build();

            this.anotherUserId = userId;
            mAWSAppSyncClient.mutate(createConversationMutation).enqueue(conversationMutationCallback);
        }
    }

    public void joinConversation(String conversationId) {
        messagesSubscription = new MessagesSubscription(conversationId, mAWSAppSyncClient);
        messagesSubscription.addObserver(ChatController.this); //??
    }

    private GraphQLCall.Callback<CreateConversationMutation.Data> conversationMutationCallback = new GraphQLCall.Callback<CreateConversationMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateConversationMutation.Data> response) {
            if (response.hasErrors()) {
                for (Error error : response.errors()) {
                    Log.e("###CONV MUTATION", error.message());
                }
            } else {
                Log.i("###CONV MUTATION", "Added Conversation");

                conversationId = response.data().createConversation().id();

                CreateUserConversationsMutation createUserConversationsMutation = CreateUserConversationsMutation.builder()
                        .conversationId(conversationId)
                        .userId(anotherUserId)
                        .build();

                mAWSAppSyncClient.mutate(createUserConversationsMutation).enqueue(createUserConversationsMutationCallback);

                createUserConversationsMutation = CreateUserConversationsMutation.builder()
                        .conversationId(conversationId)
                        .userId(UserData.getInstance().id)
                        .build();

                mAWSAppSyncClient.mutate(createUserConversationsMutation).enqueue(createUserConversationsMutationCallback);

                messagesSubscription = new MessagesSubscription(conversationId, mAWSAppSyncClient);
                messagesSubscription.addObserver(ChatController.this); //??
            }
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("###CONV MUTATION", e.toString());
        }
    };

    private GraphQLCall.Callback<CreateUserConversationsMutation.Data> createUserConversationsMutationCallback = new GraphQLCall.Callback<CreateUserConversationsMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateUserConversationsMutation.Data> response) {
            if (response.hasErrors()) {
                for (Error error : response.errors()) {
                    Log.e("###UCONV MUTATION", error.message());
                }
            } else {
                Log.i("###UCONV MUTATION", "Added User Conversation");
            }
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("###UCONV MUTATION", e.toString());
        }
    };

    public void enviarMensagem(String mensagem) {
        if (!mensagem.isEmpty()) {
            CreateMessageMutation createMessageMutation = CreateMessageMutation.builder()
                    .conversationId(conversationId)
                    .createdAt(new Timestamp(System.currentTimeMillis()).toString())
                    .content(mensagem)
                    .build();

            Log.v("###MSG MUTATION", "Trying to mutate message to conversation id " + conversationId);

            mAWSAppSyncClient.mutate(createMessageMutation).enqueue(enviarMsgCallback);
        }
    }

    private GraphQLCall.Callback<CreateMessageMutation.Data> enviarMsgCallback = new GraphQLCall.Callback<CreateMessageMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateMessageMutation.Data> response) {
            if (response.hasErrors()) {
                for (Error error : response.errors()) {
                    Log.e("###MSG MUTATION", error.message());
                }
            } else {
                Log.i("###MSG MUTATION", "Added Message");
            }
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("###MSG MUTATION", e.toString());
        }
    };

    @Override
    public void update(Observable o, Object arg) {

        String[] split = ((String) arg).split(", content=");
        String content = split[1].split(", conversationId=")[0];

        split = ((String) arg).split(", sender=");
        String senderId = split[1].substring(0, split[1].length() - 2);
        //String senderId = split[1].split("}}")[0];

        if (!senderId.equals(UserData.getInstance().id)) {
            Log.i("###", "Msg received -> " + content);
            Log.i("###", "Sender -> " + senderId);
            setChanged();
            notifyObservers(content);
        } else {
            Log.v("###", "Msg sent -> " + content);
        }
    }
}
