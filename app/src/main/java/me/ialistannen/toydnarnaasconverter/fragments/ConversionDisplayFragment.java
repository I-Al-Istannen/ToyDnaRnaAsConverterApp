package me.ialistannen.toydnarnaasconverter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.List;
import me.ialistannen.toydnarnaasconverter.R;
import me.ialistannen.toydnarnaasconverter.codon.Codon;
import me.ialistannen.toydnarnaasconverter.view.CodonListView;

/**
 * A fragment that displays a codon sequence.
 */
public class ConversionDisplayFragment extends FragmentBase {

  @BindView(R.id.codon_list)
  CodonListView codonListView;

  private List<Codon> codons = new ArrayList<>();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_conversion_display, container, false);

    ButterKnife.bind(this, view);

    codonListView.setItems(codons);

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    getFragmentHolderActivity().setTitle(R.string.fragment_conversion_display_title);
  }

  /**
   * @param codons The {@link Codon}s this Fragment should display.
   */
  void setCodons(List<Codon> codons) {
    this.codons = new ArrayList<>(codons);

    if (codonListView != null) {
      codonListView.setItems(codons);
    }
  }
}
