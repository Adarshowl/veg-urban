package wrteam.ecart.shop.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import wrteam.ecart.shop.R;
import wrteam.ecart.shop.helper.ApiConfig;
import wrteam.ecart.shop.helper.Constant;

public class ScanAndPayFragment extends Fragment {

    Activity activity;
    String qrCodeMessage;
    private View root;
    private ImageView ivQRCode;
    private TextView tvShareQRCode, tvNotAvailable;

    public ScanAndPayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        root = inflater.inflate(R.layout.fragment_scan_and_pay, container, false);
        activity = getActivity();

        ivQRCode = root.findViewById(R.id.shareimage);
        tvShareQRCode = root.findViewById(R.id.tvShareQrCode);
        tvNotAvailable = root.findViewById(R.id.tvNotAvailable);

        tvShareQRCode.setOnClickListener(v -> {
            shareImageWithText();
        });

        getQRCode();
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.scan_and_pay);
    }

    private void shareImageWithText() {

        Drawable drawable = ivQRCode.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        try {
            File file = new File(getActivity().getExternalCacheDir(), File.separator + "image.jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, qrCodeMessage);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);

            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/jpg");
            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // get data from api
    void getQRCode() {

        Map<String, String> params = new HashMap<>();
        params.put("image", "1");

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean(Constant.ERROR)) {
                        Picasso.get()
                                .load(jsonObject.getJSONObject("data").getString("image"))
                                .fit()
                                .centerInside()
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .into(ivQRCode);
//                        tvShareQRCode.setText(jsonObject.getJSONObject("data").getString("name"));
                        qrCodeMessage = jsonObject.getJSONObject("data").getString("name");
                    } else {
                        tvShareQRCode.setVisibility(View.GONE);
                        ivQRCode.setVisibility(View.GONE);
                        tvNotAvailable.setText("No data available.");
                        tvNotAvailable.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.GET_QR_CODE, params, true);
    }

}

//surajcomputersindore@gmail.com