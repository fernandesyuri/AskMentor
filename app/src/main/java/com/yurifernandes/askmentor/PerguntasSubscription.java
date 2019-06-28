package com.yurifernandes.askmentor;

import android.util.Log;

import com.amazonaws.amplify.generated.graphql.SubscribeToNewQuestionSubscription;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

public class PerguntasSubscription {

    private AppSyncSubscriptionCall subscriptionWatcher;
    private String myUser;

    public PerguntasSubscription(AWSAppSyncClient mAWSAppSyncClient) {

        myUser = AWSMobileClient.getInstance().getUsername();

        SubscribeToNewQuestionSubscription subscription = SubscribeToNewQuestionSubscription.builder().build();
        subscriptionWatcher = mAWSAppSyncClient.subscribe(subscription);
        subscriptionWatcher.execute(subCallback);
    }

    private AppSyncSubscriptionCall.Callback subCallback = new AppSyncSubscriptionCall.Callback() {
        @Override
        public void onResponse(@Nonnull Response response) {
            Log.i("###Subscription", response.data().toString());
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("###Subscription", e.toString() + "\n" + e.getCause().toString());
        }

        @Override
        public void onCompleted() {
            Log.i("###Subscription", "Subscription completed");
        }
    };

}
