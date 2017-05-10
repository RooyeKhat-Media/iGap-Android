package net.iGap.emoji;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Collection;
import net.iGap.emoji.emoji.Emoji;
import net.iGap.emoji.listeners.OnEmojiClickedListener;

final class RecentEmojiGridView extends EmojiGridView {
    private RecentEmoji recentEmojis;
    private EmojiArrayAdapter emojiArrayAdapter;

    RecentEmojiGridView(@NonNull final Context context) {
        super(context);
    }

    public RecentEmojiGridView init(@Nullable final OnEmojiClickedListener onEmojiClickedListener, final RecentEmoji recentEmoji) {
        this.recentEmojis = recentEmoji;

        final Collection<Emoji> emojis = recentEmojis.getRecentEmojis();
        emojiArrayAdapter = new EmojiArrayAdapter(getContext(), emojis.toArray(new Emoji[emojis.size()]));
        emojiArrayAdapter.setOnEmojiClickedListener(onEmojiClickedListener);
        this.setAdapter(emojiArrayAdapter);

        return this;
    }

    public void invalidateEmojis() {
        emojiArrayAdapter.updateEmojis(recentEmojis.getRecentEmojis());
    }

    public int numberOfRecentEmojis() {
        return emojiArrayAdapter.getCount();
    }
}
