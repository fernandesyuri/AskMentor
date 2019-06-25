package com.yurifernandes.askmentor.ChatFolder;

import java.util.ArrayList;

public class ChatPresenter implements ChatContract.Presenter {

    private ArrayList<ChatObject> chatObjects;
    private ChatContract.View view;

    public ChatPresenter() {
        // Create the ArrayList for the chat objects
        this.chatObjects = new ArrayList<>();

        // Add an initial greeting message
        ChatResponse greetingMsg = new ChatResponse();
        greetingMsg.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        chatObjects.add(greetingMsg);

        ChatInput teste = new ChatInput();
        teste.setText("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.");
        chatObjects.add(teste);
    }

    @Override
    public void attachView(ChatContract.View view) {
        this.view = view;
    }

    @Override
    public ArrayList<ChatObject> getChatObjects() {
        return this.chatObjects;
    }

    @Override
    public void onEditTextActionDone(String inputText) {
        // Create new input object
        ChatInput inputObject = new ChatInput();
        inputObject.setText(inputText);

        // Add it to the list and tell the adapter we added something
        this.chatObjects.add(inputObject);
        view.notifyAdapterObjectAdded(chatObjects.size() - 1);

        // Also scroll down if we aren't at the bottom already
        view.scrollChatDown();
    }
}
