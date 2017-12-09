package i.am.eipeks.cloudinary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class MessageAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages){
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(Message message){
        messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.message_layout, parent, false);
        }

        TextView messageContent = convertView.findViewById(R.id.message_content);
        TextView timeStamp = convertView.findViewById(R.id.time_stamp);
        ImageView imageSent = convertView.findViewById(R.id.image_sent);

        Message message = messages.get(position);

        if (message.getMessageType().equals(Constants.IMAGE)){
            imageSent.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(message.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(imageSent);
        } else {
            imageSent.setVisibility(View.GONE);
            messageContent.setVisibility(View.VISIBLE);
            messageContent.setText(message.getMessageContent());
        }
        timeStamp.setText(message.getMessageTime());
        return convertView;
    }
}
