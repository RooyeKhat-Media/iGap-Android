package io.github.meness.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.widget.GridView;

import com.vanniktech.emoji.R;

class EmojiGridView extends GridView {
    EmojiGridView(final Context context) {
        super(context);

        final Resources resources = getResources();

        setColumnWidth(resources.getDimensionPixelSize(R.dimen.emoji_grid_view_column_width));

        final int spacing = resources.getDimensionPixelSize(R.dimen.emoji_grid_view_spacing);
        setHorizontalSpacing(spacing);
        setVerticalSpacing(spacing);
        setPadding(spacing, spacing, spacing, spacing);
        setNumColumns(AUTO_FIT);
        setClipToPadding(false);
    }
}
