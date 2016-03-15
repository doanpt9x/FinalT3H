package com.t3h.mail;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.t3h.final_t3h.R;

import java.util.List;

/**
 * Created by Shiloh Marion on 11/03/2016.
 */
public class ListMailAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<MailItem> mLisItem;

    public ListMailAdapter(Context mContext, List<MailItem> mLisItem) {
        this.mContext = mContext;
        this.mLisItem = mLisItem;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mLisItem.size();
    }

    @Override
    public MailItem getItem(int position) {
        return mLisItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.gmail_item, parent, false);
            // get all UI view
            holder = new ViewHolder(convertView);
            // set tag for holder
            convertView.setTag(holder);
        } else {
            // if holder created, get tag from view
            holder = (ViewHolder) convertView.getTag();
        }

        MailItem item = mLisItem.get(position);
        holder.tvFrom.setText(item.getFrom());
        holder.ivAttachFile.setVisibility(item.isAttacked() ? View.VISIBLE : View.GONE);
        holder.tvTime.setText(item.getTime());
        holder.tvSnippet.setText(item.getSnippet());
        holder.ivStar.setImageResource(item.isStarred() ? R.drawable.ic_star : R.drawable.ic_star_outline);

        //get first letter of each String item
        String firstLetter = String.valueOf(Character.isLetterOrDigit(item.getFrom().charAt(0))? item.getFrom().charAt(0): item.getFrom().charAt(1)).toUpperCase();
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color = generator.getColor(item);
        TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, color); // radius in px
        holder.ivMailIcon.setImageDrawable(drawable);

        return convertView;
    }

    private class ViewHolder {
        private ImageView ivMailIcon, ivAttachFile, ivStar;
        private TextView tvFrom, tvTime, tvSnippet;

        public ViewHolder(View v) {
            ivMailIcon = (ImageView) v.findViewById(R.id.iv_mail_icon);
            ivAttachFile = (ImageView) v.findViewById(R.id.iv_attach_file);
            ivStar = (ImageView) v.findViewById(R.id.iv_star);

            tvFrom = (TextView) v.findViewById(R.id.tv_from);
            tvTime = (TextView) v.findViewById(R.id.tv_time);
            tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);
        }
    }
}
