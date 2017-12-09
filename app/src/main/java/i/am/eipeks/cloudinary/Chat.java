package i.am.eipeks.cloudinary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Chat extends AppCompatActivity implements View.OnClickListener {

    private RequestParams requestParams;
    private Pusher pusher;
    private PusherOptions options;
    private Channel channel;
    private EditText typedMessage;
    private ImageButton sendMessage, loadImage;
    private ListView messagesList;
    private Upload upload;
    private Message message;
    private boolean hasUploadedPicture = false;
    private String imagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        MediaManager.init(this);
        upload = RetrofitUtils.getRetrofit().create(Upload.class);

        typedMessage = findViewById(R.id.typed_message);
        sendMessage = findViewById(R.id.send);
        loadImage = findViewById(R.id.load_image);
        messagesList = findViewById(R.id.messages);

        if (TextUtils.isEmpty(typedMessage.getText())){
            sendMessage.setEnabled(false);
        } else {
            sendMessage.setEnabled(true);
        }

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
                final Message message = gson.fromJson(data, Message.class);
                message.setMessageType(Constants.TEXT);
                messages.add(message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Chat.this, message.getMessageContent(), Toast.LENGTH_SHORT).show();
                        messagesList.setSelection(messagesList.getAdapter().getCount() - 1);
                    }
                });
            }
        });
        pusher.connect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send:
                if (hasUploadedPicture){
                    upload.picture(typedMessage.getText().toString(), "Eipeks", imagePath)
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                    switch (response.code()){
                                        case 200:

                                            break;
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                                }
                            });
                } else {
                    upload.message(typedMessage.getText().toString(), "Eipeks").enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            switch (response.code()){
                                case 200:
                                    typedMessage.setText("");
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(Chat.this, "Error uploading message", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.load_image:
                Intent  chooseImage = new Intent();
                chooseImage.setType("image/*");
                chooseImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(chooseImage, "Select Picture"), Constants.IMAGE_CHOOSER_INTENT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IMAGE_CHOOSER_INTENT && resultCode == RESULT_OK){
            if (data != null && data.getData() != null){
                Uri uri = data.getData();
                hasUploadedPicture = true;
                imagePath = getRealPathFromURI(uri);
            }
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

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
