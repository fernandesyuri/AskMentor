package com.yurifernandes.askmentor;

import android.util.Log;

import com.amazonaws.amplify.generated.graphql.SubscribeToNewMessageSubscription;
import com.amazonaws.amplify.generated.graphql.SubscribeToNewQuestionSubscription;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.Observable;

import javax.annotation.Nonnull;

public class MessagesSubscription extends Observable {

    private AppSyncSubscriptionCall subscriptionWatcher;
    private String myUser;

    public MessagesSubscription(String conversationId, AWSAppSyncClient mAWSAppSyncClient) {

        myUser = AWSMobileClient.getInstance().getUsername();

        SubscribeToNewMessageSubscription subscription = SubscribeToNewMessageSubscription.builder()
                .conversationId(conversationId)
                .build();

        Log.v("###MSG Subscription", "Trying to subscribe to conversation id " + conversationId);

        subscriptionWatcher = mAWSAppSyncClient.subscribe(subscription);
        subscriptionWatcher.execute(subCallback);
    }

    private AppSyncSubscriptionCall.Callback subCallback = new AppSyncSubscriptionCall.Callback() {
        @Override
        public void onResponse(@Nonnull Response response) {
            Log.i("###MSG Subscription", response.data().toString());
            setChanged();
            notifyObservers(response.data().toString());
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("###MSG Subscription", e.toString() + "\n" + e.getCause().toString());
        }

        @Override
        public void onCompleted() {
            Log.i("###MSG Subscription", "Subscription completed");
        }
    };

}
