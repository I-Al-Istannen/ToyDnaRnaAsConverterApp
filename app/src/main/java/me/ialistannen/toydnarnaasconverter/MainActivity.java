package me.ialistannen.toydnarnaasconverter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.ialistannen.toydnarnaasconverter.fragments.DnaInputFragment;
import me.ialistannen.toydnarnaasconverter.fragments.FragmentHolderActivity;
import me.ialistannen.toydnarnaasconverter.fragments.MRnaInputFragment;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ButterKnife.bind(this);
  }

  @OnClick(R.id.m_rna_to_aa_sequence)
  void onmRnaToAaSequence() {
    startFragment(MRnaInputFragment.class.getSimpleName());
  }

  @OnClick(R.id.dna_to_aa_sequence)
  void onDnaToAaSequence() {
    startFragment(DnaInputFragment.class.getSimpleName());
  }

  private void startFragment(String simpleName) {
    Intent intent = new Intent(this, FragmentHolderActivity.class);
    intent.putExtra(
        FragmentHolderActivity.FRAGMENT_EXTRA_KEY, simpleName
    );
    startActivity(intent);
  }
}
