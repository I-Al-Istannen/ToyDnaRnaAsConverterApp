package me.ialistannen.toydnarnaasconverter.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.List;
import me.ialistannen.toydnarnaasconverter.R;
import me.ialistannen.toydnarnaasconverter.codon.Codon;
import me.ialistannen.toydnarnaasconverter.util.PixelUtil;

/**
 * A small holder to display a codon and what it maps to
 */
public class CodonListView extends RecyclerView {

  {
    init();
  }

  public CodonListView(Context context) {
    super(context);
  }

  public CodonListView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public CodonListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  private void init() {
    setLayoutManager(
        new GridLayoutManager(
            getContext(),
            4,
            LinearLayoutManager.VERTICAL,
            false
        )
    );
    setAdapter(new Adapter());

    addItemDecoration(new ItemDecoration() {
      @Override
      public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
        int padding = PixelUtil.dpToPixels(getContext(), 8);
        outRect.top = padding;
        outRect.left = padding;
        outRect.right = padding;
        outRect.bottom = padding;
      }
    });
  }

  /**
   * @param items The {@link Codon}s to display
   */
  public void setItems(List<Codon> items) {
    ((Adapter) getAdapter()).setCodons(items);
  }

  private static class Adapter extends RecyclerView.Adapter<CodonViewHolder> {

    private List<Codon> codons = new ArrayList<>();

    @Override
    public CodonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());
      return new CodonViewHolder(
          inflater.inflate(R.layout.codon_list_view_item, parent, false)
      );
    }

    @Override
    public void onBindViewHolder(CodonViewHolder holder, int position) {
      holder.setCodon(codons.get(position));
    }

    @Override
    public int getItemCount() {
      return codons.size();
    }

    /**
     * @param codons The {@link Codon}s to display.
     */
    private void setCodons(List<Codon> codons) {
      this.codons = new ArrayList<>(codons);
    }
  }

  static class CodonViewHolder extends ViewHolder {

    @BindView(R.id.codon)
    TextView codon;

    @BindView(R.id.codon_meaning)
    TextView codonMeaning;

    private CodonViewHolder(View itemView) {
      super(itemView);

      ButterKnife.bind(this, itemView);
    }

    private void setCodon(Codon codon) {
      this.codon.setText(codon.getSequence());

      @StringRes
      int codonMeaningText = -1;
      String formatArgument = codon.getSequence();

      switch (codon.getType()) {
        case START:
          codonMeaningText = R.string.codon_meaning_start;
          //noinspection ConstantConditions
          formatArgument = codon.asAcid().getAbbreviation();
          break;
        case STOP:
          codonMeaningText = R.string.codon_meaning_stop;
          break;
        case AMINO_ACID:
          codonMeaningText = R.string.codon_meaning_amino_acid;
          //noinspection ConstantConditions
          formatArgument = codon.asAcid().getAbbreviation();
          break;
      }

      codonMeaning.setText(
          itemView.getContext().getString(codonMeaningText, formatArgument)
      );
    }
  }
}
