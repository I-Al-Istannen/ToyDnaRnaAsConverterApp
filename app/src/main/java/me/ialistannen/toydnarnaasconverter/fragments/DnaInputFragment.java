package me.ialistannen.toydnarnaasconverter.fragments;

import android.app.AlertDialog;
import java.util.List;
import me.ialistannen.toydnarnaasconverter.R;
import me.ialistannen.toydnarnaasconverter.codon.Codon;
import me.ialistannen.toydnarnaasconverter.util.BaseSequenceConverter;

/**
 * Allows for the input of a DNA base sequence.
 */
public class DnaInputFragment extends SequenceInputFragment {

  @Override
  public void onResume() {
    super.onResume();
    getActivity().setTitle(R.string.fragment_dna_input_title);
  }

  @Override
  protected void onReceivedSequence(String sequence) {
    try {
      List<Codon> codons = mRnaToCodons(BaseSequenceConverter.dnaToMRna(sequence));

      if (codons.isEmpty()) {
        return;
      }

      ConversionDisplayFragment displayFragment = new ConversionDisplayFragment();
      displayFragment.setCodons(codons);

      getFragmentHolderActivity().switchToFragmentPushBack(displayFragment);
    } catch (IllegalArgumentException e) {
      new AlertDialog.Builder(getActivity())
          .setTitle(R.string.error_parsing_sequence)
          .setMessage(e.getMessage())
          .create()
          .show();
    }
  }
}
