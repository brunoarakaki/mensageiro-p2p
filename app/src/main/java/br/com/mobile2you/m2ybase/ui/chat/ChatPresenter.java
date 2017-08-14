package br.com.mobile2you.m2ybase.ui.chat;

import java.util.ArrayList;
import java.util.List;

import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;
import br.com.mobile2you.m2ybase.ui.base.BasePresenter;
import rx.Subscription;

/**
 * Created by Bruno on 11-Aug-17.
 */

public class ChatPresenter extends BasePresenter<ChatMvpView> {

    private ChatMvpView mChatMvpView;
    private List<MessageResponse> mCachedMessages;
    private Subscription mSubscription;

    public ChatPresenter(){

    }

    public void dummyData(){
        List<MessageResponse> messages = new ArrayList<>();
        messages.add(new MessageResponse(2, "I AM BATMAN", 0));
        messages.add(new MessageResponse(0, "I speak for the rest of the board and Mr Wayne, in expressing our own excitement.", 0));
        messages.add(new MessageResponse(2, "When a forest grows too wild, a purging fire is inevitable and natural. Tomorrow the world will watch in horror as its greatest city destroys itself. The movement back to harmony will be unstoppable this time.", 0));
        messages.add(new MessageResponse(2, "Let me get this straight. You think that your client, one of the wealthiest, most powerful men in the world is secretly a vigilante who spends his nights beating criminals to a pulp with his bare hands and your plan is to blackmail this person? Good luck.N", 0));
        messages.add(new MessageResponse(0, "Look around you. You'll see two councilmen, a union official, couple off-duty cops and a judge. I wouldn't have a second's hesitation of blowing your head off in front of them. Now, that's power you can't buy. That's the power of fear.", 0));
        messages.add(new MessageResponse(2, "Let me get this straight. You think that your client, one of the wealthiest, most powerful men in the world is secretly a vigilante who spends his nights beating criminals to a pulp with his bare hands and your plan is to blackmail this person? Good luck.", 0));
        messages.add(new MessageResponse(0, "You want order in Gotham. Batman must take off his mask and turn himself in. Oh, and every day he doesn't, people will die. Starting tonight. I'm a man of my word.", 0));
        messages.add(new MessageResponse(0, "Pretty soon we will be chasing down over due library books.", 0));
        messages.add(new MessageResponse(2, "Breathe in your fears. Face them. To conquer fear, you must become fear. You must bask in the fear of other men. And men fear most what they cannot see. You have to become a terrible thought. A wraith. You have to become an idea! Feel terror cloud your senses. Feel its power to distort. To control. And know that this power can be yours. Embrace your worst fear. Become one with the darkness.", 0));
        mChatMvpView.showMessages(messages);
    }

    public void sendMessages() {
        List<MessageResponse> messages = new ArrayList<>();
        messages.add(new MessageResponse(2, "I AM BATMAN", 0));
        messages.add(new MessageResponse(0, "I speak for the rest of the board and Mr Wayne, in expressing our own excitement.", 0));
        messages.add(new MessageResponse(2, "When a forest grows too wild, a purging fire is inevitable and natural. Tomorrow the world will watch in horror as its greatest city destroys itself. The movement back to harmony will be unstoppable this time.", 0));
        messages.add(new MessageResponse(2, "Let me get this straight. You think that your client, one of the wealthiest, most powerful men in the world is secretly a vigilante who spends his nights beating criminals to a pulp with his bare hands and your plan is to blackmail this person? Good luck.N", 0));
        messages.add(new MessageResponse(0, "Look around you. You'll see two councilmen, a union official, couple off-duty cops and a judge. I wouldn't have a second's hesitation of blowing your head off in front of them. Now, that's power you can't buy. That's the power of fear.", 0));
        messages.add(new MessageResponse(2, "Let me get this straight. You think that your client, one of the wealthiest, most powerful men in the world is secretly a vigilante who spends his nights beating criminals to a pulp with his bare hands and your plan is to blackmail this person? Good luck.", 0));
        messages.add(new MessageResponse(0, "You want order in Gotham. Batman must take off his mask and turn himself in. Oh, and every day he doesn't, people will die. Starting tonight. I'm a man of my word.", 0));
        messages.add(new MessageResponse(0, "Pretty soon we will be chasing down over due library books.", 0));
        messages.add(new MessageResponse(2, "Breathe in your fears. Face them. To conquer fear, you must become fear. You must bask in the fear of other men. And men fear most what they cannot see. You have to become a terrible thought. A wraith. You have to become an idea! Feel terror cloud your senses. Feel its power to distort. To control. And know that this power can be yours. Embrace your worst fear. Become one with the darkness.", 0));

        for (MessageResponse message : messages){
            sendMessage(message);
        }
    }

    public void loadMessages(int user_id, int sender_id){
//        mChatMvpView.showMessages(messages);
        mChatMvpView.loadContactMessages(user_id, sender_id);
    }

    public void sendMessage(MessageResponse messageResponse){
        mChatMvpView.addMessage(messageResponse);
        long id = mChatMvpView.saveMessage(messageResponse);
    }

    @Override
    public void attachView(ChatMvpView mvpView) {
        super.attachView(mvpView);
        mChatMvpView = mvpView;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }
}
