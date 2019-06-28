package com.yurifernandes.askmentor;

import android.util.Log;

import com.amazonaws.amplify.generated.graphql.SubscribeToNewUCsSubscription;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.Observable;

import javax.annotation.Nonnull;

public class UCSubscription extends Observable {

    private AppSyncSubscriptionCall subscriptionWatcher;
    private String myUser;

    public UCSubscription(AWSAppSyncClient mAWSAppSyncClient) {

        myUser = AWSMobileClient.getInstance().getUsername();

        SubscribeToNewUCsSubscription subscription = SubscribeToNewUCsSubscription.builder()
                .userId(UserData.getInstance().id)
                .build();
        subscriptionWatcher = mAWSAppSyncClient.subscribe(subscription);
        subscriptionWatcher.execute(subCallback);
    }

    private AppSyncSubscriptionCall.Callback subCallback = new AppSyncSubscriptionCall.Callback() {
        @Override
        public void onResponse(@Nonnull Response response) {
            Log.v("###UC Subscription", response.data().toString());

            String[] split = response.data().toString().split(", conversationId=");
            String conversationId = split[1].split(", userId=")[0];

            setChanged();
            notifyObservers(conversationId);

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
