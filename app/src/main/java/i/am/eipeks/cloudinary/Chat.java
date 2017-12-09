package i.am.eipeks.cloudinary;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.cloudinary.android.MediaManager;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class Chat extends AppCompatActivity implements View.OnClickListener {

    private RequestParams requestParams;
    private Pusher pusher;
    private PusherOptions options;
    private Channel channel;
    private EditText typedMessage;
    private ImageButton sendMessage, loadImage;
    private ListView messagesList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        MediaManager.init(this);

        typedMessage = findViewById(R.id.typed_message);
        sendMessage = findViewById(R.id.send);
        loadImage = findViewById(R.id.load_image);
        messagesList = findViewById(R.id.messages);

        final List<Message> messages = new ArrayList<>();
        messagesList.setAdapter(new MessageAdapter(this, messages));

        loadImage.setOnClickListener(this);
        sendMessage.setOnClickListener(this);
        typedMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)){
                    sendMessage.setEnabled(false);
                } else {
                    sendMessage.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        requestParams = new RequestParams();
        options = new PusherOptions();
        options.setCluster(Constants.PUSHER_CLUSTER_TYPE);
        pusher = new Pusher(Constants.PUSHER_KEY, options);
        channel = pusher.subscribe("messages");
        channel.bind("new-message", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                Gson gson = new Gson();
                Message message = gson.fromJson(data, Message.class);
                messages.add(message);
                messagesList.setSelection(messagesList.getAdapter().getCount() - 1);
            }
        });
        pusher.connect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                String message = typedMessage.getText().toString();
                requestParams.put("message", message);
                requestParams.put("user", "Eipeks");

                AsyncHttpClient client = new AsyncHttpClient();
                client.post(Constants.MESSAGE_ENDPOINT, requestParams, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        
                    }
                });
                break;
            case R.id.load_image:
                break;
        }
    }

    private void selectImage(){
        final CharSequence[] charSequence = {"Take Photo", "Choose from gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(charSequence, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }

}
