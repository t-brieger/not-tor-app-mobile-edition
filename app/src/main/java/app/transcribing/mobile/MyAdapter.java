package app.transcribing.mobile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import app.transcribing.mobile.LazyLoading.ImageLoader;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<DataModel> dataModelList;
    private Context mContext;

    // View holder class whose objects represent each list item

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView cardImageView;
        private TextView titleTextView;
        private TextView subTitleTextView;
        private MaterialButton TranscribeButton;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.card_title);
            subTitleTextView = itemView.findViewById(R.id.card_subtitle);
            TranscribeButton = itemView.findViewById(R.id.action_button_1);
        }

        void bindData(DataModel dataModel, Context context) {
            if (ImageLoader.isSupportedImage(dataModel.getUrl())) {
                ImageLoader.instance.DisplayImage(dataModel.getUrl(), cardImageView);
                titleTextView.setText(dataModel.getTitle());
                subTitleTextView.setText(dataModel.getSubTitle());
            } else {
                Log.w(this.getClass().getCanonicalName(), "unsupported image type: " + dataModel.getUrl());
                cardImageView.setVisibility(View.GONE);
                titleTextView.setVisibility(View.GONE);
                subTitleTextView.setVisibility(View.GONE);
                TranscribeButton.setVisibility(View.GONE);
            }
        }
    }

    public MyAdapter(List<DataModel> modelList, Context context) {
        dataModelList = modelList;
        mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate out card list item

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_img_sub_btn, parent, false);
        // Return a new view holder

        MyViewHolder mvh = new MyViewHolder(view);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Bind data for the item at position

        holder.bindData(dataModelList.get(position), mContext);
    }

    @Override
    public int getItemCount() {
        // Return the total number of items

        return dataModelList.size();
    }
}
