package co.edu.udea.compumovil.gr10_20172.sugiereme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    ImageView imageView;
    TextView descriptionView;
    Button buttonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView=(ImageView) findViewById(R.id.activity_detail_image);
        descriptionView=(TextView) findViewById(R.id.activity_detail_text_example);
        buttonView=(Button) findViewById(R.id.activity_detail_button);

        descriptionView.setText(getIntent().getStringExtra("description"));
        Picasso.with(getBaseContext())
                .load(getIntent().getStringExtra("image"))
                .into(imageView);

    }
}
