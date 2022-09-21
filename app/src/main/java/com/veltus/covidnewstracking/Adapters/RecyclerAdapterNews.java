package com.veltus.covidnewstracking.Adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.veltus.covidnewstracking.Activities.MainActivity;
import com.veltus.covidnewstracking.ObjectClass.NewsArticle;
import com.veltus.covidnewstracking.R;
import com.veltus.covidnewstracking.SharedPreferences.UsersSharedPreferences;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RecyclerAdapterNews extends RecyclerView.Adapter<RecyclerAdapterNews.MyViewHolder> {
    public String website;
    private List<NewsArticle> newsList;
    private OnItemClickListener mOnItemClickListener;


    public RecyclerAdapterNews(List<NewsArticle> newsList, OnItemClickListener onItemClickListener) {
        this.newsList = newsList;
        this.mOnItemClickListener = onItemClickListener;
    }

    /* Search the favoritesList for matching articles and return the index of the position */
    public static int searchListForFav(String title, String author) {
        for (int i = 0; i < MainActivity.getFavoritesList().size(); i++) {
            if (MainActivity.getFavoritesList().get(i).getTitle().equalsIgnoreCase(title) && MainActivity.getFavoritesList().get(i).getAuthor().equalsIgnoreCase(author)) {
                return i;
            }
        }
        /* If no articles are found return the index value as -1 */
        return -1;
    }

    @NonNull
    @Override
    public RecyclerAdapterNews.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_news, parent, false);
        return new MyViewHolder(view, mOnItemClickListener);


    }

    /* Add all method that adds the article to the ArrayList */
    public void addAll(List<NewsArticle> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    /* Clear method that clears all items in the ArrayList */
    public void clear() {
        this.newsList.clear();
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterNews.MyViewHolder holder, int position) {
        /* Get the position and save it to article*/
        NewsArticle article = newsList.get(position);

        /* Search the favoritesList for any items with the same title and author */
        int i = searchListForFav(article.getTitle(), article.getAuthor());

        /* If a matching item is not found set the bookmark toggle button to false */
        if (i == -1) {
            holder.button.setChecked(false);

        } else {
            /* If a matching item is found set the bookmark toggle button to true */
            holder.button.setChecked(true);
        }
        /* If the article has a null image hide the ImageView */
        if (article.getImage() == null) {
            holder.articleImage.setVisibility(View.GONE);

        } else {
            /* If the article has an image set the bitmap to the ImageView */
            holder.articleImage.setImageBitmap(article.getImage());
        }

        /* Find the article position values and set them to the corresponding view holder */
        website = newsList.get(position).getWebsite();
        holder.newsArticle = article;
        holder.i = i;
        String title = newsList.get(position).getTitle();
        holder.articleTitle.setText(title);
        String author = newsList.get(position).getAuthor();
        holder.articleAuthor.setText(author + " ");

        /* Convert String date from ISO_OFFSET_DATE_TIME to an OffsetDateTime object */
        String date = newsList.get(position).getDate();
        OffsetDateTime ldt = OffsetDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String convertedDate = ldt.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy h:mm a"));

        /* Set the converted date to the corresponding view holder */
        holder.articleDate.setText(convertedDate);


    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    /* OnItemClickListener for opening selected article in browser */
    public interface OnItemClickListener {
        void onItemClick(NewsArticle position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnItemClickListener onItemClickListener;
        private ImageView articleImage;
        private TextView articleTitle;
        private TextView articleAuthor;
        private TextView articleDate;
        private ToggleButton button;
        private int i;
        private NewsArticle newsArticle;


        public MyViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            /* Find and link views in the list_view_news_feed.xml to the values in MyViewHolder */
            articleImage = itemView.findViewById(R.id.article_image_view);
            articleTitle = itemView.findViewById(R.id.article_title_text_view);
            articleAuthor = itemView.findViewById(R.id.article_author_text_view);
            articleDate = itemView.findViewById(R.id.article_date_text_view);
            button = itemView.findViewById(R.id.favorite_button);

            /* Set an onItemClickListener for each article */
            this.onItemClickListener = onItemClickListener;

            /* Set an onClickListener for the bookmark button */
            itemView.setOnClickListener(this);

            itemView.findViewById(R.id.favorite_button).setOnClickListener(v -> {
                /* Search the favoritesList for any items with the same title and author */
                int index = searchListForFav(newsArticle.getTitle(), newsArticle.getAuthor());

                /* If a matching item is found remove it from the index position */
                if (index != -1) {
                    MainActivity.getFavoritesList().remove(index);
                    Toast.makeText(itemView.getContext(), itemView.getContext().getString(R.string.article_removed), Toast.LENGTH_SHORT).show();

                } else {
                    /* If no matching item is found add it to the favoritesList */
                    MainActivity.getFavoritesList().add(newsArticle);
                    Toast.makeText(itemView.getContext(), itemView.getContext().getString(R.string.article_added), Toast.LENGTH_SHORT).show();

                }

                /* Vibrate on button click */
                MainActivity.vibrator.vibrate(100);

                /* Save the updated favoritesList to SharedPreferences */
                UsersSharedPreferences.saveList(itemView.getContext(), MainActivity.getFavoritesList());

            });
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(newsList.get(getAdapterPosition()));
        }
    }


}
