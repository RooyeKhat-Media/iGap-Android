package net.iGap.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDataUsage;
import net.iGap.interfaces.DataUsageListener;
import net.iGap.module.AndroidUtils;
import net.iGap.module.structs.DataUsageStruct;
import net.iGap.realm.RealmDataUsage;

import java.util.ArrayList;
import java.util.Collection;

import static net.iGap.G.context;

public class DataUsageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<DataUsageStruct> dataList;
    private long totalReceive;
    private long totalSend;
    private boolean connectivityType;
    private DataUsageListener clearData;

    public DataUsageAdapter(Context context, ArrayList<DataUsageStruct> dataList, long totalReceive, long totalSend, boolean connectivityType, DataUsageListener clearData) {
        Log.i("WWW", "totalReceive: " + totalReceive);
        this.context = context;
        this.dataList = dataList;
        this.totalReceive = totalReceive;
        this.totalSend = totalSend;
        this.connectivityType = connectivityType;
        this.clearData = clearData;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());

        switch (viewType) {
            case 0:
                return new BaseHolder(inflater.inflate(R.layout.item_data_usage, parent, false));
            case 1:
                return new TotalViewHolder(inflater.inflate(R.layout.item_data_usage_total, parent, false));
            case 2:
                return new ClearDataHolder(inflater.inflate(R.layout.item_data_usage_reset, parent, false));
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaseHolder) {
            ((BaseHolder) holder).txtByteReceivedNum.setText(AndroidUtils.humanReadableByteCount(dataList.get(position).getByteReceived(), true));
            ((BaseHolder) holder).txtByteSentNum.setText(AndroidUtils.humanReadableByteCount(dataList.get(position).getByteSend(), true));


            switch (dataList.get(position).getTitle()) {
                case "IMAGE":
                    ((BaseHolder) holder).txtTitle.setText(context.getResources().getString(R.string.image_message));
                    break;
                case "VIDEO":
                    ((BaseHolder) holder).txtTitle.setText(context.getResources().getString(R.string.video_message));

                    break;

                case "FILE":
                    ((BaseHolder) holder).txtTitle.setText(context.getResources().getString(R.string.file_message));

                    break;
                case "AUDIO":
                    ((BaseHolder) holder).txtTitle.setText(context.getResources().getString(R.string.audio_message));
                    break;

                case "UNRECOGNIZED":
                    ((BaseHolder) holder).txtTitle.setText(context.getResources().getString(R.string.st_Other));
                    break;

            }
            //    ((BaseHolder) holder).txtTitle.setText(dataList.get(position).getTitle());
    /*        if (HelperCalander.isPersianUnicode) {
            *//*    holder.txtLastMessage.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtLastMessage.getText().toString()));
                holder.txtUnread.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txtUnread.getText().toString()));*//*

                ((BaseHolder) holder).txtSentNum.setText(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(dataList.get(position).getSendNum())));
                ((BaseHolder) holder).txtReceivedNum.setText(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(dataList.get(position).getReceivednum())));
            }
*/
            ((BaseHolder) holder).txtSentNum.setText(String.valueOf(dataList.get(position).getSendNum()));

            ((BaseHolder) holder).txtReceivedNum.setText(String.valueOf(dataList.get(position).getReceivednum()));

        } else if (holder instanceof TotalViewHolder) {

            //  ((TotalViewHolder)holder).txtTotalReceivedByte.setText(String.valueOf(totalReceive));
            ((TotalViewHolder) holder).txtTotalReceivedByte.setText(AndroidUtils.humanReadableByteCount(totalReceive, true));
            //  ((TotalViewHolder)holder).txtTotalSentByte.setText(String.valueOf(totalSend));
            ((TotalViewHolder) holder).txtTotalSentByte.setText(AndroidUtils.humanReadableByteCount(totalSend, true));

        } else if (holder instanceof ClearDataHolder) {
            ((ClearDataHolder) holder).txtClearData.setText(context.getResources().getString(R.string.clear_data_usage));
            ((ClearDataHolder) holder).rvClearDataUsage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new MaterialDialog.Builder(context).title(R.string.clearDataUsage)
                            //  .content(String.format(context.getString(R.string.pin_messages_content), context.getString(R.string.unpin)))

                            .positiveText(R.string.yes)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    clearData.doClearDB(connectivityType);
                                    dialog.dismiss();
                                }
                            }).negativeText(R.string.no).show();


                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return dataList.get(position).getViewType();
    }


    public class BaseHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtSentNum, txtReceivedNum, txtByteSentNum, txtByteReceivedNum, txtByteReceived, txtByteSent, txtReceived, txtSent;
        CardView rootBaseCard;
        View view1, view2, view3;

        public BaseHolder(View itemView) {
            super(itemView);
            txtByteReceivedNum = (TextView) itemView.findViewById(R.id.txtByteReceivedNum);
            txtByteReceivedNum.setTypeface(G.typeface_IRANSansMobile);
           // txtByteReceivedNum.setTextColor(Color.parseColor(G.textSubTheme));

            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtTitle.setTypeface(G.typeface_IRANSansMobile);



            txtSentNum = (TextView) itemView.findViewById(R.id.txtSentNum);
            txtSentNum.setTypeface(G.typeface_IRANSansMobile);
        //    txtSentNum.setTextColor(Color.parseColor(G.textSubTheme));

            txtReceivedNum = (TextView) itemView.findViewById(R.id.txtReceivedNum);
            txtReceivedNum.setTypeface(G.typeface_IRANSansMobile);
         //   txtReceivedNum.setTextColor(Color.parseColor(G.textSubTheme));

            txtByteSentNum = (TextView) itemView.findViewById(R.id.txtByteSentNum);
            txtByteSentNum.setTypeface(G.typeface_IRANSansMobile);
       //     txtByteSentNum.setTextColor(Color.parseColor(G.textSubTheme));


            txtByteReceived = (TextView) itemView.findViewById(R.id.txtByteReceived);
            txtByteReceived.setText(context.getResources().getString(R.string.bytes_received));
            txtByteReceived.setTypeface(G.typeface_IRANSansMobile);
            txtByteReceived.setTextColor(Color.parseColor(G.textTitleTheme));

            txtByteSent = (TextView) itemView.findViewById(R.id.txtByteSent);
            txtByteSent.setText(context.getResources().getString(R.string.bytes_sent));
            txtByteSent.setTypeface(G.typeface_IRANSansMobile);
            txtByteSent.setTextColor(Color.parseColor(G.textTitleTheme));

            txtReceived = (TextView) itemView.findViewById(R.id.txtReceived);
            txtReceived.setText(context.getResources().getString(R.string.received));
            txtReceived.setTypeface(G.typeface_IRANSansMobile);
            txtReceived.setTextColor(Color.parseColor(G.textTitleTheme));

            txtSent = (TextView) itemView.findViewById(R.id.txtSent);
            txtSent.setText(context.getResources().getString(R.string.sent));
            txtSent.setTypeface(G.typeface_IRANSansMobile);
            txtSent.setTextColor(Color.parseColor(G.textTitleTheme));

            rootBaseCard = itemView.findViewById(R.id.rootBaseCard);
            rootBaseCard.setCardBackgroundColor(Color.parseColor(G.backgroundTheme));


            view1 = itemView.findViewById(R.id.view1);
            view2 = itemView.findViewById(R.id.view2);
            view3 = itemView.findViewById(R.id.view3);

            view1.setBackgroundColor(Color.parseColor(G.backgroundTheme_2));
            view2.setBackgroundColor(Color.parseColor(G.backgroundTheme_2));
            view3.setBackgroundColor(Color.parseColor(G.backgroundTheme_2));

        }
    }

    public class TotalViewHolder extends RecyclerView.ViewHolder {
        TextView txtTotalSentByte, txtTotalReceivedByte, txtTotalReceived, txtTotalSent, txtTitle;
        CardView rootDataUsageTotal;


        public TotalViewHolder(View itemView) {
            super(itemView);
            txtTotalSentByte = itemView.findViewById(R.id.txtTotalSentByte);
         //   txtTotalSentByte.setTextColor(Color.parseColor(G.textSubTheme));
            txtTotalSentByte.setTypeface(G.typeface_IRANSansMobile);

            txtTotalReceivedByte = itemView.findViewById(R.id.txtTotalReceivedByte);
        //    txtTotalReceivedByte.setTextColor(Color.parseColor(G.textSubTheme));
            txtTotalReceivedByte.setTypeface(G.typeface_IRANSansMobile);



            txtTotalReceived = itemView.findViewById(R.id.txtTotalReceived);
            txtTotalReceived.setText(context.getResources().getString(R.string.total_received));
            txtTotalReceived.setTypeface(G.typeface_IRANSansMobile);
            txtTotalReceived.setTextColor(Color.parseColor(G.textTitleTheme));

            txtTotalSent = itemView.findViewById(R.id.txtTotalSent);
            txtTotalSent.setText(context.getResources().getString(R.string.total_sent));
            txtTotalSent.setTypeface(G.typeface_IRANSansMobile);
            txtTotalSent.setTextColor(Color.parseColor(G.textTitleTheme));

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtTitle.setText(context.getResources().getString(R.string.total));
            txtTitle.setTypeface(G.typeface_IRANSansMobile);

            rootDataUsageTotal = itemView.findViewById(R.id.rootDataUsageTotal);
            rootDataUsageTotal.setCardBackgroundColor(Color.parseColor(G.backgroundTheme));


        }
    }

    public class ClearDataHolder extends RecyclerView.ViewHolder {
        TextView txtClearData;
        RelativeLayout rvClearDataUsage;
        CardView rootDataUsageReset;

        public ClearDataHolder(View itemView) {
            super(itemView);
            txtClearData = itemView.findViewById(R.id.txtClearData);
            txtClearData.setTypeface(G.typeface_IRANSansMobile);

            rvClearDataUsage = itemView.findViewById(R.id.rvClearDataUsage);
            rootDataUsageReset = itemView.findViewById(R.id.rootDataUsageReset);
            rootDataUsageReset.setCardBackgroundColor(Color.parseColor(G.backgroundTheme));
        }
    }

}
