package com.veltus.covidnewstracking.Adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.veltus.covidnewstracking.Activities.MainActivity;
import com.veltus.covidnewstracking.Fragments.FavoritesFragment;
import com.veltus.covidnewstracking.Fragments.NewsFeedFragment;
import com.veltus.covidnewstracking.ObjectClass.NewsArticle;
import com.veltus.covidnewstracking.Queries.DownloadImageUrl;
import com.veltus.covidnewstracking.R;
import com.veltus.covidnewstracking.SharedPreferences.UsersSharedPreferences;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RecyclerAdapterFavorites extends RecyclerView.Adapter<RecyclerAdapterFavorites.MyViewHolder> {
    public String website;
    private List<NewsArticle> newsList;
    private OnItemClickListener mOnItemClickListener;


    public RecyclerAdapterFavorites(List<NewsArticle> newsList, OnItemClickListener onItemClickListener) {
        this.newsList = newsList;
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerAdapterFavorites.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_favorites, parent, false);
        return new MyViewHolder(view, mOnItemClickListener);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterFavorites.MyViewHolder holder, int position) {

        /* Get the position and save it to article */
        NewsArticle article = newsList.get(position);

        /* Find the imageUrl from the article and check if it's null */
        String imageUrl = article.getImageUrl();

        /* If the imageUrl is not null and is connected to the internet, download image and set the ImageView to visible */
        if (!imageUrl.equalsIgnoreCase("null") && NewsFeedFragment.hasInternetConnection) {
            new DownloadImageUrl(holder.articleImage).execute(imageUrl);
            holder.articleImage.setVisibility(View.VISIBLE);

        } else {
            /* If imageUrl is null or noInternetConnection hide the articleImage ImageView */
            holder.articleImage.setVisibility(View.GONE);
        }

        /* Find the article position values and set them to the corresponding view holder */
        website = newsList.get(position).getWebsite();
        holder.newsArticle = article;
        String title = newsList.get(position).getTitle();
        holder.articleTitle.setText(title);
        String author = newsList.get(position).getAuthor();
        holder.articleAuthor.setText(author);

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
        private ImageButton button;
        private NewsArticle newsArticle;


        public MyViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            /* Find and link views in the list_view_favorites.xml to the values in MyViewHolder */
            articleImage = itemView.findViewById(R.id.article_image_view);
            articleTitle = itemView.findViewById(R.id.article_title_text_view);
            articleAuthor = itemView.findViewById(R.id.article_author_text_view);
            articleDate = itemView.findViewById(R.id.article_date_text_view);
            button = itemView.findViewById(R.id.delete_button);

            /* Set an onItemClickListener for each article */
            this.onItemClickListener = onItemClickListener;

            /* Set an onClickListener for the delete button */
            itemView.setOnClickListener(this);
            button.setOnClickListener(v -> {
                /* If delete button is clicked remove selected article from favoritesList */
                MainActivity.getFavoritesList().remove(newsArticle);
                FavoritesFragment.getArticleArrayList().remove(newsArticle);
                /* Save the updated favoritesList to SharedPreferences and notify adapter */
                UsersSharedPreferences.saveList(itemView.getContext(), MainActivity.getFavoritesList());
                FavoritesFragment.getFavoritesAdapter().notifyDataSetChanged();
                NewsFeedFragment.getNewsFeedAdapter().notifyDataSetChanged();
                /* Vibrate on button click */
                MainActivity.vibrator.vibrate(100);
                Toast.makeText(itemView.getContext(), itemView.getContext().getString(R.string.article_deleted), Toast.LENGTH_SHORT).show();


            });
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(newsList.get(getAdapterPosition()));
        }
    }

}
