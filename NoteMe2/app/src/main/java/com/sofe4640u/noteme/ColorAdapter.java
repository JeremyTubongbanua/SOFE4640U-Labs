package com.sofe4640u.noteme;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private List<NoteColour> colors;
    private OnColorClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnColorClickListener {
        void onColorClick(NoteColour color);
    }

    public ColorAdapter(List<NoteColour> colors, OnColorClickListener listener) {
        this.colors = colors;
        this.listener = listener;
    }

    public void resetSelection() {
        selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.colour_item, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        NoteColour color = colors.get(position);
        holder.bind(color, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    class ColorViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout colorLayout;
        private TextView colorText;
        private View colorCircle;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            colorLayout = itemView.findViewById(R.id.colorLayout);
            colorText = itemView.findViewById(R.id.colorName);
            colorCircle = itemView.findViewById(R.id.colorCircle);
        }

        public void bind(NoteColour color, boolean isSelected) {
            colorText.setText(color.name());

            int colorValue = Color.rgb(color.getR(), color.getG(), color.getB());
            colorCircle.setBackgroundColor(colorValue);

            // Update UI based on selection
//            if (isSelected) {
//                // Highlight selected item
//                colorLayout.setBackgroundResource(R.drawable);
//            } else {
//                // Remove highlight
//                colorLayout.setBackgroundResource(0);
//            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int previousSelectedPosition = selectedPosition;

                    if (selectedPosition == getAdapterPosition()) {
                        selectedPosition = RecyclerView.NO_POSITION;
                        listener.onColorClick(null); // Pass null to indicate no color filter
                    } else {
                        selectedPosition = getAdapterPosition();
                        listener.onColorClick(color);
                    }

                    notifyItemChanged(previousSelectedPosition);
                    notifyItemChanged(selectedPosition);
                }
            });
        }
    }
}
