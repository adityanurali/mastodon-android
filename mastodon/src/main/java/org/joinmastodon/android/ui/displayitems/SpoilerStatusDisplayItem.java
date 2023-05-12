package org.joinmastodon.android.ui.displayitems;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joinmastodon.android.R;
import org.joinmastodon.android.fragments.BaseStatusListFragment;
import org.joinmastodon.android.model.Status;
import org.joinmastodon.android.ui.OutlineProviders;
import org.joinmastodon.android.ui.drawables.SpoilerStripesDrawable;
import org.joinmastodon.android.ui.text.HtmlParser;
import org.joinmastodon.android.ui.utils.CustomEmojiHelper;

import java.util.ArrayList;

import me.grishka.appkit.imageloader.ImageLoaderViewHolder;
import me.grishka.appkit.imageloader.requests.ImageLoaderRequest;

public class SpoilerStatusDisplayItem extends StatusDisplayItem{
	public final Status status;
	public final ArrayList<StatusDisplayItem> contentItems=new ArrayList<>();
	private final CharSequence parsedTitle;
	private final CustomEmojiHelper emojiHelper;

	public SpoilerStatusDisplayItem(String parentID, BaseStatusListFragment parentFragment, Status status){
		super(parentID, parentFragment);
		this.status=status;
		parsedTitle=HtmlParser.parseCustomEmoji(status.spoilerText, status.emojis);
		emojiHelper=new CustomEmojiHelper();
		emojiHelper.setText(parsedTitle);
	}

	@Override
	public int getImageCount(){
		return emojiHelper.getImageCount();
	}

	@Override
	public ImageLoaderRequest getImageRequest(int index){
		return emojiHelper.getImageRequest(index);
	}

	@Override
	public Type getType(){
		return Type.SPOILER;
	}

	public static class Holder extends StatusDisplayItem.Holder<SpoilerStatusDisplayItem> implements ImageLoaderViewHolder{
		private final TextView title, action;
		private final View button;

		public Holder(Context context, ViewGroup parent){
			super(context, R.layout.display_item_spoiler, parent);
			title=findViewById(R.id.spoiler_title);
			action=findViewById(R.id.spoiler_action);
			button=findViewById(R.id.spoiler_button);

			button.setOutlineProvider(OutlineProviders.roundedRect(8));
			button.setClipToOutline(true);
			LayerDrawable spoilerBg=(LayerDrawable) button.getBackground().mutate();
			spoilerBg.setDrawableByLayerId(R.id.left_drawable, new SpoilerStripesDrawable(true));
			spoilerBg.setDrawableByLayerId(R.id.right_drawable, new SpoilerStripesDrawable(false));
			button.setBackground(spoilerBg);
			button.setOnClickListener(v->item.parentFragment.onRevealSpoilerClick(this));
		}

		@Override
		public void onBind(SpoilerStatusDisplayItem item){
			title.setText(item.parsedTitle);
			action.setText(item.status.spoilerRevealed ? R.string.spoiler_hide : R.string.spoiler_show);
		}

		@Override
		public void setImage(int index, Drawable image){
			item.emojiHelper.setImageDrawable(index, image);
			title.invalidate();
		}

		@Override
		public void clearImage(int index){
			setImage(index, null);
		}
	}
}