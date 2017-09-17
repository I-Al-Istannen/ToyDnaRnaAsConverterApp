package me.ialistannen.toydnarnaasconverter.fragments;

import java.util.List;
import me.ialistannen.toydnarnaasconverter.R;
import me.ialistannen.toydnarnaasconverter.codon.Codon;

/**
 * A fragment to input a mRNA sequence.
 */
public class MRnaInputFragment extends SequenceInputFragment {

  @Override
  public void onResume() {
    super.onResume();
    getFragmentHolderActivity().setTitle(R.string.fragment_mrna_input_title);
  }

  @Override
  protected void onReceivedSequence(String sequence) {
    List<Codon> codons = mRnaToCodons(sequence);

    if (codons.isEmpty()) {
      return;
    }

    ConversionDisplayFragment displayFragment = new ConversionDisplayFragment();
    displayFragment.setCodons(codons);

    getFragmentHolderActivity().switchToFragmentPushBack(displayFragment);
  }
}
