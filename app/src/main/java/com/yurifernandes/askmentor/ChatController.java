package com.yurifernandes.askmentor;

import android.util.Log;

import com.amazonaws.amplify.generated.graphql.CreateConversationMutation;
import com.amazonaws.amplify.generated.graphql.CreateQuestionMutation;
import com.amazonaws.amplify.generated.graphql.CreateUserConversationsMutation;
import com.amazonaws.amplify.generated.graphql.SubscribeToNewMessageSubscription;
import com.amazonaws.amplify.generated.graphql.SubscribeToNewQuestionSubscription;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.sql.Timestamp;

import javax.annotation.Nonnull;

public class ChatController {

    private AWSAppSyncClient mAWSAppSyncClient;
    private AppSyncSubscriptionCall subscriptionWatcher;
    private String myUserId;
    private String anotherUserId;

    public ChatController(String userId, AWSAppSyncClient mAWSAppSyncClient) {

        myUserId = AWSMobileClient.getInstance().getIdentityId();
        this.mAWSAppSyncClient = mAWSAppSyncClient;
    }

    public void createConversationWith(String userId) {

        if (!userId.equals(myUserId)) {
            CreateConversationMutation createConversationMutation = CreateConversationMutation.builder()
                    .createdAt(new Timestamp(System.currentTimeMillis()).toString())
                    .name(new Timestamp(System.currentTimeMillis()).toString())
                    .build();

            this.anotherUserId = userId;
            mAWSAppSyncClient.mutate(createConversationMutation).enqueue(conversationMutationCallback);
        }
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

                CreateUserConversationsMutation createUserConversationsMutation = CreateUserConversationsMutation.builder()
                        .conversationId(response.data().createConversation().id())
                        .userId(anotherUserId)
                        .build();

                mAWSAppSyncClient.mutate(createUserConversationsMutation);

                createUserConversationsMutation = CreateUserConversationsMutation.builder()
                        .conversationId(response.data().createConversation().id())
                        .userId(myUserId)
                        .build();

                mAWSAppSyncClient.mutate(createUserConversationsMutation);
            }
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("###CONV MUTATION", e.toString());
        }
    };
}
